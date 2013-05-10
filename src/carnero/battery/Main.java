package carnero.battery;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.BatteryManager;
import android.os.IBinder;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

public class Main extends Service {

	private StatusView mStatusView;
	private BatteryStatusReceiver mReceiver;
	private NotificationManager mNotificationManager;
	private Notification.Builder mNotificationBuilder;
	// constants
	private static final int NOTIFICATION_ID = 47;

	@Override
	public void onCreate() {
		super.onCreate();

		// battery status view
		mStatusView = new StatusView(this);

		final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, // over anything else (even lockscreen and expanded status bar)
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, // above status bar
				PixelFormat.TRANSLUCENT
		);
		lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		lp.setTitle(getString(R.string.app_name));

		final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		wm.addView(mStatusView, lp);

		// notification
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotificationBuilder = new Notification.Builder(this)
				.setOngoing(true)
				.setSmallIcon(R.drawable.ic_notification)
				.setTicker(getString(R.string.app_name))
				.setContentTitle(getString(R.string.notification_loading))
				.setContentText("");

		startForeground(NOTIFICATION_ID, mNotificationBuilder.build());

		// battery status receiver
		mReceiver = new BatteryStatusReceiver();

		final IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy() {
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}

		if (mStatusView != null) {
			final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
			wm.removeView(mStatusView);
			mStatusView = null;
		}

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// classes

	private class BatteryStatusReceiver extends BroadcastReceiver {

		private int mStatus = -1;
		private int mStatusOld = -1;
		private int mLevel = 0;
		private int mScale = 0;
		private boolean mCharging = false;
		private int mPercent;

		@Override
		public void onReceive(Context context, Intent intent) {
			mStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			mLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			mScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
			mCharging = (mStatus == BatteryManager.BATTERY_STATUS_CHARGING || mStatus == BatteryManager.BATTERY_STATUS_FULL);

			if (mStatusView != null) {
				mStatusView.onBatteryChanged(mCharging, mLevel, mScale);
			}

			if (mNotificationManager != null && mNotificationBuilder != null) {
				mPercent = (int) (((float) mLevel / (float) mScale) * 100);

				mNotificationBuilder.setContentTitle(Integer.toString(mPercent) + "%");
				if (mStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
					mNotificationBuilder.setContentText(getString(R.string.notification_charging));
				} else if (mStatus == BatteryManager.BATTERY_STATUS_FULL) {
					mNotificationBuilder.setContentText(getString(R.string.notification_full));
				} else {
					mNotificationBuilder.setContentText(getString(R.string.notification_discharging));
				}
				if (mPercent < 15) {
					mNotificationBuilder.setLights(getResources().getColor(R.color.led_critical), 1000, 500);
				}
				if (mStatus != mStatusOld) {
					mNotificationBuilder.setWhen(System.currentTimeMillis());
				}

				mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
			}

			mStatus = mStatusOld;
		}
	}
}

package carnero.battery;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
		Intent intent = new Intent(this, Intro.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new Notification(R.drawable.icon, getString(R.string.app_name), (Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR));
		notification.setLatestEventInfo(this, getString(R.string.app_name), getString(R.string.app_name), pending);

		startForeground(NOTIFICATION_ID, notification);

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

	public class BatteryStatusReceiver extends BroadcastReceiver {

		int mStatus = -1;
		int mLevel = 0;
		int mScale = 0;
		boolean mCharging = false;

		@Override
		public void onReceive(Context context, Intent intent) {
			mStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			mLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			mScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
			mCharging = (mStatus == BatteryManager.BATTERY_STATUS_CHARGING || mStatus == BatteryManager.BATTERY_STATUS_FULL);

			if (mStatusView != null) {
				mStatusView.onBatteryChanged(mCharging, mLevel, mScale);
			}
		}
	}
}

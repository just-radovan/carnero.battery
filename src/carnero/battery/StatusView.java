package carnero.battery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import carnero.battery.listener.BatteryStatusListener;

public class StatusView extends View implements BatteryStatusListener {

	private Paint mBackground;
	private Paint mForeground;
	private int mColorCharging;
	private int mColorDischarging;
	private int mHeight;
	private float mPercent;
	private int mWidthLevel;
	private boolean mBattCharging = false;
	private int mBattLevel = 0;
	private int mBattScale = 100;

	public StatusView(Context context) {
		super(context);
		init();
	}

	public StatusView(Context context, AttributeSet attributes) {
		super(context, attributes);
		init();
	}

	public StatusView(Context context, AttributeSet attributes, int style) {
		super(context, attributes, style);
		init();
	}

	private void init() {
		mHeight = (int) getResources().getDimension(R.dimen.status_height);

		mBackground = new Paint();
		mBackground.setAntiAlias(true);
		mBackground.setColor(getResources().getColor(R.color.status_background));

		mForeground = new Paint();
		mForeground.setAntiAlias(true);

		mColorCharging = getResources().getColor(R.color.status_fg_charging);
		mColorDischarging = getResources().getColor(R.color.status_fg_discharging);
	}

	@Override
	public void onMeasure(int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec);

		setMeasuredDimension(getMeasuredWidth(), mHeight);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mBattScale <= 0) {
			return;
		}
		mPercent = (float) mBattLevel / (float) mBattScale;
		mWidthLevel = (int) (mPercent * getWidth());
		if (mBattCharging) {
			mForeground.setColor(mColorCharging);
		} else {
			mForeground.setColor(mColorDischarging);
		}

		canvas.save();

		canvas.drawRect(0, 0, mWidthLevel, mHeight, mForeground);
		canvas.drawRect(mWidthLevel, 0, getWidth(), mHeight, mBackground);

		canvas.restore();
	}

	public void onBatteryChanged(boolean charging, int level, int scale) {
		mBattCharging = charging;
		mBattLevel = level;
		mBattScale = scale;

		invalidate();
	}
}

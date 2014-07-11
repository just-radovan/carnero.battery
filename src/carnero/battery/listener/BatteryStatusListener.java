package carnero.battery.listener;

public interface BatteryStatusListener {

    public void onBatteryChanged(boolean status, int level, int scale, float temp);
}

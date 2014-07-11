package carnero.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final Intent serviceIntent = new Intent(context, Main.class);
        context.startService(serviceIntent);
    }
}

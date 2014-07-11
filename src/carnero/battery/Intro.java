package carnero.battery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Intro extends Activity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        final Intent intent = new Intent(this, Main.class);
        startService(intent);

        finish();
    }
}

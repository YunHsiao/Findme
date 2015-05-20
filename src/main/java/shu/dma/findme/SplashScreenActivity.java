package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.inmobi.commons.InMobi;

/**
 * App Entrance Activity.
 * Show tips in sequence every time the app is launched.
 */
public class SplashScreenActivity extends Activity implements OnClickListener {

    private static RelativeLayout rl;
    private static ViewFlipper ctn;
    private static TextView tips;
    private static Activity context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        InMobi.initialize(this, getString(R.string.resID));
        context = this;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                tips = (TextView) findViewById(R.id.tips);
                tips.setText(Constants.getTips(context));
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences.Editor editor = getSharedPreferences("data", 0).edit();
                editor.putBoolean("tclear", false);
                editor.putBoolean("gclear", false);
                editor.apply();
                DBManager.openDatabase(context);
                DBManager.registerListener(new SensorEventListener() {
                    public void onSensorChanged(SensorEvent event) {
                        DBManager.parallaxView(findViewById(R.id.tips),
                                -Float.parseFloat(Constants.df.format(event.values[0]*2)),
                                (Float.parseFloat(Constants.df.format(event.values[1]))*2 - 14));
                    }
                    public void onAccuracyChanged(Sensor s, int accuracy) { }
                });
                AudioUtil.init(context);
                AudioUtil.setMusicRunning(getSharedPreferences("data", 0).getBoolean("music", true));
                AudioUtil.setSoundRunning(getSharedPreferences("data", 0).getBoolean("sound", true));
                AudioUtil.PlayMusic();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                ctn = (ViewFlipper) findViewById(R.id.splashctn);
                ctn.setVisibility(View.VISIBLE);
                ctn.startAnimation(ctn.getInAnimation());
                rl = (RelativeLayout) findViewById(R.id.splashrl);
                rl.setOnClickListener((OnClickListener) context);
                super.onPostExecute(aVoid);
            }
        }.execute();
        /*sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener lsn = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                final float alpha = 0.8f;
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];
                tips.setText("v0: " + event.values[0] + "\nv1: " + event.values[1] + "\nv2: " + event.values[2]
                        + "\ng0: " + gravity[0] + "\ng1: " + gravity[1] + "\ng2: " + gravity[2]
                        + "\na0: " + linear_acceleration[0] + "\na1: " + linear_acceleration[1] + "\na2: " + linear_acceleration[2]);
            }
            public void onAccuracyChanged(Sensor s, int accuracy) { }
        };
        sensorMgr.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);/**/
    }

        /*HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        MyHandler myHandler = new MyHandler(handlerThread.getLooper());
        Message msg = myHandler.obtainMessage();
        msg.sendToTarget();
        handlerThread.quit();
        ctn = (ViewFlipper) findViewById(R.id.splashctn);
        ctn.setVisibility(View.VISIBLE);
        ctn.startAnimation(ctn.getInAnimation());
        rl = (RelativeLayout) findViewById(R.id.splashrl);
        rl.setOnClickListener((OnClickListener) context);
    }

    class MyHandler extends Handler {
        public MyHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            SharedPreferences.Editor editor = getSharedPreferences("data", 0).edit();
            editor.putBoolean("tclear", false);
            editor.putBoolean("gclear", false);
            editor.apply();
            DBManager.openDatabase(context);
            AudioUtil.init(context);
            AudioUtil.setMusicRunning(getSharedPreferences("data", 0).getBoolean("music", true));
            AudioUtil.setSoundRunning(getSharedPreferences("data", 0).getBoolean("sound", false));
            AudioUtil.PlayMusic();
        }
    }*/

    public void onClick(View v) {
        startActivity(new Intent(SplashScreenActivity.this, MainMenuActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SplashScreenActivity.this, MainMenuActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    protected void onStop() {
        super.onStop();
        AudioUtil.PauseMusic();
    }

    protected void onResume() {
        super.onResume();
        AudioUtil.PlayMusic();
    }

}

package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

public class InfoActivity extends Activity implements OnClickListener {

    private static Button feedback, back;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_about);
        feedback = (Button) findViewById(R.id.feedback);
        back = (Button) findViewById(R.id.abback);
        feedback.setOnClickListener(this);
        back.setOnClickListener(this);
        DBManager.registerListener(new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                DBManager.parallaxView(findViewById(R.id.credit),
                        -Float.parseFloat(Constants.df.format(event.values[0]*2)),
                        (Float.parseFloat(Constants.df.format(event.values[1]))*2 - 14));
            }
            public void onAccuracyChanged(Sensor s, int accuracy) { }
        });
    }

    public void onClick(View v) {
        AudioUtil.PlaySound(R.raw.button);
        if (v.getId() == R.id.feedback) {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:shu.dma.findme@gmail.com"));
            data.putExtra(Intent.EXTRA_SUBJECT, "FindMeFeedBack");
            startActivity(data);
        } else {
            startActivity(new Intent(InfoActivity.this, MainMenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    public void onBackPressed() {
        back.performClick();
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

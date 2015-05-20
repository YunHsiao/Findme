package shu.dma.findme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity implements OnClickListener {

    private static Button start, settings, info, exit;
    private static TextView achieve;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_main);
        start = (Button) findViewById(R.id.startgame);
        settings = (Button) findViewById(R.id.settings);
        info = (Button) findViewById(R.id.info);
        exit = (Button) findViewById(R.id.exitgame);
        achieve = (TextView) findViewById(R.id.mainachieve);
        findViewById(R.id.mainrl).getBackground().clearColorFilter();
        exit.setOnClickListener(this);
        info.setOnClickListener(this);
        settings.setOnClickListener(this);
        start.setOnClickListener(this);
        achieve.setOnClickListener(this);
        AudioUtil.setContext(this);
    }

    public void onClick(View v) {
        AudioUtil.PlaySound(R.raw.button);
        if (v.getId() == R.id.exitgame) {
            DBManager.closeDatabase();
            System.exit(0);
        } else if (v.getId() == R.id.startgame) {
            startActivity(new Intent(MainMenuActivity.this, ModeMenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (v.getId() == R.id.info) {
            Toast.makeText(MainMenuActivity.this,
                    getString(R.string.eac_about), Toast.LENGTH_LONG).show();
            if (!this.getSharedPreferences("data", 0).getBoolean("ac_about", false)) {
                SharedPreferences.Editor editor = this.getSharedPreferences("data", 0).edit();
                editor.putBoolean("ac_about", true);
                editor.apply();
            }
            startActivity(new Intent(MainMenuActivity.this, InfoActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (v.getId() == R.id.mainachieve) {
            startActivity(new Intent(MainMenuActivity.this, AchievementsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        finish();
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.cexit))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(getString(R.string.econfirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DBManager.closeDatabase();
                        System.exit(0);
                    }
                })
                .setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { }
                }).show();
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

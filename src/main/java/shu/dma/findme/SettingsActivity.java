package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener {

    private static Button music, sound, apply;
    private static EditText freetime;
    private static Editor editor;
    private static RelativeLayout bg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_settings);
        music = (Button) findViewById(R.id.musicswitch);
        sound = (Button) findViewById(R.id.soundswitch);
        apply = (Button) findViewById(R.id.apply);
        freetime = (EditText) findViewById(R.id.freetime);
        bg = (RelativeLayout) findViewById(R.id.settingsrl);
        music.setOnClickListener(this);
        sound.setOnClickListener(this);
        apply.setOnClickListener(this);
        if (!AudioUtil.getMusicRunning()) {
            music.setBackgroundResource(R.drawable.btn_music_off);
        } else {
            music.setBackgroundResource(R.drawable.btn_music_on);
        }
        if (!AudioUtil.getSoundRunning()) {
            sound.setBackgroundResource(R.drawable.btn_sound_off);
        } else {
            sound.setBackgroundResource(R.drawable.btn_sound_on);
        }
        bg.getBackground().setColorFilter(Color.GRAY, Mode.MULTIPLY);
        editor = getSharedPreferences("data", 0).edit();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.soundswitch) {
            AudioUtil.soundSwitch();
            boolean isplaying = AudioUtil.getSoundRunning();
            if (isplaying) sound.setBackgroundResource(R.drawable.btn_sound_on);
            else sound.setBackgroundResource(R.drawable.btn_sound_off);
            editor.putBoolean("sound", isplaying);
            AudioUtil.PlaySound(R.raw.button);
        } else if (v.getId() == R.id.musicswitch) {
            AudioUtil.PlaySound(R.raw.button);
            AudioUtil.musicSwitch();
            boolean isplaying = AudioUtil.getMusicRunning();
            if (isplaying) music.setBackgroundResource(R.drawable.btn_music_on);
            else music.setBackgroundResource(R.drawable.btn_music_off);
            editor.putBoolean("music", isplaying);
        } else {
            AudioUtil.PlaySound(R.raw.button);
            String ft = freetime.getText().toString();
            if (!ft.equals("")) {
                //editor.putInt("FreeTime", Integer.parseInt(ft));
                if (getSharedPreferences("data", 0).getBoolean("showResizeInfo", false)
                        && Integer.parseInt(ft) > 0 && Integer.parseInt(ft) <= Constants.TIPS_NUMBER) {
                    editor.putInt(Constants.LAST_TIPS, R.string.tip01 + Integer.parseInt(ft) - 1);
                }
                if (Integer.parseInt(ft) == Constants.TIPS_NUMBER) {
                    /*int last = getSharedPreferences("data", 0).getInt(Constants.LAST_TIPS, R.string.tip01);
                    Toast.makeText(this, getString(last - 1), Toast.LENGTH_LONG).show();/**/
                    Toast.makeText(this, getString(R.string.cheat), Toast.LENGTH_LONG).show();
                }
            }
            editor.commit();
            startActivity(new Intent(SettingsActivity.this, MainMenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    public void onBackPressed() {
        apply.performClick();
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

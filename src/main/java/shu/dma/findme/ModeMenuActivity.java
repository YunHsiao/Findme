package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class ModeMenuActivity extends Activity implements OnClickListener {

    private static Button text, graphic, teasy, thard, tfree, geasy, ghard, gfree, back;
    private StringBuffer code = new StringBuffer();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_mode);
        text = (Button) findViewById(R.id.textMode);
        graphic = (Button) findViewById(R.id.graphicMode);
        teasy = (Button) findViewById(R.id.teasyMode);
        thard = (Button) findViewById(R.id.thardMode);
        tfree = (Button) findViewById(R.id.tfreeMode);
        geasy = (Button) findViewById(R.id.geasyMode);
        ghard = (Button) findViewById(R.id.ghardMode);
        gfree = (Button) findViewById(R.id.gfreeMode);
        back = (Button) findViewById(R.id.ModeBack);
        back.setOnClickListener(this);
        geasy.setOnClickListener(this);
        ghard.setOnClickListener(this);
        gfree.setOnClickListener(this);
        teasy.setOnClickListener(this);
        thard.setOnClickListener(this);
        tfree.setOnClickListener(this);
        graphic.setOnClickListener(this);
        text.setOnClickListener(this);
        setTextVisible();
    }

    public void onClick(View v) {
        AudioUtil.PlaySound(R.raw.button);
        if (v.getId() == R.id.textMode) {
            code.append('t');
            if (code.toString().equals("gtgt")) {
                if (!this.getSharedPreferences("data", 0).getBoolean("ac_music", false)) {
                    SharedPreferences.Editor editor = this.getSharedPreferences("data", 0).edit();
                    editor.putBoolean("ac_music", true);
                    editor.apply();
                }
                Toast.makeText(ModeMenuActivity.this,
                        getString(R.string.eac_music), Toast.LENGTH_LONG).show();
                AudioUtil.EggFound();
            } else if (code.toString().equals("gtgtggt")) {
                DBManager.Demo();
                SharedPreferences.Editor editor = getSharedPreferences("data", 0).edit();
                editor.putBoolean("clear", true);
                editor.apply();
                Toast.makeText(ModeMenuActivity.this,
                        getString(R.string.eac_developer), Toast.LENGTH_LONG).show();
            } else if (code.toString().equals("gtgtgtgtgt")) {
                boolean showResizeInfo = getSharedPreferences("data", 0).getBoolean("showResizeInfo", false);
                SharedPreferences.Editor editor = getSharedPreferences("data", 0).edit();
                showResizeInfo = !showResizeInfo;
                editor.putBoolean("showResizeInfo", showResizeInfo);
                editor.apply();
                Toast.makeText(ModeMenuActivity.this, getString(R.string.dev_resizeInfo)
                        + " " + showResizeInfo, Toast.LENGTH_LONG).show();
            }
            setTextVisible();
        } else if (v.getId() == R.id.graphicMode) {
            setGraphicVisible();
            code.append('g');
            if (!this.getSharedPreferences("data", 0).getBoolean("ac_graphic", false)) {
                SharedPreferences.Editor editor = this.getSharedPreferences("data", 0).edit();
                editor.putBoolean("ac_graphic", true);
                editor.apply();
                Toast.makeText(ModeMenuActivity.this,
                        getString(R.string.eac_graphic), Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.ModeBack) {
            startActivity(new Intent(ModeMenuActivity.this, MainMenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if (v.getId() == R.id.teasyMode || v.getId() == R.id.thardMode || v.getId() == R.id.tfreeMode) {
            Intent intent = new Intent(ModeMenuActivity.this, TextLevelSelectActivity.class);
            Bundle data = new Bundle();
            if (v.getId() == R.id.teasyMode) data.putInt("time", 12000);
            else if (v.getId() == R.id.thardMode) data.putInt("time", 6000);
            else {
                int free = getSharedPreferences("data", 0).getInt("FreeTime", 18);
                data.putInt("time", free * 1000);
                data.putBoolean("free", true);
                data.putBoolean("isText", true);
                intent = new Intent(ModeMenuActivity.this, FreeModeInitActivity.class);
            }
            intent.putExtras(data);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent intent = new Intent(ModeMenuActivity.this, GraphicLevelSelectActivity.class);
            Bundle data = new Bundle();
            if (v.getId() == R.id.geasyMode) data.putInt("time", 12000);
            else if (v.getId() == R.id.ghardMode) data.putInt("time", 9000);
            else {
                int free = getSharedPreferences("data", 0).getInt("FreeTime", 18);
                data.putInt("time", free * 1000);
                data.putBoolean("free", true);
                data.putBoolean("isText", false);
                intent = new Intent(ModeMenuActivity.this, FreeModeInitActivity.class);
            }
            intent.putExtras(data);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    public void onBackPressed() {
        back.performClick();
    }

    public static void setTextVisible() {
        geasy.setVisibility(View.GONE);
        ghard.setVisibility(View.GONE);
        gfree.setVisibility(View.GONE);
        teasy.setVisibility(View.VISIBLE);
        thard.setVisibility(View.VISIBLE);
        tfree.setVisibility(View.VISIBLE);
    }

    public static void setGraphicVisible() {
        teasy.setVisibility(View.GONE);
        thard.setVisibility(View.GONE);
        tfree.setVisibility(View.GONE);
        geasy.setVisibility(View.VISIBLE);
        ghard.setVisibility(View.VISIBLE);
        gfree.setVisibility(View.VISIBLE);
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

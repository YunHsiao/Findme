package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class GraphicLevelSelectActivity extends Activity implements OnClickListener {

    private static int currentlv, beginning;
    private static Button back, previous, next, level[] = new Button[12];
    private StringBuffer code = new StringBuffer();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_level);
        back = (Button) findViewById(R.id.levelback);
        previous = (Button) findViewById(R.id.levelprevious);
        next = (Button) findViewById(R.id.levelnext);
        currentlv = (Integer)DBManager.GContinue()[2];
        if (getSharedPreferences("data", 0).getBoolean("clear", false) ||
                getSharedPreferences("data", 0).getBoolean("gclear", false))
            currentlv = Constants.GRAPHIC_LEVEL;
        if (currentlv == Constants.GRAPHIC_LEVEL) beginning = 1;
        else beginning = 1 + (currentlv % 12 == 0 ? currentlv / 12 - 1 : currentlv / 12) * 12;
        int lid = R.id.lv01;
        for (int i = 0; i < 12; i++) {
            level[i] = (Button) findViewById(lid);
            level[i].setOnClickListener(this);
            lid++;
        }
        writeLv();
        back.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    private void writeLv() {
        int j = beginning, stars[] = DBManager.Gstar(beginning);
        for (int i = 0; i < 12; i++) {
            if (j <= Constants.GRAPHIC_LEVEL) {
                level[i].setVisibility(View.VISIBLE);
                level[i].setText(String.valueOf(j));
                if (j > currentlv) {
                    level[i].setEnabled(false);
                    level[i].setBackgroundResource(R.drawable.btn_level_0);
                } else {
                    if (j == currentlv)
                        if (!getIntent().getBooleanExtra("free", false))
                            level[i].setEnabled(true);
                        else
                            level[i].setEnabled(false);
                    else
                        level[i].setEnabled(true);
                    if (stars[i] == 0) level[i].setBackgroundResource(R.drawable.btn_level_0);
                    else if (stars[i] == 1) level[i].setBackgroundResource(R.drawable.btn_level_1);
                    else if (stars[i] == 2) level[i].setBackgroundResource(R.drawable.btn_level_2);
                    else if (stars[i] == 3) level[i].setBackgroundResource(R.drawable.btn_level_3);
                }
            } else {
                level[i].setVisibility(View.GONE);
            }
            j++;
        }
        int lastBeginning = 1 + (Constants.GRAPHIC_LEVEL % 12 == 0 ?
                Constants.GRAPHIC_LEVEL / 12 - 1 : Constants.GRAPHIC_LEVEL / 12) * 12;
        if (beginning >= lastBeginning) next.setEnabled(false);
        if (beginning < lastBeginning) next.setEnabled(true);
        if (beginning > 1) previous.setEnabled(true);
        if (beginning <= 1) previous.setEnabled(false);
    }

    public void onClick(View v) {
        AudioUtil.PlaySound(R.raw.button);
        if (v.getId() == R.id.levelback) {
            startActivity(new Intent(GraphicLevelSelectActivity.this, ModeMenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if (v.getId() == R.id.levelnext) {
            code.append('n');
            if (code.toString().equals("pnpn")) {
                Editor editor = getSharedPreferences("data", 0).edit();
                editor.putBoolean("gclear", true);
                editor.apply();
                Toast.makeText(GraphicLevelSelectActivity.this,
                        getString(R.string.eac_unlock), Toast.LENGTH_LONG).show();
                if (!this.getSharedPreferences("data", 0).getBoolean("ac_unlock", false)) {
                    editor.putBoolean("ac_unlock", true);
                    editor.apply();
                }
            }
            beginning += 12;
            writeLv();
        } else if (v.getId() == R.id.levelprevious) {
            code.append('p');
            if (code.toString().equals("npnp")) {
                Editor editor = getSharedPreferences("data", 0).edit();
                editor.putBoolean("gclear", true);
                editor.apply();
                Toast.makeText(GraphicLevelSelectActivity.this,
                        getString(R.string.eac_unlock), Toast.LENGTH_LONG).show();
                if (!this.getSharedPreferences("data", 0).getBoolean("ac_unlock", false)) {
                    editor.putBoolean("ac_unlock", true);
                    editor.apply();
                }
            }
            beginning -= 12;
            writeLv();
        } else {
            AudioUtil.StopGame();
            int lv = Integer.parseInt(((Button) v).getText().toString());
            DBManager.GjumpTo(lv);
            //Log.e(((Button) v).getText().toString(), String.valueOf(v.getId()));
            Intent intent = new Intent(GraphicLevelSelectActivity.this, GraphicActivity.class);
            Bundle data = getIntent().getExtras();
            startActivity(intent.putExtras(data));
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

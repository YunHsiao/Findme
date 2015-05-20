package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inmobi.monetization.IMBanner;

public class GraphicResultActivity extends Activity {

    private static int score = 0, star;
    private static TextView title, info;
    private static TextView sc;
    private static Button again;
    private static Button exit;
    private static RelativeLayout bg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_result);
        title = (TextView) findViewById(R.id.results);
        sc = (TextView) findViewById(R.id.score);
        info = (TextView) findViewById(R.id.scinfo);
        again = (Button) findViewById(R.id.jp_next);
        exit = (Button) findViewById(R.id.jp_exit);
        bg = (RelativeLayout) findViewById(R.id.resultrl);
        ((IMBanner) findViewById(R.id.banner)).loadBanner();
        String result = getIntent().getExtras().getString("result");
        title.setText(result);
        Editor editor = getSharedPreferences("data", 0).edit();
        score = getSharedPreferences("data", 0).getInt("GraphicScore", 0);
        star = getIntent().getExtras().getInt("star");
        //best = getSharedPreferences("data", 0).getInt("Level", 1);
        //gid = getIntent().getExtras().getInt("GID", R.drawable.image_01_00);
        if (star == 0) {
            //AudioUtil.PlaySound(R.raw.lose);
            bg.setBackgroundResource(R.drawable.bg_result_0);
            info.setVisibility(View.GONE);
            sc.setVisibility(View.GONE);
        } else {
            //AudioUtil.PlaySound(R.raw.win);
            info.setVisibility(View.VISIBLE);
            sc.setVisibility(View.VISIBLE);
            if (star == 1) bg.setBackgroundResource(R.drawable.bg_result_1);
            else if (star == 2) bg.setBackgroundResource(R.drawable.bg_result_2);
            else if (star == 3) bg.setBackgroundResource(R.drawable.bg_result_3);
        }
        if (getIntent().getExtras().getBoolean("free", false)) {
            info.setVisibility(View.GONE);
            sc.setVisibility(View.GONE);
        }
        if (getIntent().getExtras().getBoolean("next")) {
            if (!getIntent().getExtras().getBoolean("free", false)) {
                score += Integer.parseInt(getIntent().getExtras().getString("score"));
                editor.putInt("GraphicScore", score);
                editor.apply();
            }
            again.setBackgroundResource(R.drawable.btn_result_next);
        } else {
            again.setBackgroundResource(R.drawable.btn_result_again);
        }
        sc.setText(" " + score);
        again.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AudioUtil.PlaySound(R.raw.button);
                if (getIntent().getExtras().getBoolean("free", false)) {
                    if (getIntent().getExtras().getBoolean("Customized", false)) {
                        Intent intent = new Intent(GraphicResultActivity.this, FreeModeInitActivity.class);
                        Bundle data = getIntent().getExtras();
                        data.putBoolean("isText", false);
                        data.putBoolean("Customized", true);
                        startActivity(intent.putExtras(data));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else {
                        Intent intent = new Intent(GraphicResultActivity.this, GraphicActivity.class);
                        startActivity(intent.putExtras(getIntent().getExtras()));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(GraphicResultActivity.this, GraphicActivity.class);
                    Bundle data = getIntent().getExtras();
                    startActivity(intent.putExtras(data));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AudioUtil.PlaySound(R.raw.button);
                startActivity(new Intent(GraphicResultActivity.this, MainMenuActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    public void onBackPressed() {
        exit.performClick();
    }

    protected void onStop() {
        super.onStop();
        AudioUtil.PauseGame();
    }

    protected void onResume() {
        super.onResume();
        AudioUtil.PlayGame();
    }

}
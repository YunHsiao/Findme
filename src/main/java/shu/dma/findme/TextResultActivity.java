package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inmobi.monetization.IMBanner;
import com.inmobi.monetization.IMBannerListener;
import com.inmobi.monetization.IMErrorCode;

import java.util.Map;

public class TextResultActivity extends Activity {

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
        info = (TextView) findViewById(R.id.scinfo);
        sc = (TextView) findViewById(R.id.score);
        again = (Button) findViewById(R.id.jp_next);
        exit = (Button) findViewById(R.id.jp_exit);
        bg = (RelativeLayout) findViewById(R.id.resultrl);
        IMBanner banner = (IMBanner) findViewById(R.id.banner);
        banner.loadBanner();
        String result = getIntent().getExtras().getString("result");
        title.setText(result);
        Editor editor = getSharedPreferences("data", 0).edit();
        score = getSharedPreferences("data", 0).getInt("TextScore", 0);
        star = getIntent().getExtras().getInt("star");
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
                editor.putInt("TextScore", score);
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
                        Intent intent = new Intent(TextResultActivity.this, FreeModeInitActivity.class);
                        Bundle data = getIntent().getExtras();
                        data.putBoolean("isText", true);
                        data.putBoolean("Customized", true);
                        startActivity(intent.putExtras(data));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else {
                        Intent intent = new Intent(TextResultActivity.this, TextActivity.class);
                        startActivity(intent.putExtras(getIntent().getExtras()));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(TextResultActivity.this, TextActivity.class);
                    startActivity(intent.putExtras(getIntent().getExtras()));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AudioUtil.PlaySound(R.raw.button);
                startActivity(new Intent(TextResultActivity.this, MainMenuActivity.class));
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
package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TextActivity extends Activity implements OnClickListener {

    private static String t, f;
    private static Timer ctd;
    private static int target = -1, last = -1, timepause, clicked = 0;
    private static TextView[] textview = new TextView[Constants.TEXT_MATRIX];
    private static TextView timer, left;
    private static Button pause, resume, exit;
    private static RelativeLayout bg, rl, matrix, bgpause;
    private static int star, time = 0;
    private StringBuffer code = new StringBuffer();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_text);
        timer = (TextView) findViewById(R.id.timer);
        left = (TextView) findViewById(R.id.left);
        pause = (Button) findViewById(R.id.tpause);
        resume = (Button) findViewById(R.id.tresume);
        exit = (Button) findViewById(R.id.texit);
        bg = (RelativeLayout) findViewById(R.id.textrl);
        rl = (RelativeLayout) findViewById(R.id.trl);
        bgpause = (RelativeLayout) findViewById(R.id.bgpause);
        matrix = (RelativeLayout) findViewById(R.id.tmatrix);
        pause.setOnClickListener(this);
        resume.setOnClickListener(this);
        exit.setOnClickListener(this);
        initGrid();
        DBManager.registerListener(new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                DBManager.parallaxView(matrix, -Float.parseFloat(Constants.df.format(event.values[0]*5)),
                        (Float.parseFloat(Constants.df.format(event.values[1]))*5 - 45));
            }
            public void onAccuracyChanged(Sensor s, int accuracy) { }
        });
        int timeChanger = getIntent().getExtras() == null ? 0 : getIntent().getExtras().getInt("time");
        if (timeChanger != 0) time = timeChanger;
        ctd = new Timer(time, 100, this, TextResultActivity.class, timer, null, true, textview[target].getId());
        ctd.start();
        clicked = 0;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.texit) {
            AudioUtil.PlaySound(R.raw.button);
            ctd.Cancel();
            startActivity(new Intent(TextActivity.this, MainMenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if (v.getId() == R.id.tpause) {
            if (code.toString().equals("1243") || code.toString().equals("2431") ||
                    code.toString().equals("4312") || code.toString().equals("3124")) {
                if (!this.getSharedPreferences("data", 0).getBoolean("ac_pass", false)) {
                    SharedPreferences.Editor editor = this.getSharedPreferences("data", 0).edit();
                    editor.putBoolean("ac_pass", true);
                    editor.apply();
                }
                Toast.makeText(TextActivity.this,
                        getString(R.string.eac_pass), Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = this.getSharedPreferences("data", 0).edit();
                editor.putInt("st_pass", this.getSharedPreferences("data", 0).getInt("st_pass", 0) + 1);
                editor.apply();
                textview[target].performClick();
                return;
            } else if (code.toString().equals("3421") || code.toString().equals("4213") ||
                    code.toString().equals("2134") || code.toString().equals("1342")) {
                if (!this.getSharedPreferences("data", 0).getBoolean("ac_lost", false)) {
                    SharedPreferences.Editor editor = this.getSharedPreferences("data", 0).edit();
                    editor.putBoolean("ac_lost", true);
                    editor.apply();
                }
                Toast.makeText(TextActivity.this,
                        getString(R.string.eac_lost), Toast.LENGTH_LONG).show();
                ctd.setQuickFail();
                ctd.cancel();
                ctd.onFinish();
                return;
            }
            AudioUtil.PlaySound(R.raw.button);
            ctd.Cancel();
            pause.setVisibility(View.GONE);
            bgpause.setVisibility(View.VISIBLE);
            timepause = Integer.parseInt(timer.getText().toString());
            timer.setVisibility(View.GONE);
            left.setVisibility(View.GONE);
            rl.setVisibility(View.VISIBLE);
            matrix.setVisibility(View.GONE);
        } else if (v.getId() == R.id.tresume) {
            AudioUtil.PlaySound(R.raw.button);
            ctd = new Timer(timepause * 1000, 100, this, TextResultActivity.class, timer, null, true, textview[target].getId());
            ctd.start();
            bgpause.setVisibility(View.VISIBLE);
            timer.setVisibility(View.VISIBLE);
            rl.setVisibility(View.GONE);
            bgpause.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
            matrix.setVisibility(View.VISIBLE);
        } else {
            clicked += 1;
            String content = ((TextView) v).getText().toString();
            if (v.getId() == R.id.TextView01) code.append('1');
            else if (v.getId() == R.id.TextView01 + Constants.TEXT_MATRIX_WIDTH - 1)
                code.append('2');
            else if (v.getId() == R.id.TextView01 + Constants.TEXT_MATRIX - Constants.TEXT_MATRIX_WIDTH)
                code.append('3');
            else if (v.getId() == R.id.TextView01 + Constants.TEXT_MATRIX - 1) code.append('4');
            if (content.equals(t)) {
                ctd.Cancel();
                AudioUtil.PlaySound(R.raw.t);
                if (last != -1 && findViewById(last).isEnabled())
                    ((TextView) findViewById(last)).setTextColor(Color.WHITE);
                ((TextView) v).setTextColor(Color.GREEN);
                last = v.getId();
                if (getIntent().getExtras() != null
                        && getIntent().getExtras().getBoolean("free", false)) {
                    Intent intent = new Intent(TextActivity.this, TextResultActivity.class);
                    Bundle data = getIntent().getExtras();
                    star = ctd.getStar();
                    data.putString("result", "You win!\nStars: " + star);
                    data.putInt("star", star);
                    data.putString("score", timer.getText().toString());
                    data.putBoolean("next", true);
                    intent.putExtras(data);
                    if (!getIntent().getExtras().getBoolean("Customized", false))
                    DBManager.Tnext(star, ctd.getTimeLeft(), clicked, getIntent().getExtras() != null
                            && getIntent().getExtras().getBoolean("free", false));
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    Intent intent = new Intent(TextActivity.this, TextResultActivity.class);
                    Bundle data = new Bundle();
                    star = ctd.getStar();
                    data.putString("result", "You win!\nStars: " + star);
                    data.putInt("star", star);
                    data.putString("score", timer.getText().toString());
                    data.putBoolean("next", true);
                    intent.putExtras(data);
                    DBManager.Tnext(star, ctd.getTimeLeft(), clicked, getIntent().getExtras() != null
                            && getIntent().getExtras().getBoolean("free", false));
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            } else {
                AudioUtil.PlaySound(R.raw.f);
                if (last != -1 && findViewById(last).isEnabled())
                    ((TextView) findViewById(last)).setTextColor(Color.WHITE);
                ((TextView) v).setTextColor(Color.RED);
                last = v.getId();
            }
        }
    }

    public void onBackPressed() {
        if (pause.getVisibility() == View.VISIBLE) pause.performClick();
        else exit.performClick();
    }

    public TextView getTarget() {
        return textview[target];
    }

    public void clearListener() {
        for (int i = 0; i < Constants.TEXT_MATRIX; i++) {
            textview[i].setOnClickListener(null);
        }
    }

    protected void onPause() {
        super.onPause();
        if (!ctd.isFinished()) {
            pause.performClick();
        }
    }

    protected void onStop() {
        super.onStop();
        AudioUtil.PauseGame();
    }

    protected void onResume() {
        super.onResume();
        AudioUtil.PlayGame();
    }

    public void hint(int hint, int lines) {
        for (int i = 0; i < Constants.TEXT_MATRIX; i++) {
            if (hint < 0) hint = 0;
            if (i >= hint && i < hint + Constants.TEXT_MATRIX_WIDTH * lines) continue;
            textview[i].setTextColor(Color.DKGRAY);
            textview[i].setEnabled(false);
        }
    }

    public int randomPosition(int max) {
        int target = -1;
        while (true) {
            if (target >= 0 && target < max) {
                return target;
            } else {
                target = (int) (Math.random() * (max + 2));

            }
        }
    }

    public void initGrid() {
        if (getIntent().getExtras().getBoolean("Customized", false)) {
            t = String.valueOf(getIntent().getExtras().getChar("true",
                    getString(R.string.findcontext).charAt(0)));
            f = String.valueOf(getIntent().getExtras().getChar("false",
                    getString(R.string.context).charAt(0)));
        } else {
            String[] temp = DBManager.current();
            t = temp[0];
            f = temp[1];
        }
        ((TextView) findViewById(R.id.left)).setText(//"Lv " + temp[2] + ":\t" + f + " -> " +
                t + "  ");
        int id = R.id.TextView01 - 1, iCount = 0;
        if (getIntent().getExtras() != null
                && getIntent().getExtras().getBoolean("free", false)) {
            int w = getIntent().getExtras().getInt("width", Constants.TEXT_MATRIX_WIDTH),
                    h = getIntent().getExtras().getInt("height", Constants.TEXT_MATRIX_HEIGHT);
            int[] visibles = new int[w*h];
            for (int i = 0; i < Constants.TEXT_MATRIX; i++) {
                id++;
                textview[i] = (TextView) findViewById(id);
                if (i % Constants.TEXT_MATRIX_WIDTH > w - 1 || i / Constants.TEXT_MATRIX_WIDTH > h - 1) {
                    textview[i].setVisibility(View.GONE);
                    continue;
                }
                textview[i].setOnClickListener(this);
                textview[i].setText(f);
                textview[i].setTextColor(Color.WHITE);
                textview[i].setEnabled(true);
                textview[i].setVisibility(View.VISIBLE);
                visibles[iCount] = i;
                iCount++;
            }
            if (target != -1) {
                textview[target].setText(f);
                target = -1;
            }
            target = randomPosition(w*h);
            textview[visibles[target]].setText(t);
        } else {
            for (int i = 0; i < Constants.TEXT_MATRIX; i++) {
                id++;
                textview[i] = (TextView) findViewById(id);
                textview[i].setOnClickListener(this);
                textview[i].setText(f);
                textview[i].setTextColor(Color.WHITE);
                textview[i].setEnabled(true);
                textview[i].setVisibility(View.VISIBLE);
            }
            if (target != -1) {
                textview[target].setText(f);
                target = -1;
            }
            target = randomPosition(Constants.TEXT_MATRIX);
            textview[target].setText(t);
        }
    }

}

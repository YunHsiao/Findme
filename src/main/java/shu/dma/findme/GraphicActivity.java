package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GraphicActivity extends Activity implements OnClickListener {

    private static Bitmap t, f;
    private static Timer ctd;
    private static int target = -1, timepause, clicked = 0;
    private static boolean isFree;
    private static ImageView last, gt, gf, imageview[] = new ImageView[Constants.GRAPHIC_MATRIX];
    private static TextView timer;
    private static Button pause, resume, exit;
    private static RelativeLayout bg, rl, matrix, bgpause;
    private static int level, star, time = 0;
    private static int[] visibles;
    private static StringBuffer code = new StringBuffer();
    private float[] values = new float[2], q = new float[2];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graphic);
        pause = (Button) findViewById(R.id.gpause);
        resume = (Button) findViewById(R.id.gresume);
        exit = (Button) findViewById(R.id.gexit);
        timer = (TextView) findViewById(R.id.gtimer);
        bg = (RelativeLayout) findViewById(R.id.graphicrl);
        rl = (RelativeLayout) findViewById(R.id.grl);
        matrix = (RelativeLayout) findViewById(R.id.gmatrix);
        bgpause = (RelativeLayout) findViewById(R.id.gbgpause);
        gt = (ImageView) findViewById(R.id.gt);
        gf = (ImageView) findViewById(R.id.gf);
        pause.setOnClickListener(this);
        resume.setOnClickListener(this);
        exit.setOnClickListener(this);
        isFree = getIntent().getExtras() != null
                && getIntent().getExtras().getBoolean("free", false);
        initGrid();
        DBManager.registerListener(new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                DBManager.parallaxView(matrix, -Float.parseFloat(Constants.df.format(event.values[0]*5)),
                        (Float.parseFloat(Constants.df.format(event.values[1]))*5 - 45));
            }
            public void onAccuracyChanged(Sensor s, int accuracy) { }
        });
        int timeChanger = getIntent().getExtras() == null ?
                0 : getIntent().getExtras().getInt("time");
        if (timeChanger != 0) time = timeChanger;
        if (isFree) ctd = new Timer(time, 100, this, GraphicResultActivity.class, timer,
                getIntent().getExtras(), false, imageview[visibles[target]].getId());
        else ctd = new Timer(time, 100, this, GraphicResultActivity.class, timer,
                getIntent().getExtras(), false, imageview[target].getId());
        ctd.start();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.gexit) {
            AudioUtil.PlaySound(R.raw.button);
            ctd.Cancel();
            startActivity(new Intent(GraphicActivity.this, MainMenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if (v.getId() == R.id.gpause) {
            if (code.toString().equals("1243") || code.toString().equals("2431") ||
                    code.toString().equals("4312") || code.toString().equals("3124")) {
                if (!this.getSharedPreferences("data", 0).getBoolean("ac_pass", false)) {
                    SharedPreferences.Editor editor = this.getSharedPreferences("data", 0).edit();
                    editor.putBoolean("ac_pass", true);
                    editor.apply();
                }
                Toast.makeText(GraphicActivity.this,
                        getString(R.string.eac_pass), Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = this.getSharedPreferences("data", 0).edit();
                editor.putInt("st_gpass", this.getSharedPreferences("data", 0).getInt("st_gpass", 0) + 1);
                editor.apply();
                imageview[target].performClick();
                return;
            } else if (code.toString().equals("3421") || code.toString().equals("4213") ||
                    code.toString().equals("2134") || code.toString().equals("1342")) {
                if (!this.getSharedPreferences("data", 0).getBoolean("ac_lost", false)) {
                    SharedPreferences.Editor editor = this.getSharedPreferences("data", 0).edit();
                    editor.putBoolean("ac_lost", true);
                    editor.apply();
                }
                Toast.makeText(GraphicActivity.this,
                        getString(R.string.eac_lost), Toast.LENGTH_LONG).show();
                ctd.setQuickFail();
                ctd.cancel();
                ctd.onFinish();
                return;
            }
            AudioUtil.PlaySound(R.raw.button);
            ctd.Cancel();
            pause.setVisibility(View.GONE);
            timepause = Integer.parseInt(timer.getText().toString());
            timer.setVisibility(View.GONE);
            gt.setVisibility(View.GONE);
            gf.setVisibility(View.GONE);
            bgpause.setVisibility(View.VISIBLE);
            rl.setVisibility(View.VISIBLE);
            matrix.setVisibility(View.GONE);
        } else if (v.getId() == R.id.gresume) {
            AudioUtil.PlaySound(R.raw.button);
            ctd = new Timer(timepause * 1000, 100, this,
                    GraphicResultActivity.class, timer, getIntent().getExtras(), false, imageview[target].getId());
            ctd.start();
            rl.setVisibility(View.GONE);
            bgpause.setVisibility(View.GONE);
            timer.setVisibility(View.VISIBLE);
            pause.setVisibility(View.VISIBLE);
            //left.setVisibility(View.VISIBLE);
            gt.setVisibility(View.VISIBLE);
            //gf.setVisibility(View.VISIBLE);
            matrix.setVisibility(View.VISIBLE);
        } else {
            clicked += 1;
            if (v.getId() == R.id.g01) code.append('1');
            else if (v.getId() == R.id.g01 + Constants.GRAPHIC_MATRIX_WIDTH - 1) code.append('2');
            else if (v.getId() == R.id.g01 + Constants.GRAPHIC_MATRIX - Constants.GRAPHIC_MATRIX_WIDTH)
                code.append('3');
            else if (v.getId() == R.id.g01 + Constants.GRAPHIC_MATRIX - 1) code.append('4');
            if ((isFree && v.getId() == imageview[visibles[target]].getId()) ||
                    (!isFree && v.getId() == imageview[target].getId())) {
                ctd.Cancel();
                AudioUtil.PlaySound(R.raw.t);
                if (last != null && last.isEnabled()) last.clearColorFilter();
                last = (ImageView) v;
                last.setColorFilter(Color.GREEN, Mode.MULTIPLY);
                if (isFree) {
                    Intent intent = new Intent(GraphicActivity.this, GraphicResultActivity.class);
                    Bundle data = getIntent().getExtras();
                    star = ctd.getStar();
                    data.putString("result", "You win!\nStars: " + star);
                    data.putInt("star", star);
                    data.putString("score", timer.getText().toString());
                    data.putBoolean("next", true);
                    intent.putExtras(data);
                    if (!getIntent().getExtras().getBoolean("Customized", false))
                        DBManager.Gnext(star, ctd.getTimeLeft(), clicked, true);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    Intent intent = new Intent(GraphicActivity.this, GraphicResultActivity.class);
                    Bundle data = getIntent().getExtras();
                    star = ctd.getStar();
                    DBManager.Gnext(star, ctd.getTimeLeft(), clicked, false);
                    data.putString("result", "You win!\nStars: " + star);
                    data.putInt("star", star);
                    data.putString("score", timer.getText().toString());
                    data.putBoolean("next", true);
                    intent.putExtras(data);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            } else {
                AudioUtil.PlaySound(R.raw.f);
                if (last != null && last.isEnabled()) last.clearColorFilter();
                last = (ImageView) v;
                last.setColorFilter(Color.RED, Mode.MULTIPLY);
            }
        }
    }

    public ImageView getTarget() {
        return imageview[target];
    }

    public void clearListener() {
        for (int i = 0; i < Constants.GRAPHIC_MATRIX; i++) {
            imageview[i].setOnClickListener(null);
        }
    }

    public void onBackPressed() {
        if (pause.getVisibility() == View.VISIBLE) pause.performClick();
        else exit.performClick();
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
        for (int i = 0; i < Constants.GRAPHIC_MATRIX; i++) {
            if (hint < 0) hint = 0;
            if (i >= hint && i < hint + Constants.GRAPHIC_MATRIX_WIDTH * lines) continue;
            imageview[i].setColorFilter(Color.DKGRAY, Mode.MULTIPLY);
            imageview[i].setEnabled(false);
        }
    }

    public static int randomPosition(int max) {
        int target = -1;
        while (true) {
            if (target >= 0 && target < max) {
                return target;
            } else {
                target = (int) (Math.random() * (max + 2));

            }
        }
    }

    private void initGrid() {
        Object[] tf = DBManager.Gcurrent();
        level = (Integer)tf[2];
        //((TextView) findViewById(R.id.gleft)).setText("Level " + level + ": ");
        f = (Bitmap)tf[1];
        t = (Bitmap)tf[0];
        ((ImageView) findViewById(R.id.gt)).setImageBitmap(t);
        ((ImageView) findViewById(R.id.gf)).setImageBitmap(f);
        int id = R.id.g01 - 1, iCount = 0;
        if (isFree) {
            int w = getIntent().getExtras().getInt("width", Constants.GRAPHIC_MATRIX_WIDTH),
                    h = getIntent().getExtras().getInt("height", Constants.GRAPHIC_MATRIX_HEIGHT);
            visibles = new int[w*h];
            for (int i = 0; i < Constants.GRAPHIC_MATRIX; i++) {
                id++;
                imageview[i] = (ImageView) findViewById(id);
                if (i % Constants.GRAPHIC_MATRIX_WIDTH > w - 1 || i / Constants.GRAPHIC_MATRIX_WIDTH > h - 1) {
                    imageview[i].setVisibility(View.GONE);
                    continue;
                }
                imageview[i].setOnClickListener(this);
                imageview[i].setImageBitmap(f);
                imageview[i].clearColorFilter();
                imageview[i].setEnabled(true);
                imageview[i].setVisibility(View.VISIBLE);
                visibles[iCount] = i;
                iCount++;
            }
            /**Doesn't even need to be here. It's not a bug anyway.*/
            if (target != -1 && visibles.length > target) {
                imageview[visibles[target]].setImageBitmap(f);
                target = -1;
            }/**/
            target = randomPosition(w*h);
            imageview[visibles[target]].setImageBitmap(t);
        } else {
            for (int i = 0; i < Constants.GRAPHIC_MATRIX; i++) {
                id++;
                imageview[i] = (ImageView) findViewById(id);
                imageview[i].setOnClickListener(this);
                imageview[i].setImageBitmap(f);
                imageview[i].clearColorFilter();
                imageview[i].setEnabled(true);
                imageview[i].setVisibility(View.VISIBLE);
            }
            if (target != -1) {
                imageview[target].setImageBitmap(f);
                target = -1;
            }
            target = randomPosition(Constants.GRAPHIC_MATRIX);
            imageview[target].setImageBitmap(t);
        }
    }

}

package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class FreeModeInitActivity extends Activity implements OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static EditText fr_time, fr_width, fr_height, fr_first, fr_second;
    private static RelativeLayout frrl;
    private static LinearLayout frll;
    private static TextWatcher watcher = null;
    private static TextView fr[] = new TextView[9];
    private static CheckBox fr_content;
    private static Button start, back;
    private static boolean isText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_free_init);
        initVariables();
        DBManager.registerListener(new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                DBManager.parallaxView(findViewById(R.id.freell),
                        -Float.parseFloat(Constants.df.format(event.values[0]*3)),
                        (Float.parseFloat(Constants.df.format(event.values[1]))*3 - 21));
            }
            public void onAccuracyChanged(Sensor s, int accuracy) { }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            frrl.setVisibility(View.VISIBLE);
            frll.setVisibility(View.VISIBLE);
            refreshMatrix();
            if (watcher == null) {
                watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 1) {
                            s.delete(1, s.length());
                        } else if (s.length() > 0)
                            fr_second.requestFocus();
                        refreshMatrix();
                    }
                };
            }
            fr_first.addTextChangedListener(watcher);
            fr_second.addTextChangedListener(watcher);
        } else {
            frrl.setVisibility(View.GONE);
            frll.setVisibility(View.GONE);
            fr_first.setText("");
            fr_second.setText("");
        }
    }

    public void onClick(View v) {
        AudioUtil.PlaySound(R.raw.button);
        if (v.getId() == R.id.fr_back) {
            startActivity(new Intent(FreeModeInitActivity.this, ModeMenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if (v.getId() == R.id.fr_start) {
            if (fr_height.getText().toString().equals("")) fr_height.setText(String.valueOf(isText ?
                    Constants.TEXT_MATRIX_HEIGHT : Constants.GRAPHIC_MATRIX_HEIGHT));
            if (fr_width.getText().toString().equals("")) fr_width.setText(String.valueOf(isText ?
                    Constants.TEXT_MATRIX_WIDTH : Constants.GRAPHIC_MATRIX_WIDTH));
            if (fr_time.getText().toString().equals(""))
                fr_time.setText(String.valueOf(18));
            if (Integer.parseInt(fr_height.getText().toString()) == 1
                    && Integer.parseInt(fr_width.getText().toString()) == 1)
                fr_width.setText("2");
            if (Integer.parseInt(fr_time.getText().toString()) < 1)
                fr_time.setText("1");
            Intent intent;
            SharedPreferences.Editor editor = getSharedPreferences("data", 0).edit();
            int time = Integer.parseInt(fr_time.getText().toString());
            editor.putInt("FreeTime", time);
            editor.apply();
            Bundle data = new Bundle();
            data.putInt("time", time * 1000);
            data.putInt("width", Integer.parseInt(fr_width.getText().toString()));
            data.putInt("height", Integer.parseInt(fr_height.getText().toString()));
            data.putBoolean("free", true);
            if (fr_content.isChecked()) {
                if (isText)
                    intent = new Intent(FreeModeInitActivity.this, TextLevelSelectActivity.class);
                else
                    intent = new Intent(FreeModeInitActivity.this, GraphicLevelSelectActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                if (fr_first.getText().length() == 0)
                    fr_first.setText(fr_first.getHint());
                if (fr_second.getText().length() == 0)
                    fr_second.setText(fr_second.getHint());
                        /*Toast.makeText(FreeModeInitActivity.this, getString(R.string.efr_fill),
                                Toast.LENGTH_SHORT).show();
                        break;/**/
                intent = new Intent(FreeModeInitActivity.this, TextActivity.class);
                data.putBoolean("Customized", true);
                if (getResources().getConfiguration().locale.getLanguage().equals("zh")) {
                    data.putChar("true", fr_second.getText().charAt(0));
                    data.putChar("false", fr_first.getText().charAt(0));
                } else {
                    data.putChar("true", fr_first.getText().charAt(0));
                    data.putChar("false", fr_second.getText().charAt(0));
                }
                intent.putExtras(data);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }
    }

    public void onBackPressed() { back.performClick(); }

    protected void onStop() {
        super.onStop();
        AudioUtil.PauseMusic();
    }

    protected void onResume() {
        super.onResume();
        AudioUtil.PlayMusic();
    }

    private void initVariables() {
        isText = getIntent().getBooleanExtra("isText", true);
        fr_time = (EditText) findViewById(R.id.fr_time);
        fr_width = (EditText) findViewById(R.id.fr_width);
        fr_height = (EditText) findViewById(R.id.fr_height);
        fr_first = (EditText) findViewById(R.id.fr_first);
        fr_second = (EditText) findViewById(R.id.fr_second);
        fr_content = (CheckBox) findViewById(R.id.fr_content);
        frrl = (RelativeLayout) findViewById(R.id.frrl);
        frll = (LinearLayout) findViewById(R.id.frll);
        fr[0] = (TextView) findViewById(R.id.fr01);
        fr[1] = (TextView) findViewById(R.id.fr02);
        fr[2] = (TextView) findViewById(R.id.fr03);
        fr[3] = (TextView) findViewById(R.id.fr04);
        fr[4] = (TextView) findViewById(R.id.fr05);
        fr[5] = (TextView) findViewById(R.id.fr06);
        fr[6] = (TextView) findViewById(R.id.fr07);
        fr[7] = (TextView) findViewById(R.id.fr08);
        fr[8] = (TextView) findViewById(R.id.fr09);
        fr_content.setOnCheckedChangeListener(this);
        fr_content.setEnabled(true);
        Bundle data = getIntent().getExtras();
        if (data.getBoolean("Customized", false)) {
            fr_time.setText(String.valueOf(data.getInt("time", Constants.TIPS_NUMBER) / 1000));
            fr_width.setText(String.valueOf(data.getInt("width", Constants.TEXT_MATRIX_WIDTH)));
            fr_height.setText(String.valueOf(data.getInt("height", Constants.TEXT_MATRIX_HEIGHT)));
            fr_content.setChecked(false);
            onCheckedChanged(fr_content, fr_content.isChecked());
        } else {
            fr_content.setChecked(true);
            onCheckedChanged(fr_content, fr_content.isChecked());
        }
        if (!isText) fr_content.setEnabled(false);
        start = (Button) findViewById(R.id.fr_start);
        back = (Button) findViewById(R.id.fr_back);
        back.setOnClickListener(this);
        start.setOnClickListener(this);
        fr_width.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0){
                    if (Integer.parseInt(s.toString())
                        > (isText ? Constants.TEXT_MATRIX_WIDTH : Constants.GRAPHIC_MATRIX_WIDTH)) {
                    Toast.makeText(FreeModeInitActivity.this, (isText ? getString(R.string.st_text)
                            : getString(R.string.st_graphics)) + getString(R.string.efr_width)
                            + (isText ? Constants.TEXT_MATRIX_WIDTH : Constants.GRAPHIC_MATRIX_WIDTH),
                            Toast.LENGTH_SHORT).show();
                    s.replace(0, s.length(), String.valueOf(isText ?
                            Constants.TEXT_MATRIX_WIDTH : Constants.GRAPHIC_MATRIX_WIDTH));
                    } else
                        fr_height.requestFocus();
                }
            }
        });
        fr_height.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (Integer.parseInt(s.toString())
                            > (isText ? Constants.TEXT_MATRIX_HEIGHT : Constants.GRAPHIC_MATRIX_HEIGHT)) {
                        Toast.makeText(FreeModeInitActivity.this, (isText ? getString(R.string.st_text)
                                : getString(R.string.st_graphics)) + getString(R.string.efr_height)
                                + (isText ? Constants.TEXT_MATRIX_HEIGHT : Constants.GRAPHIC_MATRIX_HEIGHT),
                                Toast.LENGTH_SHORT).show();
                        s.replace(0, s.length(), String.valueOf(isText ?
                                Constants.TEXT_MATRIX_HEIGHT : Constants.GRAPHIC_MATRIX_HEIGHT));
                    } else if (Integer.parseInt(s.toString()) < 1) {
                        s.replace(0, s.length(), "1");
                        fr_first.requestFocus();
                    } else if (frll.getVisibility() == View.VISIBLE)
                        fr_first.requestFocus();
                }
            }
        });
        fr_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (Integer.parseInt(s.toString()) > 60) {
                        s.replace(0, s.length(), "60");
                        Toast.makeText(FreeModeInitActivity.this, getString(R.string.efr_time) + " 60s",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else if (Integer.parseInt(s.toString()) < 1) {
                        s.replace(0, s.length(), "1");
                        fr_width.requestFocus();
                    } else if (s.length() == 2)
                        fr_width.requestFocus();
                }
            }
        });
    }

    private void refreshMatrix() {
        if (getResources().getConfiguration().locale.getLanguage().equals("zh")) {
            for (int i = 0; i < fr.length; i++) {
                if (i == 4) fr[i].setText((fr_second.getText().toString().equals("")
                        ? getString(R.string.fr_hTrue) : fr_second.getText()));
                else fr[i].setText((fr_first.getText().toString().equals("")
                        ? getString(R.string.fr_hFalse) : fr_first.getText()));
            }
        } else {
            for (int i = 0; i < fr.length; i++) {
                if (i == 4) fr[i].setText((fr_first.getText().toString().equals("")
                        ? getString(R.string.fr_hTrue) : fr_first.getText()));
                else fr[i].setText((fr_second.getText().toString().equals("")
                        ? getString(R.string.fr_hFalse) : fr_second.getText()));
            }
        }
    }

}

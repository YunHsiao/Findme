package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Gaming Statistics and Player Achievements.
 * Created by 武云潇 on 2014/8/28.
 */
public class AchievementsActivity extends Activity implements View.OnClickListener {

    private static int quickEnter;
    private static int[] tl, tm, gl, gm;
    private static double[] tf, ts, gf, gs;
    private static Button share, back;
    private static TextView st_level, st_fail, st_fast, st_slow, st_least, st_most, st_pass,
                        st_glevel, st_gfail, st_gfast, st_gslow, st_gleast, st_gmost, st_gpass,
                        ac_graphic, ac_music, ac_unlock, ac_about,
                        ac_pass, ac_lost, ac_homescreen, ac_keyguard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_achievements);
        initVariables();
        loadAchievements();
        loadStatistics();
        DBManager.registerListener(new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                DBManager.parallaxView(findViewById(R.id.s_a_rl),
                        -Float.parseFloat(Constants.df.format(event.values[0]*2)),
                        (Float.parseFloat(Constants.df.format(event.values[1]))*2 - 14));
            }
            public void onAccuracyChanged(Sensor s, int accuracy) { }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.st_level) {
            quickEnter = 0;
            Toast.makeText(this, getString(R.string.st_text) + " "
                    + getString(R.string.est_level), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.st_fail) {
            quickEnter = 0;
            Toast.makeText(this, getString(R.string.st_text) + " "
                    + getString(R.string.est_fail), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.st_fast) {
            if (tf[0] != -1) {
                if (quickEnter != 1)
                    quickEnter = 1;
                else {
                    quickEnterLevel((int) tf[1], true);
                    return;
                }
                String toast;
                if (getResources().getConfiguration().locale.getLanguage().equals("zh"))
                    toast = getString(R.string.st_text) + " " + getString(R.string.est_fast) + "關卡"
                            + (int) tf[1] + "，" + tf[0] + "s  " + getString(R.string.est_enter);
                else
                    toast = getString(R.string.st_text) + " " + getString(R.string.est_fast)
                            + "Level " + (int) tf[1] + ", " + tf[0] + "s  " + getString(R.string.est_enter);
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
            } else {
                quickEnter = 0;
                Toast.makeText(this, getString(R.string.est_play), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.st_slow) {
            if (ts[0] != -1) {
                if (quickEnter != 2)
                    quickEnter = 2;
                else {
                    quickEnterLevel((int) ts[1], true);
                    return;
                }
                String toast;
                if (getResources().getConfiguration().locale.getLanguage().equals("zh"))
                    toast = getString(R.string.st_text) + " " + getString(R.string.est_slow) + "關卡"
                            + (int) ts[1] + "，" + ts[0] + "s  " + getString(R.string.est_enter);
                else
                    toast = getString(R.string.st_text) + " " + getString(R.string.est_slow)
                            + "Level " + (int) ts[1] + ", " + ts[0] + "s  " + getString(R.string.est_enter);
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
            } else {
                quickEnter = 0;
                Toast.makeText(this, getString(R.string.est_play), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.st_least) {
            if (tl[0] != -1) {
                if (quickEnter != 5)
                    quickEnter = 5;
                else {
                    quickEnterLevel(tl[1], true);
                    return;
                }
                String toast;
                if (getResources().getConfiguration().locale.getLanguage().equals("zh"))
                    toast = getString(R.string.st_text) + " " + getString(R.string.est_times) + "關卡"
                            + tl[1] + "，點擊" + tl[0] + "次  " + getString(R.string.est_enter);
                else
                    toast = getString(R.string.st_text) + " " + getString(R.string.est_times)
                            + "Level " + tl[1] + ", clicked" + tl[0] + "times  " + getString(R.string.est_enter);
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
            } else {
                quickEnter = 0;
                Toast.makeText(this, getString(R.string.est_play), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.st_most) {
            if (tm[0] != -1) {
                if (quickEnter != 6)
                    quickEnter = 6;
                else {
                    quickEnterLevel(tm[1], true);
                    return;
                }
                String toast;
                if (getResources().getConfiguration().locale.getLanguage().equals("zh"))
                    toast = getString(R.string.st_text) + " " + getString(R.string.est_times2) + "關卡"
                            + tm[1] + "，點擊" + tm[0] + "次  " + getString(R.string.est_enter);
                else
                    toast = getString(R.string.st_text) + " " + getString(R.string.est_times2)
                            + "Level " + tm[1] + ", clicked" + tm[0] + "times  " + getString(R.string.est_enter);
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
            } else {
                quickEnter = 0;
                Toast.makeText(this, getString(R.string.est_play), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.st_pass) {
            quickEnter = 0;
            Toast.makeText(this, getString(R.string.st_text) + " "
                    + getString(R.string.est_pass), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.st_glevel) {
            quickEnter = 0;
            Toast.makeText(this, getString(R.string.st_graphics) + " "
                    + getString(R.string.est_level), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.st_gfail) {
            quickEnter = 0;
            Toast.makeText(this, getString(R.string.st_graphics) + " "
                    + getString(R.string.est_fail), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.st_gfast) {
            if (gf[0] != -1) {
                if (quickEnter != 3)
                    quickEnter = 3;
                else {
                    quickEnterLevel((int) gf[1], false);
                    return;
                }
                String toast;
                if (getResources().getConfiguration().locale.getLanguage().equals("zh"))
                    toast = getString(R.string.st_text) + " " + getString(R.string.est_fast) + "關卡"
                            + (int) gf[1] + "，" + gf[0] + "s  " + getString(R.string.est_enter);
                else
                    toast = getString(R.string.st_text) + " " + getString(R.string.est_fast)
                            + "Level " + (int) gf[1] + ", " + gf[0] + "s  " + getString(R.string.est_enter);
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
            } else {
                quickEnter = 0;
                Toast.makeText(this, getString(R.string.est_play), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.st_gslow) {
            if (gs[0] != -1) {
                if (quickEnter != 4)
                    quickEnter = 4;
                else {
                    quickEnterLevel((int) gs[1], false);
                    return;
                }
                String toast;
                if (getResources().getConfiguration().locale.getLanguage().equals("zh"))
                    toast = getString(R.string.st_graphics) + " " + getString(R.string.est_slow) + "關卡"
                            + (int) gs[1] + "，" + gs[0] + "s  " + getString(R.string.est_enter);
                else
                    toast = getString(R.string.st_graphics) + " " + getString(R.string.est_slow)
                            + "Level " + (int) gs[1] + ", " + gs[0] + "s  " + getString(R.string.est_enter);
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
            } else {
                quickEnter = 0;
                Toast.makeText(this, getString(R.string.est_play), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.st_gleast) {
            if (gl[0] != -1) {
                if (quickEnter != 7)
                    quickEnter = 7;
                else {
                    quickEnterLevel(gl[1], false);
                    return;
                }
                String toast;
                if (getResources().getConfiguration().locale.getLanguage().equals("zh"))
                    toast = getString(R.string.st_graphics) + " " + getString(R.string.est_times) + "關卡"
                            + gl[1] + "，點擊" + gl[0] + "次  " + getString(R.string.est_enter);
                else
                    toast = getString(R.string.st_graphics) + " " + getString(R.string.est_times)
                            + "Level " + gl[1] + ", clicked" + gl[0] + "times  " + getString(R.string.est_enter);
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
            } else {
                quickEnter = 0;
                Toast.makeText(this, getString(R.string.est_play), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.st_gmost) {
            if (gm[0] != -1) {
                if (quickEnter != 8)
                    quickEnter = 8;
                else {
                    quickEnterLevel(gm[1], false);
                    return;
                }
                String toast;
                if (getResources().getConfiguration().locale.getLanguage().equals("zh"))
                    toast = getString(R.string.st_graphics) + " " + getString(R.string.est_times2) + "關卡"
                            + gm[1] + "，點擊" + gm[0] + "次  " + getString(R.string.est_enter);
                else
                    toast = getString(R.string.st_graphics) + " " + getString(R.string.est_times2)
                            + "Level " + gm[1] + ", clicked" + gm[0] + "times  " + getString(R.string.est_enter);
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
            } else {
                quickEnter = 0;
                Toast.makeText(this, getString(R.string.est_play), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.st_gpass) {
            quickEnter = 0;
            Toast.makeText(this, getString(R.string.st_graphics) + " "
                    + getString(R.string.est_pass), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.ac_graphic) {
            quickEnter = 0;
            Toast.makeText(this,
                    getResources().getString(R.string.eac_graphic), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.ac_music) {
            quickEnter = 0;
            Toast.makeText(this,
                    getResources().getString(R.string.eac_music), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.ac_unlock) {
            quickEnter = 0;
            Toast.makeText(this,
                    getResources().getString(R.string.eac_unlock), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.ac_about) {
            quickEnter = 0;
            Toast.makeText(this,
                    getResources().getString(R.string.eac_about), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.ac_pass) {
            quickEnter = 0;
            Toast.makeText(this,
                    getResources().getString(R.string.eac_pass), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.ac_lost) {
            quickEnter = 0;
            Toast.makeText(this,
                    getResources().getString(R.string.eac_lost), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.ac_homescreen) {
            quickEnter = 0;
            Toast.makeText(this,
                    getResources().getString(R.string.eac_homescreen), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.ac_keyguard) {
            quickEnter = 0;
            Toast.makeText(this,
                    getResources().getString(R.string.eac_keyguard), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.acback) {
            quickEnter = 0;
            AudioUtil.PlaySound(R.raw.button);
            startActivity(new Intent(AchievementsActivity.this, MainMenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if (v.getId() == R.id.share) {
            quickEnter = 0;
            AudioUtil.PlaySound(R.raw.button);

            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd_HH-mm-ss", Locale.US);
            String fPath = Environment.getExternalStorageDirectory().getPath()
                    + "/games/sdufe.dmt";
            String imagepath = fPath + "/" + sdf.format(new Date()) + ".jpg";
            share.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            View view = v.getRootView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();
            share.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            if (bitmap != null) {
                try {
                    File path = new File(fPath);
                    File file = new File(imagepath);
                    deleteDir(path);
                    if (!path.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        path.mkdirs();
                    }
                    if (!file.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            shareMsg(getString(R.string.share_title), getString(R.string.share_title),
                    getString(R.string.share_text), imagepath);
        }
    }

    @Override
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

    private void initVariables() {
        share = (Button) findViewById(R.id.share);
        back = (Button) findViewById(R.id.acback);
        st_level = (TextView) findViewById(R.id.st_level);
        st_fail = (TextView) findViewById(R.id.st_fail);
        st_fast = (TextView) findViewById(R.id.st_fast);
        st_slow = (TextView) findViewById(R.id.st_slow);
        st_least = (TextView) findViewById(R.id.st_least);
        st_most = (TextView) findViewById(R.id.st_most);
        st_pass = (TextView) findViewById(R.id.st_pass);
        st_glevel = (TextView) findViewById(R.id.st_glevel);
        st_gfail = (TextView) findViewById(R.id.st_gfail);
        st_gfast = (TextView) findViewById(R.id.st_gfast);
        st_gslow = (TextView) findViewById(R.id.st_gslow);
        st_gleast = (TextView) findViewById(R.id.st_gleast);
        st_gmost = (TextView) findViewById(R.id.st_gmost);
        st_gpass = (TextView) findViewById(R.id.st_gpass);
        ac_graphic = (TextView) findViewById(R.id.ac_graphic);
        ac_music = (TextView) findViewById(R.id.ac_music);
        ac_unlock = (TextView) findViewById(R.id.ac_unlock);
        ac_about = (TextView) findViewById(R.id.ac_about);
        ac_pass = (TextView) findViewById(R.id.ac_pass);
        ac_lost = (TextView) findViewById(R.id.ac_lost);
        ac_homescreen = (TextView) findViewById(R.id.ac_homescreen);
        ac_keyguard = (TextView) findViewById(R.id.ac_keyguard);
        ac_keyguard.setOnClickListener(this);
        ac_homescreen.setOnClickListener(this);
        ac_lost.setOnClickListener(this);
        ac_pass.setOnClickListener(this);
        ac_about.setOnClickListener(this);
        ac_unlock.setOnClickListener(this);
        ac_music.setOnClickListener(this);
        ac_graphic.setOnClickListener(this);
        back.setOnClickListener(this);
        share.setOnClickListener(this);
        st_level.setOnClickListener(this);
        st_fail.setOnClickListener(this);
        st_fast.setOnClickListener(this);
        st_slow.setOnClickListener(this);
        st_least.setOnClickListener(this);
        st_most.setOnClickListener(this);
        st_pass.setOnClickListener(this);
        st_glevel.setOnClickListener(this);
        st_gfail.setOnClickListener(this);
        st_gfast.setOnClickListener(this);
        st_gslow.setOnClickListener(this);
        st_gleast.setOnClickListener(this);
        st_gmost.setOnClickListener(this);
        st_gpass.setOnClickListener(this);
        quickEnter = 0;
    }

    private void loadAchievements() {
        if (this.getSharedPreferences("data", 0).getBoolean("ac_graphic", false))
            ac_graphic.setTextColor(getResources().getColor(R.color.holo_normal));
        if (this.getSharedPreferences("data", 0).getBoolean("ac_music", false))
            ac_music.setTextColor(getResources().getColor(R.color.holo_normal));
        if (this.getSharedPreferences("data", 0).getBoolean("ac_unlock", false))
            ac_unlock.setTextColor(getResources().getColor(R.color.holo_normal));
        if (this.getSharedPreferences("data", 0).getBoolean("ac_about", false))
            ac_about.setTextColor(getResources().getColor(R.color.holo_normal));
        if (this.getSharedPreferences("data", 0).getBoolean("ac_pass", false))
            ac_pass.setTextColor(getResources().getColor(R.color.holo_normal));
        if (this.getSharedPreferences("data", 0).getBoolean("ac_lost", false))
            ac_lost.setTextColor(getResources().getColor(R.color.holo_normal));
        if (this.getSharedPreferences("data", 0).getBoolean("ac_homescreen", false))
            ac_homescreen.setTextColor(getResources().getColor(R.color.holo_normal));
        if (this.getSharedPreferences("data", 0).getBoolean("ac_keyguard", false))
            ac_keyguard.setTextColor(getResources().getColor(R.color.holo_normal));
    }

    private void loadStatistics() {
        tf = format(getFastestRecord(true));
        ts =  format(getSlowestRecord(true));
        gf = format(getFastestRecord(false));
        gs =  format(getSlowestRecord(false));
        tl = getLeastClicks(true);
        tm = getMostClicks(true);
        gl = getLeastClicks(false);
        gm = getMostClicks(false);
        st_level.setText(getString(R.string.st_level) + getAllLevelCount(true));
        st_fail.setText(getString(R.string.st_fail) + getFailedLevelCount(true));
        if (tf[0] != -1) st_fast.setText(getString(R.string.st_fast) + " " + tf[0] + "s");
        else st_fast.setText(getString(R.string.st_fast) + " -");
        if (ts[0] != -1) st_slow.setText(" " + getString(R.string.st_slow) + " " + ts[0] + "s");
        else st_slow.setText(" " + getString(R.string.st_slow) + " -");
        if (tl[0] != -1) st_least.setText(getString(R.string.st_times) + " " + tl[0]);
        else st_least.setText(getString(R.string.st_times) + " -");
        if (tm[0] != -1) st_most.setText(" " + getString(R.string.st_slow) + " " + tm[0]);
        else st_most.setText(" " + getString(R.string.st_slow) + " -");
        st_pass.setText(getString(R.string.st_pass) + getPassedLevel(true));
        st_glevel.setText(getString(R.string.st_level) + getAllLevelCount(false));
        st_gfail.setText(getString(R.string.st_fail) + getFailedLevelCount(false));
        if (gf[0] != -1) st_gfast.setText(getString(R.string.st_fast) + " " + gf[0] + "s");
        else st_gfast.setText(getString(R.string.st_fast) + " -");
        if (gs[0] != -1) st_gslow.setText(" " + getString(R.string.st_slow) + " " + gs[0] + "s");
        else st_gslow.setText(" " + getString(R.string.st_slow) + " -");
        if (gl[0] != -1) st_gleast.setText(getString(R.string.st_times) + " " + gl[0]);
        else st_gleast.setText(getString(R.string.st_times) + " -");
        if (gm[0] != -1) st_gmost.setText(" " + getString(R.string.st_slow) + " " + gm[0]);
        else st_gmost.setText(" " + getString(R.string.st_slow) + " -");
        st_gpass.setText(getString(R.string.st_pass) + getPassedLevel(false));
    }

    private int getAllLevelCount(boolean isText) {
        if (isText)
            return Integer.parseInt(DBManager.Continue()[2]) - 1;
        else
            return (Integer)DBManager.GContinue()[2] - 1;
    }

    private int getFailedLevelCount(boolean isText) {
        if (isText)
            return this.getSharedPreferences("data", 0).getInt("st_fail", 0);
        else
            return this.getSharedPreferences("data", 0).getInt("st_gfail", 0);
    }

    private double[] getFastestRecord(boolean isText) {
        return DBManager.getFastestRecord(isText);
    }

    private double[] getSlowestRecord(boolean isText) {
        return DBManager.getSlowestRecord(isText);
    }

    private int[] getLeastClicks(boolean isText) {
        return DBManager.getLeastClicks(isText);
    }

    private int[] getMostClicks(boolean isText) {
        return DBManager.getMostClicks(isText);
    }

    private int getPassedLevel(boolean isText) {
        if (isText)
            return this.getSharedPreferences("data", 0).getInt("st_pass", 0);
        else
            return this.getSharedPreferences("data", 0).getInt("st_gpass", 0);
    }

    private static double[] format(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = Double.parseDouble(Constants.df.format(array[i]));
        }
        return array;
    }

    private void quickEnterLevel(int level, boolean isText) {
        if (isText){
            DBManager.jumpTo(level);
            Intent intent = new Intent(AchievementsActivity.this, TextActivity.class);
            Bundle data = new Bundle();
            data.putInt("time", 12000);
            intent.putExtras(data);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            DBManager.GjumpTo(level);
            Intent intent = new Intent(AchievementsActivity.this, GraphicActivity.class);
            Bundle data = new Bundle();
            data.putInt("time", 12000);
            intent.putExtras(data);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain");
        } else {
            File f = new File(imgPath);
            if (f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}

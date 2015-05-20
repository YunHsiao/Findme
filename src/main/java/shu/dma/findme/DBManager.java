package shu.dma.findme;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Essentials: Database Manager.
 * Also the provider of ParallaxView based on gyroscope input.
 */
public final class DBManager {

    private static final String DB_NAME = "FindMe.sqlite";
    private static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()
            + "/" + Constants.PACKAGE_NAME;
    private static SQLiteDatabase database;
    private static Context context;
    private static Cursor query, gquery;
    private static String t, f, id;
    private static int gid=1;
    private static Bitmap gt, gf;
    private static SensorManager sensorMgr;
    private static Sensor sensor;

    private DBManager() {}

    protected static void openDatabase(Context context) {
        if (database == null) {
            DBManager.context = context;
            database = openDatabase(DB_PATH + "/" + DB_NAME);
            query = database.rawQuery("SELECT * FROM Text", null);
            gquery = database.rawQuery("SELECT * FROM Graphic", null);
            //drawable2database(context.getResources());
        }
        sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private static SQLiteDatabase openDatabase(String dbfile) {
        try {
            if (!(new File(dbfile).exists())) {
                InputStream is = context.getResources().openRawResource(R.raw.findme);
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[400000];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            return SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }

    /*private static void drawable2database(Resources res) {
        int id = R.drawable.image_01_00;
        for (int i = 1; i <= 75; i++) {
            Bitmap bmp = BitmapFactory.decodeResource(res, id);
            ByteArrayOutputStream f = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, f);
            bmp = BitmapFactory.decodeResource(res, id+1);
            ByteArrayOutputStream t = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, t);
            ContentValues values = new ContentValues();
            //Log.i("database", new String(t.toByteArray()));
            values.put("f", f.toByteArray());
            values.put("t", t.toByteArray());
            database.update("Graphic", values, "_id=?", new String[]{String.valueOf(i)});
            id+=2;
        }
    }/**/

    protected static String[] Continue() {
        query = database.rawQuery("SELECT * FROM Text", null);
        query.moveToFirst();
        while (query.moveToNext()) {
            if (query.getInt(query.getColumnIndex("unlock")) == 0) {
                query.moveToPrevious();
                if (query.getInt(query.getColumnIndex("unlock")) == 1) {
                    Commit();
                    return current();
                }
            }
        }
        query.moveToLast();
        Commit();
        return current();
    }

    protected static Object[] GContinue() {
        gquery = database.rawQuery("SELECT * FROM Graphic", null);
        gquery.moveToFirst();
        while (gquery.moveToNext()) {
            if (gquery.getInt(gquery.getColumnIndex("unlock")) == 0) {
                gquery.moveToPrevious();
                if (gquery.getInt(gquery.getColumnIndex("unlock")) == 1) {
                    Gcommit();
                    return Gcurrent();
                }
            }
        }
        gquery.moveToLast();
        Gcommit();
        return Gcurrent();
    }

    protected static void Tnext(int star, double record, int clicked, boolean isFree) {
        if (!isFree) {
            double oRecord = query.getDouble(query.getColumnIndex("record"));
            int oClicked = query.getInt(query.getColumnIndex("clicked"));
            if (record < oRecord || oRecord == -1) {
                database.execSQL("UPDATE Text SET star=" + star + " WHERE _id=" + id);
                database.execSQL("UPDATE Text SET record=" + record + " WHERE _id=" + id);
                if (clicked < oClicked || oClicked == 0)
                    database.execSQL("UPDATE Text SET clicked=" + clicked + " WHERE _id=" + id);
            }
            query.moveToNext();
            if (query.isAfterLast()) query.moveToFirst();
            Commit();
            database.execSQL("UPDATE Text SET unlock=1 WHERE _id=" + id);
        } else {
            query.moveToNext(); query.moveToNext();
            if (query.getInt(query.getColumnIndex("unlock")) == 0)
                query.moveToFirst();
            else
                query.moveToPrevious();
            Commit();
        }
    }

    protected static void Gnext(int star, double record, int clicked, boolean isFree) {
        if (!isFree) {
            double oRecord = gquery.getDouble(gquery.getColumnIndex("record"));
            int oClicked = gquery.getInt(gquery.getColumnIndex("clicked"));
            if (record < oRecord || oRecord == -1) {
                database.execSQL("UPDATE Graphic SET record=" + record + " WHERE _id=" + gid);
                database.execSQL("UPDATE Graphic SET star=" + star + " WHERE _id=" + gid);
                if (clicked < oClicked || oClicked == 0)
                    database.execSQL("UPDATE Graphic SET clicked=" +  clicked + " WHERE _id=" + gid);
            }
            gquery.moveToNext();
            if (gquery.isAfterLast()) gquery.moveToFirst();
            Gcommit();
            database.execSQL("UPDATE Graphic SET unlock=1 WHERE _id=" + gid);
        } else {
            gquery.moveToNext(); gquery.moveToNext();
            if (gquery.getInt(gquery.getColumnIndex("unlock")) == 0)
                gquery.moveToFirst();
            else
                gquery.moveToPrevious();
            Gcommit();
        }
    }

    protected static void jumpTo(int id) {
        if (query.isAfterLast()) query.moveToPrevious();
        if (query.isBeforeFirst()) query.moveToNext();
        while (query.getInt(query.getColumnIndex("_id")) > id) {
            query.moveToPrevious();
        }
        while (query.getInt(query.getColumnIndex("_id")) < id) {
            query.moveToNext();
        }
        Commit();
    }

    protected static void GjumpTo(int id) {
        if (gquery.isAfterLast()) gquery.moveToPrevious();
        if (gquery.isBeforeFirst()) gquery.moveToNext();
        while (gquery.getInt(gquery.getColumnIndex("_id")) > id) {
            gquery.moveToPrevious();
        }
        while (gquery.getInt(gquery.getColumnIndex("_id")) < id) {
            gquery.moveToNext();
        }
        Gcommit();
    }

    private static void Commit() {
        t = query.getString(query.getColumnIndex("true"));
        f = query.getString(query.getColumnIndex("false"));
        id = String.valueOf(query.getInt(query.getColumnIndex("_id")));
    }

    private static void Gcommit() {
        byte[] blob = gquery.getBlob(gquery.getColumnIndex("t"));
        gt = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        blob = gquery.getBlob(gquery.getColumnIndex("f"));
        gf = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        gid = gquery.getInt(gquery.getColumnIndex("_id"));
    }

    protected static String[] current() {
        return new String[]{t, f, id};
    }

    protected static Object[] Gcurrent() {
        return new Object[]{gt, gf, gid};
    }

    protected static int[] Tstar(int startLevel) {
        query = database.rawQuery("SELECT * FROM Text", null);
        jumpTo(startLevel);
        int[] stars = new int[12];
        for (int i = 0; i < 12; i++) {
            if (query.isAfterLast()) break;
            stars[i] = query.getInt(query.getColumnIndex("star"));
            query.moveToNext();
        }
        return stars;
    }

    protected static int[] Gstar(int startLevel) {
        gquery = database.rawQuery("SELECT * FROM Graphic", null);
        GjumpTo(startLevel);
        int[] stars = new int[12];
        for (int i = 0; i < 12; i++) {
            if (gquery.isAfterLast()) break;
            stars[i] = gquery.getInt(gquery.getColumnIndex("star"));
            gquery.moveToNext();
        }
        return stars;
    }

    protected static void Demo() {
        database.execSQL("UPDATE Text SET unlock=1");
        database.execSQL("UPDATE Text SET star=2 WHERE star=0");
        database.execSQL("UPDATE Text SET clicked=1 WHERE clicked<1");
        database.execSQL("UPDATE Text SET record=1.0 WHERE record<1");
        database.execSQL("UPDATE Graphic SET unlock=1");
        database.execSQL("UPDATE Graphic SET star=2 WHERE star=0");
        database.execSQL("UPDATE Graphic SET clicked=1 WHERE clicked<1");
        database.execSQL("UPDATE Graphic SET record=1.0 WHERE record<1");
    }

    protected static int[] getLeastClicks(boolean isText) {
        Cursor least;
        int[] l = new int[2];
        if (isText) {
            least = database.rawQuery("SELECT MIN(clicked) FROM Text WHERE clicked>0", null);
            least.moveToFirst();
            l[0] = least.getInt(0);
            if (l[0] > 0) {
                least = database.rawQuery("SELECT _id FROM Text WHERE clicked=" + l[0], null);
                least.moveToFirst();
                l[1] = least.getInt(0);
            } else
                l[0] = -1;
        } else {
            least = database.rawQuery("SELECT MIN(clicked) FROM Graphic WHERE clicked>0", null);
            least.moveToFirst();
            l[0] = least.getInt(0);
            if (l[0] > 0) {
                least = database.rawQuery("SELECT _id FROM Graphic WHERE clicked=" + l[0], null);
                least.moveToFirst();
                l[1] = least.getInt(0);
            } else
                l[0] = -1;
        }
        return l;
    }

    protected static int[] getMostClicks(boolean isText) {
        Cursor most;
        int[] m = new int[2];
        if (isText) {
            most = database.rawQuery("SELECT MAX(clicked) FROM Text WHERE clicked>0", null);
            most.moveToFirst();
            m[0] = most.getInt(0);
            if (m[0] > 0) {
                most = database.rawQuery("SELECT _id FROM Text WHERE clicked=" + m[0], null);
                most.moveToFirst();
                m[1] = most.getInt(0);
            } else
                m[0] = -1;
        } else {
            most = database.rawQuery("SELECT MAX(clicked) FROM Graphic WHERE clicked>0", null);
            most.moveToFirst();
            m[0] = most.getInt(0);
            if (m[0] > 0) {
                most = database.rawQuery("SELECT _id FROM Graphic WHERE clicked=" + m[0], null);
                most.moveToFirst();
                m[1] = most.getInt(0);
            } else
                m[0] = -1;
        }
        return m;
    }

    protected static double[] getFastestRecord(boolean isText) {
        Cursor min;
        double[] i = new double[2];
        if (isText) {
            min = database.rawQuery("SELECT MIN(record) FROM Text WHERE record>0", null);
            min.moveToFirst();
            i[0] = min.getDouble(0);
            if (i[0] > 0) {
                min = database.rawQuery("SELECT _id FROM Text WHERE record=" + i[0], null);
                min.moveToFirst();
                i[1] = min.getInt(0);
            } else
                i[0] = -1;
        } else {
            min = database.rawQuery("SELECT MIN(record) FROM Graphic WHERE record>0", null);
            min.moveToFirst();
            i[0] = min.getDouble(0);
            if (i[0] > 0) {
                min = database.rawQuery("SELECT _id FROM Graphic WHERE record=" + i[0], null);
                min.moveToFirst();
                i[1] = min.getInt(0);
            } else
                i[0] = -1;
        }
        return i;
    }

    protected static double[] getSlowestRecord(boolean isText) {
        Cursor max;
        double[] a = new double[2];
        if (isText) {
            max = database.rawQuery("SELECT MAX(record) FROM Text", null);
            max.moveToFirst();
            a[0] = max.getDouble(0);
            if (a[0] > 0) {
                max = database.rawQuery("SELECT _id FROM Text WHERE record=" + a[0], null);
                max.moveToFirst();
                a[1] = max.getInt(0);
            } else
                a[0] = -1;
        } else {
            max = database.rawQuery("SELECT MAX(record) FROM Graphic", null);
            max.moveToFirst();
            a[0] = max.getDouble(0);
            if (a[0] > 0) {
                max = database.rawQuery("SELECT _id FROM Graphic WHERE record=" + a[0], null);
                max.moveToFirst();
                a[1] = max.getInt(0);
            } else
                a[0] = -1;
        }
        return a;
    }

    public static void closeDatabase() {
        if (database != null) database.close();
    }

    public static void registerListener(SensorEventListener sel) {
        sensorMgr.registerListener(sel, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public static void parallaxView(final View view, float p0, float p1) {
        /*final float q0, q1;
        if (Math.abs(q[0]-p0) < 0) return;
        else q0 = p0;
        if (Math.abs(q[1]-p1) < 0) return;
        else q1 = p1;/**/
        if (view.getVisibility() == View.GONE) { view.clearAnimation(); return; }
        TranslateAnimation animation = new TranslateAnimation(p0, p0, p1, p1);
        animation.setDuration(1);
        animation.setStartOffset(0);
        animation.setFillAfter(true);
        /*animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int left = view.getLeft() + (int) (q0);
                int top = view.getTop() + (int) (q1);
                int width = view.getWidth();
                int height = view.getHeight();
                view.clearAnimation();
                view.layout(left, top, left+width, top+height);
            }
        });/**/
        view.startAnimation(animation);
        /*q[0] = q0;
        q[1] = q1;/**/
    }

}
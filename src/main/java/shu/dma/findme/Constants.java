package shu.dma.findme;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;

public final class Constants {

    public static final int TEXT_LEVEL = 64;
    public static final int GRAPHIC_LEVEL = 75;
    public static final int TEXT_MATRIX = 70;
    public static final int TEXT_MATRIX_WIDTH = 7;
    public static final int TEXT_MATRIX_HEIGHT = 10;
    public static final int GRAPHIC_MATRIX = 54;
    public static final int GRAPHIC_MATRIX_WIDTH = 6;
    public static final int GRAPHIC_MATRIX_HEIGHT = 9;
    public static final int FIRST_HINT_LINES = 6;
    public static final int HINT_LINES = 3;
    public static final int WIDGET_MATRIX = 54;
    public static final int WIDGET_MATRIX_WIDTH = 6;
    public static final int WIDGET_MATRIX_HEIGHT = 9;
    public static final int TIPS_NUMBER = 19;
    public static final String PACKAGE_NAME = "shu.dma.findme";
    public static final String LAST_TIPS = PACKAGE_NAME + ".LAST_TIPS";
    public static DecimalFormat df = new DecimalFormat("0.0");

    private Constants() {}

    public static String getTips(Context context) {
        int last = context.getSharedPreferences("data", 0).getInt(LAST_TIPS, -1);
        SharedPreferences.Editor editor = context.getSharedPreferences("data", 0).edit();
        if (last == -1) {
            editor.putInt(LAST_TIPS, R.string.tip01);
            editor.apply();
            return context.getString(R.string.tip01);
        }
        if (last < R.string.tip01 + Constants.TIPS_NUMBER - 1) {
            editor.putInt(LAST_TIPS, last + 1);
            editor.apply();
            return context.getString(last + 1);
        } else {
            editor.putInt(LAST_TIPS, R.string.tip01);
            editor.apply();
            return context.getString(R.string.tip01);
        }
    }

}

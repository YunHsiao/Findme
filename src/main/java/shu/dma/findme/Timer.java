package shu.dma.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Universal Game Timer.
 */
public class Timer extends CountDownTimer {

    private TextActivity text;
    private GraphicActivity graphic;
    private Activity context;
    private Class<?> cls;
    private TextView display;
    private Bundle data;
    private long millisInFuture, millisUntilFinished;
    private int star;
    private int firsthint;   //Number of the Beginning Item of First Hint Area
    private int hint;   //Number of the Beginning Item of Hint Area
    private boolean isText, isFinished, firstHinted, hinted, isQuickFail;

    public Timer(long millisInFuture, long countDownInterval,
                 Activity context, Class<?> cls, TextView display, Bundle data, boolean isText, int targetID) {
        super(millisInFuture, countDownInterval);
        this.millisUntilFinished = millisInFuture;
        this.millisInFuture = millisInFuture;
        this.isText = isText;
        this.cls = cls;
        this.display = display;
        this.context = context;
        firsthint = hintBeginning(targetID, Constants.FIRST_HINT_LINES);
        hint = hintBeginning(targetID, Constants.HINT_LINES);
        if (isText) this.text = (TextActivity) context;
        else this.graphic = (GraphicActivity) context;
        if (data == null) this.data = new Bundle();
        else this.data = data;
        display.setText(millisInFuture / 1000 + "");
    }

    @Override
    public void onTick(long millisUntilFinished) {
        this.millisUntilFinished = millisUntilFinished;
        display.setText(millisUntilFinished / 1000 + "");
        if (millisUntilFinished > (millisInFuture / 3 * 2)) star = 3;
        else if (millisUntilFinished > (millisInFuture / 3)) {
            star = 2;
            if (!firstHinted) {
                if (isText) {
                    ((TextActivity) context).hint(firsthint, Constants.FIRST_HINT_LINES);
                } else {
                    ((GraphicActivity) context).hint(firsthint, Constants.FIRST_HINT_LINES);
                }
                firstHinted = true;
            }
        } else {
            star = 1;
            if (!hinted) {
                if (isText) {
                    ((TextActivity) context).hint(hint, Constants.HINT_LINES);
                } else {
                    ((GraphicActivity) context).hint(hint, Constants.HINT_LINES);
                }
                hinted = true;
            }
        }
    }

    public void Cancel() {
        super.cancel();
        isFinished = true;
    }

    public void onFinish() {
        if (isText) {
            ((TextActivity) context).clearListener();
            if (!isQuickFail && (context.getIntent().getExtras() == null
                    || !context.getIntent().getExtras().getBoolean("free", false))) {
                SharedPreferences.Editor editor = context.getSharedPreferences("data", 0).edit();
                editor.putInt("st_fail", context.getSharedPreferences("data", 0).getInt("st_fail", 0) + 1);
                editor.apply();
            }
        } else {
            ((GraphicActivity) context).clearListener();
            if (!isQuickFail) {
                SharedPreferences.Editor editor = context.getSharedPreferences("data", 0).edit();
                editor.putInt("st_gfail", context.getSharedPreferences("data", 0).getInt("st_gfail", 0) + 1);
                editor.apply();
            }
        }
        display.setText("0");
        isFinished = true;
        if (isText) {
            text.getTarget().setTextColor(Color.GREEN);
            if (context.getIntent().getExtras() != null
                    && context.getIntent().getExtras().getBoolean("free", false)) {
                Intent intent = new Intent(context, cls);
                data = context.getIntent().getExtras();
                data.putString("result", "You lose~");
                data.putBoolean("next", false);
                data.putInt("star", 0);
                intent.putExtras(data);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                context.startActivity(intent);
                context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                context.finish();
            } else {
                Intent intent = new Intent(context, cls);
                data.putString("result", "You lose~");
                data.putBoolean("next", false);
                data.putInt("star", 0);
                intent.putExtras(data);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                context.startActivity(intent);
                context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                context.finish();
            }
        }
        else {
            graphic.getTarget().setColorFilter(Color.GREEN, Mode.MULTIPLY);
            Intent intent = new Intent(context, cls);
            data.putString("result", "You lose~");
            data.putBoolean("next", false);
            data.putInt("star", 0);
            intent.putExtras(data);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            context.startActivity(intent);
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            context.finish();
        }
    }
    public void setQuickFail() { isQuickFail = true; }

    public int getStar() { return star; }

    public boolean isFinished() { return isFinished; }

    public double getTimeLeft() { return (double) (millisInFuture - millisUntilFinished) / 1000; }

    /**
     * get the number of beginner item of specified hint area.
     *
     * @param targetID ID of target item
     * @param lines    height of hint area
     * @return hint
     */
    public int hintBeginning(int targetID, int lines) {
        if (isText) {
            // Current Line Height.
            int CLH = (targetID - R.id.TextView01 + 1) / Constants.TEXT_MATRIX_WIDTH + 1;
            if ((targetID - R.id.TextView01 + 1) % Constants.TEXT_MATRIX_WIDTH == 0) CLH--;
            if (CLH == 1) {
                return 0;
            } else if (CLH == Constants.TEXT_MATRIX_HEIGHT) {
                return (Constants.TEXT_MATRIX_HEIGHT - lines) * Constants.TEXT_MATRIX_WIDTH;
            } else {
                return (CLH - lines + 1) * Constants.TEXT_MATRIX_WIDTH;
            }
        } else {
            int CLH = (targetID - R.id.g01 + 1) / Constants.GRAPHIC_MATRIX_WIDTH + 1;
            if ((targetID - R.id.g01 + 1) % Constants.GRAPHIC_MATRIX_WIDTH == 0) CLH--;
            if (CLH == 1) {
                return 0;
            } else if (CLH == Constants.GRAPHIC_MATRIX_HEIGHT) {
                return (Constants.GRAPHIC_MATRIX_HEIGHT - lines) * Constants.GRAPHIC_MATRIX_WIDTH;
            } else {
                return (CLH - lines + 1) * Constants.GRAPHIC_MATRIX_WIDTH;
            }
        }
    }

}

package shu.dma.findme;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.SparseIntArray;

/**
 * Essentials: Audio Manager.
 */
public class AudioUtil {
    private static MediaPlayer mMediaPlayer;
    private static MediaPlayer gMediaPlayer;
    private static SoundPool soundPool;
    private static AudioManager mgr;
    private static boolean musicRunning = true;
    private static boolean soundRunning = true;
    private static boolean ON = true;
    private static boolean egg = false;
    private static Context mContext;
    private static ActivityManager am;
    private static ComponentName cn;
    private static SparseIntArray soundPoolMap;
    private static final int musicId[] = {R.raw.backgroud, R.raw.game, R.raw.loneliness};
    private static final int soundId[] = {R.raw.f, R.raw.t, R.raw.win, R.raw.lose, R.raw.button};

    protected static void setContext(Context context) { mContext = context; }

    protected static void init(Context context) {
        if (mMediaPlayer == null) {
            mContext = context;
            initMusic();
            initSoundPool();
        }
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    private static void initMusic() {
        mMediaPlayer = MediaPlayer.create(mContext, musicId[0]);
        gMediaPlayer = MediaPlayer.create(mContext, musicId[1]);
        mMediaPlayer.setLooping(true);
        gMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(0.7f, 0.7f);
    }

    protected static void EggFound() {
        egg = true;
        gMediaPlayer = MediaPlayer.create(mContext, musicId[2]);
        gMediaPlayer.setLooping(true);
    }

    private static void initSoundPool() {
        mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new SparseIntArray();
        for (int aSoundId : soundId)
            soundPoolMap.put(aSoundId, soundPool.load(mContext, aSoundId, 1));
    }

    protected static void PlaySound(int resid) {
        if (!soundRunning) {
            return;
        }
        Integer soundId = soundPoolMap.get(resid);
        if (soundRunning) {
            soundPool.play(soundId,
                    mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 1, 0, 1f);
        }
    }

    protected static void PauseMusic() {
        if (am != null) cn = am.getRunningTasks(2).get(0).topActivity;
        //Log.d("Audio", cn.getPackageName());
        if (!cn.getPackageName().equals(Constants.PACKAGE_NAME)) {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
            }
        }
    }

    private static void Pause() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    protected static void PauseGame() {
        cn = am.getRunningTasks(2).get(0).topActivity;
        if (!cn.getPackageName().equals(Constants.PACKAGE_NAME)) {
            if (gMediaPlayer != null) {
                if (gMediaPlayer.isPlaying()) {
                    gMediaPlayer.pause();
                }
            }
        }
    }

    protected static void StopGame() {
        if (egg) gMediaPlayer = MediaPlayer.create(mContext, musicId[2]);
        else gMediaPlayer = MediaPlayer.create(mContext, musicId[1]);
        gMediaPlayer.setLooping(true);
    }

    /*protected static void StopMusic() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            } else if (gMediaPlayer.isPlaying()) {
                gMediaPlayer.stop();
            }
        }
    }/**/

    protected static void PlayMusic() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying() && musicRunning) {
            if (gMediaPlayer.isPlaying()) {
                gMediaPlayer.pause();
            }
        mMediaPlayer.start();
        }
    }

    protected static void PlayGame() {
        if (gMediaPlayer != null && !gMediaPlayer.isPlaying() && musicRunning) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
            gMediaPlayer.start();
        }
    }

    protected static void setMusicRunning(boolean musicRunning) {
        AudioUtil.musicRunning = musicRunning;
    }

    protected static void setSoundRunning(boolean soundRunning) {
        AudioUtil.soundRunning = soundRunning;
    }

    protected static boolean getMusicRunning() {
        return musicRunning;
    }

    protected static boolean getSoundRunning() {
        return soundRunning;
    }

    protected static void musicSwitch() {
        ON = !ON;
        if (!ON) {
            setMusicRunning(false);
            Pause();
        } else {
            setMusicRunning(true);
            PlayMusic();
        }
    }

    protected static void soundSwitch() {
        ON = !ON;
        if (!ON) {
            setSoundRunning(false);
        } else {
            setSoundRunning(true);
        }
    }
}
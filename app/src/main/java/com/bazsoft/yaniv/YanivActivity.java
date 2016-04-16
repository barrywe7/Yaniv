package com.bazsoft.yaniv;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class YanivActivity extends Activity {
    public static final String DEBUG_TAG = Const.DEBUG_TAG;
    public static HashMap<Long, ParsedPlayerInfoDataSet> gFriendData;
    public static ArrayList<ScoreModel> gAllScores;
    public static ArrayList<ScoreModel> gFriendScores;
    /**
     * An array of booleans showing whether a player has attained an
     * achievement.
     */
    public static boolean[] mAchievements = new boolean[Achievement.values().length];
    protected AppPreferences mAppPrefs;
    protected FacebookApplication mFacebook;

    private static boolean isConnectedNetwork(NetworkInfo info) {
        return info != null && info.isAvailable() && NetworkInfo.State.CONNECTED == info.getState();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (gFriendData == null) {
            gFriendData = new HashMap<>();
        }
        mAppPrefs = AppPreferences.getInstance(getApplicationContext());
        toggleMute(mAppPrefs.getMute());
        String language = mAppPrefs.getLanguage();
        if (!language.equals("default")) {
            //changeLocale(language);
        }

    }

    public void changeLocale(String language) {
        Locale locale;
        if (language.equals("default")) {
            locale = Locale.getDefault();
        } else {
            locale = new Locale(language);
        }
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

    }

    public void toggleMute(boolean mute) {
        SoundManager.toggleMute(mute);
        if (mute) {
            setVolumeControlStream(AudioManager.STREAM_RING);
        } else {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    public File getPlayerAvatarFile(long playerId) {
        return getBaseContext().getFileStreamPath(playerId + ".jpg");
    }

    public boolean doesPlayerAvatarFileExist(long playerId) {
        final File imageFile = getPlayerAvatarFile(playerId);
        return (imageFile.exists() && (imageFile.lastModified() + (2 * Const.WEEK)) > System.currentTimeMillis());

    }

    public Drawable getPlayerAvatarDrawable(long playerId) {
        FileInputStream fis;
        Drawable drawable = null;
        try {
            fis = openFileInput(playerId + ".jpg");
            drawable = new BitmapDrawable(BitmapFactory.decodeFileDescriptor(fis.getFD()));
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (drawable == null) {
            drawable = getResources().getDrawable(R.drawable.avatar_not_set);
        }
        return drawable;
    }

    public boolean isNetworkConnection() {
        return isConnectedNetwork(((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo());
    }
}
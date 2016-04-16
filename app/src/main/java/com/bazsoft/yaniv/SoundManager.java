package com.bazsoft.yaniv;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;

public class SoundManager {

    public static final String DEBUG_TAG = "Sound Manager Log";
    private static SoundManager _instance;
    private static SoundPool mSoundPool;
    private static HashMap<SoundEffect, Integer> mSoundPoolMap = new HashMap<>();
    private static AudioManager mAudioManager;
    private static Context mContext;
    private static boolean mMute;

    private SoundManager() {
    }

    /**
     * Requests the instance of the Sound Manager and creates it if it does not
     * exist.
     *
     * @return Returns the single instance of the SoundManager
     */
    public static synchronized SoundManager getInstance() {
        if (_instance == null) {
            _instance = new SoundManager();
        }
        return _instance;
    }

    public static void toggleMute(boolean mute) {
        mMute = mute;
    }


    /**
     * Initialises the storage for the sounds
     *
     * @param context The Application context
     */
    public static void initSounds(final Context context) {
        mContext = context;
        if (mSoundPool == null) {
            mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
            mSoundPoolMap = new HashMap<>();
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            loadSounds();
        }
    }

    /**
     * Loads all the sound effects.
     */
    private static void loadSounds() {
        for (final SoundEffect sound : SoundEffect.values()) {
            int id = mContext.getResources().getIdentifier(sound.resourceName, "raw", "com.bazsoft.yaniv");
            mSoundPoolMap.put(sound, mSoundPool.load(mContext, id, 1));
        }
    }

    /**
     * Plays a Sound
     *
     * @param sound - The SoundEffect to be played
     */
    public static void playSound(final SoundEffect sound) {
        float streamVolume = 0.0f;
        try {
            streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        } catch (NullPointerException e) {
            Log.e(DEBUG_TAG, "Null pointer returned from audio manager");
        }

        if (streamVolume == 0.0f || mMute) {
            return;
        }
        final Integer soundToPlay = mSoundPoolMap.get(sound);
        if (soundToPlay != null) {
            mSoundPool.play(mSoundPoolMap.get(sound), streamVolume, streamVolume, 1, 0, 1.0f);
        } else {
            Log.e(DEBUG_TAG, sound.toString() + " not found in sound pool map");
        }

    }

    /**
     * Stop a Sound
     *
     * @param sound - the sound to be stopped
     */
    public static void stopSound(SoundEffect sound) {
        mSoundPool.stop(mSoundPoolMap.get(sound));
    }

    /**
     * Deallocates the resources and Instance of SoundManager
     */
    public static void cleanup() {
        mSoundPool.release();
        mSoundPool = null;
        mSoundPoolMap.clear();
        mAudioManager.unloadSoundEffects();
        _instance = null;

    }


}

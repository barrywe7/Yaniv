package com.bazsoft.yaniv;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

/**
 * Updates all the information about a player as a background Asynchronous Task
 * except for their avatar.
 *
 * @author Barry Irvine
 */
public class AccountTask extends AsyncTask<Object, String, Boolean> {

    /**
     * A link to the Shared Preferences of the application.
     */
    protected static AppPreferences mAppPrefs;
    /**
     * The context that calls the background task.
     */
    private static Context mContext;
    /**
     * The progress bar view used to display when the background task is busy.
     */
    private static ProgressBar mProgressBar;

    /**
     * Defines a new Account Task for the given context and progress bar. The
     * shared preferences are also defined.
     *
     * @param context     The context of the parent activity.
     * @param progressBar The progress bar used to show when the background task is
     *                    busy.
     */
    public AccountTask(Context context, ProgressBar progressBar) {
        mContext = context;
        mAppPrefs = AppPreferences.getInstance(mContext);
        mProgressBar = progressBar;
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        Boolean succeeded = false;

        Integer playerId = mAppPrefs.getPlayerId();
        String name = mAppPrefs.getPreferencesPlayerName();
        Integer played = mAppPrefs.getPlayed();
        Integer won = mAppPrefs.getWon();
        Integer rating = mAppPrefs.getRating();
        String achievements = mAppPrefs.getAchievements();
        String email = mAppPrefs.getEmail();
        // If the email is set encrypt it with SHA
        if (!email.equals("")) {
            try {
                MessageDigest sha = MessageDigest.getInstance("SHA");

                byte[] enc = sha.digest(email.getBytes());
                StringBuilder sb = new StringBuilder();

                for (byte enc1 : enc) {
                    sb.append(Integer.toHexString(enc1 & 0xFF));
                }
                email = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                Log.w(Const.DEBUG_TAG, "Failed to get SHA, using hashcode()");
                email = String.valueOf(email.hashCode());
            }
        }

        Vector<NameValuePair> vars = new Vector<>();

        if (playerId == -1) {
            String uniqueId;
            /*
             * if we don't have a playerId yet, we must pass up a uniqueId. A
			 * good place to get a unique identifier from is the Device Id which
			 * is conveniently stored in the TelephonyManager data. This
			 * requires the use of READ_PHONE_STATE permission
			 */
            final TelephonyManager telManager = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            uniqueId = telManager.getDeviceId();
            /*
             * If we didn't get a device id - perhaps this a tablet with no
			 * phone - then use the Android Id.
			 */
            if (uniqueId == null) {
                uniqueId = Secure.getString(mContext.getContentResolver(),
                        Secure.ANDROID_ID);
            }

            // hash the value to get a unique, but non-identifiable value to use
            String mdUniqueId;
            try {
                MessageDigest sha = MessageDigest.getInstance("SHA");

                byte[] enc = sha.digest(uniqueId.getBytes());
                StringBuilder sb = new StringBuilder();

                for (byte enc1 : enc) {
                    sb.append(Integer.toHexString(enc1 & 0xFF));
                }
                mdUniqueId = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                Log.w(Const.DEBUG_TAG, "Failed to get SHA, using hashcode()");
                mdUniqueId = String.valueOf(uniqueId.hashCode());
            }
            vars.add(new BasicNameValuePair("uniqueId", mdUniqueId));

        } else {
            // otherwise, we use the playerId to update data
            try {
                vars.add(new BasicNameValuePair("updateId", AESencryption
                        .encrypt(playerId.toString())));
            } catch (Exception e) {
                Log.e(Const.DEBUG_TAG, e.getMessage());
            }
        }
        try {
            vars.add(new BasicNameValuePair("name", AESencryption.encrypt(name)));
            vars.add(new BasicNameValuePair("played", AESencryption
                    .encrypt(played.toString())));
            vars.add(new BasicNameValuePair("won", AESencryption.encrypt(won
                    .toString())));
            vars.add(new BasicNameValuePair("rating", AESencryption
                    .encrypt(rating.toString())));
            vars.add(new BasicNameValuePair("achievements", AESencryption
                    .encrypt(achievements)));
            vars.add(new BasicNameValuePair("email", AESencryption
                    .encrypt(email)));
        } catch (Exception e) {
            Log.e(Const.DEBUG_TAG, e.getMessage());
        }
        String url = Const.YANIV_SERVER_ACCOUNT_EDIT + "?"
                + URLEncodedUtils.format(vars, "utf8");

        HttpGet request = new HttpGet(url);

        try {

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpClient client = new DefaultHttpClient();
            String responseBody = client.execute(request, responseHandler);

            if (responseBody != null && responseBody.length() > 0) {
                Integer resultId = Integer.parseInt(responseBody);
                mAppPrefs.setPlayerId(resultId);
                succeeded = true;
                if (resultId != -1) {
                    mAppPrefs.setServerUpdated(true);
                }
            }

        } catch (ClientProtocolException e) {
            Log.e(Const.DEBUG_TAG, "Failed to get playerId (protocol): ", e);
        } catch (IOException e) {
            Log.e(Const.DEBUG_TAG, "Failed to get playerId (io): ", e);
        } catch (NumberFormatException e) {
            Log.e(Const.DEBUG_TAG,
                    "Response string did not just contain a number");
        }
        return succeeded;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPreExecute() {
        mProgressBar.setVisibility(View.VISIBLE);
    }
}
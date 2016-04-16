package com.bazsoft.yaniv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class FacebookApplication extends YanivActivity implements
        DialogListener, RequestListener {
    public static final int FACEBOOK_AUTH_REQUEST = 32665;
    private static final Facebook facebookClient = new Facebook(
            "245421998814180");
    private static FacebookApplication _instance;
    private static Context mContext;
    private String mWallMessage;
    private Activity mActivity;

    /**
     * Creates a new Facebook Application for the given context. Since this is
     * private it can only be obtained via the getInstance method.
     */
    private FacebookApplication() {
        mAppPrefs = AppPreferences.getInstance(mContext);

    }

    /**
     * Requests the instance of the Facebook Application and creates it if it
     * does not exist.
     *
     * @return Returns the single instance of the Facebook Application
     */
    public static synchronized FacebookApplication getInstance(Context context) {
        if (_instance == null)
            _instance = new FacebookApplication();
        mContext = context;
        return _instance;
    }

    public Facebook getFacebook() {
        return facebookClient;
    }

    /**
     * Authorises a Facebook session for the application. It requests the
     * permission to write on the wall.
     *
     * @param activity The activity that the dialog should be run in.
     */
    public void authoriseFacebook(Activity activity) {
        String token = mAppPrefs.getFacebookToken();
        long expires = mAppPrefs.getFacebookTokenExpires();
        if (token != null && expires != -1) {
            facebookClient.setAccessToken(token);
            facebookClient.setAccessExpires(expires);
        }

        // Either we didn't have a token stored or it has expired
        if (!facebookClient.isSessionValid()) {
            if (token != null) {
                facebookClient.authorize(activity, this);
            } else {
                facebookClient.authorize(activity,
                        new String[]{"publish_stream"}, this);
            }
        }
    }

    public void postToWall(String message, Activity activity) {
        mWallMessage = message;
        mActivity = activity;

        Bundle parameters = new Bundle();
        parameters.putString("message", message);
        parameters.putString("link",
                mContext.getResources().getString(R.string.game_url));
        if (!facebookClient.isSessionValid()) {
            facebookClient.authorize(activity,
                    new String[]{"publish_stream"}, this);
        }
        if (facebookClient.isSessionValid()) {
            AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(
                    facebookClient);
            mAsyncRunner.request("me/feed", parameters, "POST", this, null);
            mWallMessage = null;
            mActivity = null;
        }
    }

    @Override
    public void onComplete(Bundle values) {
        // Connected to Facebook successfully
        Toast.makeText(mContext, "Logged in to Facebook", Toast.LENGTH_LONG)
                .show();
        mAppPrefs.saveFacebookToken(facebookClient.getAccessToken(),
                facebookClient.getAccessExpires());
        // This happens when we tried to post to the wall but the old token had
        // expired
        if (mWallMessage != null) {
            postToWall(mWallMessage, mActivity);
        }
    }

    @Override
    public void onFacebookError(FacebookError facebookError) {
        // This happens when trying to connect to Facebook and (for example) the
        // key is incorrect.
        mAppPrefs.setFacebookEnabled(false);
        Toast.makeText(mContext, "Facebook error " + facebookError.toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(DialogError dialogError) {
        mAppPrefs.setFacebookEnabled(false);
        if (dialogError.getMessage().equals(
                "The connection to the server was unsuccessful")) {
            Toast.makeText(
                    mContext,
                    "Unable to make a connection to Facebook. Please check your network settings.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, dialogError.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCancel() {
        // This happens if user presses back before they completed Facebook
        // authorisation.
    }

    @Override
    public void onComplete(String response, Object state) {
        // The on complete apparently doesn't mean it was error free
        // We need to parse the result to see if we posted to the wall okay.
        try {
            Util.parseJson(response);
            FacebookApplication.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,
                            "Message posted successfully to Facebook",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            Log.w(DEBUG_TAG, "JSON Error in response");
        } catch (FacebookError e) {
            Log.w(DEBUG_TAG, "Facebook Error: " + e.getMessage());
            if (e.getErrorType().equals("OAuthException")) {
                // This happens when the user has removed permissions from Yaniv
                // in Facebook
                FacebookApplication.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                mContext,
                                "Unable to post Facebook message. Please re-enable permissions on Settings screen.",
                                Toast.LENGTH_LONG).show();
                        mAppPrefs.saveFacebookToken("", (long) -1);
                        mAppPrefs.setFacebookEnabled(false);
                    }
                });
            }
        }
    }

    @Override
    public void onIOException(final IOException error, Object state) {
        // This happens when posting a message and there is not valid Internet
        // connection.
        FacebookApplication.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        mContext,
                        "Unable to connect to Facebook server. Please check your network settings.",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onFileNotFoundException(final FileNotFoundException error,
                                        Object state) {
        // Not sure when this happens
        FacebookApplication.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        mContext,
                        "File not found error " + error.toString()
                                + ". Please contact the developer.",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onMalformedURLException(final MalformedURLException error,
                                        Object state) {
        // Not sure when this happens
        FacebookApplication.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        mContext,
                        "Malformed URL error " + error.toString()
                                + ". Please contact the developer.",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onFacebookError(FacebookError facebookError, Object state) {
        Toast.makeText(mContext, "Facebook error " + facebookError.toString(),
                Toast.LENGTH_LONG).show();

    }

}

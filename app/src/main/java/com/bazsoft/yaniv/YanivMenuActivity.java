package com.bazsoft.yaniv;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class YanivMenuActivity extends YanivActivity implements OnItemClickListener, OnClickListener, RadioGroup.OnCheckedChangeListener {

    /**
     * The maximum time between two back key presses before the application will exit.
     */
    private final static long BACK_EXIT_THRESHOLD = 3000; // 3 seconds
    private static final int ALERT_NEW_GAME = 0;
    private static final int ALERT_NEW_GAME_PLAYERS = 1;
    private static Toast exitWarning;
    private static String mDialogMessage;
    /**
     * The time since the back key was pressed
     */
    private long mLastBackExitPress = 0;
    private AccountTask mAccountUpload;

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        AdView adView = (AdView) this.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppPrefs = AppPreferences.getInstance(getApplicationContext());
        initSounds();
        populateMenu();
        exitWarning = Toast.makeText(this, R.string.exit_using_back_warning, Toast.LENGTH_SHORT);
        if (mAppPrefs.isFirstTime()) {
            startActivity(new Intent(YanivMenuActivity.this, YanivSettingsActivity.class));
            mAppPrefs.setFirstTime(false);
        }
    }

    private int computePenalty() {
        int penalty = 0;
        int playersIn = 0;
        boolean applyPenalty = false;
        int numPlayers = mAppPrefs.getGameNumPlayers();
        int player1Score = mAppPrefs.getPlayerScore(1);
        if (player1Score < 200) {
            // First calculate how many players are still in
            for (int i = 2; i <= numPlayers; i++) {
                if (mAppPrefs.getPlayerScore(i) < 200) {
                    playersIn++;
                }
            }
            // Now calculate penalty
            for (int i = 2; i <= numPlayers; i++) {
                int score = mAppPrefs.getPlayerScore(i);
                if (score < 200) {
                    penalty += ((201 - score) / (numPlayers - playersIn));
                }
                if (score > 0) {
                    applyPenalty = true;
                }
            }
        }
        if (penalty > 200) {
            penalty = 200;
        }

        return applyPenalty ? penalty : 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FacebookApplication.FACEBOOK_AUTH_REQUEST:
                mFacebook.getFacebook().authorizeCallback(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initYanivMinimum(View newGameDialogLayout) {
        final RadioGroup radioGroup = (RadioGroup) newGameDialogLayout.findViewById(R.id.RadioGroup_Yaniv_RadioButtons);
        final int minimum = mAppPrefs.getYanivMinimum();
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View view = radioGroup.getChildAt(i);
            if (Integer.parseInt((String) view.getTag()) == minimum) {
                radioGroup.check(view.getId());
            }

        }
        radioGroup.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SoundManager.playSound(SoundEffect.CLICK);
        final int score = Integer.parseInt((String) group.findViewById(checkedId).getTag());
        mAppPrefs.setYanivMinimum(score);
    }

    private void initSounds() {
        SoundManager.getInstance();
        SoundManager.initSounds(this);
    }

    private void populateMenu() {
        GridView menuList = (GridView) findViewById(R.id.GridView_Menu);
        ArrayList<String> items = new ArrayList<>();
        if (mAppPrefs.isSavedGame()) {
            items.add(getResources().getString(R.string.menu_item_resume));
        }
        if (mAppPrefs.isPlayer1ScoreUpToDate()) {
            items.add(getResources().getString(R.string.menu_item_new_game));
        }
        items.add(getResources().getString(R.string.menu_item_tutorial));
        items.add(getResources().getString(R.string.menu_item_settings));
        items.add(getResources().getString(R.string.menu_item_achievements));

        ArrayAdapter<String> adapt = new ArrayAdapter<>(this, R.layout.menu_item, items);
        menuList.setAdapter(adapt);

        menuList.setOnItemClickListener(this);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case ALERT_NEW_GAME:
                ((CustomDialog) dialog).setMessage(mDialogMessage);
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
        TextView textView = (TextView) itemClicked;
        String strText = textView.getText().toString();
        SoundManager.playSound(SoundEffect.CLICK);
        Intent intent;
        if (strText.equalsIgnoreCase(getResources().getString(R.string.menu_item_resume))) {
            // Launch the game activity
            intent = new Intent(getApplicationContext(), YanivGameActivity.class);
            intent.putExtra("com.bazsoft.yaniv.MODE", GameMode.RESUME.ordinal());
            startActivity(intent);
        } else if (strText.equalsIgnoreCase(getResources().getString(R.string.menu_item_new_game))) {
            int penalty = computePenalty();
            if (penalty > 0) {
                mDialogMessage = String.format(getResources().getString(R.string.alert_new_game), penalty);
                showDialog(ALERT_NEW_GAME);
            } else {
                showDialog(ALERT_NEW_GAME_PLAYERS);
            }
        } else if (strText.equalsIgnoreCase(getResources().getString(R.string.menu_item_tutorial))) {
            intent = new Intent(getApplicationContext(), YanivGameActivity.class);
            intent.putExtra("com.bazsoft.yaniv.MODE", GameMode.TUTORIAL.ordinal());
            startActivity(intent);
        } else if (strText.equalsIgnoreCase(getResources().getString(R.string.menu_item_settings))) {
            // Launch the settings activity
            startActivity(new Intent(YanivMenuActivity.this, YanivSettingsActivity.class));
        } else if (strText.equalsIgnoreCase(getResources().getString(R.string.menu_item_achievements))) {
            startActivity(new Intent(YanivMenuActivity.this, YanivAchievementsActivity.class));
        }
    }

    /**
     * Creates the various dialogs
     */
    @Override
    protected Dialog onCreateDialog(final int id) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        switch (id) {
            case ALERT_NEW_GAME:
                builder.setTitle(R.string.alert_title_new_game).setMessage("").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SoundManager.playSound(SoundEffect.CLICK);
                        dialog.cancel();
                        mAppPrefs.deductRating(computePenalty());
                        updateServerData();
                        mAppPrefs.deleteWonPlayerGames();
                        showDialog(ALERT_NEW_GAME_PLAYERS);
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SoundManager.playSound(SoundEffect.CLICK);
                        dialog.cancel();
                    }
                });
                break;
            case ALERT_NEW_GAME_PLAYERS:
                final View newGameDialogLayout = LayoutInflater.from(this).inflate(R.layout.new_game, (ViewGroup) findViewById(R.id.root), true);
                final Spinner playerSpinner = (Spinner) newGameDialogLayout.findViewById(R.id.Spinner_NumPlayers);

                ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.num_players, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                playerSpinner.setAdapter(adapter);
                playerSpinner.setSelection(mAppPrefs.getPreferencesNumPlayers() - 2);
                playerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                        mAppPrefs.setPreferencesNumPlayers(selectedItemPosition + 2);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
                initYanivMinimum(newGameDialogLayout);

                builder.setTitle(R.string.alert_title_new_game).setView(newGameDialogLayout).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SoundManager.playSound(SoundEffect.CLICK);
                        dialog.cancel();
                        Intent intent = new Intent(getApplicationContext(), YanivGameActivity.class);
                        intent.putExtra("com.bazsoft.yaniv.MODE", GameMode.NEW.ordinal());
                        startActivity(intent);
                    }
                });

        }
        return builder.create();

    }

    /**
     * update the server with the latest settings data - everything but the image
     */
    private void updateServerData() {
        // make sure we don't collide with another pending update
        if (mAccountUpload == null || mAccountUpload.getStatus() == AsyncTask.Status.FINISHED || mAccountUpload.isCancelled()) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.ProgressBar_Menu);
            mAccountUpload = new AccountTask(getApplicationContext(), progressBar);
            mAccountUpload.execute();
        } else {
            Log.w(DEBUG_TAG, "Warning: update task already in progress");
        }
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"yaniv.bazsoft@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Please fix this");
        emailIntent.setType("plain/text");
        ApplicationInfo appInfo = null;
        StringBuilder sb = new StringBuilder();
        final PackageManager pm = this.getPackageManager();
        final String packageName = this.getPackageName();
        try {
            appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            Log.e(DEBUG_TAG, "Unable to get package name");
        }
        sb.append("Hi,\nI found this bug or would like to suggest this improvement: \n\n\n\n\n==========\n");
        sb.append(appInfo.loadLabel(pm)).append(" ").append(BuildConfig.VERSION_NAME).append("\n"); // E.g. Yaniv 1.0
        sb.append("Android release: ").append(Build.VERSION.RELEASE).append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n");
        sb.append("Brand: ").append(Build.BRAND).append(" Device:").append(Build.DEVICE).append("\n");
        emailIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());

        startActivity(Intent.createChooser(emailIntent, "Send your email using..."));
    }

    @Override
    public void onClick(View view) {
        SoundManager.playSound(SoundEffect.CLICK);
        switch (view.getId()) {
            case R.id.ImageView_Star:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.market_url))));
                break;
            case R.id.ImageView_Email:
                sendEmail();
                break;
            case R.id.ImageView_Help:
                startActivity(new Intent(YanivMenuActivity.this, YanivHelpActivity.class));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long now = System.currentTimeMillis();
            long sinceLastBackExitPress = now - mLastBackExitPress;

            if (sinceLastBackExitPress < BACK_EXIT_THRESHOLD) {
                exitWarning.cancel();
                finish();
            } else {
                mLastBackExitPress = now;
                exitWarning.show();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
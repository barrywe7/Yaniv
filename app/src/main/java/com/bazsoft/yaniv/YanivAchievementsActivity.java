package com.bazsoft.yaniv;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Defines the Achievements and Scores screen
 *
 * @author Barry Irvine
 */
public class YanivAchievementsActivity extends YanivActivity {

    /**
     * The alert used to check with the user if they wish to reset their achievements.
     */
    private static final int ALERT_RESET_ACHIEVEMENTS = 0;
    /**
     * The alert used to check with the user if they wish to reset their rating.
     */
    private static final int ALERT_RESET_RATING = 1;
    /**
     * The asynchronous task used to download all the scores (top 25).
     */
    private static ScoreDownloaderTask allScoresDownloader;
    /**
     * The asynchronous task used to download a player's friends scores.
     */
    private static ScoreDownloaderTask friendScoresDownloader;
    /**
     * A collection of tasks used to download the avatars associated with players.
     */
    private static ArrayList<ImageDownloadTask> imageDownloader;
    /**
     * The list view used to display the achievements.
     */
    private static GridView mAchievementsList;
    /**
     * The current position on screen within the achievements list view. This is used so that if the user changes the screen orientation etc. they still appear on
     * the same row of the list.
     */
    private static int mCurrentPosition = 0;
    /**
     * The current tab that is open on the screen. E.g. achievements, all scores or friend scores.
     */
    private static Integer mCurrentTab;
    /**
     * The asynchronous task used to upload a player's details.
     */
    private static AccountTask mAccountUpload;
    /**
     *
     */
    private static boolean doneFriendScores;
    /**
     * A link to the shared preferences.
     */
    protected AppPreferences mAppPrefs;

    /**
     * Defines the three tabs used on the achievements screen.
     */
    private void defineTabs() {
        final TabHost host = (TabHost) findViewById(R.id.TabHost1);
        host.setup();
        // Achievements tab
        final TabSpec achievementsTab = host.newTabSpec("achievementsTab");
        final View achievementsIndicator = getLayoutInflater().inflate(R.layout.achievement_tab_indicator, host.getTabWidget(), false);
        final TextView achievementsTitle = (TextView) achievementsIndicator.findViewById(R.id.title);
        achievementsTitle.setText(R.string.achievements);
        achievementsTab.setIndicator(achievementsIndicator);
        achievementsTab.setContent(R.id.GridView_Achievements);
        host.addTab(achievementsTab);
        // All Scores tab
        final TabSpec allScoresTab = host.newTabSpec("allTab");
        final View allScoresIndicator = getLayoutInflater().inflate(R.layout.score_tab_indicator, host.getTabWidget(), false);
        final TextView allScoresTitle = (TextView) allScoresIndicator.findViewById(R.id.title);
        allScoresTitle.setText(R.string.all_scores);
        allScoresTab.setIndicator(allScoresIndicator);
        allScoresTab.setContent(R.id.GridView_AllScores);
        host.addTab(allScoresTab);
        // Friends Scores tab
        final TabSpec friendScoresTab = host.newTabSpec("friendsTab");
        final View friendScoresIndicator = getLayoutInflater().inflate(R.layout.score_tab_indicator, host.getTabWidget(), false);
        final TextView friendScoresTitle = (TextView) friendScoresIndicator.findViewById(R.id.title);
        friendScoresTitle.setText(R.string.friends_scores);
        friendScoresTab.setIndicator(friendScoresIndicator);
        friendScoresTab.setContent(R.id.GridView_FriendScores);
        host.addTab(friendScoresTab);
        // Set the default tab
        if (mCurrentTab != null) {
            host.setCurrentTab(mCurrentTab);
        } else {
            host.setCurrentTabByTag("achievementsTab");
        }

        GridView allScoresView = (GridView) findViewById(R.id.GridView_AllScores);
        GridView friendScoresView = (GridView) findViewById(R.id.GridView_FriendScores);

        imageDownloader = new ArrayList<>();
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.ProgressBar_Achievements);
        if (gAllScores == null || gAllScores.size() == 0 && isNetworkConnection()) {
            allScoresDownloader = new ScoreDownloaderTask(progressBar, allScoresView);
            allScoresDownloader.execute(Const.YANIV_SERVER_SCORES);
        } else {
            ScoreArrayAdapter adapter = new ScoreArrayAdapter(YanivAchievementsActivity.this, R.layout.score_row, gAllScores);
            allScoresView.setAdapter(adapter);
        }

        final int playerId = mAppPrefs.getPlayerId();

        if (playerId != -1 && gFriendScores != null && gFriendScores.size() > 0) {
            final ScoreArrayAdapter adapter = new ScoreArrayAdapter(YanivAchievementsActivity.this, R.layout.score_row, gFriendScores);
            friendScoresView.setAdapter(adapter);
        } else if (playerId != -1 && gFriendScores == null && !doneFriendScores && isNetworkConnection()) {
            friendScoresDownloader = new ScoreDownloaderTask(progressBar, friendScoresView);
            friendScoresDownloader.execute(Const.YANIV_SERVER_SCORES + "?playerId=" + playerId);
        } else {
            final ArrayList<String> items = new ArrayList<>();
            items.add(getResources().getString(R.string.no_scores));
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(YanivAchievementsActivity.this, R.layout.no_scores, items);
            friendScoresView.setAdapter(adapter);
            friendScoresView.setColumnWidth(getResources().getDisplayMetrics().widthPixels);
        }

    }

    /**
     * Define the row showing the current player's rating
     */
    private void initRatingRow() {
        TextView textview;
        textview = (TextView) findViewById(R.id.TextView_Player_Name);
        textview.setText(mAppPrefs.getGamePlayerName());
        textview = (TextView) findViewById(R.id.TextView_Player_Played);
        int played = mAppPrefs.getPlayed();
        textview.setText(Integer.toString(played));
        textview = (TextView) findViewById(R.id.TextView_Player_Rating);
        textview.setText(Integer.toString(mAppPrefs.getRating()));
        textview = (TextView) findViewById(R.id.TextView_Player_Won);
        int won = mAppPrefs.getWon();
        int pct = 0;
        if (played > 0) {
            pct = (won * 100 / played);
        }
        textview.setText(won + " (" + Integer.toString(pct) + "%)");
    }

    /**
     * Creates the various dialogs
     */
    @Override
    protected Dialog onCreateDialog(final int id) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        switch (id) {
            case ALERT_RESET_ACHIEVEMENTS:
                builder.setMessage(R.string.alert_reset_achievements).setTitle(R.string.alert_title_reset_achievements)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                for (int i = 0; i < mAchievements.length; i++) {
                                    mAchievements[i] = false;
                                }
                                mAppPrefs.saveAchievements(mAchievements);
                                updateServerData();
                                populateAchievements();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                break;
            case ALERT_RESET_RATING:
                builder.setMessage(R.string.alert_reset_rating).setTitle(R.string.alert_title_reset_rating)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                mAppPrefs.resetRating();
                                updateServerData();
                                initRatingRow();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                break;
        }
        return builder.create();
    }

    /**
     * Creates the options menu when the menu button is pressed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.achievementoptions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.reset_achievements_menu_item:
                showDialog(ALERT_RESET_ACHIEVEMENTS);
                return true;
            case R.id.reset_rating_menu_item:
                showDialog(ALERT_RESET_RATING);
                return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentPosition = mAchievementsList.getFirstVisiblePosition();

        TabHost host = (TabHost) findViewById(R.id.TabHost1);
        mCurrentTab = host.getCurrentTab();

    /*
     * Cancel any asynch tasks still in progress.
     */
        if (allScoresDownloader != null && allScoresDownloader.getStatus() != AsyncTask.Status.FINISHED) {
            allScoresDownloader.cancel(true);
        }
        if (friendScoresDownloader != null && friendScoresDownloader.getStatus() != AsyncTask.Status.FINISHED) {
            friendScoresDownloader.cancel(true);
        }
        if (mAccountUpload != null && mAccountUpload.getStatus() != AsyncTask.Status.FINISHED) {
            mAccountUpload.cancel(true);
        }
        for (ImageDownloadTask imageDownload : imageDownloader) {
            if (imageDownload != null && imageDownload.getStatus() != AsyncTask.Status.FINISHED) {
                imageDownload.cancel(true);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.achievement);
        mAppPrefs = AppPreferences.getInstance(getApplicationContext());
        defineTabs();
        initRatingRow();
        populateAchievements();
        if (!mAppPrefs.isServerUpdated()) {
            Log.d(DEBUG_TAG, "Server not up to date. Kicking off an update");
            updateServerData();
        }

    }

    private void populateAchievements() {
        mAppPrefs.updateAchievements(mAchievements);
        mAchievementsList = (GridView) findViewById(R.id.GridView_Achievements);

    /*
     * Loop through each achievement to see if player has one.
     */
        ArrayList<AchievementModel> items = new ArrayList<>();
        for (Achievement achievement : Achievement.values()) {
            AchievementModel item = new AchievementModel(mAchievements[achievement.ordinal()], achievement.title, achievement.description);
            items.add(item);
        }

        AchievementArrayAdapter adapter = new AchievementArrayAdapter(this, R.layout.achievement_row, items);
        mAchievementsList.setAdapter(adapter);
        if (mCurrentPosition != 0) {
            mAchievementsList.setSelection(mCurrentPosition);
        }
    }

    /**
     * update the server with the latest settings data - everything but the image
     */
    private void updateServerData() {
        // make sure we don't collide with another pending update
        if (mAccountUpload == null || mAccountUpload.getStatus() == AsyncTask.Status.FINISHED || mAccountUpload.isCancelled()) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.ProgressBar_Achievements);
            mAccountUpload = new AccountTask(getApplicationContext(), progressBar);
            mAccountUpload.execute();
        } else {
            Log.w(DEBUG_TAG, "Warning: update task already in progress");
        }
    }

    /**
     * Defines the achievements array so that it can be displayed in a view correctly.
     *
     * @author Barry Irvine
     */
    private class AchievementArrayAdapter extends ArrayAdapter<AchievementModel> {

        private final int layoutRow;
        private final LayoutInflater inflater;
        /**
         * An array of all the values to be displayed.
         */
        private final ArrayList<AchievementModel> values;

        public AchievementArrayAdapter(Activity activity, int layoutRow, ArrayList<AchievementModel> values) {
            super(activity, layoutRow, values);
            this.inflater = activity.getLayoutInflater();
            this.layoutRow = layoutRow;
            this.values = values;
        }

        /**
         * Defines the values for each achievement within the view.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View rowView = inflater.inflate(layoutRow, null, true);
            TextView title = (TextView) rowView.findViewById(R.id.TextView_TrophyTitle);
            title.setText(values.get(position).title);

            TextView description = (TextView) rowView.findViewById(R.id.TextView_TrophyDescription);
            description.setText(values.get(position).description);

      /*
       * If the player has got the trophy then display it fully otherwise display the faded image.
       */
            ImageView trophy = (ImageView) rowView.findViewById(R.id.ImageView_Trophy);
            if (values.get(position).trophy)
                trophy.setImageResource(R.drawable.trophy);
            else
                trophy.setImageResource(R.drawable.trophy_fade);

            return rowView;
        }

    }

    /**
     * Defines all the attributes necessary to display an achievement.
     *
     * @author Barry Irvine
     */
    private class AchievementModel {

        /**
         * The full textual description of an achievement.
         */
        private String description;
        /**
         * The title of an achievement.
         */
        private String title;
        /**
         * Whether the player has obtained this trophy or not yet.
         */
        private Boolean trophy;

        public AchievementModel(Boolean trophy, String title, String description) {
            this.trophy = trophy;
            this.title = title;
            this.description = description;
        }

    }

    /**
     * Defines the score array so that it can be displayed in a view correctly.
     *
     * @author Barry Irvine
     */
    private class ScoreArrayAdapter extends ArrayAdapter<ScoreModel> {

        private final int layoutRow;
        private final LayoutInflater inflater;
        /**
         * An array of all the values to be displayed.
         */
        private final ArrayList<ScoreModel> values;

        public ScoreArrayAdapter(Activity activity, int layoutRow, ArrayList<ScoreModel> values) {
            super(activity, layoutRow, values);
            this.inflater = activity.getLayoutInflater();
            this.layoutRow = layoutRow;
            this.values = values;
        }

        /**
         * Defines the values for each player within the view.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View rowView = inflater.inflate(layoutRow, null, true);

            TextView rank = (TextView) rowView.findViewById(R.id.TextView_PlayerRank);
            rank.setText(values.get(position).rank.toString() + ". ");
            TextView name = (TextView) rowView.findViewById(R.id.TextView_PlayerName);
            name.setText(values.get(position).playerName);

            TextView rating = (TextView) rowView.findViewById(R.id.TextView_PlayerRating);
            rating.setText(values.get(position).rating.toString());

            TextView achievements = (TextView) rowView.findViewById(R.id.TextView_PlayerAchievements);
            achievements.setText(values.get(position).achievements.toString());

            long playerId = values.get(position).playerId;
            ImageView avatarView = (ImageView) rowView.findViewById(R.id.ImageView_Avatar);
      /*
       * If it is the current player then use the avatar image from the shared preferences.
       */
            if (playerId == mAppPrefs.getPlayerId()) {
                avatarView.setBackgroundDrawable(getResources().getDrawable(R.drawable.avatar_shape_me));
                String strAvatarUri = mAppPrefs.getAvatar();
                if (strAvatarUri != null) {
                    Uri imageUri = Uri.parse(strAvatarUri);
                    avatarView.setImageURI(imageUri);
                } else {
                    // The user hasn't defined an avatar.
                    avatarView.setImageResource(R.drawable.avatar_not_set);
                }
        /*
         * If the player's image isn't already known and they have one stored then we need to download it.
         */
            } else if (!values.get(position).avatarUrl.equals(Const.YANIV_SERVER_EMPTY_URL)) {
                if (doesPlayerAvatarFileExist(playerId)) {
                    avatarView.setImageDrawable(getPlayerAvatarDrawable(playerId));
                } else {

          /*
           * Initiate a download for this image. This will be updated in the background.
           */
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.ProgressBar_Achievements);

                    ImageDownloadTask imageDownload = new ImageDownloadTask(getApplicationContext(), progressBar);
                    imageDownloader.add(imageDownload);
                    imageDownload.execute(values.get(position).avatarUrl, avatarView, values.get(position).playerId);
                }
            } else {
                avatarView.setImageResource(R.drawable.avatar_not_set);
            }

            return rowView;
        }

    }

    /**
     * This downloads all the scores from the website in the background.
     *
     * @author Barry Irvine
     */
    private class ScoreDownloaderTask extends AsyncTask<Object, String, Boolean> {

        private static final String DEBUG_TAG = "ScoreDownloaderTask";
        private final ArrayList<ScoreModel> items = new ArrayList<>();
        private final GridView mGridView;
        private final ProgressBar mProgressBar;
        private boolean IOerror;

        public ScoreDownloaderTask(ProgressBar progressBar, GridView gridView) {
            this.mProgressBar = progressBar;
            this.mGridView = gridView;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            boolean result = false;
            String pathToScores = (String) params[0];

            XmlPullParser scores;
            try {
                URL xmlUrl = new URL(pathToScores);
                scores = XmlPullParserFactory.newInstance().newPullParser();
                scores.setInput(xmlUrl.openStream(), null);
                result = true;
            } catch (XmlPullParserException e) {
                IOerror = true;
                scores = null;
            } catch (IOException e) {
                scores = null;
                IOerror = true;
            }

            if (scores != null) {
                try {
                    processScores(scores);
                } catch (XmlPullParserException e) {
                    IOerror = true;
                    Log.e(DEBUG_TAG, "Pull Parser failure", e);
                } catch (IOException e) {
                    IOerror = true;
                    Log.e(DEBUG_TAG, "IO Exception parsing XML", e);
                }
            }

            return result;
        }

        @Override
        protected void onCancelled() {
            Log.i(DEBUG_TAG, "onCancelled");
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressBar.setVisibility(View.GONE);
            switch (mGridView.getId()) {
                case R.id.GridView_AllScores:
                    gAllScores = items;
                    break;
                case R.id.GridView_FriendScores:
                    gFriendScores = items;
                    if (!IOerror) {
                        doneFriendScores = true;
                    }
                    break;
            }

            if (items.size() == 0) {

        /*
         * No scores were returned so display a No Scores message.
         */
                final ArrayList<String> items = new ArrayList<>();
                items.add(getResources().getString(R.string.no_scores));
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(YanivAchievementsActivity.this, R.layout.no_scores, items);
                this.mGridView.setAdapter(adapter);
                this.mGridView.setColumnWidth(getResources().getDisplayMetrics().widthPixels);
            } else {
        /*
         * Populate the list view with all the returned items.
         */
                final ScoreArrayAdapter adapter = new ScoreArrayAdapter(YanivAchievementsActivity.this, R.layout.score_row, items);
                this.mGridView.setAdapter(adapter);
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mProgressBar.setVisibility(View.VISIBLE);
            if (values.length == 6) {
                final int rank = Integer.parseInt(values[1]);
                final int rating = Integer.parseInt(values[0]);
                final String name = values[2];
                final int achievements = Integer.parseInt(values[3]);
                final String avatarUrl = values[4];
                final long playerId = Long.parseLong(values[5]);

                ScoreModel item = new ScoreModel(rank, name, rating, achievements, avatarUrl, playerId);
                items.add(item);
            }

        }

        /**
         * Churn through an XML score information and populate a {@code ListView}
         *
         * @param scores A standard {@code XmlPullParser} containing the scores
         * @throws XmlPullParserException Thrown on XML errors
         * @throws IOException            Thrown on IO errors reading the XML
         */
        private void processScores(XmlPullParser scores) throws XmlPullParserException, IOException {
            int eventType = -1;
            boolean bFoundScores = false;

            // Find Score records from XML
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                if (eventType == XmlResourceParser.START_TAG) {

                    // Get the name of the tag (e.g. rating)
                    final String strName = scores.getName();

                    if (strName.equals("rating")) {
                        bFoundScores = true;
                        String scoreValue = scores.getAttributeValue(null, "rating");
                        String scoreRank = scores.getAttributeValue(null, "rank");
                        String scoreUserName = scores.getAttributeValue(null, "username");
                        String scoreAchievements = scores.getAttributeValue(null, "achievements");
                        String scoreAvatar = scores.getAttributeValue(null, "avatarUrl");
                        String playerId = scores.getAttributeValue(null, "playerId");
                        publishProgress(scoreValue, scoreRank, scoreUserName, scoreAchievements, scoreAvatar, playerId);
                    }
                }
                eventType = scores.next();
            }

            // Handle no scores available
            if (!bFoundScores) {
                publishProgress();
            }
        }

    }

}
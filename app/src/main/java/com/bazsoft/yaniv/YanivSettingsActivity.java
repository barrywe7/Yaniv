package com.bazsoft.yaniv;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class YanivSettingsActivity extends YanivActivity implements OnClickListener, OnCancelListener, OnKeyListener, OnFocusChangeListener,
        CheckBox.OnCheckedChangeListener, OnLongClickListener, RadioGroup.OnCheckedChangeListener {

    private static final int DIALOG_FRIEND_EMAIL = 0;
    private final static int DIALOG_LANGUAGE = 3;
    private static final int DIALOG_NEW_FRIEND = 2;
    private static final int DIALOG_PRIVACY_POLICY = 1;
    private static final int FOLLOWER_VIEW_ID = 1;
    private static final int FRIEND_VIEW_ID = 0;
    private static final int TAKE_AVATAR_CAMERA_REQUEST = 1;
    private static final int TAKE_AVATAR_GALLERY_REQUEST = 2;
    private static ArrayList<ImageDownloadTask> imageDownloader = new ArrayList<>();
    private FriendRequestTask friendRequest;
    private String mEmailToAdd;
    private FriendInfoRequestTask mFriendsTask;
    private ImageUploadTask mImageUpload;
    private ThisPlayerInfoRequestTask playerInfoTask;
    private ParsedPlayerInfoDataSet thisPlayer;

    private void addPlayerAvatarToRow(final TableRow tableRow, ParsedPlayerInfoDataSet player, int viewId) {
        ImageView avatarView = new ImageView(this);
        avatarView.setScaleType(ScaleType.CENTER_CROP);
        avatarView.setBackgroundResource(R.drawable.avatar_shape);
        avatarView.setId(viewId);
        if (player.getEmail() != null) {
            avatarView.setTag(player);
            avatarView.setOnClickListener(this);
        }

        Long playerId = player.getId();
        String avatarUrl = player.getAvatarUrl();

        if (playerId != null && !avatarUrl.equals(Const.YANIV_SERVER_EMPTY_URL) && !doesPlayerAvatarFileExist(playerId)) {
            ImageDownloadTask imageDownload = new ImageDownloadTask(getApplicationContext(), null);
            imageDownloader.add(imageDownload);
            imageDownload.execute(avatarUrl, avatarView, playerId);
        } else {
            if (playerId == null) {
                avatarView.setBackgroundDrawable(null);
            } else {
                avatarView.setImageDrawable(getPlayerAvatarDrawable(playerId));
            }
        }
        tableRow.addView(avatarView);
        int dimen = getResources().getDimensionPixelSize(R.dimen.scoreboard_height);
        LayoutParams params = avatarView.getLayoutParams();
        params.height = dimen;
        params.width = dimen;
    }

    /**
     * {@code populateFriendsTable()} helper method -- Populate a {@code TableRow} with three columns of {@code TextView} data (styled)
     *
     * @param tableRow  The {@code TableRow} the text is being added to
     * @param player    The player to add
     * @param textColor The colour to make the text
     * @param textSize  The size to make the text
     */
    private void addTextToRowWithValues(final TableRow tableRow, ParsedPlayerInfoDataSet player, int textColor, float textSize, int viewId) {
        TextView textView = new TextView(this);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(textColor);
        textView.setText(player.getName());
        textView.setId(viewId);
        textView.setPadding(getResources().getDimensionPixelSize(R.dimen.space_padding), 0, 0, 0);

        if (player.getEmail() != null) {
            textView.setTag(player);
            textView.setOnClickListener(this);
        }
        tableRow.addView(textView);
    }

    /**
     * Scale a Bitmap, keeping its aspect ratio
     *
     * @param bitmap  Bitmap to scale
     * @param maxSide Maximum length of either side
     * @return a new, scaled Bitmap
     */
    private Bitmap createScaledBitmapKeepingAspectRatio(Bitmap bitmap, int maxSide) {

        int orgHeight = bitmap.getHeight();
        int orgWidth = bitmap.getWidth();

        // scale to no longer any either side than 75dp
        int scaledWidth = (orgWidth >= orgHeight) ? maxSide : (int) ((float) maxSide * ((float) orgWidth / (float) orgHeight));
        int scaledHeight = (orgHeight >= orgWidth) ? maxSide : (int) ((float) maxSide * ((float) orgHeight / (float) orgWidth));

        // create the scaled bitmap
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
    }

    /**
     * update the server with a friend request
     */
    private void doFriendRequest(String friendEmail, String command, String encodedEmail) {
        // make sure we don't collide with another pending update
        if (friendRequest == null || friendRequest.getStatus() == AsyncTask.Status.FINISHED || friendRequest.isCancelled()) {
            friendRequest = new FriendRequestTask();
            friendRequest.execute(friendEmail, command, encodedEmail);
        } else {
            Log.w(DEBUG_TAG, "Warning: friendRequestTask already going");
        }
    }

    /**
     * update the server with a friend request
     */
    private void doFriendRequest(String friendEmail, String command, String encodedEmail, View progress) {
        // make sure we don't collide with another pending update
        if (friendRequest == null || friendRequest.getStatus() == AsyncTask.Status.FINISHED || friendRequest.isCancelled()) {
            friendRequest = new FriendRequestTask(progress);
            friendRequest.execute(friendEmail, command, encodedEmail);
        } else {
            Log.w(DEBUG_TAG, "Warning: friendRequestTask already going");
        }
    }

    private ParsedPlayerInfoDataSet getPlayerInfo(long playerId) {
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Const.YANIV_SERVER_PLAYER_INFO + playerId);
        ParsedPlayerInfoDataSet parsedPlayerInfoDataSet = new ParsedPlayerInfoDataSet();
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            String responseBody = client.execute(request, responseHandler);
      /* Get a SAXParser from the SAXPArserFactory. */
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

      /* Get the XMLReader of the SAXParser we created. */
            XMLReader xr = sp.getXMLReader();
      /* Create a new ContentHandler and apply it to the XML-Reader */
            PlayerInfoResponseHandler playerInfoHandler = new PlayerInfoResponseHandler();
            xr.setContentHandler(playerInfoHandler);

      /* Parse the xml-data from our URL. */
            InputSource inputSource = new InputSource();
            inputSource.setEncoding("UTF-8");
            inputSource.setCharacterStream(new StringReader(responseBody));

      /* Parse the xml-data from our URL. */
            xr.parse(inputSource);
      /* Parsing has finished. */

      /* Our playerInfoHandler now provides the parsed data to us. */
            parsedPlayerInfoDataSet = playerInfoHandler.getParsedData();
            parsedPlayerInfoDataSet.setId(playerId);
        } catch (ClientProtocolException e) {
            Log.e(DEBUG_TAG, "Client protocol", e);
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "IO exception", e);
        } catch (ParserConfigurationException e) {
            Log.e(DEBUG_TAG, "Parser exception", e);
        } catch (SAXException e) {
            Log.e(DEBUG_TAG, "SAX exception", e);
        }
        return parsedPlayerInfoDataSet;

    }

    private void initYanivMinimum() {
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.RadioGroup_Yaniv_RadioButtons);
        final int minimum = mAppPrefs.getYanivMinimum();
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            final View view = radioGroup.getChildAt(i);
            if (Integer.parseInt((String) view.getTag()) == minimum) {
                radioGroup.check(view.getId());
            }
        }
        radioGroup.setOnCheckedChangeListener(this);

    }

    private void initAvatar() {
        final ImageButton avatarButton = (ImageButton) findViewById(R.id.ImageButton_Avatar);
        final String strAvatarUri = mAppPrefs.getAvatar();
        if (strAvatarUri != null) {
            Uri imageUri = Uri.parse(strAvatarUri);
            avatarButton.setImageURI(imageUri);
        }

        avatarButton.setOnClickListener(this);

        avatarButton.setOnLongClickListener(this);

    }

    private void initEmailEntry() {
        // Save email
        final EditText emailText = (EditText) findViewById(R.id.EditText_Email);
        emailText.setText(mAppPrefs.getEmail());
        emailText.setOnKeyListener(this);
        emailText.setOnFocusChangeListener(this);
    }

    private void initFacebook() {
        final CheckBox checkbox = (CheckBox) findViewById(R.id.CheckBox_Facebook);
        checkbox.setChecked(mAppPrefs.isFacebookEnabled());
        checkbox.setOnCheckedChangeListener(this);
    }

    private void initFriendEmailEntry() {
        // Set button handler to load friend email entry dialog
        final Button addFriend = (Button) findViewById(R.id.Button_Friend_Email);
        addFriend.setOnClickListener(this);
    }

    private void initLanguage() {
        final TextView languageView = (TextView) findViewById(R.id.TextView_LanguageValue);
        final String language = mAppPrefs.getLanguage();
        for (Language lingo : Language.values()) {
            if (lingo.iso_code.equals(language)) {
                languageView.setText(lingo.language);
                break;
            }
        }
    }

    private void initMuteButton() {
        final ToggleButton muteToggleButton = (ToggleButton) findViewById(R.id.ToggleButton_Mute);
        muteToggleButton.setChecked(mAppPrefs.getMute());
    }

    private void initNameEntry() {
        // Save name
        final EditText nameText = (EditText) findViewById(R.id.EditText_Name);
        nameText.setText(mAppPrefs.getPreferencesPlayerName());
        nameText.setOnKeyListener(this);
        nameText.setOnFocusChangeListener(this);
    }

    private void initPrivacyPolicy() {
        // Set button handler to Load friend email entry dialog
        final Button privacyPolicy = (Button) findViewById(R.id.Button_Privacy_Policy);
        privacyPolicy.setOnClickListener(this);
    }

    private void initSpeed() {
        final SeekBar speed = (SeekBar) findViewById(R.id.SeekBar_ComputerSpeed);
        speed.setProgress(mAppPrefs.getComputerSpeed() - 1);
        speed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int prevSpeed = mAppPrefs.getComputerSpeed() - 1;
                mAppPrefs.setComputerSpeed(progress + 1);
                if (progress < prevSpeed) {
                    SoundManager.playSound(SoundEffect.BRAKE);
                } else {
                    SoundManager.playSound(SoundEffect.SPEED);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_AVATAR_CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    // Took a picture, use the down-sized camera image provided by
                    // default
                    Bitmap cameraPic = (Bitmap) data.getExtras().get("data");
                    if (cameraPic != null) {
                        try {
                            saveAvatar(cameraPic);
                        } catch (Exception e) {
                            Log.e(DEBUG_TAG, "saveAvatar() with camera image failed.", e);
                        }
                    }
                }
                break;
            case TAKE_AVATAR_GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        try {
                            int maxLength = (int) getResources().getDimension(R.dimen.pic_size);
                            // Full image size is likely to be large. Let's scale
                            // the graphic.
                            String[] projection = {Media.ORIENTATION};

                            // Determine orientation of image
                            int imageOrientation = 0;
                            Cursor imageCursor = managedQuery(photoUri, projection, "", null, null);
                            if (imageCursor != null) {
                                imageCursor.moveToFirst();
                                imageOrientation = imageCursor.getInt(imageCursor.getColumnIndex(Media.ORIENTATION));
                                imageCursor.close();
                            }
                            Matrix matrix = new Matrix();
                            matrix.preRotate(imageOrientation);
                            Bitmap galleryPic = Media.getBitmap(getContentResolver(), photoUri);
                            Bitmap rotatedPic = Bitmap.createBitmap(galleryPic, 0, 0, galleryPic.getWidth(), galleryPic.getHeight(), matrix, true);

                            Bitmap scaledGalleryPic = createScaledBitmapKeepingAspectRatio(rotatedPic, maxLength);
                            saveAvatar(scaledGalleryPic);
                        } catch (Exception e) {
                            Log.e(DEBUG_TAG, "saveAvatar() with gallery picker failed.", e);
                        }
                    }
                }
                break;
            case FacebookApplication.FACEBOOK_AUTH_REQUEST:
                mFacebook.getFacebook().authorizeCallback(requestCode, resultCode, data);
                final CheckBox checkbox = (CheckBox) findViewById(R.id.CheckBox_Facebook);
                if (mAppPrefs.isFacebookEnabled() && mAppPrefs.getFacebookToken() != null)
                    checkbox.setChecked(mAppPrefs.isFacebookEnabled());
                // }
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        YanivSettingsActivity.this.removeDialog(DIALOG_FRIEND_EMAIL);
    }

    @Override
    public void onClick(View view) {
        SoundManager.playSound(SoundEffect.CLICK);
        final SeekBar speed;
        switch (view.getId()) {
            case R.id.ImageButton_Avatar:
                final Intent pictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(Intent.createChooser(pictureIntent, getString(R.string.settings_avatar_prompt_camera)), TAKE_AVATAR_CAMERA_REQUEST);
                break;
            case R.id.Button_Friend_Email:
                showDialog(DIALOG_FRIEND_EMAIL);
                break;
            case R.id.Button_Privacy_Policy:
                showDialog(DIALOG_PRIVACY_POLICY);
                break;
            case R.id.TextView_Slow:
                speed = (SeekBar) findViewById(R.id.SeekBar_ComputerSpeed);
                speed.setProgress(speed.getProgress() - 1);
                break;
            case R.id.TextView_Fast:
                speed = (SeekBar) findViewById(R.id.SeekBar_ComputerSpeed);
                speed.setProgress(speed.getProgress() + 1);
                break;
            case R.id.LinearLayout_Language:
                showDialog(DIALOG_LANGUAGE);
                break;
            case FRIEND_VIEW_ID:
                if (view.getTag() != null) {
                    ParsedPlayerInfoDataSet player = (ParsedPlayerInfoDataSet) view.getTag();
                    doFriendRequest("", "remove", player.getEmail(), view.getRootView().findViewById(R.id.RelativeLayout_ProgressBar));
                }
                break;
            case FOLLOWER_VIEW_ID:
                if (view.getTag() != null) {
                    ParsedPlayerInfoDataSet player = (ParsedPlayerInfoDataSet) view.getTag();
                    if (!thisPlayer.getFriends().contains(player.getId())) {
                        doFriendRequest("", "add", player.getEmail(), view.getRootView().findViewById(R.id.RelativeLayout_ProgressBar));
                    }
                }
                break;
            case R.id.ToggleButton_Mute:
                boolean mute = ((ToggleButton) view).isChecked();
                mAppPrefs.setMute(mute);
                toggleMute(mute);
                if (!mute) {
                    SoundManager.playSound(SoundEffect.CONFIRM);
                }
                break;
        }

    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        mAppPrefs = AppPreferences.getInstance(getApplicationContext());
        mFacebook = FacebookApplication.getInstance(getApplicationContext());
        // Hide the soft keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        new ContactEmails(getContentResolver());

        // Initialise the Name entry
        initNameEntry();

        // Initialise the Avatar selector
        initAvatar();

        // Initialise the email entry
        initEmailEntry();

        // Initialise the Yaniv radio buttons
        initYanivMinimum();

        // Initialise the Facebook checkbox
        initFacebook();

        // Initialise the Friend email entry
        initFriendEmailEntry();

        // Initialise the privacy policy
        initPrivacyPolicy();

        // Initialise the mute button
        initMuteButton();

        // Initialise the computer speed slider
        initSpeed();

        // Initialise the language listview
        initLanguage();

        // if we don't have a serverId yet, we need to get one
    /*
     * Integer serverId; serverId = mAppPrefs.getPlayerId(); if (serverId == -1) { updateServerData(); }
     */
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        LayoutInflater layoutInflater = this.getLayoutInflater();
        switch (id) {
            case DIALOG_LANGUAGE:

                CustomDialog.Builder languageDialogBuilder = new CustomDialog.Builder(this);

                ArrayList<CharSequence> items = new ArrayList<>();
                for (Language lingo : Language.values()) {
                    if (lingo.sdk_version <= Build.VERSION.SDK_INT) {
                        items.add(lingo.language);
                    }
                }
                final CharSequence[] itemsArray = new CharSequence[items.size()];
                languageDialogBuilder.setTitle(R.string.alert_title_language);
                languageDialogBuilder.setItems(items.toArray(itemsArray), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SoundManager.playSound(SoundEffect.CLICK);
                        Language lingo = Language.values()[which];
                        mAppPrefs.setLanguage(lingo.iso_code);
                        changeLocale(lingo.iso_code);
                        dialog.dismiss();
                        refresh();
                    }
                });
                languageDialogBuilder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        SoundManager.playSound(SoundEffect.CLICK);
                        dialog.dismiss();
                    }
                });

                return languageDialogBuilder.create();
            case DIALOG_FRIEND_EMAIL:
                final View friendDialogLayout = layoutInflater.inflate(R.layout.friend_entry, (ViewGroup) findViewById(R.id.root));
                final TableLayout table = (TableLayout) friendDialogLayout.findViewById(R.id.TableLayout_FriendList);
                final RelativeLayout progress = (RelativeLayout) friendDialogLayout.findViewById(R.id.RelativeLayout_ProgressBar);
                final AutoCompleteTextView emailText = (AutoCompleteTextView) friendDialogLayout.findViewById(R.id.AutoCompleteTextView_Friend_Email);
                if (ContactEmails.email != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.email_item, ContactEmails.email);
                    emailText.setAdapter(adapter);
                }
                emailText.setOnKeyListener(this);
                populateFriendsTable(table, progress);

                CustomDialog.Builder friendDialogBuilder = new CustomDialog.Builder(this);

                friendDialogBuilder.setView(friendDialogLayout).setCancelable(true).setOnCancelListener(this).setTitle(R.string.settings_button_friend_email)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                SoundManager.playSound(SoundEffect.CLICK);
                                String friendEmail = emailText.getText().toString();
                                if (friendEmail.length() > 0) {
                                    doFriendRequest(friendEmail, "add", "");
                                }
                                YanivSettingsActivity.this.removeDialog(DIALOG_FRIEND_EMAIL);
                            }
                        });
                return friendDialogBuilder.create();
            case DIALOG_PRIVACY_POLICY:

                final View privacyDialogLayout = layoutInflater.inflate(R.layout.privacy_policy, (ViewGroup) findViewById(R.id.root));

                CustomDialog.Builder privacyDialogBuilder = new CustomDialog.Builder(this);
                privacyDialogBuilder.setTitle(R.string.privacy_title);
                privacyDialogBuilder.setView(privacyDialogLayout);

                privacyDialogBuilder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        SoundManager.playSound(SoundEffect.CLICK);
                        dialog.dismiss();
                    }
                });
                return privacyDialogBuilder.create();
            case DIALOG_NEW_FRIEND:
                CustomDialog.Builder builder = new CustomDialog.Builder(this);
                builder.setTitle(R.string.alert_title_send_email).setMessage("").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SoundManager.playSound(SoundEffect.CLICK);
                        dialog.cancel();
                        sendEmail();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SoundManager.playSound(SoundEffect.CLICK);
                        dialog.dismiss();
                    }
                });
                return builder.create();

        }
        return null;
    }

    /**
     * Called when activity is destroyed
     */
    @Override
    public void onDestroy() {
        Log.d(DEBUG_TAG, "SHARED PREFERENCES");
        Log.d(DEBUG_TAG, "Name is: " + mAppPrefs.getPreferencesPlayerName());
        Log.d(DEBUG_TAG, "Number of players is: " + mAppPrefs.getPreferencesNumPlayers());
        Log.d(DEBUG_TAG, "Avatar is: " + mAppPrefs.getAvatar());
        Log.d(DEBUG_TAG, "Facebook is: " + mAppPrefs.isFacebookEnabled());
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        if (mImageUpload != null) {
            mImageUpload.cancel(true);
        }

        if (friendRequest != null) {
            friendRequest.cancel(true);
        }

        if (mFriendsTask != null) {
            mFriendsTask.cancel(true);
        }

        if (playerInfoTask != null) {
            playerInfoTask.cancel(true);
        }

        for (ImageDownloadTask imageDownload : imageDownloader) {
            if (imageDownload != null && imageDownload.getStatus() != AsyncTask.Status.FINISHED) {
                imageDownload.cancel(true);
            }
        }

        super.onPause();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_NEW_FRIEND:
                ((CustomDialog) dialog).setMessage(String.format(getResources().getString(R.string.alert_send_email), mEmailToAdd));
                break;

        }
    }

    private void populateFriendsTable(TableLayout friendsTable, RelativeLayout progress) {
        if (thisPlayer == null) {
            thisPlayer = new ParsedPlayerInfoDataSet();
        }
        long now = System.currentTimeMillis();
        long timeSinceLastRefresh = now - mAppPrefs.getFriendsUpdated();
        if (timeSinceLastRefresh < 14400000) { // 4 hours
            thisPlayer.setFollowers(mAppPrefs.getFollowers());
            thisPlayer.setFriends(mAppPrefs.getFriends());
            mFriendsTask = new FriendInfoRequestTask(progress, friendsTable);
            mFriendsTask.execute();
        } else {
            playerInfoTask = new ThisPlayerInfoRequestTask(progress, friendsTable);
            playerInfoTask.execute();
        }
    }

    private void refresh() {
        finish();
        Intent intent = new Intent(YanivSettingsActivity.this, YanivSettingsActivity.class);
        startActivity(intent);
    }

    private void saveAvatar(Bitmap avatar) {
        // Save the bitmap as a local file
        String strAvatarFilename = "avatar.jpg";
        try {
            avatar.compress(CompressFormat.JPEG, 100, openFileOutput(strAvatarFilename, MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            Log.e(DEBUG_TAG, "Avatar compression and save failed", e);
        }
        // Determine the Uri of the local avatar file
        Uri imageUriToSaveCameraImageTo = Uri.fromFile(new File(getFilesDir(), strAvatarFilename));

        // Save the Uri as a string preference
        mAppPrefs.setAvatar(imageUriToSaveCameraImageTo.getPath());

        // upload image to server
        uploadAvatarImage();

        // Update the ImageButton with the new image

        final ImageButton avatarButton = (ImageButton) findViewById(R.id.ImageButton_Avatar);
        final String strAvatarUri = mAppPrefs.getAvatar();
        final Uri imageUri = Uri.parse(strAvatarUri);
        avatarButton.setImageURI(null); // Workaround for refreshing an
        // ImageButton, which tries to cache the
        // previous image Uri. Passing null
        // effectively resets it.
        avatarButton.setImageURI(imageUri);
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mEmailToAdd});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Yaniv friend request");
        emailIntent.setType("plain/html");
        emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_add_friend));

        startActivity(Intent.createChooser(emailIntent, "Send your email using..."));
    }

  /*
   * private String readTextFromResource(int resourceID) { InputStream raw = getResources().openRawResource(resourceID); ByteArrayOutputStream stream = new
   * ByteArrayOutputStream(); int i; try { i = raw.read(); while (i != -1) { stream.write(i); i = raw.read(); } raw.close(); } catch (IOException e) {
   * Log.e(DEBUG_TAG, e.getMessage()); } return stream.toString(); }
   */

    /**
     * Upload a new or modified image to the server
     */
    private void uploadAvatarImage() {
        // make sure we don't collide with another pending update
        if (mImageUpload == null || mImageUpload.getStatus() == AsyncTask.Status.FINISHED || mImageUpload.isCancelled()) {
            mImageUpload = new ImageUploadTask();
            mImageUpload.execute();
        } else {
            Log.w(DEBUG_TAG, "Warning: upload task already going");
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            switch (view.getId()) {
                case R.id.EditText_Email:
                    mAppPrefs.setEmail(((EditText) view).getText().toString());
                    return true;
                case R.id.EditText_Name:
                    if (((EditText) view).getText().toString().length() > 0) {
                        mAppPrefs.setName(((EditText) view).getText().toString());
                    }
                    return true;
                case R.id.AutoCompleteTextView_Friend_Email:
                    final String friendEmail = ((AutoCompleteTextView) view).getText().toString();
                    if (friendEmail.length() > 0) {
                        doFriendRequest(friendEmail, "add", "");
                    }
                    removeDialog(DIALOG_FRIEND_EMAIL);
                    return true;
            }
        }
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            switch (view.getId()) {
                case R.id.EditText_Email:
                    mAppPrefs.setEmail(((EditText) view).getText().toString());
                    break;
                case R.id.EditText_Name:
                    mAppPrefs.setName(((EditText) view).getText().toString());
                    break;
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SoundManager.playSound(SoundEffect.CLICK);
        switch (buttonView.getId()) {
            case R.id.CheckBox_Facebook:
                mAppPrefs.setFacebookEnabled(isChecked);
                if (isChecked) {
                    mFacebook.authoriseFacebook(YanivSettingsActivity.this);
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        SoundManager.playSound(SoundEffect.CLICK);

        switch (view.getId()) {
            case R.id.ImageButton_Avatar:

                final Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                pickPhoto.setType("image/*");
                startActivityForResult(Intent.createChooser(pickPhoto, getString(R.string.settings_avatar_prompt_gallery)), TAKE_AVATAR_GALLERY_REQUEST);
                return true;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SoundManager.playSound(SoundEffect.CLICK);
        final int score = Integer.parseInt((String) findViewById(checkedId).getTag());
        mAppPrefs.setYanivMinimum(score);
    }

    private class FriendInfoRequestTask extends AsyncTask<Object, TableRow, Boolean> {

        TableLayout friendsTable;
        RelativeLayout mProgress;

        public FriendInfoRequestTask(RelativeLayout progress, TableLayout friendsTable) {
            this.mProgress = progress;
            this.friendsTable = friendsTable;
            TextView text = (TextView) progress.findViewById(R.id.TextView_Progress_Message);
            text.setText(R.string.progress_friends);
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ArrayList<ParsedPlayerInfoDataSet> friends = new ArrayList<>();
            ArrayList<ParsedPlayerInfoDataSet> followers = new ArrayList<>();
            for (Long friend : thisPlayer.getFriends()) {
                if (gFriendData.containsKey(friend)) {
                    friends.add(gFriendData.get(friend));
                } else {
                    ParsedPlayerInfoDataSet dataSet = getPlayerInfo(friend);
                    friends.add(dataSet);
                    gFriendData.put(friend, dataSet);
                }
            }
            for (Long follower : thisPlayer.getFollowers()) {
                if (gFriendData.containsKey(follower)) {
                    followers.add(gFriendData.get(follower));
                } else {
                    ParsedPlayerInfoDataSet dataSet = getPlayerInfo(follower);
                    followers.add(dataSet);
                    gFriendData.put(follower, dataSet);
                }
            }
            int textColor = getResources().getColor(android.R.color.white);
            float textSize = getResources().getDimension(R.dimen.standard_text_size);
            for (int i = 0; i < Math.max(friends.size(), followers.size()); i++) {
                TableRow newRow = new TableRow(YanivSettingsActivity.this);
                newRow.setPadding(0, getResources().getDimensionPixelSize(R.dimen.space_padding), 0, 0);
                newRow.setGravity(Gravity.CENTER_VERTICAL);
                if (i < friends.size()) {
                    addPlayerAvatarToRow(newRow, friends.get(i), FRIEND_VIEW_ID);
                    addTextToRowWithValues(newRow, friends.get(i), textColor, textSize, FRIEND_VIEW_ID);
                } else {
                    ParsedPlayerInfoDataSet emptyPlayer = new ParsedPlayerInfoDataSet();
                    addPlayerAvatarToRow(newRow, emptyPlayer, FRIEND_VIEW_ID);
                    addTextToRowWithValues(newRow, emptyPlayer, textColor, textSize, FRIEND_VIEW_ID);
                }
                if (i < followers.size()) {
                    addPlayerAvatarToRow(newRow, followers.get(i), FOLLOWER_VIEW_ID);
                    addTextToRowWithValues(newRow, followers.get(i), textColor, textSize, FOLLOWER_VIEW_ID);
                } else {
                    ParsedPlayerInfoDataSet emptyPlayer = new ParsedPlayerInfoDataSet();
                    addPlayerAvatarToRow(newRow, emptyPlayer, FOLLOWER_VIEW_ID);
                    addTextToRowWithValues(newRow, emptyPlayer, textColor, textSize, FOLLOWER_VIEW_ID);
                }
                publishProgress(newRow);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgress.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(TableRow... values) {
            friendsTable.addView(values[0]);

        }

    }

    private class FriendRequestTask extends AsyncTask<String, Boolean, ParsedFriendDataSet> {
        String command;
        String friendEmail;
        View progress;

        public FriendRequestTask() {
            this.progress = findViewById(R.id.ProgressBar_Settings);
        }

        public FriendRequestTask(View progress) {
            this.progress = progress;
            TextView text = (TextView) progress.findViewById(R.id.TextView_Progress_Message);
            text.setText(R.string.progress_friend_request);

        }

        @Override
        protected ParsedFriendDataSet doInBackground(String... params) {
            ParsedFriendDataSet parsedFriendDataSet = new ParsedFriendDataSet();
            try {
                friendEmail = params[0];
                command = params[1];
                String encFriendEmail;
                Integer playerId = mAppPrefs.getPlayerId();

                if (friendEmail.equals("")) {
                    encFriendEmail = params[2];
                } else {

                    try {
                        MessageDigest sha = MessageDigest.getInstance("SHA");

                        byte[] enc = sha.digest(friendEmail.getBytes());
                        StringBuilder sb = new StringBuilder();

                        for (byte enc1 : enc) {
                            sb.append(Integer.toHexString(enc1 & 0xFF));
                        }
                        encFriendEmail = sb.toString();
                    } catch (NoSuchAlgorithmException e) {
                        Log.w(Const.DEBUG_TAG, "Failed to get SHA, using hashcode()");
                        encFriendEmail = String.valueOf(friendEmail.hashCode());
                    }
                }

                Vector<NameValuePair> vars = new Vector<>();
                vars.add(new BasicNameValuePair("command", command));
                vars.add(new BasicNameValuePair("playerId", playerId.toString()));
                vars.add(new BasicNameValuePair("friend", encFriendEmail));

                HttpClient client = new DefaultHttpClient();

                // an example of using HttpClient with HTTP POST and form
                // variables
                HttpPost request = new HttpPost(Const.YANIV_SERVER_FRIEND_EDIT);
                request.setEntity(new UrlEncodedFormEntity(vars));

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = client.execute(request, responseHandler);

        /* Get a SAXParser from the SAXPArserFactory. */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();

        /* Get the XMLReader of the SAXParser we created. */
                XMLReader xr = sp.getXMLReader();
        /* Create a new ContentHandler and apply it to the XML-Reader */
                FriendResponseHandler friendHandler = new FriendResponseHandler();
                xr.setContentHandler(friendHandler);

        /* Parse the xml-data from our URL. */
                InputSource inputSource = new InputSource();
                inputSource.setEncoding("UTF-8");
                inputSource.setCharacterStream(new StringReader(responseBody));

        /* Parse the xml-data from our URL. */
                xr.parse(inputSource);
        /* Parsing has finished. */

        /* Our friendHandler now provides the parsed data to us. */
                parsedFriendDataSet = friendHandler.getParsedData();

            } catch (MalformedURLException e) {
                Log.e(DEBUG_TAG, "Failed to edit friend malformed url", e);
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Failed to edit friend io exception", e);
            } catch (SAXException e) {
                Log.e(DEBUG_TAG, "SAX Exception", e);
            } catch (ParserConfigurationException e) {
                Log.e(DEBUG_TAG, "Parser config error", e);
            }

            return parsedFriendDataSet;
        }

        @Override
        protected void onPostExecute(final ParsedFriendDataSet result) {
            progress.setVisibility(View.GONE);
            if (result.isValid()) {
                if (command.equals("add")) {
                    if (result.getFound()) {
                        if (result.getAdded()) {
                            Toast.makeText(getApplicationContext(), "Friend added successfully.", Toast.LENGTH_LONG).show();
                            mAppPrefs.setFriendsUpdated(0);

                        } else {
                            Toast.makeText(getApplicationContext(), "You have already added " + friendEmail, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        mEmailToAdd = friendEmail;
                        showDialog(DIALOG_NEW_FRIEND);
                    }
                }
                if (command.equals("remove")) {
                    if (result.getRemoved()) {
                        Toast.makeText(getApplicationContext(), "Friend removed successfully.", Toast.LENGTH_LONG).show();
                        mAppPrefs.setFriendsUpdated(0);

                    } else {
                        Toast.makeText(getApplicationContext(), "Friend no longer found on the webserver.", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error processing friend request. Please check your Internet connection.", Toast.LENGTH_LONG).show();
            }

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

    }

    private class ImageUploadTask extends AsyncTask<Object, String, Boolean> {
        private static final String DEBUG_TAG = "ImageUploadTask";
        ProgressDialog pleaseWaitDialog;

        @Override
        protected Boolean doInBackground(Object... params) {
            // an example using HttpClient and HttpMime to upload a file via
            // HTTP POST in the same
            // way a web browser might, using multipart MIME encoding
            String avatar = mAppPrefs.getAvatar();
            Integer playerId = mAppPrefs.getPlayerId();

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            File file = new File(avatar);
            FileBody encFile = new FileBody(file);

            entity.addPart("avatar", encFile);
            try {
                entity.addPart("updateId", new StringBody(playerId.toString()));
            } catch (UnsupportedEncodingException e) {
                Log.e(DEBUG_TAG, "Failed to add avatar for player id.", e);
            }

            HttpPost request = new HttpPost(Const.YANIV_SERVER_ACCOUNT_EDIT);
            request.setEntity(entity);

            HttpClient client = new DefaultHttpClient();

            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = client.execute(request, responseHandler);

                if (responseBody != null && responseBody.length() > 0) {
                    Log.w(DEBUG_TAG, "Unexpected response from avatar upload: " + responseBody);
                }

            } catch (ClientProtocolException e) {
                Log.e(DEBUG_TAG, e.toString());
            } catch (IOException e) {
                Log.e(DEBUG_TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            Log.i(DEBUG_TAG, "onCancelled");
            pleaseWaitDialog.dismiss();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(DEBUG_TAG, "onPostExecute");
            pleaseWaitDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            pleaseWaitDialog = ProgressDialog.show(YanivSettingsActivity.this, "Yaniv", "Uploading avatar image...", true, true);
            pleaseWaitDialog.setOnCancelListener(new OnCancelListener() {

                public void onCancel(DialogInterface dialog) {
                    ImageUploadTask.this.cancel(true);
                }
            });
        }

    }

    private class ThisPlayerInfoRequestTask extends AsyncTask<Object, Integer, Boolean> {

        TableLayout friendsTable;

        RelativeLayout mProgress;

        public ThisPlayerInfoRequestTask(RelativeLayout progressBar, TableLayout friendsTable) {
            mProgress = progressBar;
            this.friendsTable = friendsTable;

        }

        @Override
        protected Boolean doInBackground(Object... params) {
            thisPlayer = getPlayerInfo(mAppPrefs.getPlayerId());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (thisPlayer.isValid()) {
                mAppPrefs.setFriendsUpdated(System.currentTimeMillis());
                mAppPrefs.setFriends(thisPlayer.getFriends());
                mAppPrefs.setFollowers(thisPlayer.getFollowers());
                mFriendsTask = new FriendInfoRequestTask(mProgress, friendsTable);
                mFriendsTask.execute();
            } else {
                mProgress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Error retrieving friend details", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            mProgress.setVisibility(View.VISIBLE);
        }
    }

}

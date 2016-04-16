package com.bazsoft.yaniv;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class YanivSplashActivity extends YanivActivity implements AnimationListener {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        String copyrightInfo = getResources().getString(R.string.copyright_info);
        String text = String.format(copyrightInfo, BuildConfig.VERSION_NAME);
        TextView tv = (TextView) findViewById(R.id.TextView_Splash_Version);
        tv.setText(text);
        startAnimating();
    }

    /**
     * Helper method to start the animation on the splash screen
     */
    private void startAnimating() {

        // Load animations for all views within the TableLayout
        Animation spinin = AnimationUtils.loadAnimation(this,
                R.anim.custom_anim);
        LayoutAnimationController controller = new LayoutAnimationController(
                spinin);
        TableLayout table = (TableLayout) findViewById(R.id.TableLayout_Splash);
        for (int i = 0; i < table.getChildCount(); i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            row.setLayoutAnimation(controller);
        }
        ImageView title = (ImageView) findViewById(R.id.ImageView_Yaniv);
        Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
        title.startAnimation(scale);
        scale.setAnimationListener(this);
        //finish();
        //startActivity(new Intent(YanivSplashActivity.this, YanivMenuActivity.class));
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // The animation has ended, transition to the Main Menu screen
        finish();
        startActivity(new Intent(YanivSplashActivity.this, YanivMenuActivity.class));

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }
}
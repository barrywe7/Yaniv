package com.bazsoft.yaniv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.net.URL;

public class ImageDownloadTask extends AsyncTask<Object, String, Bitmap> {

    private static final String DEBUG_TAG = "ImageDownloadTask";
    private static Context mContext;
    private static int tasks = 0;
    private ImageView imageView;
    private Long playerId;
    private View progress;

    public ImageDownloadTask(Context context, View progress) {
        mContext = context;
        this.progress = progress;
    }

    @Override
    protected Bitmap doInBackground(Object... params) {

        tasks++;

        Bitmap bitmap = null;
        URL imageUrl;
        String avatarUrl = (String) params[0];
        imageView = (ImageView) params[1];
        playerId = (Long) params[2];

        try {
            // Create a Drawable by decoding a stream from a remote URL
            imageUrl = new URL(avatarUrl);
            bitmap = BitmapFactory.decodeStream(imageUrl.openStream());
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Decoding Bitmap stream failed.");

        }
        return bitmap;

    }

    @Override
    protected void onCancelled() {
        tasks--;
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        Log.i(DEBUG_TAG, "onPostExecute");
        if (result != null) {
            imageView.setImageBitmap(result);
            FileOutputStream fos;
            try {
                fos = mContext.openFileOutput(playerId + ".jpg", Context.MODE_PRIVATE);
                result.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            imageView.setImageResource(R.drawable.avatar_not_set);
        }


        tasks--;
        if (progress != null && tasks == 0) {
            progress.setVisibility(View.GONE);
        }
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
    }
}
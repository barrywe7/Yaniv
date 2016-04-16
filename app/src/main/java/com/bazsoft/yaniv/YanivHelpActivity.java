package com.bazsoft.yaniv;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class YanivHelpActivity extends YanivActivity {
    private static TextView mHelpTextView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        // Read raw file into string and populate TextView
        InputStream iFile = getResources().openRawResource(R.raw.yanivhelp);
        try {
            mHelpTextView = (TextView) findViewById(R.id.TextView_HelpText);
            String strFile = inputStreamToString(iFile);
            mHelpTextView.setText(strFile);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "InputStreamToString failure", e);
        }
    }

    /**
     * Converts an input stream to a string
     *
     * @param is The {@code InputStream} object to read from
     * @return A {@code String} object representing the string for of the input
     * @throws IOException Thrown on read failure from the input
     */
    public String inputStreamToString(InputStream is) throws IOException {
        BufferedReader dataIO = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String strLine;

        StringBuilder sBuffer = new StringBuilder("");


        while ((strLine = dataIO.readLine()) != null) {
            sBuffer.append(strLine).append("\n");
        }
        dataIO.close();
        is.close();
        return sBuffer.toString();
    }
}

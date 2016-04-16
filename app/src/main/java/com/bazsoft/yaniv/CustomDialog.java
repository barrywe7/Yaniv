package com.bazsoft.yaniv;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public void setMessage(String message) {
        ((TextView) findViewById(R.id.message)).setText(message);
    }


    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private String neutralButtonText;
        private View contentView;

        private DialogInterface.OnClickListener
                positiveButtonClickListener,
                negativeButtonClickListener,
                neutralButtonClickListener,
                itemsListener;
        private DialogInterface.OnCancelListener
                onCancelListener;

        private boolean cancelable = true;
        private CharSequence[] items;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog message from String
         *
         * @param message The message to be set
         * @return A builder
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message The message to be set
         * @return The builder
         */
        public Builder setMessage(final int message) {
            return setMessage(context.getString(message));
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title The title of the dialog
         * @return The builder
         */
        public Builder setTitle(final int title) {
            return setTitle(context.getString(title));
        }

        /**
         * Set the Dialog title from String
         *
         * @param title The title of the dialog
         * @return The builder
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         *
         * @param v The custom content view
         * @return The builder
         */
        public Builder setView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
            this.items = items;
            this.itemsListener = listener;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText The text on the positive button
         * @param listener           The on click listener for the positive button
         * @return The builder
         */
        public Builder setPositiveButton(final int positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = context.getString(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         *
         * @param positiveButtonText The text on the positive button
         * @param listener           The on click listener for the positive button
         * @return The builder
         */
        public Builder setPositiveButton(final String positiveButtonText, final DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         *
         * @param negativeButtonText The text on the negative button
         * @param listener           The on click listener for the negative button
         * @return The builder
         */
        public Builder setNegativeButton(final int negativeButtonText, final DialogInterface.OnClickListener listener) {
            return setNegativeButton(context.getString(negativeButtonText), listener);
        }

        /**
         * Set the negative button text and it's listener
         *
         * @param negativeButtonText The text on the negative button
         * @param listener           The on click listener for the negative button
         * @return The builder
         */
        public Builder setNegativeButton(final String negativeButtonText, final DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the neutral button resource and it's listener
         *
         * @param neutralButtonText The text on the neutral button
         * @param listener          The listener for the neutral button
         * @return The builder
         */
        public Builder setNeutralButton(final int neutralButtonText, final DialogInterface.OnClickListener listener) {
            return setNeutralButton(context.getString(neutralButtonText), listener);
        }

        /**
         * Set the neutral button text and it's listener
         *
         * @param neutralButtonText The text on the neutral button
         * @param listener          The listener for the neutral button
         * @return The builder
         */
        public Builder setNeutralButton(final String neutralButtonText, final DialogInterface.OnClickListener listener) {
            this.neutralButtonText = neutralButtonText;
            this.neutralButtonClickListener = listener;
            return this;
        }

        public Builder setOnCancelListener(final DialogInterface.OnCancelListener listener) {
            this.onCancelListener = listener;
            return this;
        }

        public Builder setCancelable(final Boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }


        /**
         * Create the custom dialog
         */
        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context, R.style.MyCustomDialog);
            final View layout = inflater.inflate(R.layout.dialog, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    layout.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(
                                    dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    layout.findViewById(R.id.negativeButton).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            negativeButtonClickListener.onClick(
                                    dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                // if no cancel button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }
            // set the neutral button
            if (neutralButtonText != null) {
                ((Button) layout.findViewById(R.id.neutralButton)).setText(neutralButtonText);
                if (neutralButtonClickListener != null) {
                    layout.findViewById(R.id.neutralButton).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            neutralButtonClickListener.onClick(
                                    dialog,
                                    DialogInterface.BUTTON_NEUTRAL);
                        }
                    });
                }
            } else {
                // if no cancel button just set the visibility to GONE
                layout.findViewById(R.id.neutralButton).setVisibility(View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(contentView);
            }
            dialog.setCancelable(cancelable);
            if (items != null) {
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                final ListView itemsLayout = (ListView) inflater.inflate(R.layout.language, null);
                ArrayAdapter<CharSequence> adapt = new ArrayAdapter<>(context, R.layout.language_item, items);
                itemsLayout.setAdapter(adapt);
                ((LinearLayout) layout.findViewById(R.id.content)).addView(itemsLayout);


                itemsLayout.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        itemsListener.onClick(dialog, position);
                    }
                });


            }
            if (onCancelListener != null) {
                dialog.setOnCancelListener(onCancelListener);
            }
            dialog.setContentView(layout);
            dialog.setVolumeControlStream(AudioManager.STREAM_MUSIC);

            return dialog;
        }

    }

}

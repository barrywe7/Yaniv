package com.bazsoft.yaniv;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class ContactEmails {

    public static String[] email;
    ContentResolver cr;

    public ContactEmails(ContentResolver cr) {
        this.cr = cr;
        if (email == null) {
            getContactEmails();
        }
    }

    public String[] getEmails() {
        return email;
    }

    public void getContactEmails() {
        EmailTask emailTask = new EmailTask();
        emailTask.execute();
    }

    private class EmailTask extends AsyncTask<Object, Boolean, String[]> {

        @Override
        protected String[] doInBackground(Object... params) {
            Cursor cur;
            ArrayList<String> emailList = new ArrayList<>();

            cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                    + " = ?", new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        String emailAddress = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        if (emailAddress != null && !emailList.contains(emailAddress)) {
                            emailList.add(emailAddress);
                        }
                    }
                    emailCur.close();
                }
            }
            cur.close();
            return emailList.toArray(new String[emailList.size()]);
        }

        @Override
        protected void onPostExecute(String[] result) {
            email = result;
        }
    }


}

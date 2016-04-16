package com.bazsoft.yaniv;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PlayerInfoResponseHandler extends DefaultHandler {


    // ===========================================================
    // Fields
    // ===========================================================

    private boolean in_name = false;
    private boolean in_avatarUrl = false;
    private boolean in_rating = false;
    private boolean in_won = false;
    private boolean in_played = false;
    private boolean in_email = false;
    private boolean in_friends = false;
    private boolean in_followers = false;

    private ParsedPlayerInfoDataSet myPlayerInfoDataSet = new ParsedPlayerInfoDataSet();

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public ParsedPlayerInfoDataSet getParsedData() {
        return this.myPlayerInfoDataSet;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {
        this.myPlayerInfoDataSet = new ParsedPlayerInfoDataSet();
    }

    @Override
    public void endDocument() throws SAXException {
        // Nothing to do
    }

    /**
     * Gets be called on opening tags like: <tag> Can provide attribute(s),
     * when xml was like: <tag attribute="attributeValue">
     */
    @Override
    public void startElement(String uri, String localName, String qName,
                             org.xml.sax.Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);
        switch (localName) {
            case "name":
                this.in_name = true;
                break;
            case "avatarUrl":
                this.in_avatarUrl = true;
                break;
            case "rating":
                this.in_rating = true;
                break;
            case "won":
                this.in_won = true;
                break;
            case "played":
                this.in_played = true;
                break;
            case "email":
                this.in_email = true;
                break;
            case "friends":
                this.in_friends = true;
                break;
            case "followers":
                this.in_followers = true;
                break;
        }

    }

    /**
     * Gets be called on closing tags like: </tag>
     */
    @Override
    public void endElement(String namespaceURI, String localName,
                           String qName) throws SAXException {
        switch (localName) {
            case "playerinfo":
                myPlayerInfoDataSet.setValid(true);
                break;
            case "name":
                this.in_name = false;
                break;
            case "avatarUrl":
                this.in_avatarUrl = false;
                break;
            case "rating":
                this.in_rating = false;
                break;
            case "won":
                this.in_won = false;
                break;
            case "played":
                this.in_played = false;
                break;
            case "email":
                this.in_email = false;
                break;
            case "friends":
                this.in_friends = false;
                break;
            case "followers":
                this.in_followers = false;
                break;
        }
    }

    /**
     * Gets be called on the following structure: <tag>characters</tag>
     */
    @Override
    public void characters(char ch[], int start, int length) {
        if (this.in_name) {
            myPlayerInfoDataSet.setName(new String(ch, start, length));
        } else if (this.in_avatarUrl) {
            myPlayerInfoDataSet.setAvatarUrl(new String(ch, start, length));
        } else if (this.in_rating) {
            myPlayerInfoDataSet.setRating(new String(ch, start, length));
        } else if (this.in_won) {
            myPlayerInfoDataSet.setWon(new String(ch, start, length));
        } else if (this.in_played) {
            myPlayerInfoDataSet.setPlayed(new String(ch, start, length));
        } else if (this.in_email) {
            myPlayerInfoDataSet.setEmail(new String(ch, start, length));
        } else if (this.in_friends) {
            myPlayerInfoDataSet.setFriends(new String(ch, start, length));
        } else if (this.in_followers) {
            myPlayerInfoDataSet.setFollowers(new String(ch, start, length));
        }
    }
}


package com.bazsoft.yaniv;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FriendResponseHandler extends DefaultHandler {

    // ===========================================================
    // Fields
    // ===========================================================

    private boolean in_found = false;
    private boolean in_removed = false;
    private boolean in_added = false;

    private ParsedFriendDataSet myParsedFriendDataSet = new ParsedFriendDataSet();

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public ParsedFriendDataSet getParsedData() {
        return this.myParsedFriendDataSet;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {
        this.myParsedFriendDataSet = new ParsedFriendDataSet();
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
            case "found":
                this.in_found = true;
                break;
            case "removed":
                this.in_removed = true;
                break;
            case "added":
                this.in_added = true;
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
            case "friend-response":
                myParsedFriendDataSet.setValid(true);
                break;
            case "found":
                this.in_found = false;
                break;
            case "removed":
                this.in_removed = false;
                break;
            case "added":
                this.in_added = false;
                break;
        }
    }

    /**
     * Gets be called on the following structure: <tag>characters</tag>
     */
    @Override
    public void characters(char ch[], int start, int length) {
        if (this.in_found) {
            myParsedFriendDataSet.setFound(new String(ch, start, length));
        } else if (this.in_added) {
            myParsedFriendDataSet.setAdded(new String(ch, start, length));
        } else if (this.in_removed)
            myParsedFriendDataSet.setRemoved(new String(ch, start, length));
    }
}

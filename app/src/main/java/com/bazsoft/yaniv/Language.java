package com.bazsoft.yaniv;

/**
 * This enum encapsulates all the languages available in Yaniv.
 *
 * @author Barry Irvine
 */
public enum Language {
    DEFAULT("(default)", "default", 1),
    ENGLISH("English", "en", 1),
    DUTCH("Nederlands", "nl", 1),
    HEBREW("עברית", "he", 9);

    /**
     * The name of the language.
     */
    public final String language;
    /**
     * The iso code of the language.
     */
    public final String iso_code;
    /**
     * The minimum SDK version that the language is supported in.
     */
    public final int sdk_version;


    /**
     * Constructor to construct each element of the enum with its iso code.
     */
    Language(final String language, final String iso_code, final int sdk_version) {
        this.language = language;
        this.iso_code = iso_code;
        this.sdk_version = sdk_version;
    }
}

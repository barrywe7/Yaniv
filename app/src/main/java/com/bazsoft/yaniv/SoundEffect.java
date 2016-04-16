package com.bazsoft.yaniv;

/**
 * This enum encapsulates all the sound effects used in Yaniv.
 *
 * @author Barry Irvine
 */
public enum SoundEffect {
    /**
     * The shuffling sound effect.
     */
    SHUFFLE("shuffle"),
    /**
     * The sound a card slapping down on the table.
     */
    SLAP("slap"),
    /**
     * The sound of cards being dealt.
     */
    DEAL("deal"),
    /**
     * The sound of a crowd cheering. Played you call Yaniv and win.
     */
    CHEER("cheer"),
    /**
     * The sound of someone in pain when a player is Assaf-ed.
     */
    OW("ow"),
    /**
     * The sound of a card being drawn from the deck or the pile.
     */
    DRAW("draw"),
    /**
     * A confirmation sound.
     */
    CONFIRM("confirm"),
    /**
     * A boo sound when the computer wins a round.
     */
    BOO("boo"),
    /**
     * A ta-da sound used when you win a trophy.
     */
    TADA("tada"),
    /**
     * A click sound effect.
     */
    CLICK("click"),
    /*
     * A speeding car sound effect.
     */
    SPEED("speed"),
    /*
     * A braking car sound effect
     */
    BRAKE("brake"),
    /*
     * Me saying Assaf.
     */
    ASSAF("assaf"),
    /*
     * Me saying Yaniv
     */
    YANIV("yaniv");

    /**
     * The field with the name of the resource that plays the sound effect.
     */
    public final String resourceName;

    /**
     * Constructor to construct each element of the enum with its own sound file.
     */
    SoundEffect(final String resourceName) {
        this.resourceName = resourceName;
    }
}

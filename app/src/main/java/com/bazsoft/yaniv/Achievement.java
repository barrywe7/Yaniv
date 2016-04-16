package com.bazsoft.yaniv;

/**
 * This enum encapsulates all the achievements possible in Yaniv.
 *
 * @author Barry Irvine
 */
public enum Achievement {
    TWO_OF_A_KIND("Two's Company", "Played two of a kind."),
    THREE_OF_A_KIND("Three's A Crowd", "Played three of a kind."),
    FOUR_OF_A_KIND("Four's A Party!", "Played four of a kind."),
    STRAIGHT_OF_THREE("First In A Series", "Played a straight of three cards with no jokers."),
    STRAIGHT_OF_FOUR("Go Fourth And Multiply", "Played a straight of four cards with no joker."),
    STRAIGHT_OF_FIVE("Five Times The Fun", "Played a straight of five cards with no jokers."),
    STRAIGHT_WITH_JOKER("You've Got To Be Joking!", "Played a straight with a joker."),
    ASSAF_YOU("You Should've Seen That Coming", "Successfully Assaf'ed a player."),
    STEALTH_ASSAF("Good things Come To Those Who Wait", "Had a chance to call Yaniv but waited to Assaf."),
    YANIV("It's The Name Of The Game", "Successfully called Yaniv."),
    ASSAF_ME("Ow! That Hurts", "Got Assaf'ed by another player."),
    YANIV_FIVE_CARDS("It's A Numbers Game", "Successfully called Yaniv with 5 cards and 5 points or fewer."),
    YANIV_ZERO_SCORE("Practically Unbeatable", "Successfully called Yaniv with zero points."),
    QUICK_HANDS("Quick On The Draw", "Successfully dropped a card that you just picked up."),
    TOO_SLOW("Too Slow Chicken Marengo", "Another player dropped a card they just picked up before you moved."),
    LUCKY_ESCAPE("Lucky Escape", "Hit exactly 200 points and halved your score."),
    HALF_FIFTY("Half The Fun", "Hit exactly 50 points and halved your score."),
    HALF_HUNDRED("Half A Century", "Hit exactly 100 points and halved your score."),
    HALF_150("Back In Double Figures", "Hit exactly 150 points and halved your score."),
    KEEP_IT_LOW("Taking The Low Road", "Won a game with less than 50 points."),
    LUCKY_STREAK("I've Said It Before And I'll Say It Again", "Win Yaniv five times in a row."),
    WIN("I Play To Win", "Win a complete game of Yaniv."),
    YANIV_KNOCK_OUT("Adios Amigo", "Won a round and knocked another player out."),
    ONE_MORE_TURN("Please Sir, Can I Have One More Go?", "Go out with a straight or three of a kind in your hand."),
    WIN_TEN("I Ten To Win", "Win 10 games of Yaniv."),
    PLAY_FIVE("I Like To Play", "Play 5 complete games of Yaniv."),
    PLAY_100("I Think I'm Addicted", "Play 100 complete games of Yaniv."),
    COMPLETE_TUTORIAL("Scholar", "Complete the Yaniv tutorial."),
    STRAIGHT_WITH_TWO_JOKERS("The Joke Is On You", "Play a straight of 5 cards with 2 jokers."),
    ASSAF_FRENZY("Assaf Frenzy", "Assaf opponents 5 times or more in one game."),
    MANY_HALVES("Three Halves Make A Whole Lotta Fun", "Halve your score 3 times or more in one game."),
    QUICK_MOVES("Did You Start Playing Already?", "Win a game within 15 rounds or fewer."),
    INTERMINABLE("We Got There In The End", "Win a game after 50 rounds or more."),
    TRIATHLON("Triathlete", "Win consecutive 2, 3 and 4 player games with no restarts."),
    PROFESSIONAL("Professional", "Obtain a rating of 10,000 or more."),
    ONE_TURN("It's The Luck Of The Deal", "Win a round in ONE turn."),
    TWO_TURNS("Did You Want To Play Too?", "Win a round in TWO turns."),
    GOLDEN_ASSAF("Golden Assaf", "Get Assaf'ed and immediately halve your score to 50 or more.");


    /**
     * The title of the achievement.
     */
    public final String title;
    /**
     * The description of the achievement.
     */
    public final String description;
    /**
     * The score value of an achievement.
     */

    /**
     * Constructor to construct each element of the enum with its description.
     */
    Achievement(final String title, final String description) {
        this.title = title;
        this.description = description;
    }
}

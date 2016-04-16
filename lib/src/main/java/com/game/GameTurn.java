package com.game;

import com.cards.Card;
import com.cards.CardVector;

/**
 * An enumerated type of where a player can pick up from: DECK or PILE or call Yaniv.
 *
 * @author Barry Irvine
 */
public class GameTurn {

    public Turn turnType;
    public Card cardPickedUp = null;
    public CardVector cardsDropped = new CardVector();
    public int branchNum = -1;

    public GameTurn(Turn turnType, Card cardPickedUp, CardVector cardsDropped, int branchNum) {
        this.turnType = turnType;
        this.cardPickedUp = cardPickedUp;
        this.cardsDropped = cardsDropped;
        this.branchNum = branchNum;
    }

    public GameTurn(Turn turnType) {
        this.turnType = turnType;
    }

    /**
     * An empty Game Turn object.
     */
    public GameTurn() {
    }
}

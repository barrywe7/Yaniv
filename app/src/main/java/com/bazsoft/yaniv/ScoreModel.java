package com.bazsoft.yaniv;

/**
 * Defines all the values needed to display each player's score.
 *
 * @author Barry Irvine
 */
public class ScoreModel {

    Integer achievements;
    String avatarUrl;
    Long playerId;
    String playerName;
    Integer rank;
    Integer rating;

    public ScoreModel(int rank, String playerName, int rating,
                      int achievements, String avatarUrl, long playerId) {
        this.rank = rank;
        this.playerName = playerName;
        this.rating = rating;
        this.achievements = achievements;
        this.avatarUrl = avatarUrl;
        this.playerId = playerId;
    }
}
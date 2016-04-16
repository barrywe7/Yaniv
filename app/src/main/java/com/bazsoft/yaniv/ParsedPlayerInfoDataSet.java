package com.bazsoft.yaniv;

import java.util.HashSet;
import java.util.Set;

public class ParsedPlayerInfoDataSet {
    private Long id;
    private String name;
    private String avatarUrl;
    private int rating;
    private int won;
    private int played;
    private String email;
    private Set<Long> friends = new HashSet<>();
    private Set<Long> followers = new HashSet<>();
    private boolean valid;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = Integer.parseInt(rating);
    }

    public int getWon() {
        return won;
    }

    public void setWon(String won) {
        this.won = Integer.parseInt(won);
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(String played) {
        this.played = Integer.parseInt(played);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Long> getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        if (!friends.equals("")) {
            String[] friendsString = friends.split("\\|");
            for (String friend : friendsString) {
                this.friends.add(Long.parseLong(friend));
            }
        }
    }

    public Set<Long> getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        if (!followers.equals("")) {
            String[] followersString = followers.split("\\|");
            for (String follower : followersString) {
                this.followers.add(Long.parseLong(follower));
            }
        }

    }

}
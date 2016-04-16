package com.bazsoft.yaniv;

public class Const {
    // Server URLs
    public static final String YANIV_SERVER_BASE = "http://bazsoft-yaniv.appspot.com/";
    public static final String YANIV_SERVER_EMPTY_URL = "http://bazsoft-yaniv.appspot.com:80";
    public static final String YANIV_SERVER_SCORES = YANIV_SERVER_BASE + "scores.jsp";
    public static final String YANIV_SERVER_FRIEND_EDIT = YANIV_SERVER_BASE + "friend";
    public static final String YANIV_SERVER_ACCOUNT_EDIT = YANIV_SERVER_BASE + "receive";
    public static final String YANIV_SERVER_AVATAR = YANIV_SERVER_BASE + "/pi?key=";
    public static final String YANIV_SERVER_PLAYER_INFO = YANIV_SERVER_BASE + "player_info.jsp?playerId=";
    public static final int SECOND = 1000;
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;
    public static final int WEEK = 7 * DAY;
    static final String DEBUG_TAG = "Yaniv game Log";


}

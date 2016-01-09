package com.example.praveen.oauthsampleapplication.utils;

import android.graphics.BitmapFactory;
import android.text.TextUtils;

/**
 * Created by praveen on 1/6/2016.
 */
public class AppCommon {

    public static final String FB_NETWORK = "facebook";
    public static final String LINKEDIN_NETWORK = "linkedin";

    /**Facebook Constant*/
    public static final String FB_APP_ID = "458542597687422";
    public static final String FB_APP_PERMISSIONS [] = new String[] {"public_profile ","user_birthday","user_relationships","user_hometown",
                                                                    "email","link"};
    public static final String FB_APP_REDIRECT_URL = "fbconnect://success";
    public static final String FB_APP_OAUTH_BASEURL = "https://m.facebook.com/dialog";
    public static final String FB_ACCESS_TOKEN_URL = "https://graph.facebook.com/v2.5";
    public static final String FB_APP_CLIENT_SECRET = "b322828853c857f6d6a88d795536049f";
    public static final String FB_ACCESS_TOKEN = "fb_access_token";
    public static final String FB_TOKEN_EXPIRE_TIME = "fb_expire_time";

    /**Linked In Constant*/
    public static final String LINKEDIN_APP_ID = "75f5adnmadabvf";
    public static final String LINKEDIN_APP_CLIENT_SECRET = "J9RVEPehFZLjOzgE";
    public static final String LINKEDIN_APP_PERMISSIONS[] = new String[] {"r_basicprofile","r_emailaddress"};
    public static final String LINKEDIN_APP_REDIRECT_URL = "https://co.example.praveen.oauthsampleapplication.redirecturl";
    public static final String LINKEDIN_APP_OAUTH_BASEURL = "https://www.linkedin.com/uas/oauth2/authorization";
    public static final String LINKEDIN_ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken";
    public static final String LINKEDIN_PROFILE_URL = "https://api.linkedin.com/v1/people/~";
    public static final String LINKEDIN_OAUTH_ACCESS_TOKEN_PARAM ="oauth2_access_token";
    public static final String LINKEDIN_ACCESS_TOKEN = "linkedin_access_token";
    public static final String LINKEDIN_TOKEN_EXPIRE_TIME = "linkedin_expire_time";


    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String AMPERSAND_PARAM = "&";
    public static final String QUESTION_MARK = "?";
    public static final String EQUALS_PARAM = "=";
    public static final String REDIRECT_URI_PARAM = "redirect_uri";
    public static final String CLIENT_SECRET_PARAM = "client_secret";
    public static final String RESPONSE_PARAM = "code";
    public static final String APP_OAUTH_URL = "/oauth";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    public static final String STATE_PARAM = "state";
    public static final String STATE = "hdwvdbwdhiw";
    public static final String DISPLAY_STRING = "touch";



    public static String getFBProfileUrl(String accessToken) {
        String profileUrl = FB_ACCESS_TOKEN_URL
                + "/me"
                + QUESTION_MARK
                + "fields="
                + "name,email,birthday,relationship_status,cover,link"
                + AMPERSAND_PARAM
                + "access_token="
                + accessToken
                ;
        return profileUrl;
    }

    public static String getFBAccessTokenUrl(String authorizationToken) {
        String accessTokenUrl = AppCommon.FB_ACCESS_TOKEN_URL
                + AppCommon.APP_OAUTH_URL
                + "/access_token?"
                + AppCommon.CLIENT_ID_PARAM + AppCommon.EQUALS_PARAM +AppCommon.FB_APP_ID
                + AppCommon.AMPERSAND_PARAM
                + AppCommon.REDIRECT_URI_PARAM +AppCommon.EQUALS_PARAM + AppCommon.FB_APP_REDIRECT_URL
                + AppCommon.AMPERSAND_PARAM
                + AppCommon.CLIENT_SECRET_PARAM + AppCommon.EQUALS_PARAM + AppCommon.FB_APP_CLIENT_SECRET
                + AppCommon.AMPERSAND_PARAM
                + AppCommon.RESPONSE_PARAM + AppCommon.EQUALS_PARAM + authorizationToken
                ;
        return accessTokenUrl;
    }

    public static String getFBAuthenticationUrl() {
        String authRequestRedirect =
                AppCommon.FB_APP_OAUTH_BASEURL+AppCommon.APP_OAUTH_URL
                        + "?client_id="+AppCommon.FB_APP_ID
                        + "&response_type=code"
                        + "&display=touch"
                        + "&scope=" + TextUtils.join(",", AppCommon.FB_APP_PERMISSIONS)
                        + "&redirect_uri="+AppCommon.FB_APP_REDIRECT_URL
                        + "&"
                        +AppCommon.STATE_PARAM + "="
                        +AppCommon.STATE
                ;
        return authRequestRedirect;
    }

    public static String getLinkedInAuthURL() {
        String authenticationUrl = LINKEDIN_APP_OAUTH_BASEURL
                +QUESTION_MARK + RESPONSE_TYPE_PARAM +EQUALS_PARAM +  RESPONSE_PARAM
                +AMPERSAND_PARAM + CLIENT_ID_PARAM + EQUALS_PARAM + LINKEDIN_APP_ID
                +AMPERSAND_PARAM + REDIRECT_URI_PARAM + EQUALS_PARAM + LINKEDIN_APP_REDIRECT_URL
                +AMPERSAND_PARAM + STATE_PARAM + EQUALS_PARAM + STATE
                +AMPERSAND_PARAM + "scope" + EQUALS_PARAM + TextUtils.join(",", LINKEDIN_APP_PERMISSIONS)
                ;
        return authenticationUrl;
    }

    public static String getLinkedInAccessTokenURL(String authorizationCode) {
        return LINKEDIN_ACCESS_TOKEN_URL
                + QUESTION_MARK + GRANT_TYPE_PARAM + EQUALS_PARAM + GRANT_TYPE
                + AMPERSAND_PARAM + RESPONSE_PARAM + EQUALS_PARAM + authorizationCode
                + AMPERSAND_PARAM + REDIRECT_URI_PARAM + LINKEDIN_APP_REDIRECT_URL
                + AMPERSAND_PARAM + CLIENT_ID_PARAM + EQUALS_PARAM + LINKEDIN_APP_ID
                + AMPERSAND_PARAM + CLIENT_SECRET_PARAM + LINKEDIN_APP_CLIENT_SECRET
                ;
    }

    public static String getLinkedInProfileUrl(String accessToken) {
        return LINKEDIN_PROFILE_URL
                + QUESTION_MARK + LINKEDIN_OAUTH_ACCESS_TOKEN_PARAM + accessToken;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqH, int reqW) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqH || width > reqW) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqH
                    && (halfWidth / inSampleSize) > reqW) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

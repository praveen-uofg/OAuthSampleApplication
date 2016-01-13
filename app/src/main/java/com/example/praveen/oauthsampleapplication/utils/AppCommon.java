package com.example.praveen.oauthsampleapplication.utils;

import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by praveen on 1/6/2016.
 */
public class AppCommon {

    public static final String FB_NETWORK = "facebook";
    public static final String LINKEDIN_NETWORK = "linkedin";

    /**Facebook Constant*/
    public static final String FB_APP_ID = "458542597687422";
    public static final String FB_APP_PERMISSIONS [] = new String[] {"public_profile ","user_birthday","user_hometown",
                                                                    "email","user_photos","user_friends"};
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


    /*Twitter Constants*/
    public static final String TWITTER_APP_ID = "uHFCakWgYRwAHaPQGcc8XXgjn";
    public static final String TWITTER_APP_CLIENT_SECRET = "wCWkdxubZYkWjga6h7fumk9WKnnpEzNyc4xx3PmfhFWJ1PEcov";
    public static final String TWITTER_APP_OAUTH_BASEURL = "https://api.twitter.com/oauth2/token";


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
        return FB_ACCESS_TOKEN_URL
                + "/me"
                + QUESTION_MARK + "fields" + EQUALS_PARAM + "name,email,birthday,picture.type(large),link,friends,hometown"
                + AMPERSAND_PARAM + "access_token" + EQUALS_PARAM + accessToken
                ;
    }

    public static String getFBAccessTokenUrl(String authorizationToken) {
        return AppCommon.FB_ACCESS_TOKEN_URL
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
    }

    public static String getFBAuthenticationUrl() {
        return FB_APP_OAUTH_BASEURL + APP_OAUTH_URL
                        + "?client_id="+FB_APP_ID
                        + "&response_type=code"
                        + "&display=touch"
                        + "&scope=" + TextUtils.join(",",FB_APP_PERMISSIONS)
                        + "&redirect_uri="+FB_APP_REDIRECT_URL
                        + "&"
                        +AppCommon.STATE_PARAM + "="
                        +AppCommon.STATE
                ;
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
                + AMPERSAND_PARAM + REDIRECT_URI_PARAM + EQUALS_PARAM + LINKEDIN_APP_REDIRECT_URL
                + AMPERSAND_PARAM + CLIENT_ID_PARAM + EQUALS_PARAM + LINKEDIN_APP_ID
                + AMPERSAND_PARAM + CLIENT_SECRET_PARAM + EQUALS_PARAM + LINKEDIN_APP_CLIENT_SECRET
                ;
    }

    public static String getLinkedInProfileUrl(String accessToken) {
        return LINKEDIN_PROFILE_URL
                + ":(id,email-address,formatted-name,picture-url,headline,num-connections,site-standard-profile-request)"
                + QUESTION_MARK +"format"
                + EQUALS_PARAM + "json";
    }

    public static String  getTwitterAuthUrl() {
        return TWITTER_APP_OAUTH_BASEURL
                + AMPERSAND_PARAM +  "grant_type" + EQUALS_PARAM + "client_credentials"
                ;
    }

    public static String getBase64EncodedString() {
        String result = null;
        String concat = TWITTER_APP_ID + ":" + TWITTER_APP_CLIENT_SECRET;
        try {
            byte[] data = concat.getBytes("UTF-8");
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
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

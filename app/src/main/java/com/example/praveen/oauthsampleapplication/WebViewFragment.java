package com.example.praveen.oauthsampleapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.praveen.oauthsampleapplication.utils.AppCommon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WebViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebViewFragment extends Fragment {

    private static final String ARG_NETWORK = "param2";

    // TODO: Rename and change types of parameters
    private String mNetwork;
    private WebView webView;
    ProgressDialog mProgress;

    private OnFragmentInteractionListener mListener;

    public WebViewFragment() {
        // Required empty public constructor
    }

    public static WebViewFragment newInstance( String network) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NETWORK, network);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNetwork = getArguments().getString(ARG_NETWORK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        webView = (WebView)view.findViewById(R.id.webView);
        setUpWebView();
        return view;
    }

    private void setUpWebView() {
        webView.setWebViewClient(new WebViewFragment.OAuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Loading");

        if (mNetwork.equals(AppCommon.FB_NETWORK)) {
            webView.loadUrl(AppCommon.getFBAuthenticationUrl());
        } else if (mNetwork.equals(AppCommon.LINKEDIN_NETWORK)){
            webView.loadUrl(AppCommon.getLinkedInAuthURL());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String accessToken, String network);
    }


    private class OAuthWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgress.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("URL","url = "+url);
            if (url.startsWith(AppCommon.FB_APP_REDIRECT_URL) || url.startsWith(AppCommon.LINKEDIN_APP_REDIRECT_URL)) {
                Log.i("Authorize", "");
                Uri uri = Uri.parse(url);

                String stateToken = uri.getQueryParameter(AppCommon.STATE_PARAM);
                if (stateToken == null || !stateToken.equals(AppCommon.STATE)) {
                    Log.e("Authorize", "State token doesn't match");
                    return true;
                }

                String authorizationToken = uri.getQueryParameter(AppCommon.RESPONSE_PARAM);
                if (authorizationToken == null) {
                    Log.i("Authorize", "The user doesn't allow authorization.");
                    return true;
                }
                String accessTokenUrl = getAccessTokenUrl(authorizationToken);
                Log.i("Authorize", "accessTokenUrl = "+accessTokenUrl);

                if (accessTokenUrl != null) {
                    new GetAccessTokenAsyncTask().execute(accessTokenUrl);
                }

            } else if (url.contains(AppCommon.DISPLAY_STRING)) {
                return false;
            } else {
                webView.loadUrl(url);
            }
            return true;
        }
    }

    private String getAccessTokenUrl(String authorizationToken) {
        if (mNetwork.equals(AppCommon.FB_NETWORK)) {
            return AppCommon.getFBAccessTokenUrl(authorizationToken);
        } else if (mNetwork.equals(AppCommon.LINKEDIN_NETWORK)) {
            return AppCommon.getLinkedInAccessTokenURL(authorizationToken);
        }
        return  null;
    }

    class GetAccessTokenAsyncTask extends AsyncTask <String, Void, Boolean> {
        String mAccessToken;
        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
            if (mAccessToken != null) {
                mListener.onFragmentInteraction(mAccessToken,ARG_NETWORK);
            }
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if (urls.length > 0) {
                String tokenUrl = urls[0];
                StringBuilder jsonString;
                try {
                    URL url = new URL(tokenUrl);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    if (mNetwork.equals(AppCommon.FB_NETWORK)) {
                        connection.setRequestMethod("GET");
                    } else if (mNetwork.equals(AppCommon.LINKEDIN_NETWORK)) {
                        connection.setRequestMethod("POST");
                    }
                    connection.setDoInput(true);
                    connection.connect();
                    if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201) {
                        jsonString = new StringBuilder();
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = br.readLine()) != null) {
                            jsonString.append(line+"\n");
                        }
                        br.close();

                        JSONObject jsonObject = new JSONObject(jsonString.toString());
                        parseJsonData(jsonObject);
                        return true;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        private void parseJsonData(JSONObject jsonObject) {
            try {
                int expiresIn = jsonObject.has("expires_in") ? jsonObject.getInt("expires_in") : 0;
                mAccessToken = jsonObject.has("access_token") ? jsonObject.getString("access_token") : null;
                Log.d("Access Token ","token = "+mAccessToken );
                if (expiresIn > 0 && mAccessToken != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND,expiresIn);

                    long expireDate = calendar.getTimeInMillis();
                    SharedPreferences preferences = getActivity().getSharedPreferences("user_info",0);
                    SharedPreferences.Editor editor = preferences.edit();
                    if (mNetwork.equals(AppCommon.FB_NETWORK)) {
                        editor.putLong(AppCommon.FB_TOKEN_EXPIRE_TIME,expireDate);
                        editor.putString(AppCommon.FB_ACCESS_TOKEN,mAccessToken);
                    } else if (mNetwork.equals(AppCommon.LINKEDIN_NETWORK)) {
                        editor.putLong(AppCommon.LINKEDIN_TOKEN_EXPIRE_TIME,expireDate);
                        editor.putString(AppCommon.LINKEDIN_ACCESS_TOKEN,mAccessToken);
                    }
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

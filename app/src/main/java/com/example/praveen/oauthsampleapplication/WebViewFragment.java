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
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mUrl;
    private String mParam2;
    private WebView webView;
    ProgressDialog mProgress;

    private OnFragmentInteractionListener mListener;

    public WebViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebViewFragment newInstance(String param1, String param2) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        webView.loadUrl(mUrl);
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

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
        // TODO: Update argument type and name
        void onFragmentInteraction(String accessToken);
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
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Toast.makeText(getActivity(),"error  = "+error,Toast.LENGTH_LONG).show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(AppCommon.FB_APP_REDIRECT_URL)) {
                Log.i("Authorize", "");
                Uri uri = Uri.parse(url);
                //We take from the url the authorizationToken and the state token. We have to check that the state token returned by the Service is the same we sent.
                //If not, that means the request may be a result of CSRF and must be rejected.
                String stateToken = uri.getQueryParameter(AppCommon.STATE_PARAM);
                if (stateToken == null || !stateToken.equals("hdwvdbwdhiw")) {
                    Log.e("Authorize", "State token doesn't match");
                    return true;
                }

                //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                String authorizationToken = uri.getQueryParameter("code");
                if (authorizationToken == null) {
                    Log.i("Authorize", "The user doesn't allow authorization.");
                    return true;
                }
                String accessTokenUrl = getAccessTokenUrl(authorizationToken);
                Toast.makeText(getActivity(),"accessUrl = "+accessTokenUrl,Toast.LENGTH_LONG).show();
                Log.i("Authorize", "accessTokenUrl = "+accessTokenUrl);
                new GetAccessTokenAsyncTask().execute(new String []{accessTokenUrl});

            } else if (url.contains(AppCommon.DISPLAY_STRING)) {
                return false;
            }
            return true;
        }
    }

    private String getAccessTokenUrl(String authorizationToken) {
        String accessTokenUrl = AppCommon.FB_ACCESS_TOKEN_URL + "/access_token?"
                + AppCommon.CLIENT_ID_PARAM + AppCommon.EQUALS_PARAM +AppCommon.FB_APP_ID
                + AppCommon.AMPERSAND_PARAM
                + AppCommon.REDIRECT_URI_PARAM +AppCommon.EQUALS_PARAM + AppCommon.FB_APP_REDIRECT_URL
                + AppCommon.AMPERSAND_PARAM
                + AppCommon.CLIENT_SECRET_PARAM + AppCommon.EQUALS_PARAM + AppCommon.FB_APP_CLIENT_SECRET
                + AppCommon.AMPERSAND_PARAM
                + AppCommon.RESPONSE_TYPE_PARAM + AppCommon.EQUALS_PARAM + authorizationToken
                ;
        return accessTokenUrl;
    }

    class GetAccessTokenAsyncTask extends AsyncTask <String, Void, Boolean> {
        String mAccessToken;
        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
            if (mAccessToken != null) {
                mListener.onFragmentInteraction(mAccessToken);
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
                    connection.setRequestMethod("GET");
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
                String mAccessToken = jsonObject.has("access_token") ? jsonObject.getString("access_token") : null;
                Log.d("Access Token ","token = "+mAccessToken );
                if (expiresIn > 0 && mAccessToken != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND,expiresIn);

                    long expireDate = calendar.getTimeInMillis();
                    SharedPreferences preferences = getActivity().getSharedPreferences("user_info",0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong("expires",expireDate);
                    editor.putString("access_token",mAccessToken);
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

package com.example.praveen.oauthsampleapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.praveen.oauthsampleapplication.utils.AppCommon;
import com.example.praveen.oauthsampleapplication.utils.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "accessToken";
    private static final String ARG_PARAM2 = "param2";

    private ProgressDialog dialog;
    private String mAccessToken;
    private String mNetwork;
    private boolean mEmptyView = false;
    private ImageView mProfileImage;
    private TextView mNameTextView;
    private TextView mBithdayTextView;
    private TextView mEmailTextView;
    private TextView mProfileLinkTextView;
    private LinearLayout mLinearLayout;
    private TextView mLocationTextView;
    private TextView mCountTextView;

    private Bitmap profileBitmap;
    String accessToken = null;

    private UserInfo userInfo;

    private OnProfileFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String accessToken, String network) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, accessToken);
        args.putString(ARG_PARAM2, network);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAccessToken = getArguments().getString(ARG_PARAM1);
            mNetwork = getArguments().getString(ARG_PARAM2);
        }
        userInfo = new UserInfo();
        loadProfile();
    }

    private void loadProfile() {

        if (mAccessToken != null) {
            mEmptyView = false;
            accessToken = mAccessToken;
        } else {
            SharedPreferences preferences = getActivity().getSharedPreferences("user_info", 0);
            if (mNetwork.equals(AppCommon.FB_NETWORK)) {
                accessToken = preferences.getString(AppCommon.FB_ACCESS_TOKEN, null);
            } else if (mNetwork.equals(AppCommon.LINKEDIN_NETWORK)) {
                accessToken = preferences.getString(AppCommon.LINKEDIN_ACCESS_TOKEN, null);
            }
        }
        if (accessToken != null) {
            mEmptyView = false;
            new GetProfileInfoAsyncTask().execute(getProfileUrl(accessToken));
        } else {
            mEmptyView = true;
        }
    }

    private String getProfileUrl(String token) {
        if (mNetwork.equals(AppCommon.FB_NETWORK)) {
            return AppCommon.getFBProfileUrl(token);
        } else if (mNetwork.equals(AppCommon.LINKEDIN_NETWORK)) {
            return AppCommon.getLinkedInProfileUrl(token);
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (mEmptyView) {
           view  = inflater.inflate(R.layout.empty_layout, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
            mLinearLayout = (LinearLayout)view.findViewById(R.id.mainLayout);
            mLinearLayout.setVisibility(View.INVISIBLE);
        }
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        if (mEmptyView) {
            ImageButton connect = (ImageButton)view.findViewById(R.id.connectButton);
            if (mNetwork.equals(AppCommon.FB_NETWORK)) {
                connect.setImageResource(R.drawable.ic_fb_connect);
            } else if (mNetwork.equals(AppCommon.LINKEDIN_NETWORK)) {
                connect.setImageResource(R.drawable.linkedin_button_selector);
            }
            connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onConnectButtonPressed(mNetwork);
                }
            });
        } else {
            mProfileImage = (ImageView)view.findViewById(R.id.profileImage);
            mNameTextView = (TextView)view.findViewById(R.id.profileName);
            mEmailTextView = (TextView)view.findViewById(R.id.profileEmail);
            mBithdayTextView = (TextView)view.findViewById(R.id.profileBirthday);
            mProfileLinkTextView = (TextView)view.findViewById(R.id.profileLink);
            mLocationTextView = (TextView)view.findViewById(R.id.profileLocation);
            mCountTextView = (TextView)view.findViewById(R.id.profileConnection);
        }
    }

    public boolean isFragmentVisible() {
        return isVisible();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileFragmentInteractionListener) {
            mListener = (OnProfileFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProfileFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    class GetProfileInfoAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            if (urls.length > 0) {
                String profileUrl = urls[0];
                StringBuilder jsonString;
                try {
                    URL url = new URL(profileUrl);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);

                    if (mNetwork.equals(AppCommon.LINKEDIN_NETWORK)) {
                        connection.setRequestProperty("Authorization","Bearer "+accessToken );
                    }

                    connection.connect();

                    if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201) {
                        jsonString = new StringBuilder();
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = br.readLine()) != null) {
                            jsonString.append(line + "\n");
                        }
                        br.close();

                        Log.v("LinkedIN Json"," json = "+jsonString.toString());
                        JSONObject jsonObject = new JSONObject(jsonString.toString());
                        if (mNetwork.equals(AppCommon.FB_NETWORK)) {
                            parseFbJsonData(jsonObject);
                        } else if (mNetwork.equals(AppCommon.LINKEDIN_NETWORK)) {
                            parseLinkedInProfileData(jsonObject);
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void parseFbJsonData(JSONObject jsonObject) {
            try {
                userInfo.setName(jsonObject.has("name")?jsonObject.getString("name") : null);
                userInfo.setEmail(jsonObject.has("email")?jsonObject.getString("email") : null);
                userInfo.setBirthday(jsonObject.has("birthday")?jsonObject.getString("birthday") : null);

                JSONObject locationObject = jsonObject.optJSONObject("hometown");
                userInfo.setLocation(locationObject.has("name")?locationObject.getString("name") : null);

                JSONObject picObject = jsonObject.optJSONObject("picture").optJSONObject("data");
                userInfo.setCoverUrl(picObject.has("url")? (String) picObject.get("url") :null);
                profileBitmap = getProfileImage(userInfo.getCoverUrl());

                JSONObject friendObject = jsonObject.optJSONObject("friends").optJSONObject("summary");
                userInfo.setFriendCount(friendObject.has("total_count")?friendObject.getInt("total_count"):0);
                Log.d("Facebook","total count = "+userInfo.getFriendCount());
                Log.d("Facebook","total count by json = "+friendObject.getInt("total_count"));

                userInfo.setProfileLink(jsonObject.has("link")?jsonObject.getString("link"):null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseLinkedInProfileData(JSONObject jsonObject) {
            try {
                userInfo.setName(jsonObject.has("formattedName")?jsonObject.getString("formattedName") : null);
                userInfo.setEmail(jsonObject.has("emailAddress")?jsonObject.getString("emailAddress") : null);
                userInfo.setCoverUrl(jsonObject.has("pictureUrl")?jsonObject.getString("pictureUrl"):null);
                userInfo.setFriendCount(jsonObject.has("numConnections")?jsonObject.getInt("numConnections"):0);
                userInfo.setHeadline(jsonObject.has("headline")?jsonObject.getString("headline"): null);
                JSONObject profileObject = jsonObject.optJSONObject("siteStandardProfileRequest");
                userInfo.setProfileLink(profileObject.has("url")?profileObject.getString("url"):null);

                profileBitmap = getProfileImage(userInfo.getCoverUrl());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private Bitmap getProfileImage(String coverUrl) {
            Bitmap bitmap = null;
            HttpURLConnection urlConnection;

            try {
                URL url = new URL(coverUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(30000);
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                in.close();
                urlConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mEmailTextView.setText(userInfo.getEmail());
            mNameTextView.setText(userInfo.getName());
            mBithdayTextView.setText(mNetwork.equals(AppCommon.FB_NETWORK) ? "Born on "+ userInfo.getBirthday() :
                                userInfo.getHeadline());
            String link = "<a href = "+"'"+userInfo.getProfileLink()+"'"+">"+userInfo.getProfileLink()+"</a>";
            mProfileLinkTextView.setText(Html.fromHtml(link));
            mProfileLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
            mLocationTextView.setText("From  "+userInfo.getLocation());
            if (mNetwork.equals(AppCommon.LINKEDIN_NETWORK)) {
                mLocationTextView.setVisibility(View.GONE);
            } else {
                mLocationTextView.setVisibility(View.VISIBLE);
            }
            mCountTextView.setText(mNetwork.equals(AppCommon.FB_NETWORK) ? "Total Friends "+String.valueOf(userInfo.getFriendCount()):
                                "Connections "+String.valueOf(userInfo.getFriendCount()));

            if (profileBitmap != null) {
                mProfileImage.setImageBitmap(profileBitmap);
            } else {
                mProfileImage.setImageResource(R.drawable.default_avatar);
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                mLinearLayout.setVisibility(View.VISIBLE);
            }
        }
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
    public interface OnProfileFragmentInteractionListener {
        void onConnectButtonPressed(String network);
    }
}

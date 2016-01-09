package com.example.praveen.oauthsampleapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.praveen.oauthsampleapplication.utils.AppCommon;

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

    // TODO: Rename and change types of parameters
    private String mAccessToken;
    private String mNetwork;
    private boolean mEmptyView = false;
    private ImageView mProfileImage;
    private TextView mNameTextView;
    private TextView mBithdayTextView;
    private TextView mEmailTextView;
    private TextView mProfileLinkTextView;

    private String name;
    private String birthday;
    private String email;
    private String coverUrl;
    private String relationShip;
    private Bitmap profileBitmap;
    private String profileLink;

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
        if (mAccessToken != null) {
            mEmptyView = false;
            String profileUrl = AppCommon.getFBProfileUrl(mAccessToken);
            new GetProfileInfoAsyncTask().execute(profileUrl);
        } else {
            SharedPreferences preferences = getActivity().getSharedPreferences("user_info", 0);
            String accessToken = preferences.getString("access_token", null);
            if (accessToken != null) {
                mEmptyView = false;
                String profileUrl = AppCommon.getFBProfileUrl(accessToken);
                new GetProfileInfoAsyncTask().execute(profileUrl);
            } else {
                mEmptyView = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (mEmptyView) {
           view  = inflater.inflate(R.layout.empty_layout, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
        }
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        if (mEmptyView) {
            ImageButton connect = (ImageButton)view.findViewById(R.id.connectButton);
            // TODO: ADD drawabel to button as per network
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
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onConnectButtonPressed(mNetwork);
        }
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
        protected Void doInBackground(String... urls) {
            if (urls.length > 0) {
                String profileUrl = urls[0];
                StringBuilder jsonString;
                try {
                    URL url = new URL(profileUrl);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();

                    if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201) {
                        jsonString = new StringBuilder();
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = br.readLine()) != null) {
                            jsonString.append(line + "\n");
                        }
                        br.close();
                        JSONObject jsonObject = new JSONObject(jsonString.toString());
                        parseJsonData(jsonObject);
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

        private void parseJsonData(JSONObject jsonObject) {
            try {
                name = jsonObject.has("name")?jsonObject.getString("name") : null;
                email = jsonObject.has("email")?jsonObject.getString("email") : null;
                birthday = jsonObject.has("birthday")?jsonObject.getString("birthday") : null;
                relationShip = jsonObject.has("relationship_status")?jsonObject.getString("relationship_status") : null;
                JSONObject coverObject = jsonObject.optJSONObject("cover");
                coverUrl = coverObject.has("source")? (String) coverObject.get("source") :null;
                profileBitmap = getProfileImage(coverUrl);
                profileLink = jsonObject.has("link")?jsonObject.getString("link"):null;
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

                /*final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in,null,options);*/
                DisplayMetrics display = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(display);
                float screenWidth = display.scaledDensity;
                float screenHeight = display.scaledDensity;
                /*options.inSampleSize = AppCommon.calculateInSampleSize(options,screenHeight/2,screenWidth);
                options.inJustDecodeBounds = false;*/

                Bitmap bitmapOrig = BitmapFactory.decodeStream(in);
                Matrix matrix = new Matrix();
                matrix.postScale(screenWidth,screenHeight);

                bitmap = Bitmap.createBitmap(BitmapFactory.decodeStream(in),0,0,bitmapOrig.getWidth(),bitmapOrig.getHeight(),matrix,true);
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
            Log.v("ProfileInfo","name = "+name+" email = "+email+" birthday = "+birthday+" cover = "+coverUrl);
            mEmailTextView.setText(email);
            mNameTextView.setText(name);
            mBithdayTextView.setText(birthday);
            String link = "<a href = "+"'"+profileLink+"'"+">"+profileLink+"</a>";
            mProfileLinkTextView.setText(Html.fromHtml(link));
            mProfileLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
            //Drawable drawable = new BitmapDrawable(getActivity().getResources(),profileBitmap);
            mProfileImage.setImageBitmap(profileBitmap);
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
        // TODO: Update argument type and name
        void onConnectButtonPressed(String network);
    }
}

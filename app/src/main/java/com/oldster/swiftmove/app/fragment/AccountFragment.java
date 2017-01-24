package com.oldster.swiftmove.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.databinding.FragmentAccountBinding;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;


public class AccountFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private FragmentAccountBinding mBind;
    private String TAG = AccountFragment.class.getSimpleName();

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /********************
     * Functions
     ********************/
    public AccountFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("จัดการบัญชี");
        }
        mBind.frameProfile.setOnClickListener(this);
        mBind.frameSettings.setOnClickListener(this);
        mBind.frameContact.setOnClickListener(this);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            public String jid;
            public String did;
            public String status;
            public JSONObject data;
            public String payload;
            public String title;
            public String message;

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    try {
                        title = intent.getStringExtra("title"); //check
                        message = intent.getStringExtra("message");//check
                        payload = intent.getStringExtra("payload");//check
                        data = new JSONObject(payload);//check
                        status = data.getString("status");//check
                        if (status.equals("end_job")){
                            did = data.getString("did");
                            jid = data.getString("jid");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json Exception: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }

                    //Handle Code Here!!
                    if (!status.equals("end_job")) {
                        new ShowDialogStatusJob(getContext(), title, message);
                    } else {
                        new ShowDialogReviewDriver(getContext(), did, jid);
                    }
                }

            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
    }
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(Contextor.getInstance().getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }


    /********************
     * Listener Zone
     ********************/
    @Override
    public void onClick(View v) {
        if (v == mBind.frameProfile) {
            FragmentTransaction f = getFragmentManager().beginTransaction();
            f.setCustomAnimations(
                    R.anim.from_right, R.anim.to_left,
                    R.anim.from_left, R.anim.to_right
            );
            f.replace(R.id.contentContainer, ProfileFragment.newInstance());
            f.addToBackStack(null);
            f.commit();
        }

        if (v == mBind.frameSettings) {
            FragmentTransaction f = getFragmentManager().beginTransaction();
            f.setCustomAnimations(
                    R.anim.from_right, R.anim.to_left,
                    R.anim.from_left, R.anim.to_right
            );
            f.replace(R.id.contentContainer, SettingsFragment.newInstance());
            f.addToBackStack(null);
            f.commit();
        }
        if (v == mBind.frameContact) {
            FragmentTransaction f = getFragmentManager().beginTransaction();
            f.setCustomAnimations(
                    R.anim.from_right, R.anim.to_left,
                    R.anim.from_left, R.anim.to_right
            );
            f.replace(R.id.contentContainer, ContactFragment.newInstance());
            f.addToBackStack(null);
            f.commit();
        }
    }
    /********************
     * Inner Class
     ********************/
}

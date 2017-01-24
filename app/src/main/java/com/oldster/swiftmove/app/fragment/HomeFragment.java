package com.oldster.swiftmove.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.AccountActivity;
import com.oldster.swiftmove.app.activity.DriverFavoriteActivity;
import com.oldster.swiftmove.app.activity.HistoryJobActivity;
import com.oldster.swiftmove.app.activity.MainActivity;
import com.oldster.swiftmove.app.adapter.HighlightPagerAdapter;
import com.oldster.swiftmove.app.databinding.FragmentHomeBinding;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private String TAG = HomeFragment.class.getSimpleName();
    private FragmentHomeBinding mBind;
    private int currentPage = 0;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /********************
     * Functions
     ********************/
    public HomeFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
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

        mBind.cardViewRoute.setOnClickListener(this);
        mBind.cardViewProcess.setOnClickListener(this);
        mBind.cardViewFavorite.setOnClickListener(this);
        mBind.cardViewSettings.setOnClickListener(this);

        mBind.viewpager.setAdapter(new HighlightPagerAdapter());
        mBind.indicator.setViewPager(mBind.viewpager);
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == mBind.viewpager.getChildCount()) {
                    currentPage = 0;
                }
                mBind.viewpager.setCurrentItem(currentPage++, true);
            }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 0, 10000);

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
                        if (status.equals("end_job")) {
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

    @Override
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

    @Override
    public void onClick(View view) {
        if (view == mBind.cardViewRoute) {
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }
        if (view == mBind.cardViewProcess) {
            Intent intent = new Intent(getContext(), HistoryJobActivity.class);
            intent.putExtra("index", 0);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }
        if (view == mBind.cardViewFavorite) {
            Intent intent = new Intent(getContext(), DriverFavoriteActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }
        if (view == mBind.cardViewSettings) {
            startActivity(new Intent(getContext(), AccountActivity.class));
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }
    }

    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
}

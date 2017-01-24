package com.oldster.swiftmove.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.HistoryJobActivity;
import com.oldster.swiftmove.app.adapter.CardProcessJobAdapter;
import com.oldster.swiftmove.app.dao.JobDataCollectionDao;
import com.oldster.swiftmove.app.databinding.FragmentProcessJobBinding;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.driver.DriverFavoriteManager;
import com.oldster.swiftmove.app.manager.user.JobProcessManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;
import com.oldster.swiftmove.app.util.VerticalSpaceItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessJobFragment extends Fragment {

    /********************
     * Variable
     ********************/
    private String TAG = ProcessJobFragment.class.getSimpleName();
    private FragmentProcessJobBinding mBind;
    private CardProcessJobAdapter cardProcessJobAdapter;
    private JobProcessManager jobProcessManager;
    private JobDataCollectionDao jobDataCollectionDao;
    private UserDataManager uManager;
    private DriverFavoriteManager driverFavoriteManager;
    private DecimalFormat df;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /********************
     * Functions
     ********************/
    public ProcessJobFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ProcessJobFragment newInstance() {
        ProcessJobFragment fragment = new ProcessJobFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_process_job, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        uManager = new UserDataManager();
        cardProcessJobAdapter = new CardProcessJobAdapter(getContext());
        jobProcessManager = new JobProcessManager();
        driverFavoriteManager = new DriverFavoriteManager();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    loadDataJob();
                }

            }
        };
        df = new DecimalFormat("000000");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Contextor.getInstance().getContext());
        mBind.recyclerView.setLayoutManager(layoutManager);
        mBind.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBind.recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(24));
        mBind.recyclerView.setAdapter(cardProcessJobAdapter);
        mBind.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
        mBind.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataJob();
            }
        });

        loadDataJob();
    }

    private void loadDataJob() {

        int uid = uManager.getUser().getUser().get(0).getUserId();
        Call<JobDataCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataJobProcess(uid);
        call.enqueue(new Callback<JobDataCollectionDao>() {
            @Override
            public void onResponse(Call<JobDataCollectionDao> call, Response<JobDataCollectionDao> response) {
                if (response.isSuccessful()) {
                    mBind.recyclerView.removeAllViews();
                    jobDataCollectionDao = response.body();
                    mBind.progressBar.setVisibility(View.GONE);
                    if (jobDataCollectionDao.getData().size() == 0) {
                        mBind.textViewMessage.setVisibility(View.VISIBLE);
                        jobProcessManager.setJobDataCollectionDao(jobDataCollectionDao);
                        cardProcessJobAdapter.setJobData(jobProcessManager.getJobDataCollectionDao());
                    } else {
                        jobProcessManager.setJobDataCollectionDao(jobDataCollectionDao);
                        cardProcessJobAdapter.setJobData(jobProcessManager.getJobDataCollectionDao());
                        mBind.textViewMessage.setVisibility(View.GONE);
                        mBind.recyclerView.setVisibility(View.VISIBLE);

                    }
                    cardProcessJobAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG + " isSuccessful else", response.errorBody().toString());
                }
                mBind.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<JobDataCollectionDao> call, Throwable t) {
                mBind.progressBar.setVisibility(View.GONE);
                mBind.textViewMessage.setVisibility(View.VISIBLE);
                Log.e(TAG + " onFailure", t.toString());
                mBind.swipeRefreshLayout.setRefreshing(false);
            }
        });
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
        outState.putBundle("jobProcessManager",
                jobProcessManager.onSaveInstanceState());
        // Save Instance State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
        jobProcessManager.onRestoreInstanceState(
                savedInstanceState.getBundle("jobProcessManager"));
    }

    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
}

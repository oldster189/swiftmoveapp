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
import com.oldster.swiftmove.app.adapter.CardHistoryJobAdapter;
import com.oldster.swiftmove.app.dao.JobDataCollectionDao;
import com.oldster.swiftmove.app.databinding.FragmentHistoryJobBinding;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.user.JobProcessManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;
import com.oldster.swiftmove.app.util.VerticalSpaceItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryJobFragment extends Fragment {

    /********************
     * Variable
     ********************/
    private String TAG = HistoryJobFragment.class.getSimpleName();
    private FragmentHistoryJobBinding mBind;
    private CardHistoryJobAdapter cardHistoryJobAdapter;
    private JobProcessManager jobProcessManager;
    private JobDataCollectionDao jobDataCollectionDao;
    private UserDataManager uManager;

    /********************
     * Functions
     ********************/
    public HistoryJobFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static HistoryJobFragment newInstance() {
        HistoryJobFragment fragment = new HistoryJobFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_history_job, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        uManager = new UserDataManager();
        cardHistoryJobAdapter = new CardHistoryJobAdapter(getContext());
        jobProcessManager = new JobProcessManager();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Contextor.getInstance().getContext());
        mBind.recyclerView.setLayoutManager(layoutManager);
        mBind.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBind.recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(24));
        //  cardProcessJobAdapter.setJobData(jobProcessManager.getJobDataCollectionDao());
        mBind.recyclerView.setAdapter(cardHistoryJobAdapter);
        mBind.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
        mBind.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataJob();

            }
        });
     /* if (savedInstanceState == null){
            mBind.recyclerView.setVisibility(View.GONE);
            loadDataJob();
        }
*/
        loadDataJob();

    }

    private void loadDataJob() {
        int uid = uManager.getUser().getUser().get(0).getUserId();
        Log.e(TAG, uid+" uid");
        Call<JobDataCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataJobHistory(uid);
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
                        cardHistoryJobAdapter.setJobData(jobProcessManager.getJobDataCollectionDao());
                    } else {
                        jobProcessManager.setJobDataCollectionDao(jobDataCollectionDao);
                        cardHistoryJobAdapter.setJobData(jobProcessManager.getJobDataCollectionDao());
                        mBind.textViewMessage.setVisibility(View.GONE);
                        mBind.recyclerView.setVisibility(View.VISIBLE);
                        Log.e(TAG, jobDataCollectionDao.getData().get(0).getJobRating() + " rate " + jobDataCollectionDao.getData().get(0).getJobId());
                    }
                    cardHistoryJobAdapter.notifyDataSetChanged();
                    Log.e(TAG + " isSuccessful true", "true");
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

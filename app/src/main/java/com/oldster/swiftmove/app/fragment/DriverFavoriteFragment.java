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
import com.oldster.swiftmove.app.adapter.CardDriverFavoriteAdapter;
import com.oldster.swiftmove.app.dao.DriverFavoriteCollectionDao;
import com.oldster.swiftmove.app.databinding.FragmentDriverFavoriteBinding;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.driver.DriverFavoriteManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class DriverFavoriteFragment extends Fragment {

    /********************
     * Variable
     ********************/
    private String TAG = DriverFavoriteFragment.class.getSimpleName();
    private FragmentDriverFavoriteBinding mBind;
    private CardDriverFavoriteAdapter cardDriverFavoriteAdapter;
    private DriverFavoriteCollectionDao driverFavoriteCollectionDao;
    private DriverFavoriteManager driverFavoriteManager;
    private UserDataManager uManager;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /********************
     * Functions
     ********************/
    public DriverFavoriteFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static DriverFavoriteFragment newInstance() {
        DriverFavoriteFragment fragment = new DriverFavoriteFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_driver_favorite, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        cardDriverFavoriteAdapter = new CardDriverFavoriteAdapter(getContext(),getActivity());
        driverFavoriteManager = new DriverFavoriteManager();
        uManager = new UserDataManager();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here


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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Contextor.getInstance().getContext());
        mBind.recyclerView.setLayoutManager(layoutManager);
        mBind.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBind.recyclerView.setAdapter(cardDriverFavoriteAdapter);
        mBind.recyclerView.setVisibility(View.GONE);
        mBind.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
        mBind.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDriverFavorite();
            }
        });

        loadDriverFavorite();

    }

    private void loadDriverFavorite() {
        mBind.textViewMessage.setVisibility(View.GONE);
        mBind.recyclerView.setVisibility(View.GONE);
        mBind.progressBar.setVisibility(View.VISIBLE);
        int uid = uManager.getUser().getUser().get(0).getUserId();
        Call<DriverFavoriteCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataDriverFavorite(uid);
        call.enqueue(new Callback<DriverFavoriteCollectionDao>() {
            @Override
            public void onResponse(Call<DriverFavoriteCollectionDao> call, Response<DriverFavoriteCollectionDao> response) {
                mBind.swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    mBind.progressBar.setVisibility(View.GONE);
                    driverFavoriteCollectionDao = response.body();
                    mBind.recyclerView.removeAllViews();
                    driverFavoriteManager.setDataDriverFavorite(driverFavoriteCollectionDao);
                    if (driverFavoriteCollectionDao.getData().size() == 0) {
                        mBind.textViewMessage.setVisibility(View.VISIBLE);
                    } else {
                        cardDriverFavoriteAdapter.setData(driverFavoriteManager.getDataDriverFavorite());
                        mBind.textViewMessage.setVisibility(View.GONE);
                        mBind.recyclerView.setVisibility(View.VISIBLE);
                        cardDriverFavoriteAdapter.notifyDataSetChanged();
                    }

                } else {
                    Log.e(TAG + " isSuccess else", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<DriverFavoriteCollectionDao> call, Throwable t) {
                Log.e(TAG + " onFailure", t.toString());
                mBind.textViewMessage.setVisibility(View.VISIBLE);
                mBind.progressBar.setVisibility(View.GONE);
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

    /********************
     * Inner Class
     ********************/
}

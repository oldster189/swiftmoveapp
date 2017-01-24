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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.adapter.CardDriverAdapter;
import com.oldster.swiftmove.app.dao.DriverDataAfterSortCollection;
import com.oldster.swiftmove.app.dao.DriverFavoriteCollectionDao;
import com.oldster.swiftmove.app.databinding.FragmentDriverBinding;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.PositionMapManager;
import com.oldster.swiftmove.app.manager.driver.DriverFavoriteManager;
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

public class DriverFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private FragmentDriverBinding mBind;
    private String TAG = DriverFragment.class.getSimpleName();
    private CardDriverAdapter cardDriverAdapter;
    private DriverDataAfterSortCollection dataDriver;
    private UserDataManager uManager;
    private DriverFavoriteManager driverFavoriteManager;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /********************
     * Functions
     ********************/
    public DriverFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static DriverFragment newInstance() {
        DriverFragment fragment = new DriverFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_driver, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        cardDriverAdapter = new CardDriverAdapter(getContext());
        uManager = new UserDataManager();
        driverFavoriteManager = new DriverFavoriteManager();
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

        Glide.with(getActivity())
                .load(R.drawable.active_sel)
                .asGif()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBind.subRing);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Contextor.getInstance().getContext());
        mBind.recyclerView.setLayoutManager(layoutManager);
        mBind.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBind.recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(24));
        mBind.recyclerView.setAdapter(cardDriverAdapter);
        mBind.recyclerView.setVisibility(View.GONE);
        mBind.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
        mBind.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataDriver();

            }
        });

        SaveCacheDriverFavorite();
        loadDataDriver();


    }

    private void SaveCacheDriverFavorite() {
        int uid = uManager.getUser().getUser().get(0).getUserId();
        Call<DriverFavoriteCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataDriverFavorite(uid);
        call.enqueue(new Callback<DriverFavoriteCollectionDao>() {
            @Override
            public void onResponse(Call<DriverFavoriteCollectionDao> call, Response<DriverFavoriteCollectionDao> response) {
                if (response.isSuccessful()) {
                    DriverFavoriteCollectionDao data = response.body();
                    Log.e(TAG, "Size:"+data.getData().size() + "");
                    driverFavoriteManager.setDataDriverFavorite(data);
                    Log.e(TAG + " SaveCache ", "true");
                } else {
                    Log.e(TAG + " isSuccess else", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<DriverFavoriteCollectionDao> call, Throwable t) {
                Log.e(TAG + " onFailure", t.toString());
            }
        });
    }


    private void loadDataDriver() {
        mBind.textViewMessage.setVisibility(View.GONE);
        mBind.recyclerView.setVisibility(View.GONE);
        mBind.progressBar.setVisibility(View.VISIBLE);

        String province = PositionMapManager.getInstance().getProvinceFrom();
        double lat = PositionMapManager.getInstance().getLatitudeFrom();
        double lng = PositionMapManager.getInstance().getLongitudeFrom();
        Log.e(TAG, province + " " + lat + " " + lng + " " + 25 + " " + PositionMapManager.getInstance().getTypeCar());
        Call<DriverDataAfterSortCollection> call = HttpManager.getInstance()
                .getService()
                .loadDataDriver(province, lat, lng, 25, PositionMapManager.getInstance().getTypeCar());

        call.enqueue(new Callback<DriverDataAfterSortCollection>() {
            @Override
            public void onResponse(Call<DriverDataAfterSortCollection> call, Response<DriverDataAfterSortCollection> response) {
                mBind.swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    mBind.recyclerView.removeAllViews();
                    dataDriver = response.body();
                    if (dataDriver.isSuccess()) {
                        mBind.progressBar.setVisibility(View.GONE);
                        if (dataDriver.getDriver().size() == 0) {
                            Log.e(TAG + " size", "0");
                            mBind.imgNo.setVisibility(View.VISIBLE);
                            mBind.textViewMessage.setVisibility(View.VISIBLE);
                            mBind.textViewPerson.setText("0");
                        } else {
                            Log.e(TAG + " size", dataDriver.getDriver().size() + "");
                            cardDriverAdapter.setDataAfterSortCollection(dataDriver);
                            mBind.textViewPerson.setText(String.valueOf(dataDriver.getDriver().size()));
                            mBind.textViewMessage.setVisibility(View.GONE);
                            mBind.imgNo.setVisibility(View.GONE);
                            mBind.recyclerView.setVisibility(View.VISIBLE);
                            cardDriverAdapter.notifyDataSetChanged();
                        }
                        Log.e(TAG + " isSuccess body", "true");
                    } else {
                        Log.e(TAG + " isSuccess body", "false");
                    }
                } else {
                    Log.e(TAG + " isSuccessful", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<DriverDataAfterSortCollection> call, Throwable t) {
                Log.e(TAG + " onFailure", t.toString());
                mBind.textViewMessage.setVisibility(View.VISIBLE);
                mBind.textViewPerson.setText("0");
                mBind.progressBar.setVisibility(View.GONE);
                mBind.swipeRefreshLayout.setRefreshing(false);
                mBind.imgNo.setVisibility(View.VISIBLE);
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

    }

    /********************
     * Inner Class
     ********************/
}

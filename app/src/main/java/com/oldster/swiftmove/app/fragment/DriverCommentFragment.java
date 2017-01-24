package com.oldster.swiftmove.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.oldster.swiftmove.app.adapter.CardCommentAdapter;
import com.oldster.swiftmove.app.dao.CommentDataCollectionDao;
import com.oldster.swiftmove.app.dao.DriverDataAfterSort;
import com.oldster.swiftmove.app.databinding.FragmentCommentDriverBinding;
import com.oldster.swiftmove.app.manager.HttpManager;
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

public class DriverCommentFragment extends Fragment {

    /********************
     * Variable
     ********************/
    private String TAG = DriverCommentFragment.class.getSimpleName();
    private FragmentCommentDriverBinding mBind;

    private CardCommentAdapter commentAdapter;
    private DriverDataAfterSort dataDriver;

    /********************
     * Functions
     ********************/
    public DriverCommentFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static DriverCommentFragment newInstance(DriverDataAfterSort data) {
        DriverCommentFragment fragment = new DriverCommentFragment();
        Bundle args = new Bundle();
        args.putParcelable("dataDriver", data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataDriver = getArguments().getParcelable("dataDriver");
        init(savedInstanceState);
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_comment_driver, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        commentAdapter = new CardCommentAdapter(getContext());
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBind.recyclerView.setLayoutManager(layoutManager);
        mBind.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBind.recyclerView.setAdapter(commentAdapter);
        mBind.progressBar.setVisibility(View.VISIBLE);
        loadDataComment();
    }

    private void loadDataComment() {
        Call<CommentDataCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataComment(dataDriver.getDriverId());
        call.enqueue(new Callback<CommentDataCollectionDao>() {
            @Override
            public void onResponse(Call<CommentDataCollectionDao> call, Response<CommentDataCollectionDao> response) {

                if (response.isSuccessful()) {
                    //  mBind.recyclerView.removeAllViews();
                    CommentDataCollectionDao dao = response.body();
                    if (dao.getData().size() > 0) {
                        mBind.recyclerView.setVisibility(View.VISIBLE);
                        mBind.tvNoComment.setVisibility(View.GONE);
                        mBind.progressBar.setVisibility(View.GONE);
                        commentAdapter.setData(dao);
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        mBind.recyclerView.setVisibility(View.GONE);
                        mBind.tvNoComment.setVisibility(View.VISIBLE);
                        mBind.progressBar.setVisibility(View.GONE);
                    }


                } else {
                    Log.e(TAG + " onResponse ELSE", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<CommentDataCollectionDao> call, Throwable t) {
                mBind.tvNoComment.setVisibility(View.VISIBLE);
                mBind.progressBar.setVisibility(View.GONE);
                Log.e(TAG + " onFailure", t.toString());
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

package com.oldster.swiftmove.app.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.ResetPasswordActivity;
import com.oldster.swiftmove.app.databinding.FragmentForgotPasswordBinding;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private String TAG = ForgotPasswordFragment.class.getSimpleName();

    private FragmentForgotPasswordBinding mBind;

    /********************
     * Functions
     ********************/
    public ForgotPasswordFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ForgotPasswordFragment newInstance() {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false);
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
        mBind.btnConfirm.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (v == mBind.btnConfirm) {
            startActivity(new Intent(getContext(), ResetPasswordActivity.class));
            getActivity().overridePendingTransition(R.anim.from_left, R.anim.to_right);
        }
    }

    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
}

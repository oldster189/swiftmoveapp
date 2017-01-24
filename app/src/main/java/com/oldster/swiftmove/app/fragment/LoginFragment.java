package com.oldster.swiftmove.app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.ForgotPasswordActivity;
import com.oldster.swiftmove.app.activity.HomeActivity;
import com.oldster.swiftmove.app.activity.MainActivity;
import com.oldster.swiftmove.app.activity.SignupActivity;
import com.oldster.swiftmove.app.dao.UserDataCollectionDao;
import com.oldster.swiftmove.app.databinding.FragmentLoginBinding;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {


    /********************
     * Variable
     ********************/
    FragmentLoginBinding mBind;

    private String TAG = LoginFragment.class.getSimpleName();
    private UserDataManager userDataManager;
    private ProgressDialog progressDialog;
    private UserDataCollectionDao user;

    private String email;
    private String pass;

    /********************
     * Functions
     ********************/
    public LoginFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        userDataManager = new UserDataManager();


    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here

        //Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bangna-new.ttf");
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SukhumvitSet-Medium.ttf");
        mBind.tvTextLogin.setTypeface(type);
        progressDialog = new ProgressDialog(getContext());

        mBind.textViewSignup.setOnClickListener(this);
        mBind.tvForgotPassword.setOnClickListener(this);
        mBind.btnLogin.setOnClickListener(this);
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


    private void userLogin() {


        email = mBind.editTextEmailLogin.getText().toString().trim();
        pass = mBind.editTextPassword.getText().toString().trim();

        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }

        progressDialog.setMessage("กำลังเข้าสู่ระบบ...");
        progressDialog.show();
        //logging in the user

        Call<UserDataCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loginUser(email, pass);
        call.enqueue(new UserLoadCallBack());


    }

    private void showToast(String text) {
        Toast.makeText(getActivity(),
                text,
                Toast.LENGTH_SHORT).show();
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private boolean validateEmail() {
        email = mBind.editTextEmailLogin.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            mBind.inputLayoutEmail.setError("ระบุอีเมลให้ถูกต้อง");
            requestFocus(mBind.editTextEmailLogin);
            return false;
        } else {
            mBind.inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        pass = mBind.editTextPassword.getText().toString().trim();
        if (pass.isEmpty() || pass.length() < 8) {
            mBind.inputLayoutPassword.setError("ระบุรหัสผ่านให้ถูกต้อง");
            requestFocus(mBind.editTextPassword);
            return false;
        } else {
            mBind.inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    /********************
     * Listener Zone
     ********************/
    @Override
    public void onClick(View view) {


        if (view == mBind.btnLogin) {
            userLogin();
        }

        if (view == mBind.textViewSignup) {
            startActivity(new Intent(getContext(), SignupActivity.class));
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }

        if (view == mBind.tvForgotPassword) {
            startActivity(new Intent(getContext(), ForgotPasswordActivity.class));
            getActivity().overridePendingTransition(R.anim.from_left, R.anim.to_right);
        }

    }


    /********************
     * Inner Class
     ********************/


    private class UserLoadCallBack implements Callback<UserDataCollectionDao> {

        public UserLoadCallBack() {

        }

        @Override
        public void onResponse(Call<UserDataCollectionDao> call, Response<UserDataCollectionDao> response) {
            if (response.isSuccessful()) {
                user = response.body();
                if (user.isSuccess()) {
                    userDataManager.setUser(user);
                    Intent intent = new Intent(Contextor.getInstance().getContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
                    getActivity().finish();
                } else {
                    showToast("อีเมล หรือ รหัสผ่านไม่ถูกต้อง!");
                }
            } else {
                showToast("เกิดข้อผิดพลาดในการเข้าสู่ระบบ!");
                Log.e(TAG, response.errorBody().toString());
            }
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(Call<UserDataCollectionDao> call, Throwable t) {
            showToast("การเชื่อมต่อเครือข่ายล้มเหลว กรุณาลองใหม่อีกครั้ง!");
            Log.e(TAG, t.toString());
            progressDialog.dismiss();
        }
    }


}

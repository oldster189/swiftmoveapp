package com.oldster.swiftmove.app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.HomeActivity;
import com.oldster.swiftmove.app.activity.LoginActivity;
import com.oldster.swiftmove.app.activity.MainActivity;
import com.oldster.swiftmove.app.dao.UserDataCollectionDao;
import com.oldster.swiftmove.app.databinding.FragmentSignupBinding;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignupFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    /********************
     * Variable
     ********************/
    private FragmentSignupBinding mBind;
    private ProgressDialog progressDialog;

    private UserDataCollectionDao user;
    private UserDataManager userDataManager;
    private String pw;
    private String ch;
    private int num;
    private int lower;
    private int upper;
    private String fname;
    private String lname;
    private String tel;
    private String email;
    private String passre;
    private String pass;


    /********************
     * Functions
     ********************/
    public SignupFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s)
        userDataManager = new UserDataManager();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SukhumvitSet-Medium.ttf");
        mBind.tvTextRegister.setTypeface(type);

        progressDialog = new ProgressDialog(getActivity());

        mBind.btnRegister.setOnClickListener(this);
        mBind.editTextPassword.addTextChangedListener(new MyTextWatcher(mBind.editTextPassword));

        mBind.editTextFname.setOnFocusChangeListener(this);
        mBind.editTextLname.setOnFocusChangeListener(this);
        mBind.editTextEmail.setOnFocusChangeListener(this);
        mBind.editTextPassword.setOnFocusChangeListener(this);
        mBind.editTextPasswordRe.setOnFocusChangeListener(this);


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


    private void userSignup() {
        if (!validateFname()) {
            return;
        }
        if (!validateLname()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!validatePasswordRe()) {
            return;
        }
        if (!validateTel()) {
            return;
        }
        progressDialog.setMessage("กำลังสมัครสมาชิก...");
        progressDialog.show();

        Call<UserDataCollectionDao> call = HttpManager.getInstance().getService()
                .registerUser(fname, lname, email, pass, tel);
        call.enqueue(new UserLoadCallBack());
    }
    private boolean validateFname() {
        fname = mBind.editTextFname.getText().toString().trim();

        if (fname.isEmpty()) {
            mBind.inputLayoutFname.setError("ระบุชื่อ");
            requestFocus(mBind.editTextFname);
            return false;
        } else {
            mBind.inputLayoutFname.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLname() {
        lname = mBind.editTextLname.getText().toString().trim();

        if (lname.isEmpty()) {
            mBind.inputLayoutLname.setError("ระบุนามสกุล");
            requestFocus(mBind.editTextLname);
            return false;
        } else {
            mBind.inputLayoutLname.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validatePassword() {
        pass = mBind.editTextPassword.getText().toString().trim();
        if (pass.isEmpty() || pass.length() < 8) {
            mBind.inputLayoutPassword.setError("ระบุรหัสผ่าน");
            requestFocus(mBind.editTextPassword);
            return false;
        } else {
            mBind.inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePasswordRe() {
        passre = mBind.editTextPasswordRe.getText().toString().trim();
        if (passre.isEmpty() || !passre.equals(pass)) {
            mBind.inputLayoutPasswordRe.setError("รหัสผ่านไม่ตรงกัน ลองใหม่อีกครั้ง");
            requestFocus(mBind.editTextPasswordRe);
            return false;
        } else {
            mBind.inputLayoutPasswordRe.setErrorEnabled(false);
        }
        return true;
    }



    private boolean validateEmail() {
        email = mBind.editTextEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            mBind.inputLayoutEmail.setError("ระบุอีเมลให้ถูกต้อง");
            requestFocus(mBind.editTextEmail);
            return false;
        } else {
            mBind.inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateTel() {
        tel = mBind.editTextTel.getText().toString().trim();
        if (tel.isEmpty() || tel.length() < 10) {
            mBind.inputLayoutTel.setError("ระบุหมายเลขโทรศัพท์");
            requestFocus(mBind.editTextTel);
            return false;
        } else {
            mBind.inputLayoutTel.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void showToast(String text) {
        Toast.makeText(getActivity(),
                text,
                Toast.LENGTH_SHORT).show();
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /********************
     * Listener Zone
     ********************/
    @Override
    public void onClick(View view) {
        if (view == mBind.btnRegister) {

                userSignup();
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (view == mBind.editTextFname) {
            if (!b) {
                if (mBind.editTextFname.getText().toString().isEmpty()) {
                    mBind.inputLayoutFname.setError("ระบุชื่อ");
                } else {
                    mBind.inputLayoutFname.setErrorEnabled(false);
                }
            } else {
                // mBind.inputLayoutFname.setErrorEnabled(false);
            }
        }
        if (view == mBind.editTextLname) {
            if (!b) {
                if (mBind.editTextLname.getText().toString().isEmpty()) {
                    mBind.inputLayoutLname.setError("ระบุนามสกุล");
                } else {
                    mBind.inputLayoutLname.setErrorEnabled(false);
                }
            } else {
                //  mBind.inputLayoutLname.setErrorEnabled(false);
            }
        }
        if (view == mBind.editTextEmail) {
            if (!b) {
                if (mBind.editTextEmail.getText().toString().isEmpty() || !isValidEmail(mBind.editTextEmail.getText().toString())) {
                    mBind.inputLayoutEmail.setError("ระบุอีเมลให้ถูกต้อง");
                } else {
                    mBind.inputLayoutEmail.setErrorEnabled(false);
                }
            } else {
                //  mBind.inputLayoutEmail.setErrorEnabled(false);
            }
        }
        if (view == mBind.editTextPassword) {
            if (!b) {
                if (mBind.editTextPassword.getText().toString().isEmpty() || mBind.editTextPassword.getText().toString().length() < 8) {
                    mBind.inputLayoutPassword.setError("ระบุรหัสผ่าน");
                } else {
                    mBind.inputLayoutPassword.setErrorEnabled(false);
                }
            } else {
                // mBind.inputLayoutPassword.setErrorEnabled(false);
                if (!isValidEmail(mBind.editTextEmail.getText().toString())) {
                    mBind.inputLayoutEmail.setError("ระบุอีเมลให้ถูกต้อง");
                }
            }
        }
        if (view == mBind.editTextPasswordRe) {
            if (!b) {
                if (mBind.editTextPasswordRe.getText().toString().isEmpty() ||
                        !mBind.editTextPasswordRe.getText().toString().equals(mBind.editTextPassword.getText().toString())) {
                    mBind.inputLayoutPasswordRe.setError("ระบุรหัสผ่านอีกครั้ง");
                } else {
                    mBind.inputLayoutPasswordRe.setErrorEnabled(false);
                }
            } else {
                // mBind.inputLayoutPasswordRe.setErrorEnabled(false);
                if (mBind.editTextPassword.getText().toString().length() < 8) {
                    mBind.inputLayoutPassword.setError("ระบุรหัสผ่านให้ถูกต้อง");
                }
            }
        }
    }

    /********************
     * Inner Class
     ********************/

    private class UserLoadCallBack implements Callback<UserDataCollectionDao> {

        private UserLoadCallBack() {

        }

        @Override
        public void onResponse(Call<UserDataCollectionDao> call, Response<UserDataCollectionDao> response) {
            if (response.isSuccessful()) {
                user = response.body();
                if (user.isSuccess()) {
                    userDataManager.setUser(user);
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
                    getActivity().finish();

                } else {
                    showToast(user.getMessage());
                }
            } else {
                showToast("เกิดข้อผิดพลาดในการลงทะเบียน!");

            }
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(Call<UserDataCollectionDao> call, Throwable t) {
            showToast("การเชื่อมต่อเครือข่ายล้มเหลว กรุณาลองใหม่อีกครั้ง!");
            progressDialog.dismiss();
        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.editTextPassword:
                    pw = mBind.editTextPassword.getText().toString();
                    if (pw.length() >= 8) {
                        mBind.tvTextLevelPassword.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        for (int i = 0; i < pw.length(); i++) {
                            ch = String.valueOf(pw.charAt(i));
                            if (isNumeric(ch)) {
                                num++;
                            } else if (Character.isLowerCase(pw.charAt(i))) {
                                lower++;
                            } else if (Character.isUpperCase(pw.charAt(i))) {
                                upper++;
                            }
                        }
                        if (pw.length() >= 8 && lower >= 1 && upper >= 1 && num >= 1) {
                            mBind.tvTextLevelPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.newColorGreenNormal));
                            mBind.tvTextLevelPassword.setText("ความปลอดภัย : มาก ");
                        } else if (pw.length() >= 8 && lower >= 1 || upper >= 1) {
                            mBind.tvTextLevelPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
                            mBind.tvTextLevelPassword.setText("ความปลอดภัย : ปานกลาง ");
                        } else if (num >= 8 && lower == 0 && upper == 0) {
                            mBind.tvTextLevelPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                            mBind.tvTextLevelPassword.setText("ความปลอดภัย : น้อย ");
                        }

                    } else {
                        mBind.tvTextLevelPassword.setText("");
                    }
                    num = 0;
                    lower = 0;
                    upper = 0;
                    break;

            }
        }

    }
}

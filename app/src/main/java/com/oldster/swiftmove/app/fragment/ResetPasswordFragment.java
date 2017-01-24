package com.oldster.swiftmove.app.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.LoginActivity;
import com.oldster.swiftmove.app.databinding.FragmentResetPasswordBinding;


public class ResetPasswordFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private String TAG = ResetPasswordFragment.class.getSimpleName();

    private FragmentResetPasswordBinding mBind;

    /********************
     * Functions
     ********************/
    public ResetPasswordFragment() {
        super();
    }

    private String pw;
    private String ch;
    private int num;
    private int lower;
    private int upper;

    @SuppressWarnings("unused")
    public static ResetPasswordFragment newInstance() {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false);
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
        mBind.btnSavePassword.setOnClickListener(this);
        mBind.editTextPasswordNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pw = mBind.editTextPasswordNew.getText().toString();
                if (pw.length() >= 8) {
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

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == mBind.btnSavePassword) {
            startActivity(new Intent(getContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            getActivity().finish();
        }
    }

    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
}

package com.oldster.swiftmove.app.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.Constants.EndPoints;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.UserDataCollectionDao;
import com.oldster.swiftmove.app.dao.UserDataDao;
import com.oldster.swiftmove.app.databinding.FragmentProfileBinding;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_PICKER = 1234;
    /********************
     * Variable
     ********************/

    private String TAG = ProfileFragment.class.getSimpleName();
    private FragmentProfileBinding mBind;
    private UserDataManager userDataManager;
    private UserDataDao user;
    private ProgressDialog progressDialog;
    private UserDataCollectionDao userDao;

    private String imgEncode;
    private String imgName;
    private ArrayList<Image> images = new ArrayList<>();
    private String encodedImage;
    private String pw;
    private boolean checkInputPassword = false;
    private String ch;
    private int num = 0;
    private int lower = 0;
    private int upper = 0;
    private boolean isUpdate = true;
    private Bitmap yourbitmap;
    private int n;
    private Subscription subScription;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /********************
     * Functions
     ********************/
    public ProfileFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        userDataManager = new UserDataManager();
        user = userDataManager.getUser().getUser().get(0);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ข้อมูลส่วนตัว");
        }
        progressDialog = new ProgressDialog(getContext());

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

        loadUserData();

        mBind.btnEditPassword.setOnClickListener(this);
        mBind.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mBind.inputLayoutPassword.setErrorEnabled(false);
                pw = mBind.editTextPassword.getText().toString();
                if (pw.length() >= 8) {
                    checkInputPassword = true;
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
                    checkInputPassword = false;
                }
                num = 0;
                lower = 0;
                upper = 0;
            }
        });
        mBind.btnSave.setOnClickListener(this);
        mBind.imageProfile.setOnClickListener(this);
    }


    private void loadUserData() {
        if (user.getUserImgName() != null && !user.getUserImgName().equals("")) {
            Glide.with(getContext())
                    .load(EndPoints.BASE_URL_IMG + user.getUserImgName())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mBind.imageProfile);
        }
        if (user.getUserFname() != null) {
            mBind.editTextFname.setText(user.getUserFname());
        }
        if (user.getUserLname() != null) {
            mBind.editTextLname.setText(user.getUserLname());
        }
        mBind.editTextEmail.setText(user.getUserEmail());
        mBind.editTextTel.setText(user.getUserTel());
    }

    private void updateDataUser() {
        String fname = mBind.editTextFname.getText().toString().trim();
        String lname = mBind.editTextLname.getText().toString().trim();
        String email = mBind.editTextEmail.getText().toString().trim();
        String tel = mBind.editTextTel.getText().toString().trim();
        String passwordNew = null;
        String passwordOld = mBind.editTextPasswordOld.getText().toString().trim();

        if (!validateEmail()) {
            return;
        }
        if (mBind.editTextPassword.isEnabled()) {
            if (mBind.editTextPassword.getText().toString().length() >= 8) {
                passwordNew = mBind.editTextPassword.getText().toString().trim();
                mBind.inputLayoutPassword.setErrorEnabled(false);
                isUpdate = true;
            } else {
                isUpdate = false;
                mBind.inputLayoutPassword.setError("ระบุอย่างน้อย 8 ตัวอักขระ");
            }
        }
        if (!validateFname()) {
            return;
        }
        if (!validateLname()) {
            return;
        }
        if (!validateTel()) {
            return;
        }
        progressDialog.setMessage("กำลังปรับปรุงข้อมูล...");
        progressDialog.show();
        if (user.getUserImgName() != null) {
            n = checkSubString(user.getUserImgName());
        } else {
            n = 1;
        }
        if (images.size() > 0) {
            imgEncode = encodedImage;
            imgName = "img_profile_" + user.getUserId() + "_" + n + ".jpg";
        }
        if (isUpdate) {
            Call<UserDataCollectionDao> call = HttpManager.getInstance().getService()
                    .updateUser(fname, lname, email, tel, imgName, imgEncode, passwordOld, passwordNew, user.getUserId());
            call.enqueue(new Callback<UserDataCollectionDao>() {
                @Override
                public void onResponse(Call<UserDataCollectionDao> call, Response<UserDataCollectionDao> response) {

                    if (response.isSuccessful()) {
                        userDao = response.body();
                        if (userDao.isSuccess()) {
                            userDataManager.setUser(userDao);
                            Log.e(TAG, userDao.getUser().get(0).getUserImgName());
                            Toast.makeText(getContext(), "ปรับปรุงข้อมูลสำเร็จ", Toast.LENGTH_LONG).show();
                            mBind.editTextPassword.setEnabled(false);
                            mBind.editTextPassword.setText("********");
                            mBind.editTextPasswordOld.setText("");
                        } else {
                            Log.e(TAG, userDao.getMessage());
                            showToast(userDao.getMessage());
                        }
                    } else {
                        showToast("ปรับปรุงข้อมูลไม่สำเร็จ!");
                        Log.e(TAG, response.errorBody().toString());
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<UserDataCollectionDao> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e(TAG, t.toString());
                    showToast("การเชื่อมต่อเครือข่ายล้มเหลว กรุณาลองใหม่อีกครั้ง!");
                }
            });
        }

    }

    private int checkSubString(String text) {
        ArrayList<String> result = new ArrayList<>();
        for (int k = text.length(); k > 4; k--) {
            result.add(text.substring(k - 5, k - 4));
            Log.e("result.add", text.substring(k - 5, k - 4));
            if (text.substring(k - 6, k - 5).equals("_")) {
                break;
            }
        }
        int i = 0;
        int sum = 0;
        Log.e("result.size", result.size() + "");
        while (i < result.size()) {
            if (i == 0) {
                sum = sum + Integer.parseInt(result.get(0));
            }
            if (i == 1) {
                sum = sum + (10 * Integer.parseInt(result.get(1)));
            }
            i++;
        }
        Log.e("sum", sum + "");
        return (sum + 1);
    }

    private void showToast(String text) {
        Toast.makeText(Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT).show();
    }

    // Validating Fname
    private boolean validateFname() {
        String fname = mBind.editTextFname.getText().toString().trim();
        if (fname.isEmpty()) {
            //showToast("กรุณาป้อนชื่อจริง");
            mBind.inputLayoutFname.setError("ระบุชื่อ");
            requestFocus(mBind.editTextFname);
            return false;
        } else {
            mBind.inputLayoutFname.setErrorEnabled(false);
        }
        return true;
    }

    // Validating Lname
    private boolean validateLname() {
        String lname = mBind.editTextLname.getText().toString().trim();
        if (lname.isEmpty()) {
            //showToast("กรุณาป้อนนามสกุล");
            mBind.inputLayoutLname.setError("ระบุนามสกุล");
            requestFocus(mBind.editTextLname);
            return false;
        } else {
            mBind.inputLayoutLname.setErrorEnabled(false);
        }

        return true;
    }

    // Validating Tel
    private boolean validateTel() {
        String tel = mBind.editTextTel.getText().toString().trim();
        if (tel.isEmpty() || !isValidTel(tel)) {
            // showToast("กรุณาป้อนหมายเลขโทรศัพท์");
            mBind.inputLayoutTel.setError("ระบุหมายเลขโทรศัพท์");
            requestFocus(mBind.editTextTel);
            return false;
        } else {
            mBind.inputLayoutTel.setErrorEnabled(false);
        }

        return true;
    }

    // Validating email
    private boolean validateEmail() {
        String email = mBind.editTextEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            // showToast("กรุณาป้อนอีเมลให้ถูกต้อง");
            mBind.inputLayoutEmail.setError("ระบุอีเมลให้ถูกต้อง");
            requestFocus(mBind.editTextEmail);
            return false;
        } else {
            mBind.inputLayoutEmail.setErrorEnabled(false);
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

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidTel(String tel) {
        int num = 0;
        if (tel.length() == 10) {
            for (int i = 0; i < tel.length(); i++) {
                //check number_tel is type number
                if (isNumeric(String.valueOf(tel.charAt(i)))) {
                    num++;
                }
            }
            return num == 10;
        } else {
            return false;
        }

    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void showFileChooser() {
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_SINGLE);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, images);
        startActivityForResult(intent, REQUEST_CODE_PICKER);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            mBind.progressBarImage.setVisibility(View.VISIBLE);
            setImageByReactiveX();
            //setImage();
        }
    }

    private Bitmap getImagesBitmap() {
        try {
            yourbitmap = ImageLoader.init().from(images.get(0).getPath()).requestSize(512, 512).getBitmap();
            encodedImage = ImageBase64.encode(yourbitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return yourbitmap;
    }

    private void setImageByReactiveX() {
        Observable<Bitmap> listObservable = Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                return getImagesBitmap();
            }
        });
        subScription = listObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        mBind.progressBarImage.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG + " Throwable", e.toString());
                        mBind.progressBarImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        //mBind.imageProfile.setImageBitmap(bitmap);
                        setImage();
                    }
                });
    }

    private void unsubscribe() {
        if (subScription != null && !subScription.isUnsubscribed()) {
            subScription.unsubscribe();
        }
    }

    private void setImage() {
        Glide.with(ProfileFragment.this)
                .load(images.get(0).getPath())
                .fitCenter()
                .into(mBind.imageProfile);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubscribe();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }


    /********************
     * Listener Zone
     ********************/
    @Override
    public void onClick(View view) {
        if (view == mBind.btnSave) {
            if (mBind.editTextPasswordOld.getText().toString().length() >= 8) {
                updateDataUser();
                mBind.inputLayoutPasswordOld.setErrorEnabled(false);
            } else {
                mBind.inputLayoutPasswordOld.setError("ระบุรหัสผ่านปัจจุบัน");
                requestFocus(mBind.editTextPasswordOld);
            }
        }
        if (view == mBind.btnEditPassword) {
            mBind.editTextPassword.setEnabled(true);
            mBind.editTextPassword.setText("");
            requestFocus(mBind.editTextPassword);
        }
        if (view == mBind.imageProfile) {
            showFileChooser();
        }
    }

    /********************
     * Inner Class
     ********************/

}

package com.oldster.swiftmove.app.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.OverviewActivity;
import com.oldster.swiftmove.app.dao.DriverDataAfterSort;
import com.oldster.swiftmove.app.databinding.FragmentJobBinding;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class JobFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_PICKER = 1234;
    private static final int TIME_DIALOG_ID = 1;
    private static final int DATE_DIALOG_ID = 0;
    /********************
     * Variable
     ********************/
    private String TAG = JobFragment.class.getSimpleName();
    private FragmentJobBinding mBinding;
    private ArrayList<Image> images = new ArrayList<>();
    private DriverDataAfterSort dataDriver;
    private String date;
    private String time;
    private boolean serviceLift = false;
    private boolean serviceLiftPlus = false;
    private boolean serviceCart = false;
    private Animation shake;

    private int pYear;
    private int pMonth;
    private int pDay;

    private int pHour;
    private int pMin;
    private int liftPrice = 0;
    private int liftPlusPrice = 0;
    private int cartPrice = 0;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /********************
     * Functions
     ********************/
    public JobFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static JobFragment newInstance(DriverDataAfterSort data) {
        JobFragment fragment = new JobFragment();
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_job, container, false);
        View rootView = mBinding.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        Glide.with(getActivity())
                .load(R.drawable.active_sel)
                .asGif()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.subRing);

        //date time
        mBinding.btnDate.setOnClickListener(this);
        mBinding.btnTime.setOnClickListener(this);

        //services
        mBinding.cbServiceCartStatus.setOnClickListener(this);
        mBinding.cbServiceLiftStatus.setOnClickListener(this);
        mBinding.cbServiceLiftPlusStatus.setOnClickListener(this);

        //choose image
        mBinding.imgBtnChooseImg.setOnClickListener(this);

        //delete image
        mBinding.imgBtnCancel1.setOnClickListener(this);
        mBinding.imgBtnCancel2.setOnClickListener(this);
        mBinding.imgBtnCancel3.setOnClickListener(this);

        //Button next
        mBinding.btnNext.setOnClickListener(this);


        String liftStatus = dataDriver.getDriverDetailServiceLiftStatus();
        if (!liftStatus.equals("t")) {
            mBinding.frameLayoutLift.setVisibility(View.GONE);
        } else {
            liftPrice = dataDriver.getDriverDetailServiceLiftPrice();
            if (liftPrice == 0) {
                mBinding.tvServiceLift.setText("ฟรี");
            } else {
                mBinding.tvServiceLift.setText("฿" + liftPrice + "");
            }
        }


        String liftPlusStatus = dataDriver.getDriverDetailServiceLiftPlusStatus();
        if (!liftPlusStatus.equals("t")) {
            mBinding.frameLayoutLiftPlus.setVisibility(View.GONE);
        } else {
            liftPlusPrice = dataDriver.getDriverDetailServiceLiftPlusPrice();
            if (liftPlusPrice == 0) {
                mBinding.tvServiceLiftPlusPrice.setText("ฟรี");
            } else {
                mBinding.tvServiceLiftPlusPrice.setText("฿" + liftPlusPrice + "");
            }
        }

        String cartStatus = dataDriver.getDriverDetailServiceCartStatus();


        if (!cartStatus.equals("t")) {
            mBinding.frameLayoutCart.setVisibility(View.GONE);
        } else {
            cartPrice = dataDriver.getDriverDetailServiceCartPrice();
            if (cartPrice == 0) {
                mBinding.tvServiceCart.setText("ฟรี");
            } else {
                mBinding.tvServiceCart.setText("฿" + cartPrice + "");
            }

        }
        shake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shack);
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
    }

    private void startAnimationDate(String text) {
        //SET Animation shake ButtonTo
        mBinding.tvDateText.requestFocus();
        mBinding.tvDateText.setError(text);
        mBinding.frameDate.startAnimation(shake);
    }

    private void startAnimationTime(String text) {
        //SET Animation shake ButtonTo
        mBinding.tvTimeText.requestFocus();
        mBinding.tvTimeText.setError(text);
        mBinding.frameTime.startAnimation(shake);
    }

    private void showPicker(int code) {
        final Calendar c = Calendar.getInstance();
        pYear = c.get(Calendar.YEAR);
        pMonth = c.get(Calendar.MONTH);
        pDay = c.get(Calendar.DAY_OF_MONTH);
        pHour = c.get(Calendar.HOUR_OF_DAY);
        pMin = c.get(Calendar.MINUTE);
        onCreateDialog(code).show();

    }

    public Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:

                DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        mBinding.btnDate.setText(date);
                        mBinding.tvDateText.setError(null);
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(getContext(), pDateSetListener, pYear, pMonth, pDay);
                dialog.getDatePicker().setMinDate(new Date().getTime());
                return dialog;

            case TIME_DIALOG_ID:
                TimePickerDialog.OnTimeSetListener pTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                        if (date != null) {
                            DecimalFormat df3 = new DecimalFormat("00");
                            time = df3.format(hourOfDay) + ":" + df3.format(minute);

                            //check less than 45 min
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String dateCurrent = pYear + "-" + (pMonth + 1) + "-" + pDay + " " + pHour + ":" + pMin;
                            String dateChoice = date + " " + time;
                            Date d1 = null;
                            Date d2 = null;
                            try {
                                d1 = format.parse(dateCurrent);
                                d2 = format.parse(dateChoice);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long diff = 0;
                            if (d2 != null && d1 != null) {
                                diff = d2.getTime() - d1.getTime();
                            }
                            int minutes = (int) ((diff / (1000 * 60)) % 60);
                            int hours = (int) ((diff / (1000 * 60 * 60)) % 24);
                            Log.e(TAG, "hour:" + hours + " minutes:" + minutes);

                            if (hours >= 0 && minutes >= 45) {
                                mBinding.btnTime.setText(time);
                                mBinding.tvTimeText.setError(null);
                            } else {
                                time = null;
                                startAnimationTime("ระบุเวลาอย่างน้อย 45 นาที");
                            }
                        }else {
                            startAnimationDate("ระบุวันที่");
                        }
                    }
                };
                if (pMin + 45 >= 60) {
                    return new TimePickerDialog(getContext(), pTimeSetListener, (pHour + 1), ((pMin + 45) % 60), true);
                }
                return new TimePickerDialog(getContext(), pTimeSetListener, pHour, (pMin + 45), true);
        }
        return null;
    }

    private void showFileChooser() {
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, 3);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, images);
        startActivityForResult(intent, REQUEST_CODE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            setImage(images);
        }
    }

    private void setImage(ArrayList<Image> images) {
        for (int i = 0; i < images.size(); i++) {
            if (i == 0) {
                Glide.with(getContext())
                        .load(images.get(i).getPath())
                        .crossFade()
                        .into(mBinding.imgView1);
                mBinding.imgView1.setVisibility(View.VISIBLE);
                mBinding.btnFrameLayout1.setVisibility(View.VISIBLE);
            }
            if (i == 1) {
                Glide.with(getContext())
                        .load(images.get(i).getPath())
                        .crossFade()
                        .into(mBinding.imgView2);
                mBinding.imgView2.setVisibility(View.VISIBLE);
                mBinding.btnFrameLayout2.setVisibility(View.VISIBLE);
            }
            if (i == 2) {
                Glide.with(getContext())
                        .load(images.get(i).getPath())
                        .crossFade()
                        .into(mBinding.imgView3);
                mBinding.imgView3.setVisibility(View.VISIBLE);
                mBinding.btnFrameLayout3.setVisibility(View.VISIBLE);
            }
        }
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
    @Override
    public void onClick(View v) {

        if (v == mBinding.btnDate) {
            showPicker(DATE_DIALOG_ID);
        }
        if (v == mBinding.btnTime) {
            showPicker(TIME_DIALOG_ID);
        }
        if (v == mBinding.cbServiceCartStatus) {
            serviceCart = mBinding.cbServiceCartStatus.isChecked();
        }
        if (v == mBinding.cbServiceLiftStatus) {
            serviceLift = mBinding.cbServiceLiftStatus.isChecked();
        }
        if (v == mBinding.cbServiceLiftPlusStatus) {
            serviceLiftPlus = mBinding.cbServiceLiftPlusStatus.isChecked();
        }
        if (v == mBinding.imgBtnChooseImg) {
            showFileChooser();
        }
        if (v == mBinding.imgBtnCancel1) {
            images.remove(0);
            mBinding.imgView1.setVisibility(View.GONE);
            mBinding.btnFrameLayout1.setVisibility(View.GONE);
        }
        if (v == mBinding.imgBtnCancel2) {
            if (images.size() > 1) {
                images.remove(1);
            } else {
                images.remove(0);
            }
            mBinding.imgView2.setVisibility(View.GONE);
            mBinding.btnFrameLayout2.setVisibility(View.GONE);
        }
        if (v == mBinding.imgBtnCancel3) {
            if (images.size() > 2) {
                images.remove(2);
            } else if (images.size() > 1) {
                images.remove(1);
            } else {
                images.remove(0);
            }
            mBinding.imgView3.setVisibility(View.GONE);
            mBinding.btnFrameLayout3.setVisibility(View.GONE);
        }
        if (v == mBinding.btnNext) {
            if (date != null && !date.equals("")) {
                if (time != null && !time.equals("")) {
                    Intent intent = new Intent(getContext(), OverviewActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("serviceLift", serviceLift);
                    intent.putExtra("serviceLiftPlus", serviceLiftPlus);
                    intent.putExtra("serviceCart", serviceCart);
                    intent.putExtra("image", images);
                    intent.putExtra("dataDriver", dataDriver);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
                } else {
                    startAnimationTime("ระบุเวลา");
                }
            } else {
                startAnimationDate("ระบุวันที่");
            }
        }
    }


    /********************
     * Inner Class
     ********************/

}

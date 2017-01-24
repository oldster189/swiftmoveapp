package com.oldster.swiftmove.app.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.HistoryJobActivity;
import com.oldster.swiftmove.app.dao.DriverDataAfterSort;
import com.oldster.swiftmove.app.dao.TemplatesMessageDado;
import com.oldster.swiftmove.app.dao.UserDataDao;
import com.oldster.swiftmove.app.databinding.FragmentOverviewBinding;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.PositionMapManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
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


public class OverviewFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private String TAG = OverviewFragment.class.getSimpleName();

    private FragmentOverviewBinding mBind;
    private String date;
    private String time;
    private boolean serviceLift;
    private boolean serviceCart;
    private boolean serviceLiftPlus;
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<String> encodedImage = new ArrayList<>();
    private String encodedBitmapImage1 = "";
    private String encodedBitmapImage2 = "";
    private String encodedBitmapImage3 = "";
    private DriverDataAfterSort dataDriver;

    private ProgressDialog progressDialog;

    private UserDataManager userManager;
    private UserDataDao userDao;
    private Bitmap yourbitmap;
    private double distance;

    private int priceCart;
    private int priceLiftPlus;
    private int priceLift;
    private double total;
    private int i = 1;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Subscription subScription;

    /********************
     * Functions
     ********************/
    public OverviewFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static OverviewFragment newInstance(String date, String time, boolean serviceLift, boolean serviceCart,
                                               boolean serviceLiftPlus, ArrayList<Image> images, DriverDataAfterSort dataDriver) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString("date", date);
        args.putString("time", time);
        args.putBoolean("serviceLift", serviceLift);
        args.putBoolean("serviceCart", serviceCart);
        args.putBoolean("serviceLiftPlus", serviceLiftPlus);
        args.putParcelableArrayList("image", images);
        args.putParcelable("dataDriver", dataDriver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        date = getArguments().getString("date");
        time = getArguments().getString("time");
        serviceLift = getArguments().getBoolean("serviceLift");
        serviceCart = getArguments().getBoolean("serviceCart");
        serviceLiftPlus = getArguments().getBoolean("serviceLiftPlus");
        images = getArguments().getParcelableArrayList("image");
        dataDriver = getArguments().getParcelable("dataDriver");

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);

        init(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

        userManager = new UserDataManager();
        userDao = userManager.getUser().getUser().get(0);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        Glide.with(getActivity())
                .load(R.drawable.active_sel)
                .asGif()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBind.subRing);

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
        //set cardView 1
        mBind.tvDateTime.setText(time + ", " + date);
        mBind.tvDriver.setText("คุณ " + dataDriver.getDriverFname() + " " + dataDriver.getDriverLname());
        String typeCar = dataDriver.getDriverDetailType();
        String textTypeCar = null;
        switch (typeCar) {
            case "EcoCar":
                textTypeCar = "รถ5ประตู";
                break;
            case "Pickup":
                textTypeCar = "รถกระบะ";
                break;
            case "Truck":
                textTypeCar = "รถกระบะตู้ทึบ";
                break;

        }
        mBind.tvTypeCar.setText(textTypeCar);

        //set cardView 2
        mBind.tvPositionFrom.setText(PositionMapManager.getInstance().getNameAddressFrom());
        mBind.tvPositionTo.setText(PositionMapManager.getInstance().getNameAddressTo());

        distance = PositionMapManager.getInstance().getDistance();
        DecimalFormat df = new DecimalFormat("0.00");
        mBind.tvDistance.setText("" + df.format(distance) + " กม.");


        //set cardView 3
        //ราคาเริ่มต้น
        mBind.tvChargeStartKm.setText("ราคาเริ่มต้น (0 - " + dataDriver.getDriverDetailChargeStartKm() + " กม.)");
        int priceStart = dataDriver.getDriverDetailChargeStartPrice();
        mBind.tvChargeStartPrice.setText("฿ " + df.format(priceStart) + "");

        //คิดตามระยะทาง
        double km = 0;
        if (distance > dataDriver.getDriverDetailChargeStartKm()) {
            km = distance - dataDriver.getDriverDetailChargeStartKm();
            mBind.tvChargeKm.setText("ระยะทาง (" + df.format(km) + " กม.)");
        } else {
            mBind.tvChargeKm.setText("ระยะทาง (" + df.format(km) + " กม.)");
        }
        double priceRate = km * dataDriver.getDriverDetailCharge();
        mBind.tvChargePrice.setText("฿ " + df.format(priceRate) + "");

        //คนขับช่วยยกของ

        if (serviceLift) {
            priceLift = dataDriver.getDriverDetailServiceLiftPrice();
            if (priceLift == 0) {
                mBind.tvServiceLift.setText("ฟรี");
            } else {
                mBind.tvServiceLift.setText("฿ " + df.format(priceLift) + "");
            }
        } else {
            mBind.frameLift.setVisibility(View.GONE);
        }

        //ผู้ช่วยยกของ
        if (serviceLiftPlus) {
            priceLiftPlus = dataDriver.getDriverDetailServiceLiftPlusPrice();
            mBind.tvServiceLiftPlusPrice.setText("฿ " + df.format(priceLiftPlus) + "");
        } else {
            mBind.relativeLiftPlus.setVisibility(View.GONE);
        }

        //รถเข็น
        if (serviceCart) {
            priceCart = dataDriver.getDriverDetailServiceCartPrice();
            mBind.tvServiceCartPrice.setText("฿ " + df.format(priceCart) + "");
        } else {
            mBind.frameCart.setVisibility(View.GONE);
        }

        //รวม
        total = priceStart + priceRate + priceLift + priceLiftPlus + priceCart;
        mBind.tvListTotal.setText("฿ " + df.format(total) + "");
        mBind.tvTotal.setText("฿ " + df.format(total) + "");


        mBind.btnConfirm.setOnClickListener(this);


    }


    private ArrayList<String> getImagesEncode() {
        try {
            for (int i = 0; i < images.size(); i++) {
                yourbitmap = ImageLoader.init().from(images.get(i).getPath()).requestSize(512, 512).getBitmap();
                encodedImage.add(ImageBase64.encode(yourbitmap));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return encodedImage;
    }

    private void unsubscribe() {
        if (subScription != null && !subScription.isUnsubscribed()) {
            subScription.unsubscribe();
        }
    }

    private void processImagesEncode() {
        Observable<ArrayList<String>> listObservable = Observable.fromCallable(new Callable<ArrayList<String>>() {
            @Override
            public ArrayList<String> call() throws Exception {
                return getImagesEncode();
            }
        });
        subScription = listObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<String>>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError " + e.toString());
                    }

                    @Override
                    public void onNext(ArrayList<String> encodedImage) {

                        for (int i = 0; i < encodedImage.size(); i++) {
                            if (i == 0)
                                encodedBitmapImage1 = encodedImage.get(0);
                            if (i == 1)
                                encodedBitmapImage2 = encodedImage.get(1);
                            if (i == 2)
                                encodedBitmapImage3 = encodedImage.get(2);
                        }

                        sentDataToServer();
                    }
                });
    }


    private void insertJob() {
        progressDialog.setMessage("กำลังบันทึกข้อมูล...");
        progressDialog.show();
        processImagesEncode();
    }

    private void sentDataToServer() {
        double frLat = PositionMapManager.getInstance().getLatitudeFrom();
        double frLng = PositionMapManager.getInstance().getLongitudeFrom();
        String frAddress = PositionMapManager.getInstance().getNameAddressFrom();
        String toAddress = PositionMapManager.getInstance().getNameAddressTo();
        double toLat = PositionMapManager.getInstance().getLatitudeTo();
        double toLng = PositionMapManager.getInstance().getLongitudeTo();


        int chargeStartPrice = dataDriver.getDriverDetailChargeStartPrice();
        int chargeStartKm = dataDriver.getDriverDetailChargeStartKm();
        int chargeRate = dataDriver.getDriverDetailCharge();

        UserDataManager user = new UserDataManager();
        String jobStatus = "รอการยืนยัน";
        int userId = user.getUser().getUser().get(0).getUserId();
        int driverId = dataDriver.getDriverId();


        Call<TemplatesMessageDado> call = HttpManager.getInstance()
                .getService()
                .insertJob(frLat, frLng, frAddress, toLat, toLng, toAddress,
                        time, date, serviceLift, priceLift, serviceLiftPlus, priceLiftPlus,
                        serviceCart, priceCart, chargeStartPrice, chargeStartKm, chargeRate, jobStatus, distance, encodedBitmapImage1, encodedBitmapImage2, encodedBitmapImage3, userId, driverId, total);
        call.enqueue(insertJobCallback);
    }

    private void displayAlertConfirm() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String message = getString(R.string.please_confirm_services_again);
        builder.setMessage(message)
                .setNegativeButton("ยกเลิก",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton("ยืนยัน",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                insertJob();
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
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
    public void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    /*
         * Save Instance State Here
         */
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
    public void onClick(View v) {
        if (v == mBind.btnConfirm) {
            displayAlertConfirm();
        }

    }


    Callback<TemplatesMessageDado> insertJobCallback = new Callback<TemplatesMessageDado>() {
        @Override
        public void onResponse(Call<TemplatesMessageDado> call, Response<TemplatesMessageDado> response) {
            if (response.isSuccessful()) {
                TemplatesMessageDado dao = response.body();
                if (dao.isSuccess()) {
                    PositionMapManager.getInstance().setNameAddressTo(null);
                    PositionMapManager.getInstance().setLongitudeTo(0);
                    PositionMapManager.getInstance().setLatitudeTo(0);
                    PositionMapManager.getInstance().setTypeCar(null);
                    progressDialog.dismiss();
                    Intent intent = new Intent(Contextor.getInstance().getContext(), HistoryJobActivity.class);
                    intent.putExtra("index", 0);
                    startActivity(intent);
                }
            } else {
                Log.e(TAG+"job response", "มีข้อผิดพลาด" + response.errorBody().toString());
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<TemplatesMessageDado> call, Throwable t) {
            Log.e(TAG+"job onFailure", t.toString());
            progressDialog.dismiss();
        }
    };

    /********************
     * Inner Class
     ********************/
}

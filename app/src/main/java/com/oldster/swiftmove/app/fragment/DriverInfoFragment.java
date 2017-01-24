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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.DriverDataAfterSort;
import com.oldster.swiftmove.app.dao.DriverDataAfterSortCollection;
import com.oldster.swiftmove.app.databinding.FragmentInfoDriverBinding;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class DriverInfoFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private FragmentInfoDriverBinding mBinding;
    private String TAG = DriverInfoFragment.class.getSimpleName();
    private int position;

    private DriverDataAfterSortCollection dataAfterSortCollection;
    private DriverDataAfterSort dataDriver;
    private int liftPrice = 0;
    private int liftPlusPrice = 0;
    private int cartPrice = 0;

    /********************
     * Functions
     ********************/
    public DriverInfoFragment() {
        super();
    }


    @SuppressWarnings("unused")
    public static DriverInfoFragment newInstance(DriverDataAfterSort data) {
        DriverInfoFragment fragment = new DriverInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("dataDriver", data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        dataDriver = getArguments().getParcelable("dataDriver");
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_info_driver, container, false);
        View rootView = mBinding.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here


        DecimalFormat df = new DecimalFormat("0.0");
        DecimalFormat df2 = new DecimalFormat("00.00");
        mBinding.rtbProductRating.setRating(Float.parseFloat(dataDriver.getDriverRatingAvg() + ""));
        mBinding.textViewScore.setText(df.format(dataDriver.getDriverRatingAvg()) + " (" + dataDriver.getDriverRatingCount() + ")");
        mBinding.textViewBrand.setText(dataDriver.getDriverDetailBrand());
        mBinding.textViewModel.setText(dataDriver.getDriverDetailModel());
        mBinding.textViewLicensePlate.setText(dataDriver.getDriverDetailLicensePlate()+" ( "+dataDriver.getDriverDetailProvinceLicensePlate()+" )");
        String typeCar;
        switch (dataDriver.getDriverDetailType()) {
            case "Pickup":
                typeCar = "รถกระบะ";
                break;
            case "Truck":
                typeCar = "รถกระบะตู้ทึบ";
                break;
            default:
                typeCar = "รถ 5 ประตู";
                break;
        }
        mBinding.textViewTypeCar.setText(typeCar);

        mBinding.chargeStart.setText("ราคาเริ่มต้น (0 - " + dataDriver.getDriverDetailChargeStartKm() + " กม.)");
        mBinding.textViewChargeStart.setText("฿" + df2.format(dataDriver.getDriverDetailChargeStartPrice()) + "");
        mBinding.textViewCharge.setText("฿" +  df2.format(dataDriver.getDriverDetailCharge()) + " / กม.");

        String liftStatus = dataDriver.getDriverDetailServiceLiftStatus();
        if (liftStatus.equals("t")) {
            liftPrice = dataDriver.getDriverDetailServiceLiftPrice();
            if (liftPrice == 0) {
                mBinding.textViewServiceLift.setText("ฟรี");
            } else {
                mBinding.textViewServiceLift.setText("฿" +  df2.format(liftPrice) + "");
            }
        } else {
            mBinding.textViewServiceLift.setText("ไม่มี");
        }

        String liftPlusStatus = dataDriver.getDriverDetailServiceLiftPlusStatus();

        if (liftPlusStatus.equals("t")) {
            liftPlusPrice = dataDriver.getDriverDetailServiceLiftPlusPrice();
            if (liftPlusPrice == 0) {
                mBinding.textViewServiceLiftPlusPrice.setText("ฟรี");
            } else {
                mBinding.textViewServiceLiftPlusPrice.setText("฿" +  df2.format(liftPlusPrice) + "");
            }

        } else {
            mBinding.textViewServiceLiftPlusPrice.setText("ไม่มี");

        }

        String cartStatus = dataDriver.getDriverDetailServiceCartStatus();

        if (cartStatus.equals("t")) {
            cartPrice = dataDriver.getDriverDetailServiceCartPrice();
            if (cartPrice == 0) {
                mBinding.textViewServiceCartPrice.setText("ฟรี");
            } else {
                mBinding.textViewServiceCartPrice.setText("฿" +  df2.format(cartPrice) + "");
            }
        } else {
            mBinding.textViewServiceCartPrice.setText("ไม่มี");

        }

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

    }

    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
}

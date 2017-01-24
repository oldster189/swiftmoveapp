package com.oldster.swiftmove.app.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.JobDataDao;
import com.oldster.swiftmove.app.databinding.FragmentJobDetailBinding;
import com.oldster.swiftmove.app.manager.PositionMapManager;

import java.text.DecimalFormat;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class JobDetailFragment extends Fragment {

    /********************
     * Variable
     ********************/
    private String TAG = JobDetailFragment.class.getSimpleName();
    private JobDataDao jobDataDao;
    private FragmentJobDetailBinding mBind;


    private boolean serviceLift;
    private boolean serviceCart;
    private boolean serviceLiftPlus;
    private double distance;
    private String date;
    private String time;
    private int priceCart;
    private int priceLiftPlus;
    private int priceLift;
    private double total;
    /********************
     * Functions
     ********************/
    public JobDetailFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static JobDetailFragment newInstance(JobDataDao dataDao) {
        JobDetailFragment fragment = new JobDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("dataDao",dataDao);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobDataDao = getArguments().getParcelable("dataDao");
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater,R.layout.fragment_job_detail,container,false);
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
        String jobStatus = jobDataDao.getJobStatusName();
        mBind.tvStatusJob.setText(jobStatus);
        DecimalFormat df2 = new DecimalFormat("000000");
        mBind.tvIdJob.setText("#"+df2.format(jobDataDao.getJobId()));
        switch (jobStatus){
            case "เสร็จสิ้น":
                mBind.tvStatusJob.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                mBind.tvStatusJob.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_oval));
                mBind.imgStatus.setImageResource(R.drawable.ic_correct);
                break;
            case "ยกเลิก":
                mBind.tvStatusJob.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                mBind.tvStatusJob.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_oval_red_pink));
                mBind.imgStatus.setImageResource(R.drawable.ic_multiply);
                break;
            default:
                mBind.tvStatusJob.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                mBind.tvStatusJob.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_oval_purple));
                mBind.imgStatus.setVisibility(View.VISIBLE);
                mBind.imgStatus.setImageResource(R.drawable.ic_clock2);
        }

        mBind.tvDateTime.setText(jobDataDao.getJobTime() + ", " + jobDataDao.getJobDate());
        mBind.tvDriver.setText("คุณ " + jobDataDao.getDriverFirstName() + " " + jobDataDao.getDriverLastName());
        String typeCar = jobDataDao.getDriverDetailType();
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
        mBind.tvPositionFrom.setText(jobDataDao.getJobFromNameAddress());
        mBind.tvPositionTo.setText(jobDataDao.getJobToNameAddress());

        distance = jobDataDao.getJobDistance();
        DecimalFormat df = new DecimalFormat("0.00");
        mBind.tvDistance.setText("" + df.format(distance) + " กม.");


        //set cardView 3
        //ราคาเริ่มต้น
        mBind.tvChargeStartKm.setText("ราคาเริ่มต้น (0 - " + jobDataDao.getJobChargeStartKm() + " กม.)");
        int priceStart = jobDataDao.getJobChargeStartPrice();
        mBind.tvChargeStartPrice.setText("฿ " + df.format(priceStart) + "");

        //คิดตามระยะทาง
        double km = 0;
        if (distance > jobDataDao.getJobChargeStartKm()) {
            km = distance - jobDataDao.getJobChargeStartKm();
            mBind.tvChargeKm.setText("ระยะทาง (" + df.format(km) + " กม.)");
        } else {
            mBind.tvChargeKm.setText("ระยะทาง (" + df.format(km) + " กม.)");
        }
        double priceRate = km * jobDataDao.getJobCharge();
        mBind.tvChargePrice.setText("฿ " + df.format(priceRate) + "");

        //คนขับช่วยยกของ

        if (serviceLift) {
            priceLift = jobDataDao.getJobServiceLiftPrice();
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
            priceLiftPlus = jobDataDao.getJobServiceLiftPlusPrice();
            mBind.tvServiceLiftPlusPrice.setText("฿ " + df.format(priceLiftPlus) + "");
        } else {
            mBind.relativeLiftPlus.setVisibility(View.GONE);
        }

        //รถเข็น
        if (serviceCart) {
            priceCart = jobDataDao.getJobServiceCartPrice();
            mBind.tvServiceCartPrice.setText("฿ " + df.format(priceCart) + "");
        } else {
            mBind.frameCart.setVisibility(View.GONE);
        }

        //รวม
        total = priceStart + priceRate + priceLift + priceLiftPlus + priceCart;
        mBind.tvListTotal.setText("฿ " + df.format(total) + "");
        mBind.tvTotal.setText("฿ " + df.format(total) + "");
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

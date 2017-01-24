package com.oldster.swiftmove.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.JobDetailActivity;
import com.oldster.swiftmove.app.activity.MainActivity;
import com.oldster.swiftmove.app.dao.JobDataCollectionDao;
import com.oldster.swiftmove.app.manager.PositionMapManager;
import com.oldster.swiftmove.app.manager.user.JobProcessManager;
import com.oldster.swiftmove.app.util.DateTimeValue;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;

import java.text.DecimalFormat;
import java.util.Calendar;

import mehdi.sakout.fancybuttons.FancyButton;

public class CardHistoryJobAdapter extends RecyclerView.Adapter<CardHistoryJobAdapter.MyViewHolder> {

    private String TAG = CardHistoryJobAdapter.class.getSimpleName();
    private int lastPosition = -1;

    private Context mContext;

    private JobDataCollectionDao jobData;
    private JobProcessManager jobProcessManager;


    public void setJobData(JobDataCollectionDao jobData) {
        this.jobData = jobData;
    }

    public CardHistoryJobAdapter(Context mContext) {
        this.mContext = mContext;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView1;
        TextView tvIdJob, tvStatus, tvDateTime, tvPositionFrom, tvPositionTo, tvServiceCharge, tvTypeCar;
        FancyButton btnJob,btnReJob;
        ImageView imgStatus;
        LinearLayout linExpTime;

        MyViewHolder(View itemView) {
            super(itemView);
            jobProcessManager = new JobProcessManager();
            tvIdJob = (TextView) itemView.findViewById(R.id.tvIdJob);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatusJob);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            tvPositionFrom = (TextView) itemView.findViewById(R.id.tvPositionFrom);
            tvTypeCar = (TextView) itemView.findViewById(R.id.tvTypeCar);
            tvPositionTo = (TextView) itemView.findViewById(R.id.tvPositionTo);
            tvServiceCharge = (TextView) itemView.findViewById(R.id.tvTotalPrice);
            cardView1 = (CardView) itemView.findViewById(R.id.cardView1);
            btnJob = (FancyButton) itemView.findViewById(R.id.btnJob);
            btnReJob  = (FancyButton) itemView.findViewById(R.id.btnReJob);
            imgStatus = (ImageView) itemView.findViewById(R.id.imgStatus);
            linExpTime = (LinearLayout) itemView.findViewById(R.id.linExpTime);


        }


    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.card_job_process, parent, false);
        return new MyViewHolder(itemView);
    }

    private String getDateCurrent() {
        final Calendar c = Calendar.getInstance();
        int pYear = c.get(Calendar.YEAR);
        int pMonth = c.get(Calendar.MONTH);
        int pDay = c.get(Calendar.DAY_OF_MONTH);
        int pHour = c.get(Calendar.HOUR_OF_DAY);
        int pMin = c.get(Calendar.MINUTE);
        int pSec = c.get(Calendar.SECOND);
        return pYear + "-" + (pMonth + 1) + "-" + pDay + " " + pHour + ":" + pMin + ":" + pSec;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (position < getItemCount()) {

            holder.linExpTime.setVisibility(View.GONE);
            DecimalFormat df = new DecimalFormat("000000");
            DecimalFormat df2 = new DecimalFormat("00.00");
            holder.tvIdJob.setText("#" + df.format(jobData.getData().get(position).getJobId()) + "");
            final String jobStatus = jobData.getData().get(position).getJobStatusName();
            switch (jobStatus) {
                case "เสร็จสิ้น":
                    holder.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    holder.tvStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_oval));
                    holder.imgStatus.setImageResource(R.drawable.ic_correct);
                    Log.e(TAG, jobData.getData().get(position).getJobComment() + " " + jobData.getData().get(position).getJobRating() + " " + jobData.getData().get(position).getJobId());
                    if ((jobData.getData().get(position).getJobComment() == null || jobData.getData().get(position).getJobComment().equals("")) && jobData.getData().get(position).getJobRating() == 0) {
                        holder.btnJob.setVisibility(View.VISIBLE);
                        holder.btnJob.setText("ให้คะแนนผู้ขับ");
                        holder.btnJob.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new ShowDialogReviewDriver(mContext, jobData.getData().get(position).getDriverId() + "", jobData.getData().get(position).getJobId() + "");

                                notifyDataSetChanged();
                            }
                        });
                    } else {
                        holder.btnJob.setVisibility(View.GONE);
                    }
                    break;
                default:
                    holder.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    holder.tvStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_oval_red_pink));
                    holder.imgStatus.setImageResource(R.drawable.ic_multiply);
                    holder.btnJob.setVisibility(View.GONE);
                    break;
            }

            holder.tvStatus.setText(jobStatus);
            DateTimeValue date = new DateTimeValue(jobData.getData().get(position).getJobDate(), jobData.getData().get(position).getJobTime());
            holder.tvDateTime.setText(" " + date.getDate() + " " + date.getMount() + " " + date.getYear() + " | " + date.getTime() + " น.");
            holder.tvPositionFrom.setText(jobData.getData().get(position).getJobFromNameAddress());
            holder.tvPositionTo.setText(jobData.getData().get(position).getJobToNameAddress());
            switch (jobData.getData().get(position).getDriverDetailType()) {
                case "Pickup":
                    holder.tvTypeCar.setText("รถกระบะ");
                    break;
                case "Truck":
                    holder.tvTypeCar.setText("รถกระบะตู้ทึบ");
                    break;
                default:
                    holder.tvTypeCar.setText("รถ 5 ประตู");
            }

            int chargeStartPrice = jobData.getData().get(position).getJobChargeStartPrice();
            int chargeStartKm = jobData.getData().get(position).getJobChargeStartKm();
            int chargeRate = jobData.getData().get(position).getJobCharge();


            double distance = jobData.getData().get(position).getJobDistance();

            String liftStatus = jobData.getData().get(position).getJobServiceLiftStatus();
            int liftPrice;
            if (liftStatus.equals("t")) {
                liftPrice = jobData.getData().get(position).getJobServiceLiftPrice();
            } else {
                liftPrice = 0;
            }


            String liftPlusStatus = jobData.getData().get(position).getJobServiceLiftPlusStatus();

            int liftPlusPrice;
            if (liftPlusStatus.equals("t")) {
                liftPlusPrice = jobData.getData().get(position).getJobServiceLiftPlusPrice();
            } else {
                liftPlusPrice = 0;
            }

            String cartStatus = jobData.getData().get(position).getJobServiceCartStatus();
            int cartPrice;
            if (cartStatus.equals("t")) {
                cartPrice = jobData.getData().get(position).getJobServiceCartPrice();
            } else {
                cartPrice = 0;
            }
            double dis2 = distance - chargeStartKm;
            if (dis2 <= 0) {
                dis2 = 0;
            }

            double total = (dis2 * chargeRate) + liftPrice + liftPlusPrice + cartPrice + chargeStartPrice;


            holder.tvServiceCharge.setText("฿ " + df2.format(total) + "");
            holder.cardView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, JobDetailActivity.class);
                    intent.putExtra("jobData", jobData.getData().get(position));
                    mContext.startActivity(intent);
                }
            });
            holder.btnReJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double latFrom = jobData.getData().get(position).getJobFromLatitude();
                    double lngFrom = jobData.getData().get(position).getJobFromLongitude();
                    String nameFrom = jobData.getData().get(position).getJobFromNameAddress();
                    double latTo = jobData.getData().get(position).getJobToLatitude();
                    double lngTo = jobData.getData().get(position).getJobToLongitude();
                    String nameTo = jobData.getData().get(position).getJobToNameAddress();
                    String typeCar = jobData.getData().get(position).getDriverDetailType();
                    String province = jobData.getData().get(position).getDriverProvince();
                    PositionMapManager.getInstance().setLatitudeFrom(latFrom);
                    PositionMapManager.getInstance().setLongitudeFrom(lngFrom);
                    PositionMapManager.getInstance().setLatitudeTo(latTo);
                    PositionMapManager.getInstance().setLongitudeTo(lngTo);
                    PositionMapManager.getInstance().setNameAddressFrom(nameFrom);
                    PositionMapManager.getInstance().setNameAddressTo(nameTo);
                    PositionMapManager.getInstance().setTypeCar(typeCar);
                    PositionMapManager.getInstance().setProvinceFrom(province);
                    mContext.startActivity(new Intent(mContext, MainActivity.class));

                }
            });

            setAnimation(holder.itemView, position);
        }


    }


    @Override
    public int getItemCount() {
        if (jobData == null) {
            return 0;
        }
        if (jobData.getData() == null) {
            return 0;
        }
        return jobData.getData().size();

    }


    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext
                    , R.anim.up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}

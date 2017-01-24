package com.oldster.swiftmove.app.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.bumptech.glide.Glide;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.HistoryJobActivity;
import com.oldster.swiftmove.app.activity.JobDetailActivity;
import com.oldster.swiftmove.app.dao.JobDataCollectionDao;
import com.oldster.swiftmove.app.dao.TemplatesMessageDado;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.user.JobProcessManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;
import com.oldster.swiftmove.app.util.DateTimeValue;
import com.oldster.swiftmove.app.util.ProcessTimerReceiver;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.ALARM_SERVICE;

public class CardProcessJobAdapter extends RecyclerView.Adapter<CardProcessJobAdapter.MyViewHolder> {

    private String TAG = CardProcessJobAdapter.class.getSimpleName();
    private static final long TIME_SET_COOLDOWN = 1500000;
    private int lastPosition = -1;

    private Context mContext;

    private JobDataCollectionDao jobData;
    private JobProcessManager jobProcessManager;
    private UserDataManager userDataManager;


    public void setJobData(JobDataCollectionDao jobData) {
        this.jobData = jobData;
    }

    public CardProcessJobAdapter(Context mContext) {
        this.mContext = mContext;

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CountDownTimer timer;
        CardView cardView1;
        TextView tvIdJob, tvStatus, tvDateTime, tvPositionFrom, tvPositionTo, tvTypeCar, tvServiceCharge, tvExpTime;
        FancyButton btnJob,btnReJob;
        ImageView imgStatus;
        LinearLayout linExpTime;

        MyViewHolder(View itemView) {
            super(itemView);
            userDataManager = new UserDataManager();
            jobProcessManager = new JobProcessManager();
            tvIdJob = (TextView) itemView.findViewById(R.id.tvIdJob);
            tvExpTime = (TextView) itemView.findViewById(R.id.tvExpTime);
            tvTypeCar = (TextView) itemView.findViewById(R.id.tvTypeCar);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatusJob);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            tvPositionFrom = (TextView) itemView.findViewById(R.id.tvPositionFrom);
            tvPositionTo = (TextView) itemView.findViewById(R.id.tvPositionTo);
            tvServiceCharge = (TextView) itemView.findViewById(R.id.tvTotalPrice);
            cardView1 = (CardView) itemView.findViewById(R.id.cardView1);
            btnJob = (FancyButton) itemView.findViewById(R.id.btnJob);
            btnReJob = (FancyButton) itemView.findViewById(R.id.btnReJob);
            imgStatus = (ImageView) itemView.findViewById(R.id.imgStatus);
            linExpTime = (LinearLayout) itemView.findViewById(R.id.linExpTime);
            btnJob.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (v.getId() == btnJob.getId()) {
                String status = jobData.getData().get(getAdapterPosition()).getJobStatusName();
                if (status.equals("รอการยืนยัน")) {
                    displayAlertCancel(getAdapterPosition());
                }
            }
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

            final DecimalFormat df = new DecimalFormat("000000");
            DecimalFormat df2 = new DecimalFormat("00.00");
            final DecimalFormat df3 = new DecimalFormat("00");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String dateCurrent = getDateCurrent();
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = format.parse(dateCurrent);
                d2 = format.parse(jobData.getData().get(position).getJobCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long diff = d2.getTime() - d1.getTime();

            long t1 = diff + TIME_SET_COOLDOWN;
            final int idJob = jobData.getData().get(position).getJobId();
            if (t1 > 0 && t1 <= TIME_SET_COOLDOWN && jobData.getData().get(0).getJobStatusName().equals("รอการยืนยัน")) {
                AlarmManager processTimer = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(mContext, ProcessTimerReceiver.class);
                intent.putExtra("jid", idJob);
                intent.putExtra("status", "ยกเลิก");
                intent.putExtra("message", "รายการหมายเลข #" + df.format(idJob) + " ของคุณ หมดเวลาดำเนินการแล้ว");
                intent.putExtra("did", jobData.getData().get(position).getDriverId());
                intent.putExtra("uid", userDataManager.getUser().getUser().get(0).getUserId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                processTimer.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TIME_SET_COOLDOWN, pendingIntent);


                holder.timer = new CountDownTimer(t1, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000) % 60;
                        int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                        int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                        holder.tvExpTime.setText(df3.format(minutes) + ":" + df3.format(seconds) + " นาที");
                    }

                    public void onFinish() {
                        holder.tvExpTime.setText("หมดเวลาดำเนินการแล้ว");
                    }
                }.start();

            } else {
                holder.linExpTime.setVisibility(View.GONE);
            }
            holder.tvIdJob.setText("#" + df.format(jobData.getData().get(position).getJobId()) + "");
            String jobStatus = jobData.getData().get(position).getJobStatusName();
            holder.btnReJob.setVisibility(View.GONE);
            switch (jobStatus) {

                case "รอการยืนยัน":
                    holder.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    holder.tvStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_oval_purple));
                    holder.imgStatus.setVisibility(View.VISIBLE);
                    holder.imgStatus.setImageResource(R.drawable.ic_clock2);
                    holder.btnJob.setVisibility(View.VISIBLE);
                    holder.btnJob.setBackgroundColor(ContextCompat.getColor(mContext, R.color.newColorBlueNormal));
                    break;
                default:
                    holder.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    holder.tvStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_oval_purple));
                    holder.imgStatus.setVisibility(View.VISIBLE);
                    holder.imgStatus.setImageResource(R.drawable.ic_clock2);
                    holder.btnJob.setVisibility(View.GONE);
                    holder.btnJob.setBackgroundColor(ContextCompat.getColor(mContext, R.color.newColorBluePress));
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


            setAnimation(holder.itemView, position);
        }


    }

    private void updateJobStatus(String status, String message, int position) {
        Call<TemplatesMessageDado> call = HttpManager.getInstance()
                .getService()
                .userUpdateJobStatus(jobData.getData().get(position).getJobId(),
                        status,
                        message,
                        jobData.getData().get(position).getDriverId());
        call.enqueue(new Callback<TemplatesMessageDado>() {
            @Override
            public void onResponse(Call<TemplatesMessageDado> call, Response<TemplatesMessageDado> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(mContext, HistoryJobActivity.class);
                    intent.putExtra("index", 1);
                    mContext.startActivity(intent);
                    ((AppCompatActivity) mContext).overridePendingTransition(R.anim.from_right, R.anim.to_left);
                } else {
                    Log.e("job response", "มีข้อผิดพลาด" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<TemplatesMessageDado> call, Throwable t) {
                Log.e("job onFailure", t.toString());
            }
        });
    }

    private void displayAlertCancel(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final String message = "ยืนยันการยกเลิก";
        builder.setMessage(message)
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("ยืนยัน",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DecimalFormat df = new DecimalFormat("000000");
                                updateJobStatus("ยกเลิก", "ผู้ใช้บริการได้ยกเลิกรายการหมายเลข #" + df.format(jobData.getData().get(position).getJobId()) + " ของคุณ", position);
                                dialog.dismiss();
                                Intent intent = new Intent(Contextor.getInstance().getContext(), ProcessTimerReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(Contextor.getInstance().getContext(), 0, intent, 0);
                                AlarmManager alarmManager = (AlarmManager) Contextor.getInstance().getContext().getSystemService(ALARM_SERVICE);
                                alarmManager.cancel(pendingIntent);
                            }
                        });
        builder.create().show();
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

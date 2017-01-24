package com.oldster.swiftmove.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oldster.swiftmove.app.Constants.EndPoints;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.DriverInfoActivity;
import com.oldster.swiftmove.app.activity.JobActivity;
import com.oldster.swiftmove.app.dao.DriverDataAfterSortCollection;
import com.oldster.swiftmove.app.dao.DriverFavoriteCollectionDao;
import com.oldster.swiftmove.app.manager.driver.DriverFavoriteManager;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class CardDriverAdapter extends RecyclerView.Adapter<CardDriverAdapter.MyViewHolder> {
    private String TAG = CardDriverAdapter.class.getSimpleName();
    private int lastPosition = -1;

    private Context mContext;

    private DriverDataAfterSortCollection dataDriver;
    private DriverFavoriteManager driverFavoriteManager;


    public void setDataAfterSortCollection(DriverDataAfterSortCollection dataDriver) {
        this.dataDriver = dataDriver;
    }

    public CardDriverAdapter(Context mContext) {
        this.mContext = mContext;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.card_driver, parent, false);

        return new MyViewHolder(itemView);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         CircleImageView imageCardDriver;
         TextView textViewCardName,
                textViewCardTitlePrice,
                textViewCardPrice,
                textViewCardRate,
                textViewCardFree,
                tvDistance;
         FancyButton btnDetail, btnAccept;
         FrameLayout frameRibbon;
        MyViewHolder(View itemView) {
            super(itemView);
            driverFavoriteManager = new DriverFavoriteManager();
            textViewCardName = (TextView) itemView.findViewById(R.id.textViewCardName);
            textViewCardTitlePrice = (TextView) itemView.findViewById(R.id.textViewCardTitlePrice);
            textViewCardPrice = (TextView) itemView.findViewById(R.id.textViewCardPrice);
            textViewCardRate = (TextView) itemView.findViewById(R.id.textViewCardRate);
            textViewCardFree = (TextView) itemView.findViewById(R.id.textViewCardFree);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            imageCardDriver = (CircleImageView) itemView.findViewById(R.id.imageCardDriver);
            btnDetail = (FancyButton) itemView.findViewById(R.id.btnDetail);
            btnAccept = (FancyButton) itemView.findViewById(R.id.btnAccept);
            frameRibbon = (FrameLayout) itemView.findViewById(R.id.frameRibbon);


            btnAccept.setOnClickListener(this);
            btnDetail.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (v.getId() == btnAccept.getId()) {
                Intent intent = new Intent(mContext, JobActivity.class);
                intent.putExtra("dataDriver", dataDriver.getDriver().get(getAdapterPosition()));
                mContext.startActivity(intent);
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.from_right, R.anim.to_left);

            }
            if (v.getId() == btnDetail.getId()) {
                Intent intent = new Intent(mContext, DriverInfoActivity.class);
                intent.putExtra("dataDriver", dataDriver.getDriver().get(getAdapterPosition()));
                mContext.startActivity(intent);
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.from_right, R.anim.to_left);
            }
        }
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position < getItemCount()) {

            int did = dataDriver.getDriver().get(position).getDriverId();
            DriverFavoriteCollectionDao data = driverFavoriteManager.getDataDriverFavorite();
            if (data != null && data.getData() != null)
                for (int i = 0; i < data.getData().size(); i++) {
                    if (data.getData().get(i).getDriverId() == did) {
                        holder.frameRibbon.setVisibility(View.VISIBLE);
                    }
                }
            if (dataDriver.getDriver().get(position).getDriverFname() != null) {
                holder.textViewCardName.setText("คุณ " + dataDriver.getDriver().get(position).getDriverFname() + "");
            }
            holder.textViewCardTitlePrice.setText("ราคาเริ่มต้น (0 - " + dataDriver.getDriver().get(position).getDriverDetailChargeStartKm() + " กม.)");
            holder.textViewCardPrice.setText("฿" + dataDriver.getDriver().get(position).getDriverDetailChargeStartPrice() + "");
            holder.textViewCardRate.setText("฿" + dataDriver.getDriver().get(position).getDriverDetailCharge() + " / กม.");
            double values = dataDriver.getDriver().get(position).getDriverDistance() / 1000d;
            String distance = new DecimalFormat("0.00").format(values);
            holder.tvDistance.setText("ระยะห่าง " + distance + " กม.");
            if (dataDriver.getDriver().get(position).getDriverDetailServiceLiftStatus().equals("t")) {
                int liftPrice = dataDriver.getDriver().get(position).getDriverDetailServiceLiftPrice();
                if (liftPrice == 0) {
                    holder.textViewCardFree.setText("ฟรี");
                } else {
                    holder.textViewCardFree.setText("฿" + liftPrice + "");
                }
            } else {
                holder.textViewCardFree.setText("ไม่มี");
            }
            if (dataDriver.getDriver().get(position).getDriverImgName() != null
                    && !dataDriver.getDriver().get(position).getDriverImgName().equals("")) {
                Log.e("image",dataDriver.getDriver().get(position).getDriverImgName()+" "+did);
                Glide.with(mContext)
                        .load(EndPoints.BASE_URL_IMG_DRIVER + dataDriver.getDriver().get(position).getDriverImgName())
                        .fitCenter()
                        .crossFade()
                        .into( holder.imageCardDriver);
            }
            setAnimation(holder.itemView, position);
        }


    }

    @Override
    public int getItemCount() {
        if (dataDriver == null) {
            return 0;
        }
        if (dataDriver.getDriver() == null) {
            return 0;
        }
        return dataDriver.getDriver().size();

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

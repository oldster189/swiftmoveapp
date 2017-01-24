package com.oldster.swiftmove.app.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.oldster.swiftmove.app.Constants.EndPoints;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.DriverFavoriteCollectionDao;
import com.oldster.swiftmove.app.dao.TemplatesMessageDado;
import com.oldster.swiftmove.app.manager.HttpManager;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Old'ster on 5/12/2559.
 */

public class CardDriverFavoriteAdapter extends RecyclerView.Adapter<CardDriverFavoriteAdapter.MyViewHolder> {

    private String TAG = CardDriverFavoriteAdapter.class.getSimpleName();
    private int lastPosition = -1;

    private Context mContext;
    private Activity mActivity;
    private ProgressDialog progressDialog;
    private DriverFavoriteCollectionDao data;


    public void setData(DriverFavoriteCollectionDao data) {
        this.data = data;
    }

    public CardDriverFavoriteAdapter(Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.card_driver_favorite, parent, false);
        return new MyViewHolder(itemView);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        CircleImageView imageProfileNav;
        TextView tvNameDriver, tvStar;
        RatingBar ratingBar;
        ImageView imgViewTypeCar, overflow;
        Vibrator mVibrate;

        MyViewHolder(View itemView) {
            super(itemView);
            mVibrate = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            cardView = (CardView) itemView.findViewById(R.id.cardView1);
            imageProfileNav = (CircleImageView) itemView.findViewById(R.id.imageProfileNav);
            tvNameDriver = (TextView) itemView.findViewById(R.id.tvNameDriver);
            tvStar = (TextView) itemView.findViewById(R.id.tvStar);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            imgViewTypeCar = (ImageView) itemView.findViewById(R.id.imgViewTypeCar);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);

        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (position < getItemCount()) {
            progressDialog = new ProgressDialog(mContext);
            if (data.getData().get(position).getDriverImgName() != null) {
                Glide.with(mContext)
                        .load(EndPoints.BASE_URL_IMG_DRIVER + data.getData().get(position).getDriverImgName())
                        .crossFade()
                        .into(holder.imageProfileNav);
            }
            DecimalFormat df = new DecimalFormat("0.0");
            holder.tvNameDriver.setText(data.getData().get(position).getDriverFirstName() + " " + data.getData().get(position).getDriverLastName());
            holder.tvStar.setText(df.format(data.getData().get(position).getRatingAvg()) + " (" + data.getData().get(position).getRatingCount() + ")");

            holder.ratingBar.setRating(Float.parseFloat(data.getData().get(position).getRatingAvg() + ""));
            switch (data.getData().get(position).getDriverDetailType()) {
                case "Pickup":
                    Glide.with(mContext)
                            .load(R.drawable.ic_pickup_active)
                            .crossFade()
                            .into(holder.imgViewTypeCar);
                    break;
                case "Truck":
                    Glide.with(mContext)
                            .load(R.drawable.ic_truck_active)
                            .crossFade()
                            .into(holder.imgViewTypeCar);
                    break;
                case "EcoCar":
                    Glide.with(mContext)
                            .load(R.drawable.ic_ecocar_active)
                            .crossFade()
                            .into(holder.imgViewTypeCar);
                    break;
            }
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder.overflow, position);
                }
            });
            setAnimation(holder.itemView, position);
        }
    }


    @Override
    public int getItemCount() {
        if (data == null) return 0;
        if (data.getData() == null) return 0;
        return data.getData().size();
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

    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_bottom_sheet, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;

        MyMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete:
                    progressDialog.setMessage("กำลังลบข้อมูล...");
                    progressDialog.show();
                    int uid = data.getData().get(position).getUserId();
                    int did = data.getData().get(position).getDriverId();
                    updateDriverFavorite(uid, did);
                    return true;
            }
            return false;
        }

        private void updateDriverFavorite(int uid, int did) {
            Call<TemplatesMessageDado> call = HttpManager.getInstance()
                    .getService()
                    .userUpdateDriverFavorite(uid, did);
            call.enqueue(new Callback<TemplatesMessageDado>() {
                @Override
                public void onResponse(Call<TemplatesMessageDado> call, Response<TemplatesMessageDado> response) {
                    if (response.isSuccessful()) {
                        progressDialog.dismiss();
                        data.getData().remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, data.getData().size());
                    } else {
                        Toast.makeText(mContext, "ลบข้อมูลไม่สำเร็จ ลองใหม่อีกครั้ง!!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Log.e(TAG,"del favorite "+ response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<TemplatesMessageDado> call, Throwable t) {
                    Toast.makeText(mContext, "ลบข้อมูลไม่สำเร็จ ลองใหม่อีกครั้ง!!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"del favorite "+ t.toString());
                    progressDialog.dismiss();
                }
            });

        }
    }

}

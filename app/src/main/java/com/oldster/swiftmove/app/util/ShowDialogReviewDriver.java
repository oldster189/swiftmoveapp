package com.oldster.swiftmove.app.util;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.DriverFavoriteCollectionDao;
import com.oldster.swiftmove.app.dao.TemplatesMessageDado;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.driver.DriverFavoriteManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;

import java.text.DecimalFormat;

import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Old'ster on 8/12/2559.
 */

public class ShowDialogReviewDriver {
    private String TAG = ShowDialogReviewDriver.class.getSimpleName();
    private final DecimalFormat df;
    private Context mContext;
    private UserDataManager uManager;
    private DriverFavoriteManager driverFavoriteManager;
    private boolean isFavorite = false;
    private String did, jid;
    private float ratingValue;
    private String commentDetail;
    private Dialog dialog;

    public ShowDialogReviewDriver(Context mContext, String did, String jid) {
        this.mContext = mContext;
        this.did = did;
        this.jid = jid;
        uManager = new UserDataManager();
        driverFavoriteManager = new DriverFavoriteManager();
        df = new DecimalFormat("000000");
        showDialog();
    }

    private void showDialog() {
        dialog = new Dialog(mContext);
        dialog.setTitle("ให้คะแนนผู้ให้บริการ");
        dialog.setContentView(R.layout.fragment_rating_driver);
        RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        TextView tvJobId = (TextView) dialog.findViewById(R.id.tvJobId);
        final EditText editTextComment = (EditText) dialog.findViewById(R.id.etComment);
        final FancyButton btnFavorite = (FancyButton) dialog.findViewById(R.id.btnFavorite);
        FancyButton btnSent = (FancyButton) dialog.findViewById(R.id.btnSent);
        SaveCacheDriverFavorite(ratingBar, tvJobId, editTextComment, btnFavorite, btnSent);
        dialog.show();
    }

    private void SaveCacheDriverFavorite(final RatingBar ratingBar,
                                         final TextView tvJobId,
                                         final EditText editTextComment,
                                         final FancyButton btnFavorite,
                                         final FancyButton btnSent) {
        int uid = uManager.getUser().getUser().get(0).getUserId();
        Call<DriverFavoriteCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataDriverFavorite(uid);
        call.enqueue(new Callback<DriverFavoriteCollectionDao>() {
            @Override
            public void onResponse(Call<DriverFavoriteCollectionDao> call, Response<DriverFavoriteCollectionDao> response) {
                DriverFavoriteCollectionDao data = response.body();
                if (response.isSuccessful()) {
                    driverFavoriteManager.setDataDriverFavorite(data);
                    tvJobId.setText("งานหมายเลข #" + df.format(Double.parseDouble(jid)) + "");
                    for (int i = 0; i < driverFavoriteManager.getDataDriverFavorite().getData().size(); i++) {

                        if (did.equals(String.valueOf(driverFavoriteManager.getDataDriverFavorite().getData().get(i).getDriverId()))) {
                            isFavorite = true;
                            btnFavorite.setText("ยกเลิกเป็นคนขับคนโปรด");
                            btnFavorite.setBackgroundColor(ContextCompat.getColor(mContext,
                                    R.color.newColorBlueNormal));
                            btnFavorite.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                        }
                    }
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                            Log.e(TAG, v + "");
                            ratingValue = v;
                        }
                    });
                    btnFavorite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isFavorite) {
                                isFavorite = true;
                                btnFavorite.setText("ยกเลิกเป็นคนขับคนโปรด");
                                btnFavorite.setBackgroundColor(ContextCompat.getColor(mContext,
                                        R.color.newColorBlueNormal));
                                btnFavorite.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                            } else {
                                isFavorite = false;
                                btnFavorite.setText("เพิ่มเป็นคนขับคนโปรด");
                                btnFavorite.setBackgroundColor(ContextCompat.getColor(mContext,
                                        R.color.white));
                                btnFavorite.setTextColor(ContextCompat.getColor(mContext, R.color.newColorBlueNormal));
                            }
                        }
                    });

                    btnSent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commentDetail = editTextComment.getText().toString();
                            sentComment();
                        }
                    });
                } else {
                    Log.e(TAG + " isSuccess driFav else", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<DriverFavoriteCollectionDao> call, Throwable t) {
                Log.e(TAG + " onFailure driFav", t.toString());
            }
        });
    }

    private void sentComment() {
        Log.e(TAG + " data ", isFavorite + " " + Integer.parseInt(jid) + " " + ratingValue + " " + commentDetail);
        Call<TemplatesMessageDado> call = HttpManager.getInstance()
                .getService()
                .insertRating(isFavorite, Integer.parseInt(jid), ratingValue, commentDetail);
        call.enqueue(new Callback<TemplatesMessageDado>() {
            @Override
            public void onResponse(Call<TemplatesMessageDado> call, Response<TemplatesMessageDado> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(mContext, "บันทึกข้อมูลสำเร็จ!!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Log.e(TAG + "job response", "มีข้อผิดพลาด" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<TemplatesMessageDado> call, Throwable t) {
                Log.e(TAG + "onFailure", t.toString());
            }
        });

    }

}

package com.oldster.swiftmove.app.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oldster.swiftmove.app.Constants.EndPoints;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.CommentDataCollectionDao;
import com.oldster.swiftmove.app.util.DateTimeValue;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Old'ster on 5/12/2559.
 */

public class CardCommentAdapter extends RecyclerView.Adapter<CardCommentAdapter.MyViewHolder> {
    private String TAG = CardCommentAdapter.class.getSimpleName();
    private int lastPosition = -1;

    private Context mContext;

    private CommentDataCollectionDao data;


    public void setData(CommentDataCollectionDao data) {
        this.data = data;
    }

    public CardCommentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.card_comment, parent, false);
        return new MyViewHolder(itemView);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgUser;
        CardView cardView1;
        View vBar;
        TextView tvUser, tvDate, tvComment;
        RatingBar ratingBar;


        MyViewHolder(View itemView) {
            super(itemView);
            cardView1 = (CardView) itemView.findViewById(R.id.cardView1);
            imgUser = (CircleImageView) itemView.findViewById(R.id.imgUser);
            vBar = (View) itemView.findViewById(R.id.vBar);
            tvUser = (TextView) itemView.findViewById(R.id.tvUser);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position < getItemCount()) {

                if (data.getData().get(position).getUserImgName() != null) {
                    Glide.with(mContext)
                            .load(EndPoints.BASE_URL_IMG + data.getData().get(position).getUserImgName())
                            .fitCenter()
                            .crossFade()
                            .into(holder.imgUser);
                }
                holder.tvUser.setText(data.getData().get(position).getUserFirstName() + " " + data.getData().get(position).getUserLastName());
                if (data.getData().get(position).getJobComment() != null) {
                    holder.tvComment.setText(data.getData().get(position).getJobComment());
                }
                DateTimeValue date = new DateTimeValue(data.getData().get(position).getJobCreatedAt());
                holder.tvDate.setText(date.getDate() + " " + date.getMount() + " " + date.getYear());
                holder.ratingBar.setRating(Float.parseFloat(data.getData().get(position).getJobRating()+""));
                if (position + 1 == getItemCount()) {
                    holder.vBar.setVisibility(View.VISIBLE);
                } else {
                    holder.vBar.setVisibility(View.GONE);
                }
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


}

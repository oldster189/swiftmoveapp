package com.oldster.swiftmove.app.adapter;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oldster.swiftmove.app.Constants.EndPoints;

import java.util.Random;

public class HighlightPagerAdapter extends PagerAdapter {

    private final Random random = new Random();
    private int mSize;

    public HighlightPagerAdapter() {
        mSize =3;
    }

    public HighlightPagerAdapter(int count) {
        mSize = count;
    }

    @Override public int getCount() {
        return mSize;
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override public Object instantiateItem(ViewGroup view, int position) {
        ImageView imageView = new ImageView(view.getContext());
        Glide.with(view.getContext())
                .load(EndPoints.BASE_URL_IMG_NEWS + "a"+position+".png")
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(imageView);

        view.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return imageView;
    }

    public void addItem() {
        mSize++;
        notifyDataSetChanged();
    }

    public void removeItem() {
        mSize--;
        mSize = mSize < 0 ? 0 : mSize;
        notifyDataSetChanged();
    }
}
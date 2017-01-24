package com.oldster.swiftmove.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.nguyenhoanglam.imagepicker.model.Image;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.DriverDataAfterSort;
import com.oldster.swiftmove.app.fragment.OverviewFragment;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import java.util.ArrayList;

public class OverviewActivity extends AppCompatActivity {
    private String TAG = OverviewActivity.class.getSimpleName();
    private String date, time;
    private boolean serviceLift, serviceCart;
    private boolean serviceLiftPlus;
    private ArrayList<Image> images = new ArrayList<>();
    private DriverDataAfterSort dataDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            date = bundle.getString("date");
            time = bundle.getString("time");
            serviceLift = bundle.getBoolean("serviceLift");
            serviceCart = bundle.getBoolean("serviceCart");
            serviceLiftPlus = bundle.getBoolean("serviceLiftPlus");
            images = bundle.getParcelableArrayList("image");
            dataDriver = bundle.getParcelable("dataDriver");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, OverviewFragment.newInstance(date,time,serviceLift,serviceCart,serviceLiftPlus,images,dataDriver))
                    .commit();
        }


        initInstances();
    }

    private void initInstances() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
           getSupportActionBar().setTitle("สรุปภาพรวม");

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.from_left, R.anim.to_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}

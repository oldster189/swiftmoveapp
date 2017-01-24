package com.oldster.swiftmove.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.Constants.EndPoints;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.DriverDataAfterSort;
import com.oldster.swiftmove.app.fragment.DriverCommentFragment;
import com.oldster.swiftmove.app.fragment.DriverInfoFragment;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;


public class DriverInfoActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {
    private String TAG = DriverInfoActivity.class.getSimpleName();
    private DriverDataAfterSort dataDriver;
    private CircleImageView imageProfile;
    private TextView textViewName, textViewTel;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private boolean isShow = true;
    private FancyButton btnAccept;
    private int scrollRange = -1;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);
        Bundle bundle = getIntent().getExtras();
        dataDriver = bundle.getParcelable("dataDriver");
        initInstances();
    }

    private void initInstances() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            public String jid;
            public String did;
            public String status;
            public JSONObject data;
            public String payload;
            public String title;
            public String message;

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    try {
                        title = intent.getStringExtra("title"); //check
                        message = intent.getStringExtra("message");//check
                        payload = intent.getStringExtra("payload");//check
                        data = new JSONObject(payload);//check
                        status = data.getString("status");//check
                        if (status.equals("end_job")){
                            did = data.getString("did");
                            jid = data.getString("jid");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json Exception: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }

                    //Handle Code Here!!
                    if (!status.equals("end_job")) {
                        new ShowDialogStatusJob(DriverInfoActivity.this, title, message);
                    } else {
                        new ShowDialogReviewDriver(DriverInfoActivity.this, did, jid);
                    }
                }

            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        nestedScrollView.setFillViewport(true);


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        btnAccept = (FancyButton) findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);
        imageProfile = (CircleImageView) findViewById(R.id.imageProfile);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewTel = (TextView) findViewById(R.id.textViewTel);
        textViewName.setText("คุณ " + dataDriver.getDriverFname() + " " + dataDriver.getDriverLname());
        textViewTel.setText("โทร. " + dataDriver.getDriverTel());

        if (dataDriver.getDriverImgName() != null && !dataDriver.getDriverImgName().equals("")) {
            Glide.with(this)
                    .load(EndPoints.BASE_URL_IMG_DRIVER + dataDriver.getDriverImgName())
                    .fitCenter()
                    .crossFade()
                    .into(imageProfile);
        }
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(this);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {


        if (scrollRange == -1) {
            scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
            collapsingToolbarLayout.setTitle("ข้อมูลผู้ให้บริการ");
            isShow = true;
        } else if (isShow) {
            collapsingToolbarLayout.setTitle(" ");
            isShow = false;
        }


    }

    @Override
    public void onClick(View v) {
        if (v == btnAccept) {
            Intent intent = new Intent(this, JobActivity.class);
            intent.putExtra("dataDriver", dataDriver);
            startActivity(intent);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[]{"รายละเอียด", "ความคิดเห็น"};

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return DriverInfoFragment.newInstance(dataDriver);
            }
            return DriverCommentFragment.newInstance(dataDriver);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
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
        finish();
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }


    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(Contextor.getInstance().getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}

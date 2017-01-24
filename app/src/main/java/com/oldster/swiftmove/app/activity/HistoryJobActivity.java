package com.oldster.swiftmove.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.Constants.EndPoints;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.UserDataDao;
import com.oldster.swiftmove.app.fragment.HistoryJobFragment;
import com.oldster.swiftmove.app.fragment.ProcessJobFragment;
import com.oldster.swiftmove.app.manager.driver.DriverFavoriteManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;
import com.oldster.swiftmove.app.manager.user.UserUpdateFcmManager;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryJobActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = HistoryJobActivity.class.getSimpleName();
    private Bundle bundle;
    private NestedScrollView nestedScrollView;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabLayout.Tab tab;
    private UserDataManager userDataManager;

    private DrawerLayout drawer;
    private TextView textViewUsername;
    private CircleImageView imageProfileNav;

    private DriverFavoriteManager driverFavoriteManager;
    private UserUpdateFcmManager updateFcmManager;

    private boolean doubleBackToExitPressedOnce = false;
    private NavigationView navigationView;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_job);
        bundle = getIntent().getExtras();
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
                        new ShowDialogStatusJob(HistoryJobActivity.this, title, message);
                    } else {
                        new ShowDialogReviewDriver(HistoryJobActivity.this, did, jid);
                    }
                }

            }
        };


        driverFavoriteManager = new DriverFavoriteManager();
        updateFcmManager = new UserUpdateFcmManager();
        userDataManager = new UserDataManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("ประวัติการเรียกใช้บริการ");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        imageProfileNav = (CircleImageView) headerLayout.findViewById(R.id.imageProfileNav);

        textViewUsername = (TextView) headerLayout.findViewById(R.id.textViewUsername);
        loadUserData();

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(bundle.getInt("index", 0));

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        nestedScrollView.setFillViewport(true);
    }


    private void loadUserData() {
        userDataManager = new UserDataManager();
        UserDataDao user = userDataManager.getUser().getUser().get(0);

        //SET DISPLAY NAME
        textViewUsername.setText(user.getUserFname() + " " + user.getUserLname());

        //SET DISPLAY IMAGE
        if (user.getUserImgName() != null && !user.getUserImgName().equals("")) {
            Glide.with(this)
                    .load(EndPoints.BASE_URL_IMG + user.getUserImgName())
                    .fitCenter()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageProfileNav);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_info) {
            startActivity(new Intent(this, AccountActivity.class));
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }  else if (id == R.id.nav_route) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.nav_list_job) {
            Intent intent = new Intent(this, HistoryJobActivity.class);
            intent.putExtra("index", 0);
            startActivity(intent);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, HistoryJobActivity.class);
            intent.putExtra("index", 1);
            startActivity(intent);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.nav_favorite) {
            Intent intent = new Intent(this, DriverFavoriteActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.nav_logout) {
            userDataManager.clear();
            updateFcmManager.clear();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[]{"อยู่ระหว่างดำเนินการ", "ประวัติการใช้งาน"};


        ViewPagerAdapter(FragmentManager manager) {
            super(manager);

        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return ProcessJobFragment.newInstance();
            }
            return HistoryJobFragment.newInstance();
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

    public void onResume() {
        super.onResume();
        loadUserData();
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

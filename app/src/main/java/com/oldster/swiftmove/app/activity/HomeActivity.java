package com.oldster.swiftmove.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oldster.swiftmove.app.Constants.EndPoints;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.UserDataDao;
import com.oldster.swiftmove.app.dao.UserUpdateFcmDao;
import com.oldster.swiftmove.app.fcm.MyFirebaseInstanceIDService;
import com.oldster.swiftmove.app.fragment.HomeFragment;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;
import com.oldster.swiftmove.app.manager.user.UserUpdateFcmManager;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = HomeActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private TextView textViewUsername;
    private CircleImageView imageProfileNav;
    private UserDataManager userDataManager;
    private UserUpdateFcmManager updateFcmManager;
    private boolean doubleBackToExitPressedOnce = false;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDataManager = new UserDataManager();
        if (userDataManager.getCount() == 0) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_main);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contentContainer, HomeFragment.newInstance())
                        .commit();
            }

            initInstances();
        }
    }


    private void initInstances() {

        updateFcmManager = new UserUpdateFcmManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("จองรถขนย้ายสิ่งของ");
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toolbar_blue, null));
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);


        imageProfileNav = (CircleImageView) headerLayout.findViewById(R.id.imageProfileNav);

        textViewUsername = (TextView) headerLayout.findViewById(R.id.textViewUsername);

        loadUserData();
        //Sent Token FCM to Server
        if (updateFcmManager.getFcmToken() != null) {
            sendFcmToServer();
        } else {
            MyFirebaseInstanceIDService mTokenID = new MyFirebaseInstanceIDService();
            mTokenID.onTokenRefresh();
            sendFcmToServer();
        }
    }

    private void loadUserData() {
        userDataManager = new UserDataManager();
        UserDataDao user = userDataManager.getUser().getUser().get(0);
        Log.e(TAG, "Img Name:" + user.getUserImgName());

        //SET DISPLAY NAME
        textViewUsername.setText(user.getUserFname() + " " + user.getUserLname());

        //SET DISPLAY IMAGE
        if (user.getUserImgName() != null && !user.getUserImgName().equals("")) {
            Glide.with(this)
                    .load(EndPoints.BASE_URL_IMG + user.getUserImgName())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .crossFade()
                    .fitCenter()
                    .into(imageProfileNav);
        }
    }

    private void sendFcmToServer() {
        String fcmToken = updateFcmManager.getFcmToken();
        int uid = userDataManager.getUser().getUser().get(0).getUserId();
        Call<UserUpdateFcmDao> call = HttpManager.getInstance()
                .getService()
                .userUpdateFcm(uid, fcmToken);
        call.enqueue(new Callback<UserUpdateFcmDao>() {
            @Override
            public void onResponse(Call<UserUpdateFcmDao> call, Response<UserUpdateFcmDao> response) {
                if (response.isSuccessful()) {
                    UserUpdateFcmDao fcmDao = response.body();
                    if (fcmDao.isSuccess()) {
                        Log.d(TAG + " sendFcmToServer", "Successful!!!");
                    } else {
                        Log.d(TAG + " sendFcmToServer", "Unsuccessful!!!");
                    }

                } else {
                    Log.d(TAG + " sendFcmToServer ", "Unsuccessful!!!");
                }
            }

            @Override
            public void onFailure(Call<UserUpdateFcmDao> call, Throwable t) {
                Log.d(TAG + " sendFcmToServer", t.toString());
            }
        });
    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.click_agian_exit_application, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_info) {
            startActivity(new Intent(this, AccountActivity.class));
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.nav_route) {
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


    //Checking Internet Available
    public boolean isInternetAvailable() {
        String netAddress = null;
        try {
            netAddress = new NetTask().execute("www.google.com").get();
            return !netAddress.equals("");
        } catch (Exception e1) {
            return false;
        }
    }

    public class NetTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            InetAddress addr = null;
            try {
                addr = InetAddress.getByName(params[0]);
            } catch (UnknownHostException e) {
                return "";
            }
            return addr.getHostAddress();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        Log.e(TAG, "onResume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        // TODO: clear application Class PositionMapManager()
    }
}

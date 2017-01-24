package com.oldster.swiftmove.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.Constants.EndPoints;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.dao.UserDataDao;
import com.oldster.swiftmove.app.dao.UserUpdateFcmDao;
import com.oldster.swiftmove.app.fcm.MyFirebaseInstanceIDService;
import com.oldster.swiftmove.app.fragment.HomeFragment;
import com.oldster.swiftmove.app.fragment.MainFragment;
import com.oldster.swiftmove.app.manager.HttpManager;
import com.oldster.swiftmove.app.manager.user.UserDataManager;
import com.oldster.swiftmove.app.manager.user.UserUpdateFcmManager;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private TextView textViewUsername;
    private CircleImageView imageProfileNav;
    private UserDataManager userDataManager;
    private UserUpdateFcmManager updateFcmManager;
    private boolean doubleBackToExitPressedOnce = false;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Bundle bundle;
    private String title;
    private String message;
    private String payload;
    private JSONObject data;
    private String status;
    private String did;
    private String jid;
    private Handler handler;
    private int counter;

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
                        .add(R.id.contentContainer, MainFragment.newInstance())
                        .commit();
            }

            initInstances();

            handler = new Handler(Looper.getMainLooper()) {


                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    counter++;
                    if (counter < 2) {
                        sendEmptyMessageDelayed(0, 1000);
                    }
                }
            };
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }


    private void initInstances() {
        userDataManager = new UserDataManager();
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
        if (updateFcmManager.getFcmToken() != null) {
            sendFcmToServer();
        } else {
            MyFirebaseInstanceIDService mTokenID = new MyFirebaseInstanceIDService();
            mTokenID.onTokenRefresh();
            sendFcmToServer();
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

    private void loadUserData() {
        userDataManager = new UserDataManager();
        UserDataDao user = userDataManager.getUser().getUser().get(0);

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


    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // TODO: clear application Class PositionMapManager()
    }
}

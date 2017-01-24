package com.oldster.swiftmove.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.fragment.SignupFragment;
import com.oldster.swiftmove.app.manager.user.UserDataManager;

public class SignupActivity extends AppCompatActivity {
    private String TAG = SignupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserDataManager userDataManager = new UserDataManager();
        if (userDataManager.getCount() > 0) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_signup);
            getWindow().setBackgroundDrawableResource(R.drawable.bg_login) ;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contentContainer, SignupFragment.newInstance())
                        .commit();
            }

            initInstances();
        }
    }

    private void initInstances() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
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

}

package com.oldster.swiftmove.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.fragment.LoginFragment;
import com.oldster.swiftmove.app.manager.user.UserDataManager;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.bg_login);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, LoginFragment.newInstance())
                    .commit();
        }

        initInstances();
    }

    private void initInstances() {

    }

}

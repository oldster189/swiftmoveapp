package com.oldster.swiftmove.app.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.oldster.swiftmove.app.manager.user.UserDataManager;
import com.oldster.swiftmove.app.manager.user.UserUpdateFcmManager;


/**
 * Created by Old'ster on 24/8/2559.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private UserDataManager userDataManager;

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();

        //Save token to disk cache
        storeRegIdInPref(refreshToken);
    }

    private void storeRegIdInPref(String token) {
        UserUpdateFcmManager updateFcmManager = new UserUpdateFcmManager();
        updateFcmManager.storeFcmToken(token);
    }
}

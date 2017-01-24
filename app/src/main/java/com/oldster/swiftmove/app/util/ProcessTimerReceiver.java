package com.oldster.swiftmove.app.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.dao.TemplatesMessageDado;
import com.oldster.swiftmove.app.manager.HttpManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Old'ster on 7/12/2559.
 */

public class ProcessTimerReceiver extends BroadcastReceiver {
    private String status, message;
    private int jid, did,uid;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Do something every 30 seconds
        Bundle bundle = intent.getExtras();
        jid = bundle.getInt("jid");
        status = bundle.getString("status");
        message = bundle.getString("message");
        did = bundle.getInt("did");
        uid = bundle.getInt("uid");

        updateJobStatus(jid,status,message,did,uid);
    }

    private void updateJobStatus(int jid, String status, String message, int did,int uid) {
        Log.e("ProcessTimerReceiver"," jid "+jid+" status "+status+" message "+message+" did "+did);
        Call<TemplatesMessageDado> call = HttpManager.getInstance()
                .getService()
                .userUpdateJobStatusAuto(jid,
                        status,
                        message,
                        did,
                        uid);
        call.enqueue(new Callback<TemplatesMessageDado>() {
            @Override
            public void onResponse(Call<TemplatesMessageDado> call, Response<TemplatesMessageDado> response) {
                if (response.isSuccessful()) {
                    //Cancel the alarm
                    Intent intent = new Intent(Contextor.getInstance().getContext(), ProcessTimerReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Contextor.getInstance().getContext(), 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) Contextor.getInstance().getContext().getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    Log.e("ProcessTimerReceiver", "Success");
                } else {
                    Log.e("ProcessTimerReceiver", "มีข้อผิดพลาด" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<TemplatesMessageDado> call, Throwable t) {
                Log.e("ProcessTimerReceiver", t.toString());
            }
        });
    }
}
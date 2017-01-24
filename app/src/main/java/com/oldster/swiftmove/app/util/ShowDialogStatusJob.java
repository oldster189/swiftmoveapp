package com.oldster.swiftmove.app.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.oldster.swiftmove.app.R;

/**
 * Created by Old'ster on 8/12/2559.
 */

public class ShowDialogStatusJob {

    public ShowDialogStatusJob(Context mContext, String title, String message) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_advertising))
                .setMessage(message)
                .setPositiveButton("รับทราบ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
}

package com.oldster.swiftmove.app.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Old'ster on 1/12/2559.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    /*    new MaterialDialog.Builder(Contextor.getInstance().getContext())
                .title(title)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_advertising))
                .content("หมายเลขงาน : #0000" + jid + "\n" +
                        "ต้นทาง : " + frName + "\n" +
                        "ปลายทาง : " + toName + "\n" +
                        "ค่าบริการ : " + price + "\n" +
                        "ระยะทาง : " + distance)
                .positiveText("ดูรายละเอียด")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(context, DetailJobActivity.class);
                        intent.putExtra("jid", jid);
                        context.startActivity(intent);
                        ((AppCompatActivity) context).overridePendingTransition(R.anim.from_right, R.anim.to_left);
                    }
                })
                .negativeText("ดูภายหลัง")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(context, "ดูภายหลัง", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
*/
    }
}

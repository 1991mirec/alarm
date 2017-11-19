package com.example.miro.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.miro.alarm.main.WakeUp;

/**
 * Created by Miro on 12/3/2016.
 */

public class TimeAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

/*        final int isNromal = intent.getIntExtra("vol", 0);
        Toast.makeText(context,"asd" + isNromal , Toast.LENGTH_LONG).show();*/
        final int id = intent.getExtras().getInt("id");
        if (("intent" + id).equals(intent.getAction()) ||
                ("intent_intelligent" + id).equals(intent.getAction())) {
            final Intent wakeUpIntent = new Intent(context, WakeUp.class);
            wakeUpIntent.putExtras(intent.getExtras());
            wakeUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(wakeUpIntent);
        }

    }

}

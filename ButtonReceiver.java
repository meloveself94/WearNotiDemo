package edu.cs4730.wearnotidemo;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by Ryan Hoo on 6/18/2018.
 */

public class ButtonReceiver extends BroadcastReceiver {

    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private String info;

    @Override
    public void onReceive(Context context, Intent intent) {
       /* Toast.makeText(context.getApplicationContext(), "Your message", Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(MainActivity.notificationID - 1);*/

       //Try for receiver for api 26 and above
        if ("com.eugeneinflow.EXAMPLE_ACTION".equals(intent.getAction())){
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            info = String.valueOf(remoteInput.getCharSequence(EXTRA_VOICE_REPLY));
            Log.d("receive what", "onReceive: " + info);

            String receivedText = intent.getStringExtra("com.eugeneinflow.EXTRA_TEXT");
            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.cancel(MainActivity.notificationID - 1);
        }
    }
}

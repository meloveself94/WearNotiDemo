package edu.cs4730.wearnotidemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/*
 * This example shows varying notifications that work on the wearable device as well as on the 
 * the phone.  
 * 
 * 
 * Don't forget to use this command if running an emulator for wear.  Run everytime you connect the phone.
 * adb -d forward tcp:5601 tcp:5601
 */


public class MainActivity extends AppCompatActivity {
    public static String id = "test_channel_01";
    static int notificationID = 1;
    private CharSequence[] mPossiblePostResponses = new CharSequence[]{"Unlock","No"};
    // Key for the string that's delivered in the action's intent
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    ButtonReceiver exampleButtonReceiver = new ButtonReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViewById(R.id.simpleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleNoti();
            }
        });
        this.findViewById(R.id.addactionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addbuttonNoti();
            }
        });
        this.findViewById(R.id.onlywearableButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlywearableNoti();
            }
        });
        this.findViewById(R.id.bigtextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigTextNoti();
            }
        });
        this.findViewById(R.id.voicereplyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceReplytNoti();
            }
        });
        createchannel();

        IntentFilter filter = new IntentFilter("com.eugeneinflow.EXAMPLE_ACTION");
        registerReceiver(exampleButtonReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(exampleButtonReceiver);
    }

    /*
         * Just a simple notification that will show up on the wearable.
         * the user can swipe the notification to the left to reveal the Open action, which invokes the intent on the handheld device.
         */
    void simpleNoti() {

        //create the intent to launch the notiactivity, then the pentingintent.
        Intent viewIntent = new Intent(this, NotiActivity.class);
        viewIntent.putExtra("NotiID", "Notification ID is " + notificationID);

        PendingIntent viewPendingIntent =
            PendingIntent.getActivity(this, 0, viewIntent, 0);

        //Now create the notification.  We must use the NotificationCompat or it will not work on the wearable.
        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Simple Noti")
                .setContentText("This is a simple notification")
                .setChannelId(id)
                .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
            NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationID, notificationBuilder.build());
        notificationID++;
    }

    /*
     * This one adds a button to the notification.  launches the camera for this example.
     */
    void addbuttonNoti() {
        Log.i("main", "addbutton noti");
        //create the intent to launch the notiactivity, then the pentingintent.
        Intent viewIntent = new Intent(this, NotiActivity.class);
        viewIntent.putExtra("NotiID", "Notification ID is " + notificationID);

        PendingIntent viewPendingIntent =
            PendingIntent.getActivity(this, 0, viewIntent, 0);

        // we are going to add an intent to open the camera here.
        //Intent cameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PendingIntent cameraPendingIntent =
            PendingIntent.getActivity(this, 0, cameraIntent, 0);

        NotificationCompat.Action.WearableExtender inlineActionForWear2 =
            new NotificationCompat.Action.WearableExtender()
                .setHintDisplayActionInline(true)
                .setHintLaunchesActivity(true);

        // Add an action to allow replies.
        NotificationCompat.Action pictureAction =
            new NotificationCompat.Action.Builder(
                R.drawable.icn_voice2,
                "Unlock",
                cameraPendingIntent)
                .extend(inlineActionForWear2)
                .build();

        //Now create the notification.  We must use the NotificationCompat or it will not work on the wearable.
        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("add button Noti")
                .setContentText("Tap for full message.")
                .setContentIntent(viewPendingIntent)
                .setChannelId(id)
                .addAction(pictureAction);

        notificationBuilder.setVibrate(new long[]{700,700,700});
        Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
            NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationID, notificationBuilder.build());
        notificationID++;

    }

    /*
     * Both the phone and wear will have a notification.  This adds the button so it only shows
     * on the wearable device and not the phone notification.
     */
    void onlywearableNoti() {
        //create the intent to launch the notiactivity, then the pentingintent.
        Intent viewIntent = new Intent(this, NotiActivity.class);
        viewIntent.putExtra("NotiID", "Notification ID is " + notificationID);

        PendingIntent viewPendingIntent =
            PendingIntent.getActivity(this, 0, viewIntent, 0);

        // we are going to add an intent to open the camera here.
        Intent cameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        PendingIntent cameraPendingIntent =
            PendingIntent.getActivity(this, 0, cameraIntent, 0);

        // Create the action
        NotificationCompat.Action action =
            new NotificationCompat.Action.Builder(R.drawable.ic_action_time,
                "Open Camera", cameraPendingIntent)
                .build();


        //Now create the notification.  We must use the NotificationCompat or it will not work on the wearable.
        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Button on Wear Only")
                .setContentText("tap to open message")
                .setContentIntent(viewPendingIntent)
                .setChannelId(id)
                .extend(new WearableExtender().addAction(action));

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
            NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationID, notificationBuilder.build());
        notificationID++;
    }

    /*
     * using the bigtext notificiation.
     */
    void bigTextNoti() {
        //create the intent to launch the notiactivity, then the pentingintent.
        Intent viewIntent = new Intent(this, NotiActivity.class);
        viewIntent.putExtra("NotiID", "Notification ID is " + notificationID);

        PendingIntent viewPendingIntent =
            PendingIntent.getActivity(this, 0, viewIntent, 0);

        BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText("Big text style.\n"
            + "We should have more room to add text for the user to read, instead of a short message.");


        //Now create the notification.  We must use the NotificationCompat or it will not work on the wearable.
        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Simple Noti")
                .setContentText("This is a simple notification")
                .setContentIntent(viewPendingIntent)
                .setChannelId(id)
                .setStyle(bigStyle);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
            NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationID, notificationBuilder.build());
        notificationID++;
    }


    /*
     * This adds the voice response for the wearable device.
     * It comes back via an intent, which is shown in voiceNotiActivity.
     */
    void voiceReplytNoti() {

        //create the intent to launch the notiactivity, then the pentingintent.
      /*  Intent replyIntent = new Intent(this, ButtonReceiver.class);
        replyIntent.putExtra("NotiID", "Notification ID is " + notificationID);*/

      //Try for api 26 and above
        Intent replyIntent = new Intent("com.eugeneinflow.EXAMPLE_ACTION");
        replyIntent.putExtra("com.eugeneinflow.EXTRA_TEXT", "Notification ID is " + notificationID);


        PendingIntent replyPendingIntent =
            PendingIntent.getBroadcast(this, 0, replyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // create the remote input part for the notification.
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
            .setLabel("Actions")
            .setChoices(mPossiblePostResponses)
            .build();

        NotificationCompat.Action.WearableExtender inlineActionForWear2 =
                new NotificationCompat.Action.WearableExtender()
                        .setHintDisplayActionInline(true)
                        .setHintLaunchesActivity(true);

        // Create the reply action and add the remote input
        NotificationCompat.Action action =
            new NotificationCompat.Action.Builder(
                    R.drawable.icn_voice,
                "Voice Unlock",
                    replyPendingIntent)
                .addRemoteInput(remoteInput)
                .extend(inlineActionForWear2)
                .setAllowGeneratedReplies(false)
                .build();


        //Now create the notification.  We must use the NotificationCompat or it will not work on the wearable.
        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Boom Gate Nearby")
                .setContentText("Unlock?")
                .setChannelId(id)
                .setAutoCancel(true)
                .extend(new WearableExtender().addAction(action));

        notificationBuilder.setVibrate(new long[]{700,700,700});
        Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
            NotificationManagerCompat.from(this);
        notificationBuilder.setAutoCancel(true);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationID, notificationBuilder.build());

        notificationID++;

    }


    /*
* for API 26+ create notification channels
*/
    private void createchannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(id,
                getString(R.string.channel_name),  //name of the channel
                NotificationManager.IMPORTANCE_DEFAULT);   //importance level
            //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
            // Configure the notification channel.
            mChannel.setDescription(getString(R.string.channel_description));
            mChannel.enableLights(true);
            //Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(mChannel);

        }
    }

}

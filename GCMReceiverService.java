package maslsalesapp.minda.javaclasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import maslsalesapp.minda.R;

/**
 * Created by ashish on 8/8/16.
 */
public class GCMReceiverService extends GcmListenerService {

    //This method will be called on every new message received
    String image = "", title = "",desc="";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        image = data.getString("message");
        title = data.getString("title");
        desc = data.getString("desc");
//        image = data.getString("image");
        //Displaying a notiffication with the message
        sendNotification(title);
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message) {
        Intent intent = new Intent(this, NotificationImageActivity.class);
        intent.putExtra("Image", image);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        int num = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mindaapplauncher)
                .setContentText(desc)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(num, noBuilder.build()); //0 = ID of notification
    }
}
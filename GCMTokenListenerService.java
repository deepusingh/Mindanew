package maslsalesapp.minda.javaclasses;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by ashish on 8/8/16.
 */
public class GCMTokenListenerService extends InstanceIDListenerService {

    //If the token is changed registering the device again
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMIntentService.class);
        startService(intent);
    }
}
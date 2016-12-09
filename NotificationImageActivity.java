package maslsalesapp.minda.javaclasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import maslsalesapp.minda.R;
import maslsalesapp.minda.miscellaneousclasses.AppController;

/**
 * Created by dipendra on 23/8/16.
 */

public class NotificationImageActivity extends Activity {
    String imageurl = "";
    ImageLoader imageLoader;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationimage);

        mProgressDialog = new ProgressDialog(NotificationImageActivity.this);
        // Set progressdialog title
        mProgressDialog.setTitle(" Please Wait... ");
        // Set progressdialog message
        mProgressDialog.setMessage(" Loading data...");

        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.setCanceledOnTouchOutside(false);

        mProgressDialog.show();

        imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView notyimage = (NetworkImageView) findViewById(R.id.imageView2);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            imageurl = null;
        } else {
            imageurl = extras.getString("Image");
        }

        imageLoader.get(imageurl, ImageLoader.getImageListener(notyimage,
                R.drawable.blank, R.drawable
                        .blank));
        notyimage.setImageUrl(imageurl, imageLoader);

        mProgressDialog.dismiss();

    }
}

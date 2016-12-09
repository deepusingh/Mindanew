package maslsalesapp.minda.javaclasses;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

import maslsalesapp.minda.adapterandgetset.RetailerGetset;

public class Constants {
    public static final String URL = "http://mindaasl.co.in/maslapp/webservice_new.php?";
    public static final String MyPREFERENCES = "MyPrefs";
    public static String zone = "";
    public static SharedPreferences sharedpreferences;
    public static SharedPreferences.Editor editor;
    public static ArrayList<RetailerGetset> itemlist = new ArrayList<RetailerGetset>();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}



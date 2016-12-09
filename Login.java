package maslsalesapp.minda.javaclasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import maslsalesapp.minda.R;
import maslsalesapp.minda.miscellaneousclasses.Validation;

/**
 * Created by xantatech on 6/6/16.
 */
public class Login extends Activity {

    EditText username, passwd;
    Button login;
    ProgressDialog mProgressDialog, mProgressDialog1;
    RequestQueue requestQueue;
    TextView textView_link;
    String regId, deviceIdString;
    AsyncTask<Void, Void, Void> mRegisterTask;
    int version = 0, webversion = 0;
    String tid, did;
    TextView getpass;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        Constants.sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        mProgressDialog = new ProgressDialog(Login.this);
        // Set progressdialog title
        mProgressDialog.setTitle(" Please Wait... ");
        // Set progressdialog message
        mProgressDialog.setMessage(" Loging in...");

        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.setCanceledOnTouchOutside(false);


        mProgressDialog1 = new ProgressDialog(Login.this);
        // Set progressdialog title
        mProgressDialog1.setTitle(" Please Wait... ");
        // Set progressdialog message
        mProgressDialog1.setMessage(" Getting info...");

        mProgressDialog1.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog1.setCanceledOnTouchOutside(false);

        requestQueue = Volley.newRequestQueue(Login.this);


        did = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//----------------------------------------------------------------------------------------------------
//--------------------------  GCM CODE START  --------------------------------------------------------
//----------------------------------------------------------------------------------------------------

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();
                    Log.e("Reg ID", "onReceive: " + token);

                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GCMIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMIntentService.class);
            startService(itent);
        }
//----------------------------------------------------------------------------------------------------
//----------------------------------  GCM CODE END  --------------------------------------------------
//----------------------------------------------------------------------------------------------------


        username = (EditText) findViewById(R.id.username);
        passwd = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.loginclick);
        textView_link = (TextView) findViewById(R.id.textView_link);
        getpass = (TextView) findViewById(R.id.getpassword);
        deviceIdString = Secure.getString(Login.this.getContentResolver(), Secure.ANDROID_ID);

        //----------------------------------  Version CODE start  --------------------------------------------------

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionCode;

        if (Constants.sharedpreferences.contains("EMP_CODE")) {

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();

        }


        if (Constants.isNetworkAvailable(getApplicationContext())) {
            mProgressDialog1.show();
            getversion(Constants.URL + "getversion=1");
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

            builder.setTitle("No internet");
            builder.setCancelable(false);
            builder.setMessage("Make sure you are connected");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    finish();
                }
            });

            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }


        //----------------------------------  Version CODE end  --------------------------------------------------



        textView_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse("http://www.mindaasl.com"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Login.this.startActivity(intent);
            }
        });

        getpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Constants.isNetworkAvailable(getApplicationContext())) {

                    if (!(Validation.validateFirstname(username.getText().toString()))) {
                        username.setError(Html
                                .fromHtml("<font color='red'>Please enter username</font>"));
                        return;

                    } else {
                        username.setError(null);
                        mProgressDialog.show();
                        getpassword(Constants.URL, username.getText().toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Network", Toast.LENGTH_SHORT).show();
                }

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constants.isNetworkAvailable(getApplicationContext())) {

                    if (!(Validation.validateFirstname(username.getText().toString()))) {
                        username.setError(Html
                                .fromHtml("<font color='red'>Please enter username</font>"));
                        return;

                    } else if (!(Validation.validatePassword(passwd.getText().toString()))) {
                        username.setError(null);
                        passwd.setError(Html
                                .fromHtml("<font color='red'>Please enter password</font>"));
                        return;
                    } else {
                        passwd.setError(null);
                        mProgressDialog.show();
                        login.setBackgroundResource(R.drawable.round_corner_clicked);
                        getresult(Constants.URL, username.getText().toString(), passwd.getText().toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Network", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void getresult(String url, final String name, final String email) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");

                            if (status == 0) {
                                Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                String uid = jsonObject.getString("userid");
                                uid
                                        .replace(" ", "");
//                                Constants.editor.putString("USER_ID", uid);
                                Constants.editor.putString("EMP_CODE", username.getText().toString());
                                Constants.editor.commit();

                                Integer pswdstatus = jsonObject.getInt("password_status");

                                if (pswdstatus == 0) {
                                    Intent i = new Intent(getApplicationContext(), ChangePassword.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }


                            }

                            mProgressDialog.dismiss();

                            login.setBackgroundResource(R.drawable.round_corner_blue);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("login", "1");
                map.put("username", name);
                map.put("password", email);
                tid = Constants.sharedpreferences.getString("token", "0");
                map.put("tid", tid);
                map.put("did", did);
                return map;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

    public void getversion(String url) {

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            webversion = Integer.parseInt(jsonObject.getString("version"));

                            mProgressDialog1.dismiss();

                            if (version < webversion) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

                                builder.setTitle("Version is outdated");
                                builder.setCancelable(false);
                                builder.setMessage("Your Current installed MASL Sales App is outdated, please update latest from Google Play store now");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Show location settings when the user acknowledges the alert dialog
                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }

                                        finish();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        finish();
                                    }
                                });
                                Dialog alertDialog = builder.create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();

                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();

                return map;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

    public void getpassword(String url, final String username) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            mProgressDialog.dismiss();
                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");

                            if (status == 0) {

                                Toast.makeText(getApplicationContext(), "Username not exist", Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(getApplicationContext(), "Password sent to your mail id", Toast.LENGTH_SHORT).show();


                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", username);
                map.put("getpassword", "1");
                return map;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

}
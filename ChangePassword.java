package maslsalesapp.minda.javaclasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import maslsalesapp.minda.R;
import maslsalesapp.minda.miscellaneousclasses.Validation;

/**
 * Created by xantatech on 6/6/16.
 */
public class ChangePassword extends Activity {

    EditText passwd;
    ProgressDialog mProgressDialog;
    Button login;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.changepassword);

        Constants.sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        mProgressDialog = new ProgressDialog(ChangePassword.this);
        // Set progressdialog title
        mProgressDialog.setTitle(" Please Wait... ");
        // Set progressdialog message
        mProgressDialog.setMessage(" Loging in...");

        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.setCanceledOnTouchOutside(false);

        requestQueue = Volley.newRequestQueue(ChangePassword.this);

        passwd = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.changepswd);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constants.isNetworkAvailable(getApplicationContext())) {

                    if (!(Validation.validatePassword(passwd.getText().toString()))) {

                        passwd.setError(Html
                                .fromHtml("<font color='red'>Please enter password</font>"));
                        return;
                    } else if (passwd.length() != 6) {

                        Toast.makeText(getApplicationContext(), "Enter 6 digit Password", Toast.LENGTH_SHORT).show();

                    } else {
                        passwd.setError(null);
                        mProgressDialog.show();
                        getresult(Constants.URL, passwd.getText().toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Network", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void getresult(String url, final String paswd) {

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
                                Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();


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
                map.put("fpassword", "1");
                map.put("empcode", Constants.sharedpreferences.getString("EMP_CODE", "0"));
                map.put("newpassword", paswd);
                return map;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

}

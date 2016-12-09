package maslsalesapp.minda.javaclasses;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by xantatech on 31/5/16.
 */
public class ChangePasswordnew extends Fragment {


    EditText passwd, oldpassword;
    ProgressDialog mProgressDialog;
    Button login;
    RequestQueue requestQueue;
    String oldpasswordstring = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.changepasswordnew, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        passwd = (EditText) rootView.findViewById(R.id.password);
        oldpassword = (EditText) rootView.findViewById(R.id.oldpassword);
        login = (Button) rootView.findViewById(R.id.changepswd);
        Button back = (Button) rootView.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dashboard fragment2 = new Dashboard();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.commit();

            }
        });


        mProgressDialog = new ProgressDialog(getActivity());
        // Set progressdialog title
        mProgressDialog.setTitle(" Please Wait... ");
        // Set progressdialog message
        mProgressDialog.setMessage(" Loading data...");

        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.setCanceledOnTouchOutside(false);

        requestQueue = Volley.newRequestQueue(getActivity());

        mProgressDialog.show();

        getpassword(Constants.URL, Constants.sharedpreferences.getString("EMP_CODE", "0"));


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constants.isNetworkAvailable(getActivity())) {

                    if (!(Validation.validatePassword(oldpassword.getText().toString()))) {

                        oldpassword.setError(Html
                                .fromHtml("<font color='red'>Please enter old password</font>"));
                        return;
                    } else if (!(Validation.validatePassword(passwd.getText().toString()))) {
                        oldpassword.setError(null);
                        passwd.setError(Html
                                .fromHtml("<font color='red'>Please enter password</font>"));
                        return;
                    } else if (!oldpasswordstring.equalsIgnoreCase(oldpassword.getText().toString())) {

                        Toast.makeText(getActivity(), "Old Password is wrong", Toast.LENGTH_SHORT).show();

                    } else if (passwd.length() != 6) {

                        Toast.makeText(getActivity(), "Enter 6 digit Password", Toast.LENGTH_SHORT).show();

                    } else {
                        passwd.setError(null);
                        mProgressDialog.show();
                        getresult(Constants.URL, passwd.getText().toString());
                    }
                } else {
                    Toast.makeText(getActivity(), "No Network", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return rootView;
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


                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");


                            oldpasswordstring = jsonObject1.getString("password").trim();





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
                map.put("getuserpassword", "1");
                return map;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
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

                                Toast.makeText(getActivity(), "" + jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                oldpassword.setText("");
                                passwd.setText("");

                                Constants.editor.clear();
                                Constants.editor.commit();

                                Intent i = new Intent(getActivity(), Login.class);
                                startActivity(i);
                                getActivity().finish();
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();

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

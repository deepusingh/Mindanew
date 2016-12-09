package maslsalesapp.minda.javaclasses;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import maslsalesapp.minda.R;
import maslsalesapp.minda.adapterandgetset.RetailerGetset;

/**
 * Created by xantatech on 26/7/16.
 */
public class StartDiscussion extends Fragment {

    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    ArrayList<String> namelist;
    ArrayList<RetailerGetset> getlist;
    Button placeorder;
    EditText remark;
    TextView question;
    String usercode = "", usertype = "", username = "", page = "";
    Bundle bundle = new Bundle();
    Dialog dialog;
    Spinner selectbp;
    String spinnervalue = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.discussionpage, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();


        dialog = new Dialog(getActivity());

        mProgressDialog = new ProgressDialog(getActivity());
        // Set progressdialog title
        mProgressDialog.setTitle(" Please Wait... ");
        // Set progressdialog message
        mProgressDialog.setMessage(" Loading data...");

        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.setCanceledOnTouchOutside(false);

        requestQueue = Volley.newRequestQueue(getActivity());


        if (getArguments().getString("Code") != null) {
            usercode = getArguments().getString("Code");

        }
        if (getArguments().getString("Type") != null) {
            usertype = getArguments().getString("Type");
        }
        if (getArguments().getString("name") != null) {
            username = getArguments().getString("name");
        }


        getretailer(Constants.URL + "getquery=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0") + "&custype=" + usertype + "&cuscode=" + usercode);


        placeorder = (Button) rootView.findViewById(R.id.update);
        remark = (EditText) rootView.findViewById(R.id.remark);
        selectbp = (Spinner) rootView.findViewById(R.id.selectbp);
        question = (TextView) rootView.findViewById(R.id.question);

        selectbp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {

                    String[] parts = namelist.get(position).split(" : ");
                    spinnervalue = parts[0];
                    question.setText("Query : " + getlist.get(position - 1).getDesc().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button back = (Button) rootView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bundle.putString("Code", usercode);
                bundle.putString("Type", usertype);
                bundle.putString("name", username);


                if (usertype.equals("retailer")) {
                    Retailer fragment2 = new Retailer();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragment2.setArguments(bundle);
                    fragmentTransaction.commit();
                } else if (usertype.equals("bp")) {
                    Businesspartner fragment2 = new Businesspartner();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragment2.setArguments(bundle);
                    fragmentTransaction.commit();

                } else {
                    Mechanic fragment2 = new Mechanic();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragment2.setArguments(bundle);
                    fragmentTransaction.commit();

                }


            }
        });

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (remark.getText().toString().equals("")) {

                    remark.setError("Enter Answer");

                } else {
                    mProgressDialog.show();

                    placeorder.setBackgroundColor(Color.RED);
                    placeorder.setClickable(false);

                    addorder(Constants.URL);


                }

            }
        });


        return rootView;
    }

    public void addorder(String url) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");

                            if (status == 0) {
                                placeorder.setBackgroundColor(Color.parseColor("#22A7E8"));
                                placeorder.setClickable(true);
                                Toast.makeText(getActivity(), "Not Placed", Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(getActivity(), "Reply placed", Toast.LENGTH_SHORT).show();
                                getretailer(Constants.URL + "getquery=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0") + "&custype=" + usertype + "&cuscode=" + usercode);
                                remark.setText("");

                            }
                            placeorder.setBackgroundColor(Color.parseColor("#22A7E8"));
                            mProgressDialog.dismiss();

                            bundle.putString("Code", usercode);
                            bundle.putString("Type", usertype);
                            bundle.putString("name", username);


                            if (usertype.equals("retailer")) {
                                Retailer fragment2 = new Retailer();
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frame_container, fragment2);
                                fragment2.setArguments(bundle);
                                fragmentTransaction.commit();
                            } else if (usertype.equals("bp")) {
                                Businesspartner fragment2 = new Businesspartner();
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frame_container, fragment2);
                                fragment2.setArguments(bundle);
                                fragmentTransaction.commit();
                            } else {
                                Mechanic fragment2 = new Mechanic();
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frame_container, fragment2);
                                fragment2.setArguments(bundle);
                                fragmentTransaction.commit();

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
                map.put("addans", "1");
                map.put("MDSRM_REF_NUM", "" + spinnervalue);
                map.put("MDSRD_DISCUS_TYPE", "ANS");
                map.put("ans", "" + remark.getText().toString());


                return map;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

    public void getretailer(String url) {
        mProgressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            mProgressDialog.dismiss();
                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");


                            if (status == 0) {
                                bundle.putString("Code", usercode);
                                bundle.putString("Type", usertype);
                                bundle.putString("name", username);


                                if (usertype.equals("retailer")) {
                                    Retailer fragment2 = new Retailer();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                                    fragment2.setArguments(bundle);
                                    fragmentTransaction.commit();
                                } else if (usertype.equals("bp")) {
                                    Businesspartner fragment2 = new Businesspartner();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                                    fragment2.setArguments(bundle);
                                    fragmentTransaction.commit();
                                } else {
                                    Mechanic fragment2 = new Mechanic();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                                    fragment2.setArguments(bundle);
                                    fragmentTransaction.commit();

                                }

                                Toast.makeText(getActivity(), "No Question", Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                namelist = new ArrayList<String>();
                                getlist = new ArrayList<RetailerGetset>();
                                namelist.clear();
                                getlist.clear();
                                namelist.add("Select Here");
                                for (int j = 0; j < jsonArray.length(); j++) {


                                    RetailerGetset pack = new RetailerGetset();
                                    JSONObject objectnew = jsonArray.getJSONObject(j);
                                    pack.setRetailercode((objectnew.getString("MDSRM_REF_NUM")));
                                    pack.setRetailername((objectnew.getString("MDSRD_DISCUS_DT")));
                                    pack.setDesc((objectnew.getString("MDSRD_DISCUS")));

                                    getlist.add(pack);

                                    namelist.add(objectnew.getString("MDSRM_REF_NUM") + " : " + objectnew.getString("MDSRD_DISCUS_DT"));
                                }

                            }

                            ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                    R.layout.add_customer_item_layout, R.id.textView3, namelist);

                            selectbp.setAdapter(list);


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


}

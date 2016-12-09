package maslsalesapp.minda.javaclasses;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maslsalesapp.minda.R;
import maslsalesapp.minda.adapterandgetset.RetailerGetset;
import maslsalesapp.minda.miscellaneousclasses.AppController;

/**
 * Created by xantatech on 31/5/16.
 */
public class MyDiscussion extends Fragment {

    ListView productlist;
    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    ArrayList<RetailerGetset> retailerlist = new ArrayList<RetailerGetset>();
    ArrayList<String> productitemlist;
    Productitem_Adapter productadpter;
    SimpleDateFormat simpleDateFormat;
    Calendar calander;
    ImageLoader imageLoader;
    Button open, close, other;
    TextView totalcount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.mydiscussions, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        productlist = (ListView) rootView.findViewById(R.id.productlist);
        imageLoader = AppController.getInstance().getImageLoader();
        Button back = (Button) rootView.findViewById(R.id.back);
        open = (Button) rootView.findViewById(R.id.open);
        close = (Button) rootView.findViewById(R.id.close);
        other = (Button) rootView.findViewById(R.id.other);
        totalcount = (TextView) rootView.findViewById(R.id.totalcount);

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

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getproductlist(Constants.URL + "allquery=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));
                close.setTextColor(getResources().getColor(R.color.white));
                open.setTextColor(getResources().getColor(R.color.black));
                other.setTextColor(getResources().getColor(R.color.white));
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getproductlistclose(Constants.URL + "allquery=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));
                open.setTextColor(getResources().getColor(R.color.white));
                close.setTextColor(getResources().getColor(R.color.black));
                other.setTextColor(getResources().getColor(R.color.white));

            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getproductlistother(Constants.URL + "allquery=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));
                open.setTextColor(getResources().getColor(R.color.white));
                close.setTextColor(getResources().getColor(R.color.white));
                other.setTextColor(getResources().getColor(R.color.black));

            }
        });


        getproductlist(Constants.URL + "allquery=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));

        return rootView;
    }


    public void getproductlist(String url) {
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


                            retailerlist = new ArrayList<RetailerGetset>();
                            productitemlist = new ArrayList<String>();
                            retailerlist.clear();
                            productitemlist.clear();

                            if (status == 0) {
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                for (int j = 0; j < jsonArray.length(); j++) {

                                    RetailerGetset pack = new RetailerGetset();
                                    JSONObject objectnew = jsonArray.getJSONObject(j);
                                    if (!objectnew.getString("ques").equalsIgnoreCase("") && objectnew.getString("ans").equalsIgnoreCase("")) {

                                        pack.setCode((objectnew.getString("ques")));
                                        pack.setDesc("");
                                        pack.setItemqty("");

                                        pack.setGroup((objectnew.getString("MDSRD_DISCUS_DT")));
                                        pack.setCategory((objectnew.getString("MDSRM_REF_NUM")));

                                        pack.setGdesc((objectnew.getString("MDSRM_CUST_TYPE")));
                                        pack.setCompany((objectnew.getString("DSRD_CUST_CODE")));
                                        pack.setItemcode((objectnew.getString("DSRD_CUST_NAME")));

                                        retailerlist.add(pack);
                                    }
                                }

                                productadpter = new Productitem_Adapter(getActivity(), retailerlist);
                                productlist.setAdapter(productadpter);


                            }
                            totalcount.setVisibility(View.VISIBLE);
                            totalcount.setText("Total record found : " + retailerlist.size());
                            View view = getActivity().getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    public void getproductlistclose(String url) {
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


                            retailerlist = new ArrayList<RetailerGetset>();
                            productitemlist = new ArrayList<String>();
                            retailerlist.clear();
                            productitemlist.clear();

                            if (status == 0) {
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                for (int j = 0; j < jsonArray.length(); j++) {

                                    RetailerGetset pack = new RetailerGetset();
                                    JSONObject objectnew = jsonArray.getJSONObject(j);

                                    if (objectnew.getString("ans").equalsIgnoreCase("")) {

                                    } else {
                                        pack.setCode((objectnew.getString("ques")));
                                        pack.setDesc((objectnew.getString("ans")));
                                        pack.setItemqty("");

                                        pack.setGroup((objectnew.getString("MDSRD_DISCUS_DT")));
                                        pack.setCategory((objectnew.getString("MDSRM_REF_NUM")));

                                        pack.setGdesc((objectnew.getString("MDSRM_CUST_TYPE")));
                                        pack.setCompany((objectnew.getString("DSRD_CUST_CODE")));
                                        pack.setItemcode((objectnew.getString("DSRD_CUST_NAME")));

                                        retailerlist.add(pack);
                                    }

                                }

                                productadpter = new Productitem_Adapter(getActivity(), retailerlist);
                                productlist.setAdapter(productadpter);


                            }
                            totalcount.setVisibility(View.VISIBLE);
                            totalcount.setText("Total record found : " + retailerlist.size());
                            View view = getActivity().getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    public void getproductlistother(String url) {
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


                            retailerlist = new ArrayList<RetailerGetset>();
                            productitemlist = new ArrayList<String>();
                            retailerlist.clear();
                            productitemlist.clear();

                            if (status == 0) {
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                for (int j = 0; j < jsonArray.length(); j++) {

                                    RetailerGetset pack = new RetailerGetset();
                                    JSONObject objectnew = jsonArray.getJSONObject(j);

                                    if (objectnew.getString("rem").equalsIgnoreCase("")) {

                                    } else {
                                        pack.setCode("");
                                        pack.setDesc("");
                                        pack.setItemqty((objectnew.getString("rem")));

                                        pack.setGroup((objectnew.getString("MDSRD_DISCUS_DT")));
                                        pack.setCategory((objectnew.getString("MDSRM_REF_NUM")));

                                        pack.setGdesc((objectnew.getString("MDSRM_CUST_TYPE")));
                                        pack.setCompany((objectnew.getString("DSRD_CUST_CODE")));
                                        pack.setItemcode((objectnew.getString("DSRD_CUST_NAME")));

                                        retailerlist.add(pack);
                                    }

                                }
                                productadpter = new Productitem_Adapter(getActivity(), retailerlist);
                                productlist.setAdapter(productadpter);


                            }
                            totalcount.setVisibility(View.VISIBLE);
                            totalcount.setText("Total record found : " + retailerlist.size());

                            View view = getActivity().getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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


    class Productitem_Adapter extends BaseAdapter {

        Context context;
        List<RetailerGetset> myList;
        LayoutInflater inflater;
        TextView tv1, tv2, tv3, tv4, view;


        public Productitem_Adapter(Context mcontext, List<RetailerGetset> pmyList) {

            this.context = mcontext;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.myList = pmyList;

        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public Object getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.mydiscussionadapter, parent, false);

            tv1 = (TextView) convertView.findViewById(R.id.tv1);
            tv2 = (TextView) convertView.findViewById(R.id.tv2);
            tv3 = (TextView) convertView.findViewById(R.id.tv3);
            tv4 = (TextView) convertView.findViewById(R.id.tv4);

            if (myList.get(position).getCode().equalsIgnoreCase("")) {
                tv1.setText("Rem: " + myList.get(position).getItemqty());
                tv2.setText("");
            } else if (myList.get(position).getDesc().equalsIgnoreCase("")) {
                tv1.setText("Q: " + myList.get(position).getCode());
                tv2.setText("");
            } else {
                tv1.setText("Q: " + myList.get(position).getCode());
                tv2.setText("A: " + String.valueOf(myList.get(position).getDesc()));

            }

            tv3.setText("Dated:  " + String.valueOf(myList.get(position).getGroup() + "-" + String.valueOf(myList.get(position).getCategory())));
            tv4.setText(String.valueOf(myList.get(position).getGdesc()) + "-" + String.valueOf(myList.get(position).getItemcode() + ":" + String.valueOf(myList.get(position).getCompany())));


            return convertView;
        }

    }


}

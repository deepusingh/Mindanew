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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maslsalesapp.minda.R;
import maslsalesapp.minda.adapterandgetset.RetailerGetset;

/**
 * Created by xantatech on 31/5/16.
 */
public class PriceList extends Fragment {

    ListView productlist;
    Spinner productgrp, productcategory;
    AutoCompleteTextView productctype;
    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    TextView totalcount;
    ArrayList<RetailerGetset> retailerlist = new ArrayList<RetailerGetset>();
    ArrayList<String> firstarraylist;
    ArrayList<String> secondarraylist;
    ArrayList<String> thirdarraylist;

    Productitem_Adapter productadpter;
    String cat = "", grp = "", vtime = "", ctype = "";
    LinearLayout toplayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pricelist, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        vtime = (currentDateTimeString);

        productgrp = (Spinner) rootView.findViewById(R.id.productgrp);
        productcategory = (Spinner) rootView.findViewById(R.id.productcategory);
        productctype = (AutoCompleteTextView) rootView.findViewById(R.id.productctype);
        productlist = (ListView) rootView.findViewById(R.id.productlist);
        toplayout = (LinearLayout) rootView.findViewById(R.id.topLayout);
        totalcount = (TextView) rootView.findViewById(R.id.totalcount);
        toplayout.setVisibility(View.GONE);

        ImageView autodelete = (ImageView) rootView.findViewById(R.id.autodelete);
        autodelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productctype.setText("");
            }
        });

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

        getproductgrp(Constants.URL + "grouplist=1");
        getproductcategory(Constants.URL + "allcpcl=1");
        getproductctype(Constants.URL + "allctype=1");

        productgrp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    grp = firstarraylist.get(position);

                    getproductcategory(Constants.URL + "pcat=1&grp=" + grp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        productcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    cat = secondarraylist.get(position);

                    getproductctype(Constants.URL + "ctype=1&cat=" + cat + "&grp=" + grp);
                    getproductlist(Constants.URL + "cproduct=1=1&cat=" + cat + "&grp=" + grp);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        productctype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ctype =(String) adapterView.getItemAtPosition(i);

                getproductlist(Constants.URL + "cproduct=1&cat=" + cat + "&grp=" + grp);

            }
        });


        return rootView;
    }


    public void getproductgrp(String url) {
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

                            } else {


                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                firstarraylist = new ArrayList<String>();
                                firstarraylist.clear();
                                firstarraylist.add("Select here");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    firstarraylist.add("" + jsonArray.get(i).toString());

                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, firstarraylist);

                                productgrp.setAdapter(list);

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

    public void getproductcategory(String url) {
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

                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                secondarraylist = new ArrayList<String>();
                                secondarraylist.clear();
                                secondarraylist.add("Select here");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    secondarraylist.add("" + jsonArray.get(i).toString());

                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, secondarraylist);


                                productcategory.setAdapter(list);

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

    public void getproductctype(String url) {
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

                            } else {


                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                thirdarraylist = new ArrayList<String>();
                                thirdarraylist.clear();
                                thirdarraylist.add("Select here");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    thirdarraylist.add("" + jsonArray.get(i).toString());

                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, thirdarraylist);


                                productctype.setAdapter(list);


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

    public void getproductlist(String url) {
        mProgressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mProgressDialog.dismiss();

                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");

                            retailerlist = new ArrayList<RetailerGetset>();
                            retailerlist.clear();
                            productadpter = new Productitem_Adapter(getActivity(), retailerlist);
                            productlist.setAdapter(productadpter);
                            productadpter.notifyDataSetChanged();

                            if (status == 0) {
                                productadpter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {

                                toplayout.setVisibility(View.VISIBLE);

                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                for (int j = 0; j < jsonArray.length(); j++) {


                                    RetailerGetset pack = new RetailerGetset();
                                    JSONObject objectnew = jsonArray.getJSONObject(j);
                                    pack.setCode((objectnew.getString("code")));
                                    pack.setDesc((objectnew.getString("desc")));
                                    pack.setCategory((objectnew.getString("category")));
                                    pack.setCompany((objectnew.getString("company")));
                                    pack.setModel((objectnew.getString("model")));
                                    pack.setPrice((objectnew.getInt("price")));
                                    pack.setGdesc((objectnew.getString("gdesc")));

                                    pack.setQty(1);
                                    pack.setMulitplyprice((objectnew.getInt("price")));

                                    retailerlist.add(pack);

                                }

                                productadpter.notifyDataSetChanged();


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
                map.put("ctype", ctype);
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
        TextView tv1, tv2, tv3, tv4, tv5;


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

            convertView = inflater.inflate(R.layout.productpricelistadapter, parent, false);

            tv1 = (TextView) convertView.findViewById(R.id.tv1);
            tv2 = (TextView) convertView.findViewById(R.id.tv2);
            tv3 = (TextView) convertView.findViewById(R.id.tv3);
            tv4 = (TextView) convertView.findViewById(R.id.tv4);
            tv5 = (TextView) convertView.findViewById(R.id.tv5);


            tv1.setText(myList.get(position).getCode());
            tv2.setText(String.valueOf(myList.get(position).getDesc()));
            tv3.setText(String.valueOf(myList.get(position).getCompany()));
            tv4.setText("  " + String.valueOf(myList.get(position).getModel()));
            tv5.setText("  " + String.valueOf(myList.get(position).getPrice()));

            return convertView;
        }

    }


}

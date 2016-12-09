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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maslsalesapp.minda.R;
import maslsalesapp.minda.adapterandgetset.ItemListGetset;
import maslsalesapp.minda.adapterandgetset.RetailerGetset;

/**
 * Created by xantatech on 31/5/16.
 */
public class Myorders extends Fragment {

    ListView productlist;
    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    ArrayList<RetailerGetset> retailerlist = new ArrayList<RetailerGetset>();
    TextView totalcount;
    ArrayList<String> productitemlist;
    Productitem_Adapter productadpter;
    Bundle bundle = new Bundle();
    Button retfilt, mechfilt, allretmec;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.myorders, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        productlist = (ListView) rootView.findViewById(R.id.productlist);
        Button back = (Button) rootView.findViewById(R.id.back);
        retfilt = (Button) rootView.findViewById(R.id.retfilt);
        mechfilt = (Button) rootView.findViewById(R.id.mecfilt);
        allretmec = (Button) rootView.findViewById(R.id.all);
        totalcount = (TextView) rootView.findViewById(R.id.totalcount);

        productlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                bundle.putString("Code", retailerlist.get(position).getCompany().toString());
                bundle.putString("TotalPrice", retailerlist.get(position).getCategory());
                bundle.putString("TotalQty", retailerlist.get(position).getItemqty());
                bundle.putString("NAME", retailerlist.get(position).getGdesc().toString());
                bundle.putString("MDR", retailerlist.get(position).getCode().toString());
                bundle.putParcelableArrayList("itemlist", retailerlist.get(position).getItemlist());


                Myorderviewcat fragment2 = new Myorderviewcat();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragment2.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


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
        // Set progressdialog message'
        mProgressDialog.setMessage(" Loading data...");

        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.setCanceledOnTouchOutside(false);

        requestQueue = Volley.newRequestQueue(getActivity());

        getproductlist(Constants.URL + "totorder=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));
//        getproductlist(Constants.URL+"totorder=1&empcode=hubli");

        retfilt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getproductlist(Constants.URL + "totorder=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0") + "&type=retailer");
                mechfilt.setTextColor(getResources().getColor(R.color.white));
                retfilt.setTextColor(getResources().getColor(R.color.black));
                allretmec.setTextColor(getResources().getColor(R.color.white));
            }
        });

        mechfilt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getproductlist(Constants.URL + "totorder=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0") + "&type=mechanic");
                retfilt.setTextColor(getResources().getColor(R.color.white));
                mechfilt.setTextColor(getResources().getColor(R.color.black));
                allretmec.setTextColor(getResources().getColor(R.color.white));

            }
        });
        allretmec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getproductlist(Constants.URL + "totorder=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));
                retfilt.setTextColor(getResources().getColor(R.color.white));
                mechfilt.setTextColor(getResources().getColor(R.color.white));
                allretmec.setTextColor(getResources().getColor(R.color.black));

            }
        });


        return rootView;
    }


    public void getproductlist(String url) {
        mProgressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");


                            retailerlist = new ArrayList<RetailerGetset>();
                            productitemlist = new ArrayList<String>();

                            retailerlist.clear();
                            productitemlist.clear();

                            productadpter = new Productitem_Adapter(getActivity(), retailerlist);
                            productlist.setAdapter(productadpter);
                            productadpter.notifyDataSetChanged();


                            if (status == 0) {
                                productadpter.notifyDataSetChanged();

                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                for (int j = 0; j < jsonArray.length(); j++) {


                                    RetailerGetset pack = new RetailerGetset();
                                    JSONObject objectnew = jsonArray.getJSONObject(j);

                                    pack.setCode((objectnew.getString("orderid")));
                                    pack.setGdesc((objectnew.getString("customer_name")));
                                    pack.setCategory((objectnew.getString("total_qty")));
                                    pack.setModel((objectnew.getString("date")));
                                    pack.setCompany((objectnew.getString("customer")));
                                    pack.setDesc((objectnew.getString("type")));
                                    pack.setItemqty((objectnew.getString("total_value")));

                                    JSONArray jsonArray1 = objectnew.getJSONArray("items");

                                    ArrayList<ItemListGetset> itemlist = new ArrayList<ItemListGetset>();

                                    for (int i = 0; i < jsonArray1.length(); i++) {


                                        ItemListGetset item = new ItemListGetset();
                                        JSONObject objitem = jsonArray1.getJSONObject(i);

                                        item.setItemcode((objitem.getString("pcode")));
                                        item.setItemrate((objitem.getString("rate")));
                                        item.setItemvalue((objitem.getString("value")));
                                        item.setItemqty((objitem.getString("quan")));

                                        itemlist.add(item);
                                    }

                                    pack.setItemlist(itemlist);
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
                            mProgressDialog.dismiss();

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

            convertView = inflater.inflate(R.layout.myorderadapter, parent, false);

            tv1 = (TextView) convertView.findViewById(R.id.tv1);
            tv2 = (TextView) convertView.findViewById(R.id.tv2);
            tv3 = (TextView) convertView.findViewById(R.id.tv3);
            tv4 = (TextView) convertView.findViewById(R.id.tv4);


            tv1.setText(" " + String.valueOf(myList.get(position).getModel()));
            tv2.setText(" " + myList.get(position).getGdesc());
            tv3.setText(" " + (myList.get(position).getItemqty()));
            tv4.setText(" " + String.valueOf(myList.get(position).getCategory()));


            return convertView;
        }

    }


}

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
public class Orderhistory extends Fragment {

    ListView productlist;
    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    ArrayList<RetailerGetset> retailerlist = new ArrayList<RetailerGetset>();
    ArrayList<String> productitemlist;
    Productitem_Adapter productadpter;
    String extracode = "";
    Bundle bundle = new Bundle();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.orderhistory, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        productlist = (ListView) rootView.findViewById(R.id.productlist);

        Button back = (Button) rootView.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dashboard fragment2 = new Dashboard();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragment2.setArguments(bundle);
                fragmentTransaction.commit();

            }
        });

        productlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                bundle.putString("Code", retailerlist.get(position).getCompany().toString());
                bundle.putString("TotalPrice", String.valueOf(retailerlist.get(position).getPrice()));
                bundle.putString("Type", retailerlist.get(position).getDesc().toString());
                bundle.putParcelableArrayList("itemlist", retailerlist.get(position).getItemlist());

                Myorderviewcat fragment2 = new Myorderviewcat();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragment2.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
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

        if (getArguments().getString("Code") != null) {
            extracode = getArguments().getString("Code");
        }

        getproductlist(Constants.URL+"orderhis=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0") + "&custcode=" + extracode);
//        getproductlist(Constants.URL+"orderhis=1&empcode=EU0005&custcode=GM0009");


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

                                    pack.setCode((objectnew.getString("orderid")));
                                    pack.setCategory((objectnew.getString("total_value")));
                                    pack.setModel((objectnew.getString("date")));
                                    pack.setCompany((objectnew.getString("customer")));
                                    pack.setDesc((objectnew.getString("type")));

                                    JSONArray jsonArray1 = objectnew.getJSONArray("items");

                                    ArrayList<ItemListGetset> itemlist = new ArrayList<ItemListGetset>();

                                    for (int i = 0; i < jsonArray1.length(); i++) {


                                        ItemListGetset item = new ItemListGetset();
                                        JSONObject objitem = jsonArray1.getJSONObject(j);

                                        item.setItemcode((objitem.getString("pcode")));
                                        item.setItemrate((objitem.getString("rate")));
                                        item.setItemvalue((objitem.getString("value")));
                                        item.setItemqty((objitem.getString("quan")));

                                        itemlist.add(item);
                                    }

                                    pack.setItemlist(itemlist);
                                    retailerlist.add(pack);

                                }

                                productadpter = new Productitem_Adapter(getActivity(), retailerlist);
                                productlist.setAdapter(productadpter);



                            }
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
        TextView tv1, tv2, tv3;


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

            convertView = inflater.inflate(R.layout.orderhistoryadapter, parent, false);

            tv1 = (TextView) convertView.findViewById(R.id.tv1);
            tv2 = (TextView) convertView.findViewById(R.id.tv2);
            tv3 = (TextView) convertView.findViewById(R.id.tv3);


            tv1.setText(" " + myList.get(position).getDesc());
            tv2.setText("Rs : " + String.valueOf(myList.get(position).getCategory()));
            tv3.setText(" " + String.valueOf(myList.get(position).getModel()));


            return convertView;
        }

    }


}

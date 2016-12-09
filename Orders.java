package maslsalesapp.minda.javaclasses;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import maslsalesapp.minda.R;
import maslsalesapp.minda.adapterandgetset.DatabaseGetset;
import maslsalesapp.minda.adapterandgetset.RetailerGetset;
import maslsalesapp.minda.miscellaneousclasses.DataBaseHandler;

/**
 * Created by xantatech on 31/5/16.
 */
public class Orders extends Fragment {

    AutoCompleteTextView productlist;
    Spinner productgrp, productcategory, productctype;
    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    ArrayList<RetailerGetset> retailerlist = new ArrayList<RetailerGetset>();
    ArrayList<String> productgrplist;
    ArrayList<String> productcategorylist;
    ArrayList<String> productctypelist;
    ArrayList<String> productitemlist;
    String type = "", extracode = "", tname = "";
    String cat = "", grp = "", vtime = "", ctype = "", geolocation = "";
    TextView productname, productcode, productprice, productoem, productmodel;// productselectedgrp
    EditText productqty;
    TextView textView_info;
    Button addtocart, viewcart;
    String addedpcode = "";
    LinearLayout detailbox;
    ImageView autodelete;
    LocationManager lm;

    TextView retmecid;
    List<DatabaseGetset> productarraydb = new ArrayList<DatabaseGetset>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.orders, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        vtime = formattedDate;

        if (getArguments().getString("Type") != null) {
            type = getArguments().getString("Type");
        }
        if (getArguments().getString("Code") != null) {
            extracode = getArguments().getString("Code");
        }
        if (getArguments().getString("name") != null) {
            tname = getArguments().getString("name");
        }


        productgrp = (Spinner) rootView.findViewById(R.id.productgrp);
        productcategory = (Spinner) rootView.findViewById(R.id.productcategory);
        productctype = (Spinner) rootView.findViewById(R.id.productctype);
        productlist = (AutoCompleteTextView) rootView.findViewById(R.id.productlist);

        productname = (TextView) rootView.findViewById(R.id.productname);
        productcode = (TextView) rootView.findViewById(R.id.productcode);
        productqty = (EditText) rootView.findViewById(R.id.qty);
        productprice = (TextView) rootView.findViewById(R.id.priceofitem);
//        productselectedgrp = (TextView) rootView.findViewById(R.id.productselectedgrp);
        productoem = (TextView) rootView.findViewById(R.id.productoem);
        productmodel = (TextView) rootView.findViewById(R.id.productmodel);
        retmecid = (TextView) rootView.findViewById(R.id.retmecid);
        textView_info = (TextView) rootView.findViewById(R.id.textView_info);

        lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            retmecid.setText("Location Not found");
        } else
        {
            getMyCurrentLocation();

        }

        ImageView imageView6 = (ImageView) rootView.findViewById(R.id.imageView6);
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    retmecid.setText("Location Not found");
                } else
                {
                    getMyCurrentLocation();

                }
            }
        });


//        retmecid.setText("" + geolocation);

        String typeHead = "<font color='#EE0000'>red</font>";
        typeHead = type;
        textView_info.setText(" " + typeHead + " -  " + extracode + " : " + tname);


        detailbox = (LinearLayout) rootView.findViewById(R.id.detailbox);
        autodelete = (ImageView) rootView.findViewById(R.id.autodelete);


        autodelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productlist.setText("");
                detailbox.setVisibility(View.GONE);
            }
        });

        detailbox.setVisibility(View.GONE);

        addtocart = (Button) rootView.findViewById(R.id.addtocart);
        viewcart = (Button) rootView.findViewById(R.id.viewcart);

        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String product2 = "";

                DataBaseHandler db = new DataBaseHandler(getActivity());

                List<DatabaseGetset> getsetImagedatas = db.getAllContacts();

                for (DatabaseGetset cn : getsetImagedatas) {

                    productarraydb = db.getAllempContacts("" + extracode);

                }
                for (int i = 0; i < productarraydb.size(); i++) {

                    String product = (productarraydb.get(i).getPid());

                    if (product.equals(addedpcode)) {

                        product2 = product;
                        Toast.makeText(getActivity(), "Already added", Toast.LENGTH_SHORT).show();

                    }

                }

                if (!product2.equals(addedpcode)) {

                    int qty = Integer.parseInt(productqty.getText().toString());
                    int price = Integer.parseInt(productprice.getText().toString());
                    int total = qty * price;
                    String totalprice = String.valueOf(total);

                    db.addContact(new DatabaseGetset("" + addedpcode, "" + productname.getText().toString(), "" + productqty.getText().toString(), "" + productprice.getText().toString(), "" + totalprice, extracode));

                    productarraydb = db.getAllempContacts("" + extracode);
                    int size = productarraydb.size();
                    Toast.makeText(getActivity(), "" + qty + " items added of amt " + totalprice, Toast.LENGTH_SHORT).show();

                    detailbox.setVisibility(View.GONE);

                    productlist.setText("");

                    productname.setText("");
                    productcode.setText("");
                    productqty.setText("");
                    productprice.setText("");
                    productoem.setText("");
                    productmodel.setText("");
                    addedpcode = "";

                }
            }
        });

        viewcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bundle bundle = new Bundle();
                bundle.putString("Code", extracode);
                bundle.putString("Type", type);
                bundle.putString("name", tname);
                bundle.putString("page", "order");
                bundle.putString("location", geolocation);

                ViewCart fragment2 = new ViewCart();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragment2.setArguments(bundle);
                fragmentTransaction.commit();
            }
        });


        Button back = (Button) rootView.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("retailer")) {
                    Retailer fragment2 = new Retailer();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragmentTransaction.commit();
                } else {
                    Mechanic fragment2 = new Mechanic();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragmentTransaction.commit();

                }
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

        addvisit(Constants.URL+"addvisit=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0") + "&ccode=" + extracode);
        getproductlist(Constants.URL+"productlist=1");
        getproductgrp(Constants.URL+"grouplist=1&type=" + type + "&code=" + extracode);

        productgrp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    grp = productgrplist.get(position);

                    getproductcategory(Constants.URL+"pcat=1&type=" + type + "&code=" + extracode + "&grp=" + grp);
                    getproductlist(Constants.URL+"cproduct=1&grp=" + grp);

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
                    cat = productcategorylist.get(position);

                    getproductctype(Constants.URL+"ctype=1&cat=" + cat + "&grp=" + grp);
                    getproductlist(Constants.URL+"cproduct=1&cat=" + cat + "&grp=" + grp);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        productctype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (productctypelist.size() != 0) {
                    if (position != 0) {
                        ctype = productctypelist.get(position);

                        getproductlist(Constants.URL+"cproduct=1&cat=" + cat + "&grp=" + grp + "&ctype=" + ctype);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        productlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String selection = (String) parent.getItemAtPosition(position);

                String[] parts = selection.split(" : ");
                String first = parts[0];
                selection = first;
                int pos = -1;

                for (int i = 0; i <= retailerlist.size(); i++) {
                    if (retailerlist.get(i).getCode().equals(selection)) {
                        pos = i;
                        break;
                    }
                }
                System.out.println("Position " + pos);

                detailbox.setVisibility(View.VISIBLE);
                View view1 = getActivity().getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }

                productname.setText(String.valueOf(retailerlist.get(pos).getDesc().toString()));
                productcode.setText(String.valueOf(retailerlist.get(pos).getCode().toString()));
                productqty.setText(String.valueOf(retailerlist.get(pos).getQty()));
                productprice.setText(String.valueOf(retailerlist.get(pos).getPrice()));
                productoem.setText(String.valueOf(retailerlist.get(pos).getCompany()));
                productmodel.setText(String.valueOf(retailerlist.get(pos).getModel()));
//                productselectedgrp.setText(String.valueOf(retailerlist.get(pos).getCategory()));
                addedpcode = (String.valueOf(retailerlist.get(pos).getCode()));
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

                            productgrplist = new ArrayList<String>();
                            if (status == 0) {
                                Toast.makeText(getActivity(), "0", Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                productgrplist.add("Select");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    productgrplist.add("" + jsonArray.get(i).toString());

                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, productgrplist);

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

                            productcategorylist = new ArrayList<String>();

                            if (status == 0) {
                                Toast.makeText(getActivity(), "0", Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                productcategorylist.add("Select");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    productcategorylist.add("" + jsonArray.get(i).toString());

                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, productcategorylist);


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

                            productctypelist = new ArrayList<String>();

                            if (status == 0) {
                                Toast.makeText(getActivity(), "0", Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                productctypelist.add("Select");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    productctypelist.add("" + jsonArray.get(i).toString());

                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, productctypelist);


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
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {

                                retailerlist = new ArrayList<RetailerGetset>();
                                productitemlist = new ArrayList<String>();
                                retailerlist.clear();
                                productitemlist.clear();


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
                                    productitemlist.add(objectnew.getString("code") + " : " + objectnew.getString("group") + " : " + objectnew.getString("category") + " : " + objectnew.getString("price"));
                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, productitemlist);
                                productlist.setThreshold(0);
                                productlist.setAdapter(list);


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

    public void addvisit(String url) {


        StringRequest postRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");


                            if (status == 0) {
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                map.put("vtime", vtime);
                map.put("geolocation", geolocation);

                return map;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

    void getMyCurrentLocation() {


        LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new MyLocationListener();


        try {
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        //don't start listeners if no provider is enabled
        //if(!gps_enabled && !network_enabled)
        //return false;

        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);

        }


        if (gps_enabled) {
            location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        }


        if (network_enabled && location == null) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);

        }


        if (network_enabled && location == null) {
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }

        if (location != null) {

            MyLat = location.getLatitude();
            MyLong = location.getLongitude();


        } else {
            Location loc = getLastKnownLocation(getActivity());
            if (loc != null) {

                MyLat = loc.getLatitude();
                MyLong = loc.getLongitude();


            }
        }
        locManager.removeUpdates(locListener); // removes the periodic updates from location listener to //avoid battery drainage. If you want to get location at the periodic intervals call this method using //pending intent.

        try {
// Getting address from found locations.
            Geocoder geocoder;

            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            addresses = geocoder.getFromLocation(MyLat, MyLong, 1);

            StateName = addresses.get(0).getAdminArea();
            CityName = addresses.get(0).getLocality();
            CountryName = addresses.get(0).getCountryName();
            Address = addresses.get(0).getAddressLine(0);
            // you can get more details other than this . like country code, state code, etc.

            System.out.println(" StateName " + StateName);
            System.out.println(" CityName " + CityName);
            System.out.println(" CountryName " + CountryName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        geolocation = Address + "-" + CityName + "-" + StateName;

        retmecid.setText("" + geolocation);

//        geolocation = CityName;
    }

    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            if (location != null) {
            }
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }

    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    Location location;

    Double MyLat, MyLong;
    String CityName = "";
    String StateName = "";
    String CountryName = "";
    String Address = "";

    public static Location getLastKnownLocation(Context context) {
        Location location = null;
        LocationManager locationmanager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        List list = locationmanager.getAllProviders();
        boolean i = false;
        Iterator iterator = list.iterator();
        do {
            //System.out.println("---------------------------------------------------------------------");
            if (!iterator.hasNext())
                break;
            String s = (String) iterator.next();
            //if(i != 0 && !locationmanager.isProviderEnabled(s))
            if (i != false && !locationmanager.isProviderEnabled(s))
                continue;
            // System.out.println("provider ===> "+s);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location location1 = locationmanager.getLastKnownLocation(s);
            if (location1 == null)
                continue;
            if (location != null) {
                //System.out.println("location ===> "+location);
                //System.out.println("location1 ===> "+location);
                float f = location.getAccuracy();
                float f1 = location1.getAccuracy();
                if (f >= f1) {
                    long l = location1.getTime();
                    long l1 = location.getTime();
                    if (l - l1 <= 600000L)
                        continue;
                }
            }
            location = location1;
            // System.out.println("location  out ===> "+location);
            //System.out.println("location1 out===> "+location);
            i = locationmanager.isProviderEnabled(s);
            // System.out.println("---------------------------------------------------------------------");
        } while (true);
        return location;
    }

}

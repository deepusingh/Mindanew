package maslsalesapp.minda.javaclasses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import maslsalesapp.minda.R;
import maslsalesapp.minda.adapterandgetset.RetailerGetset;

/**
 * Created by xantatech on 23/6/16.
 */
public class Retailer extends Fragment {

    AutoCompleteTextView retaillist;
    Spinner terlist;
    Button addnewret;
    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    ArrayList<RetailerGetset> retailerlist;
    ArrayList<String> teritory;
    ArrayList<String> namelist;
    LinearLayout userlayout, traderslayout;
    TextView retname, retaddr, retphone, retEmail, propname;
    Button retailerorderbtn, retailerhistory, retailerdiscussionbtn, reatalerordercartbtn;
    Bundle bundle = new Bundle();
    String tcode = "", tname = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.retailer, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        terlist = (Spinner) rootView.findViewById(R.id.sptername);
        retaillist = (AutoCompleteTextView) rootView.findViewById(R.id.spretname);


        userlayout = (LinearLayout) rootView.findViewById(R.id.traderslayout);
        traderslayout = (LinearLayout) rootView.findViewById(R.id.traderslayout);
        addnewret = (Button) rootView.findViewById(R.id.btaddnewretailer);
        addnewret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    // Build the alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Location Services Not Active");
                    builder.setCancelable(false);
                    builder.setMessage("Please enable Location Services and GPS");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Show location settings when the user acknowledges the alert dialog
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    Dialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                } else {

                    Addcustomer fragment2 = new Addcustomer();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
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

        ImageView autodelete = (ImageView) rootView.findViewById(R.id.autodelete);

        autodelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retaillist.setText("");
                retname.setVisibility(View.GONE);
                propname.setVisibility(View.GONE);
                retaddr.setVisibility(View.GONE);
                retphone.setVisibility(View.GONE);
                retailerorderbtn.setVisibility(View.GONE);
                retailerhistory.setVisibility(View.GONE);
                retailerdiscussionbtn.setVisibility(View.GONE);
                reatalerordercartbtn.setVisibility(View.GONE);
                traderslayout.setVisibility(View.GONE);
                addnewret.setVisibility(View.VISIBLE);
            }
        });


        retname = (TextView) rootView.findViewById(R.id.retailername);
        propname = (TextView) rootView.findViewById(R.id.propname);
        retaddr = (TextView) rootView.findViewById(R.id.retaileraddr);
        retphone = (TextView) rootView.findViewById(R.id.retailerphone);
        retEmail = (TextView) rootView.findViewById(R.id.retailerEmail);

        retailerorderbtn = (Button) rootView.findViewById(R.id.retailerorderbtn);
        retailerhistory = (Button) rootView.findViewById(R.id.retailerhistorybtn);
        retailerdiscussionbtn = (Button) rootView.findViewById(R.id.retailerdiscussionbtn);
        reatalerordercartbtn = (Button) rootView.findViewById(R.id.retailerordercatbtn);


        retname.setVisibility(View.GONE);
        propname.setVisibility(View.GONE);
        retaddr.setVisibility(View.GONE);
        retphone.setVisibility(View.GONE);
        retailerorderbtn.setVisibility(View.GONE);
        retailerhistory.setVisibility(View.GONE);
        retailerdiscussionbtn.setVisibility(View.GONE);
        reatalerordercartbtn.setVisibility(View.GONE);
        traderslayout.setVisibility(View.GONE);


        retailerorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                LocationManager lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
//                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                        !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                    // Build the alert dialog
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle("Location Services Not Active");
//                    builder.setCancelable(false);
//                    builder.setMessage("Please enable Location Services and GPS");
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            // Show location settings when the user acknowledges the alert dialog
//                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivity(intent);
//                        }
//                    });
//                    Dialog alertDialog = builder.create();
//                    alertDialog.setCanceledOnTouchOutside(false);
//                    alertDialog.show();
//                } else {

                bundle.putString("Type", "retailer");
                bundle.putString("Code", tcode);
                bundle.putString("name", tname);
                Orders fragment2 = new Orders();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragment2.setArguments(bundle);
                fragmentTransaction.commit();
//                }


            }
        });
        retailerhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bundle.putString("Code", tcode);
                bundle.putString("name", tname);
                Orderhistory fragment2 = new Orderhistory();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragment2.setArguments(bundle);
                fragmentTransaction.commit();
            }
        });
        retailerdiscussionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(true);
                dialog.setTitle("Select Type");
                dialog.setContentView(R.layout.discussiondialog);

                dialog.show();

                Button dialoge_retailer, dialoge_mechanic, other_remark;
                dialoge_retailer = (Button) dialog.findViewById(R.id.button_retailer);
                dialoge_mechanic = (Button) dialog.findViewById(R.id.button_mechanic);
                other_remark = (Button) dialog.findViewById(R.id.other_remark);


                dialoge_mechanic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bundle.putString("Code", tcode);
                        bundle.putString("name", tname);
                        bundle.putString("Type", "retailer");

                        StartDiscussion fragment2 = new StartDiscussion();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, fragment2);
                        fragmentTransaction.addToBackStack(null);
                        fragment2.setArguments(bundle);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                });


                dialoge_retailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bundle.putString("Code", tcode);
                        bundle.putString("name", tname);
                        bundle.putString("Type", "retailer");

                        PostQueries fragment2 = new PostQueries();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, fragment2);
                        fragmentTransaction.addToBackStack(null);
                        fragment2.setArguments(bundle);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                });

                other_remark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bundle.putString("Code", tcode);
                        bundle.putString("name", tname);
                        bundle.putString("Type", "retailer");

                        OtherRemark fragment2 = new OtherRemark();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, fragment2);
                        fragmentTransaction.addToBackStack(null);
                        fragment2.setArguments(bundle);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                });


            }
        });
        reatalerordercartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("Code", tcode);
                bundle.putString("Type", "retailer");
                bundle.putString("name", tname);
                bundle.putString("page", "retailer");

                ViewCart fragment2 = new ViewCart();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragment2.setArguments(bundle);
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

        if (Constants.isNetworkAvailable(getActivity())) {

            getteritory(Constants.URL + "territory=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));
        } else {


            Toast.makeText(getActivity(), "No Network", Toast.LENGTH_SHORT).show();
        }

        terlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    String code = teritory.get(position);

                    getretailer(Constants.URL + "customerlist=1&type=retailers&territory=" + code);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        retaillist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String selection = (String) parent.getItemAtPosition(position);
                String[] parts = selection.split(" : ");
                String first = parts[1];
                selection = first;
                int pos = -1;

                for (int i = 0; i <= retailerlist.size(); i++) {
                    if (retailerlist.get(i).getRetailercode().equals(selection)) {
                        pos = i;
                        break;
                    }
                }
                System.out.println("Position " + pos);

                View view1 = getActivity().getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }

                String code = retailerlist.get(pos).getRetailercode().toString();

                getdetailuser(Constants.URL + "customerd=1&type=retailers&code=" + code);


            }
        });


        return rootView;
    }

    public void getteritory(String url) {
        mProgressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println("response--------" + response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");

                            teritory = new ArrayList<String>();
                            if (status == 0) {
                                Toast.makeText(getActivity(), "0", Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("territory");

                                teritory.add("Territory");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    teritory.add("" + jsonArray.get(i).toString());

                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, teritory);

                                terlist.setAdapter(list);


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

    public void getretailer(String url) {
        mProgressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");


                            if (status == 0) {
                                Toast.makeText(getActivity(), "0", Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                retailerlist = new ArrayList<RetailerGetset>();
                                namelist = new ArrayList<String>();
                                retailerlist.clear();
                                namelist.clear();
                                for (int j = 0; j < jsonArray.length(); j++) {


                                    RetailerGetset pack = new RetailerGetset();
                                    JSONObject objectnew = jsonArray.getJSONObject(j);
                                    pack.setRetailercode((objectnew.getString("code")));
                                    pack.setRetailername((objectnew.getString("name")));


                                    retailerlist.add(pack);
                                    namelist.add(objectnew.getString("name").trim()+ " : " + objectnew.getString("code").trim());
                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, namelist);
                                retaillist.setThreshold(0);
                                retaillist.setAdapter(list);
                                retaillist.showDropDown();

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

    public void getdetailuser(String url) {
        mProgressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");

                            if (status == 0) {
                                addnewret.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), "No User available", Toast.LENGTH_SHORT).show();
                            } else {
                                userlayout.setVisibility(View.VISIBLE);
                                retname.setVisibility(View.VISIBLE);
                                propname.setVisibility(View.VISIBLE);
                                retaddr.setVisibility(View.VISIBLE);
                                retphone.setVisibility(View.VISIBLE);

                                addnewret.setVisibility(View.GONE);

                                tcode = jsonObject.getString("code");
                                tname = jsonObject.getString("name");

                                retname.setText(jsonObject.getString("name") + " (" + jsonObject.getString("code") + ")");
                                retaddr.setText(jsonObject.getString("address"));
                                retphone.setText(jsonObject.getString("phone"));
                                propname.setText("Prop : " + jsonObject.getString("propriter"));


                                retailerorderbtn.setVisibility(View.VISIBLE);
                                retailerhistory.setVisibility(View.VISIBLE);
                                retailerdiscussionbtn.setVisibility(View.VISIBLE);
                                reatalerordercartbtn.setVisibility(View.VISIBLE);
                                traderslayout.setVisibility(View.VISIBLE);


                            }

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
}

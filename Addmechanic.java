package maslsalesapp.minda.javaclasses;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import maslsalesapp.minda.R;
import maslsalesapp.minda.adapterandgetset.RetailerGetset;

/**
 * Created by xantatech on 31/5/16.
 */
public class Addmechanic extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    protected ArrayList<CharSequence> selecteddealers = new ArrayList<CharSequence>();
    protected ArrayList<CharSequence> selectedclass = new ArrayList<CharSequence>();
    EditText editext_Retname, editext_propName, editext_retPin,
            editText_add1, editText_add2,
            editext_stateName,
            editext_zoneName, editext_countName,
            editext_retailerMobileNo1,
            editext_DlrName,
            editext_MNTH_TOT_TRNO, editext_MNTH_MN_TRNO,
            editext_remarks, editext_prdcClass, editext_territoryName;
    ArrayList<String> citylist;
    Spinner editext_abc_Cat, editext_APNTD_FLAG, edittext_autoelectrical;
    Button getdealer, getclass;
    Button register;
    ArrayList<String> teritory;
    AutoCompleteTextView editext_upropcity;
    ProgressDialog mProgressDialog, mProgressDialogc;
    TextView geolocation;
    RequestQueue requestQueue;
    ArrayList<String> categorylist = new ArrayList<String>();
    ArrayList<String> flaglist = new ArrayList<String>();
    ArrayList<String> autoelectricallist = new ArrayList<String>();
    ArrayList<RetailerGetset> retailerlist;
    ArrayList<String> namelist;
    String teritorry = "", category = "", appointment = "", city = "", autoelectrical = "";
    ArrayList<String> secondarraylist;
    String systemcode = "";
    String vtime;
    ImageView imageView_signatureImg, imageView_shopImg;
    Dialog dialog;
    String encodedsignature = "", encodedimage = "";
    CharSequence[] dealers;
    CharSequence[] classes;
    ImageView autodelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.addmechanic, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        vtime = formattedDate;

        dialog = new Dialog(getActivity());

        categorylist.add("Category");
        categorylist.add("A");
        categorylist.add("B");
        categorylist.add("C");

        flaglist.add("Appoint");
        flaglist.add("Y");
        flaglist.add("N");

        autoelectricallist.add("Auto Electrician");
        autoelectricallist.add("Y");
        autoelectricallist.add("N");

        mProgressDialog = new ProgressDialog(getActivity());
        // Set progressdialog title
        mProgressDialog.setTitle(" Please Wait... ");
        // Set progressdialog message
        mProgressDialog.setMessage(" Loading ...");

        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.setCanceledOnTouchOutside(false);

        mProgressDialogc = new ProgressDialog(getActivity());
        // Set progressdialog title
        mProgressDialogc.setTitle(" Please Wait... ");
        // Set progressdialog message
        mProgressDialogc.setMessage(" Generating code ...");

        mProgressDialogc.setIndeterminate(false);
        // Show progressdialog
        mProgressDialogc.setCanceledOnTouchOutside(false);


        requestQueue = Volley.newRequestQueue(getActivity());

        editext_Retname = (EditText) rootView.findViewById(R.id.editext_Retname);
        editext_propName = (EditText) rootView.findViewById(R.id.editext_propName);
        editext_upropcity = (AutoCompleteTextView) rootView.findViewById(R.id.editext_upropcity);
        editext_retPin = (EditText) rootView.findViewById(R.id.editext_retPin);
        editText_add1 = (EditText) rootView.findViewById(R.id.editext_add1);
        editText_add2 = (EditText) rootView.findViewById(R.id.editext_add2);
        editext_stateName = (EditText) rootView.findViewById(R.id.editext_stateName);
        editext_territoryName = (EditText) rootView.findViewById(R.id.editext_territoryName);
        getdealer = (Button) rootView.findViewById(R.id.getdealer);
        getclass = (Button) rootView.findViewById(R.id.getclass);
        editext_zoneName = (EditText) rootView.findViewById(R.id.editext_zoneName1);
        editext_countName = (EditText) rootView.findViewById(R.id.editext_countryName);
        editext_remarks = (EditText) rootView.findViewById(R.id.editext_remarks);
        editext_retailerMobileNo1 = (EditText) rootView.findViewById(R.id.editext_retailerMobileNo1);
        editext_prdcClass = (EditText) rootView.findViewById(R.id.editext_prdcClass);
        editext_DlrName = (EditText) rootView.findViewById(R.id.editext_DlrName);
        editext_abc_Cat = (Spinner) rootView.findViewById(R.id.editext_abc_Cat);
        edittext_autoelectrical = (Spinner) rootView.findViewById(R.id.edittext_autoelectrical);
        editext_MNTH_TOT_TRNO = (EditText) rootView.findViewById(R.id.editext_MNTH_TOT_TRNO);
        editext_MNTH_MN_TRNO = (EditText) rootView.findViewById(R.id.editext_MNTH_MN_TRNO);
        editext_APNTD_FLAG = (Spinner) rootView.findViewById(R.id.editext_APNTD_FLAG);
        register = (Button) rootView.findViewById(R.id.register);
        geolocation = (TextView) rootView.findViewById(R.id.geolocation);
        imageView_signatureImg = (ImageView) rootView.findViewById(R.id.imageView_signatureImg);
        imageView_shopImg = (ImageView) rootView.findViewById(R.id.imageView_shopImg);
        autodelete = (ImageView) rootView.findViewById(R.id.autodelete);
        ImageView imageView6 = (ImageView) rootView.findViewById(R.id.imageView6);
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyCurrentLocation();
            }
        });


        getMyCurrentLocation();

        getempclass(Constants.URL+"pcat=1");

        getretailer(Constants.URL+"retailerlist=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));

        getteritory(Constants.URL+"allcity=1&zone=" + Constants.zone);

        editext_upropcity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = (String) parent.getItemAtPosition(position);

                getpincodeaddress(Constants.URL);
            }
        });

        editext_zoneName.setText("" + Constants.zone);

        getdealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectColoursDialog();
            }
        });
        getclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectClassDialog();
            }
        });

        autodelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editext_upropcity.setText("");
                editext_territoryName.setText("");
                editext_stateName.setText("");
                editext_retPin.setText("");
                editext_countName.setText("");
            }
        });

        imageView_signatureImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setCancelable(true);
                dialog.setTitle("Customer Signature");
                dialog.setContentView(new SignatureMainLayout(getActivity()));

                dialog.show();


            }
        });

        imageView_shopImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencamera();

            }
        });


        ArrayAdapter<String> list2 = new ArrayAdapter<String>(getActivity(),
                R.layout.add_customer_item_layout, R.id.textView3, categorylist);
        editext_abc_Cat.setAdapter(list2);


        ArrayAdapter<String> list3 = new ArrayAdapter<String>(getActivity(),
                R.layout.add_customer_item_layout, R.id.textView3, flaglist);
        editext_APNTD_FLAG.setAdapter(list3);

        ArrayAdapter<String> list4 = new ArrayAdapter<String>(getActivity(),
                R.layout.add_customer_item_layout, R.id.textView3, autoelectricallist);
        edittext_autoelectrical.setAdapter(list4);


        editext_abc_Cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    category = categorylist.get(position);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editext_APNTD_FLAG.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    appointment = flaglist.get(position);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edittext_autoelectrical.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    autoelectrical = flaglist.get(position);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        register.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (Constants.isNetworkAvailable(getActivity())) {

                                                if (editext_Retname.getText().toString().equals("") ||
                                                        editext_propName.getText().toString().equals("") ||
                                                        editext_upropcity.getText().toString().equals("") ||
                                                        editext_retPin.getText().toString().equals("") ||
                                                        editText_add1.getText().toString().equals("") ||
                                                        editText_add2.getText().toString().equals("") ||
                                                        editext_stateName.getText().toString().equals("") ||
                                                        editext_zoneName.getText().toString().equals("") ||
                                                        editext_countName.getText().toString().equals("") ||
                                                        editext_retailerMobileNo1.getText().toString().equals("") ||
                                                        editext_DlrName.getText().toString().equals("")) {
                                                    if (editext_Retname.getText().toString().equals("") &&
                                                            editext_propName.getText().toString().equals("") &&
                                                            editext_upropcity.getText().toString().equals("") &&
                                                            editext_retPin.getText().toString().equals("") &&
                                                            editText_add1.getText().toString().equals("") &&
                                                            editText_add2.getText().toString().equals("") &&
                                                            editext_stateName.getText().toString().equals("") &&
                                                            editext_zoneName.getText().toString().equals("") &&
                                                            editext_countName.getText().toString().equals("") &&
                                                            editext_retailerMobileNo1.getText().toString().equals("") &&
                                                            editext_DlrName.getText().toString().equals("")) {
                                                        Toast.makeText(getActivity(), "All fields are empty.", Toast.LENGTH_SHORT).show();
                                                    }

                                                    if (editext_Retname.getText().toString().equals("")) {
                                                        editext_Retname.setError("Enter Mechanic name");
                                                        Toast.makeText(getActivity(), "Mechanic name required", Toast.LENGTH_SHORT).show();
                                                    } else if (editext_propName.getText().toString().equals("")) {
                                                        editext_propName.setError("Enter proprietor name");
                                                        editext_Retname.setError(null);
                                                        Toast.makeText(getActivity(), "Proprietor name required", Toast.LENGTH_SHORT).show();
                                                    } else if (editText_add1.getText().toString().equals("")) {

                                                        editext_Retname.setError(null);
                                                        editext_propName.setError(null);

                                                        editText_add1.setError("Enter address");
                                                        Toast.makeText(getActivity(), "Address required", Toast.LENGTH_SHORT).show();
                                                    } else if (editext_upropcity.getText().toString().equals("")) {

                                                        editext_Retname.setError(null);
                                                        editext_propName.setError(null);
                                                        editText_add1.setError(null);

                                                        editext_upropcity.setError("Enter city");
                                                        Toast.makeText(getActivity(), "City required", Toast.LENGTH_SHORT).show();
                                                    } else if (editext_zoneName.getText().toString().equals("")) {

                                                        editext_Retname.setError(null);
                                                        editext_propName.setError(null);
                                                        editText_add1.setError(null);
                                                        editext_upropcity.setError(null);

                                                        editext_zoneName.setError("Enter zone name");
                                                        Toast.makeText(getActivity(), "Zone name required", Toast.LENGTH_SHORT).show();
                                                    } else if (editext_stateName.getText().toString().equals("")) {

                                                        editext_Retname.setError(null);
                                                        editext_propName.setError(null);
                                                        editText_add1.setError(null);
                                                        editext_upropcity.setError(null);
                                                        editext_zoneName.setError(null);

                                                        editext_stateName.setError("Enter state name");
                                                        Toast.makeText(getActivity(), "State name required", Toast.LENGTH_SHORT).show();
                                                    } else if (editext_countName.getText().toString().equals("")) {

                                                        editext_Retname.setError(null);
                                                        editext_propName.setError(null);
                                                        editText_add1.setError(null);
                                                        editext_upropcity.setError(null);
                                                        editext_zoneName.setError(null);
                                                        editext_stateName.setError(null);

                                                        editext_countName.setError("Enter country");
                                                        Toast.makeText(getActivity(), "Country name required", Toast.LENGTH_SHORT).show();
                                                    } else if (editext_retPin.getText().toString().equals("")) {

                                                        editext_Retname.setError(null);
                                                        editext_propName.setError(null);
                                                        editText_add1.setError(null);
                                                        editext_upropcity.setError(null);
                                                        editext_zoneName.setError(null);
                                                        editext_stateName.setError(null);
                                                        editext_countName.setError(null);

                                                        editext_retPin.setError("Enter pincode");
                                                        Toast.makeText(getActivity(), "Pincode required", Toast.LENGTH_SHORT).show();
                                                    }

//                                                    if (editText_add2.getText().toString().equals("")) {
//                                                        editText_add2.setError("Field cannot be left blank");
//                                                    }


                                                    else if (editext_retailerMobileNo1.getText().toString().equals("")) {

                                                        editext_Retname.setError(null);
                                                        editext_propName.setError(null);
                                                        editText_add1.setError(null);
                                                        editext_upropcity.setError(null);
                                                        editext_zoneName.setError(null);
                                                        editext_stateName.setError(null);
                                                        editext_countName.setError(null);
                                                        editext_retPin.setError(null);

                                                        editext_retailerMobileNo1.setError("Enter mobile");
                                                        Toast.makeText(getActivity(), "Mobile number required", Toast.LENGTH_SHORT).show();
                                                    } else if (editext_DlrName.getText().toString().equals("")) {

                                                        editext_Retname.setError(null);
                                                        editext_propName.setError(null);
                                                        editText_add1.setError(null);
                                                        editext_upropcity.setError(null);
                                                        editext_zoneName.setError(null);
                                                        editext_stateName.setError(null);
                                                        editext_countName.setError(null);
                                                        editext_retPin.setError(null);
                                                        editext_retailerMobileNo1.setError(null);

                                                        editext_DlrName.setError("Enter dealer name");
                                                        Toast.makeText(getActivity(), "Dealer name required", Toast.LENGTH_SHORT).show();
                                                    }
//                                                    editext_Retname.setError(null);
//                                                    editext_propName.setError(null);
//                                                    editText_add1.setError(null);
//                                                    editext_upropcity.setError(null);
//                                                    editext_zoneName.setError(null);
//                                                    editext_stateName.setError(null);
//                                                    editext_countName.setError(null);
//                                                    editext_retPin.setError(null);
//                                                    editext_retailerMobileNo1.setError(null);
//                                                    editext_DlrName.setError(null);

                                                }

                                                String test_Type = editext_Retname.getText().toString().replaceAll(" ", "%20");
                                                String test_Type1 = editext_propName.getText().toString().replaceAll(" ", "%20");

                                                getsystemcode(Constants.URL+"genid=1&type=mac&name=" + test_Type + "&propname=" + test_Type1);

                                            } else {
                                                Toast.makeText(getActivity(), "No Network", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    }

        );

        Button back = (Button) rootView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View v) {
                                        Dashboard fragment2 = new Dashboard();
                                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                        fragmentTransaction.replace(R.id.frame_container, fragment2);
                                        fragmentTransaction.commit();
                                    }


                                }

        );

        return rootView;
    }


    public void getpincodeaddress(String url) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            int status = jsonObject.getInt("success");


                            if (status == 1) {

                                JSONObject jobj = jsonObject.getJSONObject("result");

                                editext_territoryName.setText("" + jobj.getString("CM_TRTY_SNME"));
                                editext_stateName.setText("" + jobj.getString("CM_STAT_NAME"));
//                                editext_zoneName.setText("" + jobj.getString("CM_ZONE_SNME"));
                                editext_retPin.setText("" + jobj.getString("CM_PIN_CODE"));
                                editext_countName.setText("" + jobj.getString("COM_CONT_NAME"));

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
                map.put("cityd","1");
                map.put("city",city);

                return map;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

    public void getresult(String url) {
        mProgressDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");

                            if (status == 0) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), "" + jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            } else {
                                mProgressDialog.dismiss();


                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        getActivity());

                                // set title
                                alertDialogBuilder.setTitle("Mechanic Added");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("" + jsonObject.getString("msg"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                dialog.cancel();
                                                Dashboard fragment2 = new Dashboard();
                                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                                fragmentTransaction.replace(R.id.frame_container, fragment2);
                                                fragmentTransaction.commit();


                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();


//                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                final Dialog alertDialog = builder.create();
//                                alertDialog.setCanceledOnTouchOutside(false);
//                                builder.setTitle(jsonObject.getString("msg"));
//                                builder.setCancelable(false);
//                                builder.setMessage(jsonObject.getString("msg"));
//                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                        Dashboard fragment2 = new Dashboard();
//                                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                                        fragmentTransaction.replace(R.id.frame_container, fragment2);
//                                        fragmentTransaction.commit();
//
//                                        // Show location settings when the user acknowledges the alert dialog
//                                        alertDialog.dismiss();
//                                    }
//                                });
//
//                                alertDialog.show();
//                                Toast.makeText(getActivity(), "" + jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();

                            }
                            register.setClickable(true);
                            register.setBackgroundResource(R.drawable.round_corner_blue);

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
                map.put("addmech", "1");
                map.put("D3TR_MAC_CODE", systemcode);
                map.put("D3TR_MAC_NAME", editext_Retname.getText().toString());
                map.put("D3TR_PROP_NAME", editext_propName.getText().toString());
                map.put("D3TR_MAC_ADD1", editText_add1.getText().toString());
                map.put("D3TR_MAC_ADD2", editText_add2.getText().toString());
                map.put("D3TR_CITY_NAME", city);
                map.put("D3TR_MAC_PIN", editext_retPin.getText().toString());
                map.put("D3TR_STAT_NAME", editext_stateName.getText().toString());
                map.put("D3TR_TRTY_SNME", editext_territoryName.getText().toString());
                map.put("D3TR_ZONE_NAME", editext_zoneName.getText().toString());
                map.put("D3TR_CONT_NAME", editext_countName.getText().toString());
                map.put("D3TR_MAC_MOB1", editext_retailerMobileNo1.getText().toString());
                map.put("D3TR_ABC_CAT", category);
                map.put("D3TR_APNTD_FLAG", appointment);
                map.put("D3TR_GEO_LOC", "" + geolocation.getText().toString());
                map.put("D3TR_MEC_SIGN", encodedsignature);
                map.put("D3TR_MEC_IMG", encodedimage);
                map.put("D3TR_ADDED_DT", vtime);

                map.put("D3TR_MEC_AutoElectrical", autoelectrical);
                map.put("D3TR_MEC_MaxPrice", editext_MNTH_TOT_TRNO.getText().toString());
                map.put("D3TR_MEC_MinPrice", editext_MNTH_MN_TRNO.getText().toString());
                map.put("D3TR_MEC_Remark", editext_remarks.getText().toString());
                map.put("empcode", Constants.sharedpreferences.getString("EMP_CODE", "0"));


                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < selecteddealers.size(); i++) {
                    try {

                        JSONObject jsonObject = new JSONObject();

                        String s = (String) selecteddealers.get(i);
                        String[] parts = s.split(" : ");
                        String first = parts[0];

                        jsonObject.put("pcode", first);

                        jsonArray.put(jsonObject);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

                map.put("D3TR_DLR_CODE", jsonArray.toString());

                JSONArray jsonArray1 = new JSONArray();

                for (int i = 0; i < selecteddealers.size(); i++) {
                    try {

                        JSONObject jsonObject1 = new JSONObject();


                        String s = (String) selecteddealers.get(i);
                        String[] parts = s.split(" : ");
                        String first = parts[1];

                        jsonObject1.put("pcode", first);

                        jsonArray1.put(jsonObject1);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

                map.put("D3TR_DLR_NAME", jsonArray1.toString());


                JSONArray jsonArray2 = new JSONArray();

                for (int i = 0; i < selectedclass.size(); i++) {
                    try {

                        JSONObject jsonObject2 = new JSONObject();


                        jsonObject2.put("pcode", selectedclass.get(i));

                        jsonArray2.put(jsonObject2);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

                map.put("D3TR_PRDC_CLAS", jsonArray2.toString());


                return map;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

    public void getempclass(String url) {
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

                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                secondarraylist = new ArrayList<String>();
                                secondarraylist.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    secondarraylist.add("" + jsonArray.get(i).toString());

                                }
                                classes = secondarraylist.toArray(new CharSequence[secondarraylist.size()]);

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

    public void getsystemcode(String url) {

//        mProgressDialogc.show();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");


                            if (status == 0) {

                            } else {

                                systemcode = jsonObject.getString("id");
                                register.setClickable(false);
                                register.setBackgroundResource(R.drawable.round_corner_clicked);

//                                mProgressDialogc.dismiss();
                                getresult(Constants.URL);

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

    private void opencamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void getteritory(String url) {
        mProgressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");

                            teritory = new ArrayList<String>();
                            if (status == 0) {
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    teritory.add("" + jsonArray.get(i).toString());

                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, teritory);
                                editext_upropcity.setThreshold(0);
                                editext_upropcity.setAdapter(list);


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
                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                retailerlist = new ArrayList<RetailerGetset>();
                                namelist = new ArrayList<String>();
                                retailerlist.clear();
                                namelist.clear();
                                for (int j = 0; j < jsonArray.length(); j++) {


                                    RetailerGetset pack = new RetailerGetset();
                                    JSONObject objectnew = jsonArray.getJSONObject(j);
                                    pack.setRetailercode((objectnew.getString("bprt")));
                                    pack.setRetailername((objectnew.getString("bname")));


                                    retailerlist.add(pack);
                                    namelist.add(objectnew.getString("bname").trim() + " : " + objectnew.getString("bprt").trim());
                                }

                            }

                            dealers = namelist.toArray(new CharSequence[namelist.size()]);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && null != data) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView_shopImg.setImageBitmap(photo);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data2 = stream.toByteArray();
            encodedimage = Base64.encodeToString(data2, Base64.DEFAULT);

        } else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onChangeSelectedColours() {
        StringBuilder stringBuilder = new StringBuilder();

        for (CharSequence colour : selecteddealers)
            stringBuilder.append(colour + ",");

        if (selecteddealers.size() != 0) {

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        editext_DlrName.setText(stringBuilder.toString());
    }

    protected void showSelectColoursDialog() {
        boolean[] checkedColours = new boolean[dealers.length];
        int count = dealers.length;

        for (int i = 0; i < count; i++)
            checkedColours[i] = selecteddealers.contains(dealers[i]);

        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked)
                    selecteddealers.add(dealers[which]);
                else
                    selecteddealers.remove(dealers[which]);

                onChangeSelectedColours();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Dealers");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMultiChoiceItems(dealers, checkedColours, coloursDialogListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void onChangeSelectedClass() {
        StringBuilder stringBuilder = new StringBuilder();

        for (CharSequence colour : selectedclass)
            stringBuilder.append(colour + ",");

        if (selectedclass.size() != 0) {

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        editext_prdcClass.setText(stringBuilder.toString());
    }

    protected void showSelectClassDialog() {
        boolean[] checkedColours = new boolean[classes.length];
        int count = classes.length;

        for (int i = 0; i < count; i++)
            checkedColours[i] = selectedclass.contains(classes[i]);

        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked)
                    selectedclass.add(classes[which]);
                else
                    selectedclass.remove(classes[which]);

                onChangeSelectedClass();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Classes");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMultiChoiceItems(classes, checkedColours, coloursDialogListener);

        AlertDialog dialog = builder.create();
        dialog.show();
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

        geolocation.setText("" + Address + "-" + CityName + "-" + StateName);
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


    public class SignatureMainLayout extends LinearLayout implements View.OnClickListener {

        LinearLayout buttonsLayout;
        SignatureView signatureView;

        public SignatureMainLayout(Context context) {
            super(context);

            this.setOrientation(LinearLayout.VERTICAL);

            this.buttonsLayout = this.buttonsLayout();
            this.signatureView = new SignatureView(context);

            // add the buttons and signature views
            this.addView(this.buttonsLayout);
            this.addView(signatureView);

        }

        private LinearLayout buttonsLayout() {

            // create the UI programatically
            LinearLayout linearLayout = new LinearLayout(this.getContext());
            Button saveBtn = new Button(this.getContext());
            Button clearBtn = new Button(this.getContext());

            // set orientation
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setBackgroundColor(Color.GRAY);

            // set texts, tags and OnClickListener
            saveBtn.setText("OK");
            saveBtn.setTag("Save");
            saveBtn.setOnClickListener(this);

            clearBtn.setText("Clear");
            clearBtn.setTag("Clear");
            clearBtn.setOnClickListener(this);

            linearLayout.addView(saveBtn);
            linearLayout.addView(clearBtn);

            // return the whoe layout
            return linearLayout;
        }

        // the on click listener of 'save' and 'clear' buttons
        @Override
        public void onClick(View v) {
            String tag = v.getTag().toString().trim();

            // save the signature
            if (tag.equalsIgnoreCase("save")) {
                this.saveImage(this.signatureView.getSignature());
            }

            // empty the canvas
            else {
                this.signatureView.clearSignature();
            }

        }

        final void saveImage(Bitmap signature) {

            String root = Environment.getExternalStorageDirectory().toString();

            // the directory where the signature will be saved
            File myDir = new File(root + "/saved_signature");

            // make the directory if it does not exist yet
            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            // set the file name of your choice
            String fname = "signature.png";

            // in our case, we delete the previous file, you can remove this
            File file = new File(myDir, fname);
            if (file.exists()) {
                file.delete();
            }

            try {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                signature.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] data2 = stream.toByteArray();
                encodedsignature = Base64.encodeToString(data2, Base64.DEFAULT);
                stream.flush();
                stream.close();
                dialog.dismiss();
                imageView_signatureImg.setImageBitmap(signature);

                Toast.makeText(this.getContext(), "Signature saved.", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private class SignatureView extends View {

            // set the stroke width
            private static final float STROKE_WIDTH = 5f;
            private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
            private final RectF dirtyRect = new RectF();
            private Paint paint = new Paint();
            private Path path = new Path();
            private float lastTouchX;
            private float lastTouchY;

            public SignatureView(Context context) {

                super(context);

                paint.setAntiAlias(true);
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeWidth(STROKE_WIDTH);

                // set the bg color as white
                this.setBackgroundColor(Color.WHITE);

                // width and height should cover the screen
                this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 350));

            }

            /**
             * Get signature
             *
             * @return
             */
            protected Bitmap getSignature() {

                Bitmap signatureBitmap = null;

                // set the signature bitmap
                if (signatureBitmap == null) {
                    signatureBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.RGB_565);
                }

                // important for saving signature
                final Canvas canvas = new Canvas(signatureBitmap);
                this.draw(canvas);

                return signatureBitmap;
            }

            /**
             * clear signature canvas
             */
            private void clearSignature() {
                path.reset();
                this.invalidate();
            }

            // all touch events during the drawing
            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawPath(this.path, this.paint);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                float eventX = event.getX();
                float eventY = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        path.moveTo(eventX, eventY);

                        lastTouchX = eventX;
                        lastTouchY = eventY;
                        return true;

                    case MotionEvent.ACTION_MOVE:

                    case MotionEvent.ACTION_UP:

                        resetDirtyRect(eventX, eventY);
                        int historySize = event.getHistorySize();
                        for (int i = 0; i < historySize; i++) {
                            float historicalX = event.getHistoricalX(i);
                            float historicalY = event.getHistoricalY(i);

                            expandDirtyRect(historicalX, historicalY);
                            path.lineTo(historicalX, historicalY);
                        }
                        path.lineTo(eventX, eventY);
                        break;

                    default:

                        return false;
                }

                invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                        (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                        (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                        (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

                lastTouchX = eventX;
                lastTouchY = eventY;

                return true;
            }

            private void expandDirtyRect(float historicalX, float historicalY) {
                if (historicalX < dirtyRect.left) {
                    dirtyRect.left = historicalX;
                } else if (historicalX > dirtyRect.right) {
                    dirtyRect.right = historicalX;
                }

                if (historicalY < dirtyRect.top) {
                    dirtyRect.top = historicalY;
                } else if (historicalY > dirtyRect.bottom) {
                    dirtyRect.bottom = historicalY;
                }
            }

            private void resetDirtyRect(float eventX, float eventY) {
                dirtyRect.left = Math.min(lastTouchX, eventX);
                dirtyRect.right = Math.max(lastTouchX, eventX);
                dirtyRect.top = Math.min(lastTouchY, eventY);
                dirtyRect.bottom = Math.max(lastTouchY, eventY);
            }

        }

    }

}

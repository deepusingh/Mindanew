package maslsalesapp.minda.javaclasses;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
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
import java.util.ArrayList;
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
 * Created by xantatech on 5/7/16.
 */
public class ConfirmOrder extends Fragment {

    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    private static final int CAMERA_REQUEST = 1888;
    String vtime = "", encodedsignature = "", encodedimage = "";
    int amount = 0, qtycount = 0;
    ArrayList<String> namelist;
    Button placeorder, cancle;
    TextView totalprice, totalqty;
    EditText remark, marketname;
    ImageView signatureimg, imageView6, imageView_shopImg;
    String usercode = "", usertype = "", username = "", page = "";
    EditText geolocation;
    List<DatabaseGetset> productarraydb = new ArrayList<DatabaseGetset>();
    Bundle bundle = new Bundle();
    Dialog dialog;
    Spinner selectbp;
    String dealercode = "";
    String delaername = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.confirmorder, container, false);

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
        if (getArguments().getString("page") != null) {
            page = getArguments().getString("page");
        }

        DataBaseHandler db = new DataBaseHandler(getActivity());

        if (db.getAllempContacts("" + usercode).size() == 0) {

            Toast.makeText(getActivity(), "Basket is empty", Toast.LENGTH_SHORT).show();
        }
        List<DatabaseGetset> getsetImagedatas = db.getAllContacts();

        for (DatabaseGetset cn : getsetImagedatas) {

            productarraydb = db.getAllempContacts("" + usercode);

        }

        if (usertype.equals("retailer")) {
            getretailer(Constants.URL + "dealerlist=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));

        } else {
            getretailer(Constants.URL + "retailerlist=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));

        }

        placeorder = (Button) rootView.findViewById(R.id.update);
        cancle = (Button) rootView.findViewById(R.id.cancle);
        totalprice = (TextView) rootView.findViewById(R.id.totalprice);
        totalqty = (TextView) rootView.findViewById(R.id.totalqty);
        remark = (EditText) rootView.findViewById(R.id.remark);
        marketname = (EditText) rootView.findViewById(R.id.marketname);
        signatureimg = (ImageView) rootView.findViewById(R.id.imageView_signatureImg);
        imageView_shopImg = (ImageView) rootView.findViewById(R.id.imageView_shopImg);
        geolocation = (EditText) rootView.findViewById(R.id.geolocation);
        selectbp = (Spinner) rootView.findViewById(R.id.selectbp);
        imageView6 = (ImageView) rootView.findViewById(R.id.imageView6);

        imageView_shopImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencamera();

            }
        });

        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyCurrentLocation();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "This order is saved now as draft and can be placed further..", Toast.LENGTH_LONG).show();
                bundle.putString("Code", usercode);
                bundle.putString("Type", usertype);
                bundle.putString("name", username);
                bundle.putString("page", page);
                ViewCart fragment2 = new ViewCart();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragment2.setArguments(bundle);
                fragmentTransaction.commit();
            }
        });


        getMyCurrentLocation();


        selectbp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    String[] parts = namelist.get(position).split(" : ");
                    dealercode = "" + parts[0];
                    delaername = "" + parts[1];
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i = 0; i < productarraydb.size(); i++) {
            amount += Integer.parseInt(productarraydb.get(i).getTotalprice());
            qtycount += Integer.parseInt(productarraydb.get(i).getPqty());
        }

        totalprice.setText("  Total amount : " + amount);
        totalqty.setText("  Total Qty : " + qtycount);

        signatureimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setCancelable(true);
                dialog.setTitle("Add Signature");
                dialog.setContentView(new SignatureMainLayout(getActivity()));
                dialog.show();


            }
        });

        Button back = (Button) rootView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bundle.putString("Code", usercode);
                bundle.putString("Type", usertype);
                bundle.putString("name", username);
                bundle.putString("page", page);
                ViewCart fragment2 = new ViewCart();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragment2.setArguments(bundle);
                fragmentTransaction.commit();


            }
        });

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (marketname.getText().toString().equals("")) {

                    marketname.setError("Enter Market Name");

                } else if (encodedsignature.equals("")) {

                    Toast.makeText(getActivity(), "Please sign first", Toast.LENGTH_SHORT).show();

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

    private void opencamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
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

                                DataBaseHandler db = new DataBaseHandler(getActivity());

                                for (int i = 0; i < db.getAllempContacts("" + usercode).size(); i++) {
                                    DatabaseGetset picture = productarraydb.get(i);
                                    db.deleteContactall(new DatabaseGetset((picture.getEmpcode())));
                                }
                                Dashboard fragment2 = new Dashboard();
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frame_container, fragment2);
                                fragmentTransaction.commit();
                                totalprice.setText("0");
                                productarraydb.clear();
                                Toast.makeText(getActivity(), "Order placed", Toast.LENGTH_SHORT).show();

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
                map.put("addorder", "1");
                map.put("MDSRM_EMP_CODE", "" + Constants.sharedpreferences.getString("EMP_CODE", "0"));
                map.put("MDSRM_CUST_TYPE", "" + usertype);
                map.put("DSRD_CUST_CODE", "" + usercode);
                map.put("DSRD_CUST_NAME", "" + username);
                map.put("MDSRM_ORD_QTY", "" + qtycount);
                map.put("MDSRM_ORD_VAL", "" + amount);
                map.put("MDSRM_GEO_LOCATION", "" + geolocation.getText().toString());
                map.put("MDSRM_CUST_SIGN", "" + encodedsignature);
                map.put("MDSRM_MKT_NAME", "" + marketname.getText().toString());
                map.put("MDSRM_RMKS", "" + remark.getText().toString());
                map.put("MDSRM_ORD_FWD_CODE", dealercode);
                map.put("MDSRM_ORD_FWD_NAME", delaername);
                map.put("MDSRM_CUST_PIC", encodedimage);

                DataBaseHandler db = new DataBaseHandler(getActivity());
                List<DatabaseGetset> getsetImagedatas = db.getAllContacts();

                for (DatabaseGetset cn : getsetImagedatas) {

                    productarraydb = db.getAllempContacts("" + usercode);

                }
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < productarraydb.size(); i++) {
                    try {

                        JSONObject jsonObject = new JSONObject();


                        jsonObject.put("pcode", productarraydb.get(i).getPid());
                        jsonObject.put("quan", productarraydb.get(i).getPqty());

                        jsonArray.put(jsonObject);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

                map.put("product", jsonArray.toString());

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

                                namelist = new ArrayList<String>();
                                namelist.clear();
                                namelist.add("Select Here");
                                for (int j = 0; j < jsonArray.length(); j++) {


                                    RetailerGetset pack = new RetailerGetset();
                                    JSONObject objectnew = jsonArray.getJSONObject(j);
                                    pack.setRetailercode((objectnew.getString("bprt")));
                                    pack.setRetailername((objectnew.getString("bname")));

                                    namelist.add(objectnew.getString("bname").trim() + " : " + objectnew.getString("bprt"));
                                }

                            }

                            ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                    R.layout.add_customer_item_layout, R.id.textView3, namelist);

                            selectbp.setAdapter(list);

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
            saveBtn.setText("Save");
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
                signatureimg.setImageBitmap(signature);

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
                this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 250));

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

}

package maslsalesapp.minda.javaclasses;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import maslsalesapp.minda.R;
import maslsalesapp.minda.miscellaneousclasses.AppController;
import maslsalesapp.minda.miscellaneousclasses.CircularNetworkImageview;

/**
 * Created by xantatech on 31/5/16.
 */
public class Dashboard extends Fragment {

    final int CROP_PIC = 2;
    Button addneworder, addnewretailer, addnewmechanic;
    TextView totalorder, totalvisit, totalcost, totalquantity, totaldiscus, prndingdiscus, closeddiscus;
    TextView uname, uemail;
    ProgressDialog mProgressDialog,mProgressDialog1;
    RequestQueue requestQueue;
    CircularNetworkImageview uimage;
    ImageLoader imageLoader;
    Dialog dialog_camera, uploadimagedialog;
    Bitmap thePic = null;
    Uri  mCapturedImageURI;
    int version = 0, webversion = 0;
    private static final int CAMERA_REQUEST = 2015;
    private static final int PICK_IMAGE = 100;
    ArrayList<String> teritory;
    Spinner holidays;
    Uri selectedimage;
    ArrayList<String> productcategorylist = new ArrayList<>();
    String holiday = "", holidayremarks = "", geolocation = "";
    LinearLayout l1, l2, l3, l4, l5, l6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dashboard, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();


        uploadimagedialog = new Dialog(getActivity());
        uploadimagedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        uploadimagedialog.setContentView(R.layout.custom_uploadimage_dialog);
        uploadimagedialog.setCancelable(false);

        imageLoader = AppController.getInstance().getImageLoader();

        addneworder = (Button) rootView.findViewById(R.id.addneworder);
        addnewretailer = (Button) rootView.findViewById(R.id.addnewretailer);
        addnewmechanic = (Button) rootView.findViewById(R.id.addnewmechanic);

        totalorder = (TextView) rootView.findViewById(R.id.totalorder);
        totalvisit = (TextView) rootView.findViewById(R.id.totalvisit);
        totalcost = (TextView) rootView.findViewById(R.id.totalvalue);
        totalquantity = (TextView) rootView.findViewById(R.id.totalquantity);

        totaldiscus = (TextView) rootView.findViewById(R.id.totaldiscussion);
        prndingdiscus = (TextView) rootView.findViewById(R.id.pendingdiscusion);
        closeddiscus = (TextView) rootView.findViewById(R.id.closeddiscussion);

        l1 = (LinearLayout) rootView.findViewById(R.id.line1);
        l2 = (LinearLayout) rootView.findViewById(R.id.line2);
        l3 = (LinearLayout) rootView.findViewById(R.id.line3);
        l4 = (LinearLayout) rootView.findViewById(R.id.line4);
        l5 = (LinearLayout) rootView.findViewById(R.id.line5);
        l6 = (LinearLayout) rootView.findViewById(R.id.line6);

        mProgressDialog1 = new ProgressDialog(getActivity());
        // Set progressdialog title
        mProgressDialog1.setTitle(" Please Wait... ");
        // Set progressdialog message
        mProgressDialog1.setMessage(" Getting info...");

        mProgressDialog1.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog1.setCanceledOnTouchOutside(false);

        requestQueue = Volley.newRequestQueue(getActivity());


        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionCode;

        if (Constants.isNetworkAvailable(getActivity())) {
            mProgressDialog1.show();
            getversion(Constants.URL + "getversion=1");
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("No internet");
            builder.setCancelable(false);
            builder.setMessage("Make sure you are connected");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    getActivity().finish();
                }
            });

            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }



        getMyCurrentLocation();

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Myorders fragment2 = new Myorders();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.commit();

            }
        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Myorders fragment2 = new Myorders();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.commit();

            }
        });
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Myorders fragment2 = new Myorders();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.commit();

            }
        });
        l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDiscussion fragment2 = new MyDiscussion();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.commit();

            }
        });
        l5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDiscussion fragment2 = new MyDiscussion();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.commit();

            }
        });
        l6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDiscussion fragment2 = new MyDiscussion();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.commit();

            }
        });


        uname = (TextView) rootView.findViewById(R.id.uname);
        uemail = (TextView) rootView.findViewById(R.id.uemail);
        uimage = (CircularNetworkImageview) rootView.findViewById(R.id.uimage);

        uimage.setImageUrl(Constants.sharedpreferences.getString("pr_image", ""), imageLoader);

        Button changepropic = (Button) rootView.findViewById(R.id.changepropic);
        changepropic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog_camera = new Dialog(getActivity());
                dialog_camera.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_camera.setContentView(R.layout.custom_camera_dialog);
                dialog_camera.setCancelable(false);

                Button cam_cancel = (Button) dialog_camera.findViewById(R.id.cam_cancel);
//                Button camera = (Button) dialog_camera.findViewById(R.id.camera);
                Button gallery = (Button) dialog_camera.findViewById(R.id.gallery);
                dialog_camera.show();

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery();
                    }
                });
//                camera.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        open_cameras();
//                    }
//                });
                cam_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog_camera.dismiss();
                    }
                });

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


        if (Constants.isNetworkAvailable(getActivity())) {
            getresult(Constants.URL + "profile=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"));

        } else {

            totalcost.setText("" + Constants.sharedpreferences.getString("TOTALCOST", "0"));
            totalorder.setText("" + Constants.sharedpreferences.getString("TOTALORDER", "0"));
            totalvisit.setText("Total Visit " + Constants.sharedpreferences.getString("TOTALVISIT", "0"));
            totalquantity.setText("" + Constants.sharedpreferences.getString("TOTALQTY", "0"));

            totaldiscus.setText("" + Constants.sharedpreferences.getString("TOTALDISCUS", "0"));
            prndingdiscus.setText("" + Constants.sharedpreferences.getString("PENDINGDISCUS", "0"));
            closeddiscus.setText("" + Constants.sharedpreferences.getString("CLOSEDDISCUS", "0"));

            uemail.setText(Html.fromHtml(Constants.sharedpreferences.getString("DATA", "0")));
            uname.setText(Html.fromHtml(Constants.sharedpreferences.getString("NAMECODE", "0")));
            Toast.makeText(getActivity(), "No Network", Toast.LENGTH_SHORT).show();
        }

        addneworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(true);
                dialog.setTitle("Add New Visit");
                dialog.setContentView(R.layout.dialog_addpost);

                dialog.show();

                Button dialoge_retailer, dialoge_mechanic, button_addholiday, bp;
                dialoge_retailer = (Button) dialog.findViewById(R.id.button_retailer);
                dialoge_mechanic = (Button) dialog.findViewById(R.id.button_mechanic);
                button_addholiday = (Button) dialog.findViewById(R.id.button_addholiday);
                bp = (Button) dialog.findViewById(R.id.bp);


                bp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Businesspartner fragment2 = new Businesspartner();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, fragment2);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                });
                dialoge_retailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Retailer fragment2 = new Retailer();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, fragment2);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                });


                dialoge_mechanic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Mechanic fragment2 = new Mechanic();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, fragment2);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                });


                button_addholiday.setOnClickListener(new View.OnClickListener() {
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
                            Calendar calander;
                            SimpleDateFormat simpleDateFormat;
                            calander = Calendar.getInstance();
                            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy ");

                            dialog.dismiss();
                            final Dialog dialognew = new Dialog(getActivity());
                            dialognew.setCancelable(true);
                            dialognew.setTitle("Other Visit (" + simpleDateFormat.format(calander.getTime()) + ")");
                            dialognew.setContentView(R.layout.holidaydialog);

                            dialognew.show();

                            Button update, cancle;

                            final EditText holidayremark;
                            update = (Button) dialognew.findViewById(R.id.update);
                            cancle = (Button) dialognew.findViewById(R.id.cancle);
                            holidays = (Spinner) dialognew.findViewById(R.id.holidaylist);
                            holidayremark = (EditText) dialognew.findViewById(R.id.holidayremark);

                            getteritory(Constants.URL + "holidaylist=1");

                            holidays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    if (position != 0) {
                                        holiday = teritory.get(position);
                                        Toast.makeText(getActivity(), "" + holiday, Toast.LENGTH_SHORT).show();

                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            update.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    holidayremarks = holidayremark.getText().toString();
                                    addholidayvisit(Constants.URL);
                                    dialognew.cancel();

                                }
                            });
                            cancle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialognew.cancel();

                                }
                            });
                        }


                    }
                });

            }
        });


        addnewretailer.setOnClickListener(new View.OnClickListener() {
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
                    fragmentTransaction.commit();
                }
            }
        });

        addnewmechanic.setOnClickListener(new View.OnClickListener() {
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

                    Addmechanic fragment2 = new Addmechanic();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragmentTransaction.commit();
                }

//                Addmechanic fragment2 = new Addmechanic();
//                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.frame_container, fragment2);
//                fragmentTransaction.commit();
            }
        });

        return rootView;
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    private void open_cameras() {
        try {
            String fileName = "profile.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(intent, CAMERA_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getversion(String url) {

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            webversion = Integer.parseInt(jsonObject.getString("version"));

                            mProgressDialog1.dismiss();

                            if (version < webversion) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                builder.setTitle("Version is outdated");
                                builder.setCancelable(false);
                                builder.setMessage("Your Current installed MASL Sales App is outdated, please update latest from Google Play store now");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Show location settings when the user acknowledges the alert dialog
                                        final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }

                                        getActivity().finish();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        getActivity().finish();
                                    }
                                });
                                Dialog alertDialog = builder.create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();

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

                                JSONArray jsonArray = jsonObject.getJSONArray("result");


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    teritory.add("" + jsonObject1.getString("HOL_NAME"));

                                }
                                ArrayAdapter<String> list = new ArrayAdapter<String>(getActivity(),
                                        R.layout.select_dialog_item_retailer, R.id.textView3, teritory);
                                holidays.setAdapter(list);


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

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        try {
            if (requestCode == PICK_IMAGE && null != data) {
                selectedimage = data.getData();
                System.out.println("" + selectedimage);
                performCrop();

            } else if (requestCode == CAMERA_REQUEST) {
                String[] projection = {MediaStore.Images.Media.DATA};
                selectedimage = data.getData();
                System.out.println("" + selectedimage);
                performCrop();

            } else if (requestCode == CROP_PIC && null != data) {

                Bundle extras = data.getExtras();
                thePic = extras.getParcelable("data");

                dialog_camera.dismiss();

                uploadimagedialog.show();

                Button cam_cancel = (Button) uploadimagedialog.findViewById(R.id.cancelbtn);
                Button gallery = (Button) uploadimagedialog.findViewById(R.id.uploadimg);

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uimage.setImageBitmap(thePic);

                        Constants.editor.putString("pr_image", BitMapToString(thePic));
                        Constants.editor.commit();

                        mProgressDialog.show();

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        thePic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        uploadimage(Constants.URL + "editimage=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"), encoded);

                    }
                });
                cam_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        uploadimagedialog.dismiss();
                    }
                });


            } else {
                dialog_camera.dismiss();
                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PICK_IMAGE) {
//            try {
//                selectedImage = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                columnindex = cursor.getColumnIndex(filePathColumn[0]);
//                file_path = cursor.getString(columnindex);
//                // Log.e("Attachment Path:", attachmentFile);
//                URI = Uri.parse("file://" + file_path);
//                image_path = file_path;
//
//                cursor.close();
//                System.out.println("1234789" + image_path);
//
//                if (resultCode == 0) {
//                    dialog_camera.dismiss();
//                } else {
//
//                    bmp = BitmapFactory.decodeFile(image_path, options);
//                    dialog_camera.dismiss();
//
//
//                    uploadimagedialog.show();
//                    Button cam_cancel = (Button) uploadimagedialog.findViewById(R.id.cancelbtn);
//                    Button gallery = (Button) uploadimagedialog.findViewById(R.id.uploadimg);
//
//                    gallery.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            uimage.setImageBitmap(bmp);
//
//                            mProgressDialog.show();
//
//                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                            byte[] byteArray = byteArrayOutputStream.toByteArray();
//                            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                            uploadimage(Constants.URL+"editimage=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"), encoded);
//
//                        }
//                    });
//                    cam_cancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // TODO Auto-generated method stub
//                            uploadimagedialog.dismiss();
//                        }
//                    });
//
//
//                    System.out.println("cccccc    " + image_path);
//                }
//
//            } catch (Exception e) {
//
//            }
//
//        } else if (requestCode == CAMERA_REQUEST) {
//
//            try {
////                selectedImage = mCapturedImageURI;
//
//                String[] projection = {MediaStore.Images.Media.DATA};
//
//                @SuppressWarnings("deprecation")
//                Cursor cursor = getActivity().managedQuery(mCapturedImageURI, projection, null, null, null);
//                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                String capturedImageFilePath = cursor.getString(column_index_data);
//                image_path = capturedImageFilePath;
//
//                if (resultCode == 0) {
//                    dialog_camera.dismiss();
//                } else {
//                    dialog_camera.dismiss();
//
//                    bmp = BitmapFactory.decodeFile(image_path, options);
//
//
//                    uploadimagedialog.show();
//                    Button cam_cancel = (Button) uploadimagedialog.findViewById(R.id.cancelbtn);
//                    Button gallery = (Button) uploadimagedialog.findViewById(R.id.uploadimg);
//
//                    gallery.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            uimage.setImageBitmap(bmp);
//
//                            mProgressDialog.show();
//
//                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                            byte[] byteArray = byteArrayOutputStream.toByteArray();
//                            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                            uploadimage(Constants.URL+"editimage=1&empcode=" + Constants.sharedpreferences.getString("EMP_CODE", "0"), encoded);
//
//                        }
//                    });
//                    cam_cancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // TODO Auto-generated method stub
//                            uploadimagedialog.dismiss();
//                        }
//                    });
//
//
//                    System.out.println("cccccc    " + image_path);
//                }
//
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        }
//    }

    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(selectedimage, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 3);
            cropIntent.putExtra("aspectY", 3);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(getActivity(), "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void getresult(String url) {

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
                                if (Constants.sharedpreferences.contains("pr_image")) {
                                    Bitmap bmp = StringToBitMap(Constants.sharedpreferences.getString("pr_image", ""));
                                    uimage.setImageBitmap(bmp);
                                } else {


                                    imageLoader.get(jsonObject.getString("image"), ImageLoader.getImageListener(uimage,
                                            R.drawable.blank, R.drawable
                                                    .blank));
                                    uimage.setImageUrl(jsonObject.getString("image"), imageLoader);
                                }
                                uname.setText(jsonObject.getString("name") + "  (" + jsonObject.getString("emp_code") + ")");

                                Constants.editor.putString("NAMECODE", uname.getText().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("trtry");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    productcategorylist.add("" + jsonArray.get(i).toString());

                                }
                                StringBuffer sb = new StringBuffer("");

                                for (int y = 0; y < productcategorylist.size(); y++) {

                                    sb.append(productcategorylist.get(y) + ",");
                                }

                                sb.deleteCharAt(sb.length() - 1);

                                productcategorylist.clear();
                                JSONArray jsonArray1 = jsonObject.getJSONArray("zone");

                                for (int i = 0; i < jsonArray1.length(); i++) {

                                    productcategorylist.add("" + jsonArray1.get(i).toString());
                                    Constants.zone = jsonArray1.get(0).toString();


                                }
                                StringBuffer sb1 = new StringBuffer("");
                                ;
                                for (int y = 0; y < productcategorylist.size(); y++) {

                                    sb1.append(productcategorylist.get(y) + ",");
                                }

                                sb1.deleteCharAt(sb1.length() - 1);

                                String title = "<font color=#FF0000>HQ:</font> " +
                                        "<font color=#000000>" + jsonObject.getString("hq") + "  " + "</font>" +
                                        "<font color=#FF0000>  Zone:</font> " +
                                        "<font color=#000000>" + sb1 + "  " + "</font>" +
                                        "<font color=#FF0000>  Ter:</font> " +
                                        "<font color=#000000>" + sb + "</font>";
                                uemail.setText(Html.fromHtml(title));

                                Constants.editor.putString("TOTALCOST", "" + jsonObject.getInt("total_order_cost"));
                                Constants.editor.putString("TOTALORDER", "" + jsonObject.getInt("total_order"));
                                Constants.editor.putString("TOTALVISIT", "(" + jsonObject.getString("month") + ") : " + jsonObject.getInt("visit"));
                                Constants.editor.putString("TOTALQTY", "" + jsonObject.getInt("total_order_qty"));

                                Constants.editor.putString("TOTALDISCUS", "" + jsonObject.getInt("total_discuss"));
                                Constants.editor.putString("PENDINGDISCUS", "" + jsonObject.getInt("pending_discuss"));
                                Constants.editor.putString("CLOSEDDISCUS", "" + jsonObject.getInt("closed_discuss"));


                                Constants.editor.putString("DATA", title);
                                Constants.editor.commit();

                                totalcost.setText("" + jsonObject.getInt("total_order_cost"));
                                totalorder.setText("" + jsonObject.getInt("total_order"));
                                totalvisit.setText("Total Visit (" + jsonObject.getString("month") + ") : " + jsonObject.getInt("visit"));
                                totalquantity.setText("" + jsonObject.getInt("total_order_qty"));

                                totaldiscus.setText("" + jsonObject.getInt("total_discuss"));
                                prndingdiscus.setText("" + jsonObject.getInt("pending_discuss"));
                                closeddiscus.setText("" + jsonObject.getInt("closed_discuss"));
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

    public void addholidayvisit(String url) {

        mProgressDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");

                            if (status == 0) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                mProgressDialog.dismiss();


                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        getActivity());

                                // set title
                                alertDialogBuilder.setTitle("Holiday Added");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Visit added")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                dialog.cancel();


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


//                            System.out.println(response);
//
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            Integer status = jsonObject.getInt("success");
//
//                            if (status == 0) {
//                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
//
//                            }
//                            mProgressDialog.dismiss();

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
                map.put("rmks", "" + holidayremarks);
                map.put("hoption", "" + holiday);
                map.put("addholiday", "1");
                map.put("geolocation", "" + geolocation);
                map.put("empcode", "" + Constants.sharedpreferences.getString("EMP_CODE", "0"));
                return map;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

    public void uploadimage(String url, final String img) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);

                            Integer status = jsonObject.getInt("success");

                            if (status == 0) {

                                Toast.makeText(getActivity(), "" + jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();

                            } else {

                                uploadimagedialog.dismiss();
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();


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
                map.put("image", img);

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
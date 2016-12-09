package maslsalesapp.minda.javaclasses;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
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
public class MyNotification extends Fragment {

    ListView productlist;
    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    ArrayList<RetailerGetset> retailerlist = new ArrayList<RetailerGetset>();
    ArrayList<String> productitemlist;
    Productitem_Adapter productadpter;
    SimpleDateFormat simpleDateFormat;
    Calendar calander;
    ImageLoader imageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.mynotification, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        productlist = (ListView) rootView.findViewById(R.id.productlist);
        imageLoader = AppController.getInstance().getImageLoader();
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

        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String vtime = simpleDateFormat.format(calander.getTime());

        getproductlist(Constants.URL+"notice=1&dat=" + vtime);

        productlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialoge_image_edit);

                dialog.setTitle("Notification Image");
                final NetworkImageView open_gallery = (NetworkImageView) dialog.findViewById(R.id.imhnotice);
                final Button open_camera = (Button) dialog.findViewById(R.id.ok);

                imageLoader.get(retailerlist.get(position).getCompany(), ImageLoader.getImageListener(open_gallery,
                        R.drawable.blank, R.drawable
                                .blank));
                open_gallery.setImageUrl(retailerlist.get(position).getCompany(), imageLoader);


                open_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                dialog.show();

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
                                    pack.setCode((objectnew.getString("notiTitle")));
                                    pack.setDesc((objectnew.getString("notiDesc")));
                                    pack.setCompany((objectnew.getString("notiLandingImgUrl")));

                                    JSONObject jobjdate = new JSONObject(objectnew.getString("datePublish"));
                                    pack.setGroup((jobjdate.getString("date")));

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

            convertView = inflater.inflate(R.layout.mynotificationadapter, parent, false);

            tv1 = (TextView) convertView.findViewById(R.id.tv1);
            tv2 = (TextView) convertView.findViewById(R.id.tv2);
            tv3 = (TextView) convertView.findViewById(R.id.tv3);


            tv1.setText(" " + myList.get(position).getCode());
            tv2.setText("" + String.valueOf(myList.get(position).getDesc()));
            tv3.setText(" " + String.valueOf(myList.get(position).getGroup()));


            return convertView;
        }

    }


}

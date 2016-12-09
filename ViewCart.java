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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import maslsalesapp.minda.R;
import maslsalesapp.minda.adapterandgetset.DatabaseGetset;
import maslsalesapp.minda.miscellaneousclasses.DataBaseHandler;

/**
 * Created by xantatech on 5/7/16.
 */
public class ViewCart extends Fragment {

    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    String vtime = "";
    ListView productllist;
    Button placeorder;
    Productitem_Adapter productadpter;
    TextView textView_info;
    int amount = 0, qtycount = 0;
    TextView totalprice, totalqty;
    String usercode = "", usertype = "", username = "", page = "", geolocation = "";
    SimpleDateFormat simpleDateFormat;
    Calendar calander;
    List<DatabaseGetset> productarraydb = new ArrayList<DatabaseGetset>();
    Bundle bundle = new Bundle();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.mycart, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

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
        if (getArguments().getString("location") != null) {
            geolocation = getArguments().getString("location");
        }

        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        vtime = simpleDateFormat.format(calander.getTime());

        productllist = (ListView) rootView.findViewById(R.id.productitmes);
        placeorder = (Button) rootView.findViewById(R.id.placeorder);
        totalprice = (TextView) rootView.findViewById(R.id.totalprice);
        totalqty = (TextView) rootView.findViewById(R.id.totalqty);
        textView_info = (TextView) rootView.findViewById(R.id.textView_info);

        DataBaseHandler db = new DataBaseHandler(getActivity());

        if (db.getAllempContacts("" + usercode).size() == 0) {

            Toast.makeText(getActivity(), "Basket is empty", Toast.LENGTH_SHORT).show();
        }
        List<DatabaseGetset> getsetImagedatas = db.getAllContacts();

        for (DatabaseGetset cn : getsetImagedatas) {

//            productarraydb = db.getAllContacts();
            productarraydb = db.getAllempContacts("" + usercode);

        }

        for (int i = 0; i < productarraydb.size(); i++) {
            amount += Integer.parseInt(productarraydb.get(i).getTotalprice());
            qtycount += Integer.parseInt(productarraydb.get(i).getPqty());
        }

        productadpter = new Productitem_Adapter(getActivity(), productarraydb);
        productllist.setAdapter(productadpter);

        totalprice.setText(": " + amount);
        totalqty.setText(": " + qtycount);

        Button back = (Button) rootView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                bundle.putString("Code", usercode);
                bundle.putString("Type", usertype);
                bundle.putString("name", username);

                if (page.equals("order")) {
                    Orders fragment2 = new Orders();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragment2.setArguments(bundle);
                    fragmentTransaction.commit();
                } else if (page.equals("retailer")) {
                    Retailer fragment2 = new Retailer();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragment2.setArguments(bundle);
                    fragmentTransaction.commit();
                } else if (page.equals("mechanic")) {
                    Mechanic fragment2 = new Mechanic();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragment2.setArguments(bundle);
                    fragmentTransaction.commit();
                }

            }
        });


        TextView retmecid = (TextView) rootView.findViewById(R.id.retmecid);

        retmecid.setText("" + geolocation);
        if (geolocation.equals("")) {
            retmecid.setText("Location NOT Found");
        }

        textView_info.setText(" " + usercode + " - " + usertype + " - " + username);
        placeorder.setOnClickListener(new View.OnClickListener() {
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
                } else if (productarraydb.size() != 0) {

                    bundle.putString("Code", usercode);
                    bundle.putString("Type", usertype);
                    bundle.putString("name", username);
                    bundle.putString("page", page);
                    ConfirmOrder fragment2 = new ConfirmOrder();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment2);
                    fragment2.setArguments(bundle);
                    fragmentTransaction.commit();


                } else {
                    Toast.makeText(getActivity(), "Basket is empty", Toast.LENGTH_SHORT).show();

                }

            }
        });


        productllist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(true);
                dialog.setTitle("Update Quantity");
                dialog.setContentView(R.layout.dialog_update);

                dialog.show();

                Button dialoge_retailer, cancle;
                final EditText qty;
                dialoge_retailer = (Button) dialog.findViewById(R.id.update);
                cancle = (Button) dialog.findViewById(R.id.cancle);
                qty = (EditText) dialog.findViewById(R.id.updateqty);

                qty.setText(productarraydb.get(position).getPqty());


                dialoge_retailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DataBaseHandler db = new DataBaseHandler(getActivity());
                        String q = qty.getText().toString();
                        String a = productarraydb.get(position).getPprice();
                        int multiplyprice = Integer.parseInt(q) * Integer.parseInt(a);
                        String producttotalprice = String.valueOf(multiplyprice);

                        Toast.makeText(getActivity(), "" + q, Toast.LENGTH_SHORT).show();

                        db.updateContact("" + q, producttotalprice, productarraydb.get(position).getPid(), productarraydb.get(position).getEmpcode());


                        productarraydb.clear();
                        amount = 0;
                        qtycount = 0;

                        List<DatabaseGetset> getsetImagedatas = db.getAllContacts();

                        for (DatabaseGetset cn : getsetImagedatas) {

                            productarraydb = db.getAllempContacts("" + usercode);

                        }
                        for (int i = 0; i < productarraydb.size(); i++) {
                            amount += Integer.parseInt(productarraydb.get(i).getTotalprice());
                            qtycount += Integer.parseInt(productarraydb.get(i).getPqty());
                        }

                        productadpter = new Productitem_Adapter(getActivity(), productarraydb);
                        productllist.setAdapter(productadpter);

                        totalprice.setText("" + amount);
                        totalqty.setText("" + qtycount);
                        dialog.dismiss();
                    }
                });
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
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

        requestQueue = Volley.newRequestQueue(getActivity());

        return rootView;
    }


    class Productitem_Adapter extends BaseAdapter {

        Context context;
        List<DatabaseGetset> myList;
        LayoutInflater inflater;
        TextView productname, qty, priceofproduct, totalpriceofitem;
        ImageView delete;

        public Productitem_Adapter(Context mcontext, List<DatabaseGetset> pmyList) {

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

            convertView = inflater.inflate(R.layout.productadapter, parent, false);

            productname = (TextView) convertView.findViewById(R.id.productname);
            qty = (TextView) convertView.findViewById(R.id.qty);
            priceofproduct = (TextView) convertView.findViewById(R.id.priceofitem);
            totalpriceofitem = (TextView) convertView.findViewById(R.id.totalpriceofitem);
            delete = (ImageView) convertView.findViewById(R.id.delete);

            productname.setText(myList.get(position).getPid());
            priceofproduct.setText(String.valueOf(myList.get(position).getPprice()));
            totalpriceofitem.setText(String.valueOf(myList.get(position).getTotalprice()));
            qty.setText(String.valueOf(myList.get(position).getPqty()));

            delete.setTag(position);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    DataBaseHandler db = new DataBaseHandler(context);
                    DatabaseGetset picture = myList.get(pos);
                    db.deleteparticular(new DatabaseGetset((picture.getPid()), picture.getEmpcode()));

                    myList.remove(pos);
                    productadpter.notifyDataSetChanged();

                    Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();

                    amount = 0;
                    qtycount = 0;

                    for (int i = 0; i < productarraydb.size(); i++) {
                        amount += Integer.parseInt(productarraydb.get(i).getTotalprice());
                        qtycount += Integer.parseInt(productarraydb.get(i).getPqty());
                    }

                    totalprice.setText("" + amount);
                    totalqty.setText("" + qtycount);


                }
            });


            return convertView;
        }

    }
}

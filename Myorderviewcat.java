package maslsalesapp.minda.javaclasses;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

import maslsalesapp.minda.R;
import maslsalesapp.minda.adapterandgetset.ItemListGetset;

/**
 * Created by xantatech on 5/7/16.
 */
public class Myorderviewcat extends Fragment {

    ProgressDialog mProgressDialog;
    RequestQueue requestQueue;
    ListView productllist;
    TextView totalprice, totalqty;
    String usercode = "", username = "", mdr = "";
    Productitem_Adapter productadpter;
    ArrayList<ItemListGetset> mylist = new ArrayList<ItemListGetset>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.myordercart, container, false);

        Constants.sharedpreferences = getActivity().getSharedPreferences(Constants.MyPREFERENCES, getActivity().MODE_PRIVATE);
        Constants.editor = Constants.sharedpreferences.edit();

        productllist = (ListView) rootView.findViewById(R.id.productitmes);
        totalprice = (TextView) rootView.findViewById(R.id.totalprice);
        totalqty = (TextView) rootView.findViewById(R.id.totalqty);


        if (getArguments().getString("Code") != null) {
            usercode = getArguments().getString("Code");
        }
        if (getArguments().getString("NAME") != null) {
            username = getArguments().getString("NAME");
        }
        if (getArguments().getString("MDR") != null) {
            mdr = getArguments().getString("MDR");
        }
        if (getArguments().getString("TotalPrice") != null) {
            totalprice.setText("" + getArguments().getString("TotalPrice"));
        }

        if (getArguments().getString("TotalQty") != null) {
            totalqty.setText("" + getArguments().getString("TotalQty"));
        }
        if (getArguments().getParcelableArrayList("itemlist") != null) {
            mylist = getArguments().getParcelableArrayList("itemlist");
        }

        TextView retmecid = (TextView) rootView.findViewById(R.id.retmecid);

        Button back = (Button) rootView.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Myorders fragment2 = new Myorders();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment2);
                fragmentTransaction.commit();
            }
        });


        retmecid.setText(mdr + "-" + username + "-" + "" + usercode);

        productadpter = new Productitem_Adapter(getActivity(), mylist);
        productllist.setAdapter(productadpter);

        return rootView;
    }

    class Productitem_Adapter extends BaseAdapter {

        Context context;
        List<ItemListGetset> myList;
        LayoutInflater inflater;
        TextView productname, qty, priceofproduct, totalpriceofitem;

        public Productitem_Adapter(Context mcontext, List<ItemListGetset> pmyList) {

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

            convertView = inflater.inflate(R.layout.productorderedadapter, parent, false);

            productname = (TextView) convertView.findViewById(R.id.productname);
            qty = (TextView) convertView.findViewById(R.id.qty);
            priceofproduct = (TextView) convertView.findViewById(R.id.priceofitem);
            totalpriceofitem = (TextView) convertView.findViewById(R.id.totalpriceofitem);

            productname.setText(myList.get(position).getItemcode());
            priceofproduct.setText(String.valueOf(myList.get(position).getItemrate()));
            totalpriceofitem.setText(String.valueOf(myList.get(position).getItemvalue()));
            qty.setText(String.valueOf(myList.get(position).getItemqty()));

            return convertView;
        }

    }
}

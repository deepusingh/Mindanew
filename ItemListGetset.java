package maslsalesapp.minda.adapterandgetset;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xantatech on 25/7/16.
 */
public class ItemListGetset implements Parcelable {

    String itemcode, itemrate, itemvalue, itemqty;

    public ItemListGetset() {
    }

    public String getItemcode() {
        return itemcode;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    public String getItemrate() {
        return itemrate;
    }

    public void setItemrate(String itemrate) {
        this.itemrate = itemrate;
    }

    public String getItemvalue() {
        return itemvalue;
    }

    public void setItemvalue(String itemvalue) {
        this.itemvalue = itemvalue;
    }

    public String getItemqty() {
        return itemqty;
    }

    public void setItemqty(String itemqty) {
        this.itemqty = itemqty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(itemcode);
        dest.writeString(itemqty);
        dest.writeString(itemrate);
        dest.writeString(itemvalue);


    }

    private void readFromParcel(Parcel in) {
        itemcode = in.readString();
        itemqty = in.readString();
        itemrate = in.readString();
        itemvalue = in.readString();
    }


    public static final Parcelable.Creator<ItemListGetset> CREATOR
            = new Parcelable.Creator<ItemListGetset>() {
        public ItemListGetset createFromParcel(Parcel in) {
            return new ItemListGetset(in);
        }

        public ItemListGetset[] newArray(int size) {
            return new ItemListGetset[size];
        }
    };

    private ItemListGetset(Parcel in) {
        itemcode = in.readString();
        itemqty = in.readString();
        itemrate = in.readString();
        itemvalue = in.readString();
    }
}

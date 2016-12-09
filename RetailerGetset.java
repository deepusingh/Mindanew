package maslsalesapp.minda.adapterandgetset;

import java.util.ArrayList;

/**
 * Created by xantatech on 23/6/16.
 */
public class RetailerGetset {

    String retailercode, retailername;
    String code;
    String desc;
    String category;
    String company;
    String model;
    int price;
    String group;
    int qty;
    int mulitplyprice;
    String gdesc;
    String itemcode, itemrate, itemvalue, itemqty;
    ArrayList<ItemListGetset> itemlist;

    public ArrayList<ItemListGetset> getItemlist() {
        return itemlist;
    }

    public void setItemlist(ArrayList<ItemListGetset> itemlist) {
        this.itemlist = itemlist;
    }

    public RetailerGetset() {

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

    public int getMulitplyprice() {
        return mulitplyprice;
    }

    public void setMulitplyprice(int mulitplyprice) {
        this.mulitplyprice = mulitplyprice;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGdesc() {
        return gdesc;
    }

    public void setGdesc(String gdesc) {
        this.gdesc = gdesc;
    }

    public String getRetailercode() {
        return retailercode;
    }

    public void setRetailercode(String retailercode) {
        this.retailercode = retailercode;
    }

    public String getRetailername() {
        return retailername;
    }

    public void setRetailername(String retailername) {
        this.retailername = retailername;
    }


}

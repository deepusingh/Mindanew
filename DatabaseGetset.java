package maslsalesapp.minda.adapterandgetset;

/**
 * Created by xantatech on 6/7/16.
 */
public class DatabaseGetset {
    String pname;
    String pqty;
    String pid;
    String pprice;
    String totalprice;
    String empcode;

    public DatabaseGetset(String pid, String pname, String pqty, String pprice, String totalprice, String empcode) {
        this.pid = pid;
        this.pname = pname;
        this.pqty = pqty;
        this.pprice = pprice;
        this.totalprice = totalprice;
        this.empcode = empcode;
    }

    public DatabaseGetset() {
    }

    public DatabaseGetset(String pid) {
        this.pid = pid;
    }
    public DatabaseGetset(String pid,String empcode) {
        this.pid = pid;
        this.empcode = empcode;
    }

    public String getEmpcode() {
        return empcode;
    }

    public void setEmpcode(String empcode) {
        this.empcode = empcode;
    }

    public String getTotalprice() {

        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public String getPprice() {
        return pprice;
    }

    public void setPprice(String pprice) {
        this.pprice = pprice;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPqty() {
        return pqty;
    }

    public void setPqty(String pqty) {

        this.pqty = pqty;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
}

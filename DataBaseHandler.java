package maslsalesapp.minda.miscellaneousclasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import maslsalesapp.minda.adapterandgetset.DatabaseGetset;

public class DataBaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Mindadb4";

    // Contacts table name
    private static final String TABLE_CONTACTS = "mindadatadb4";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRODUCTQTY = "qty";
    private static final String KEY_PRODUCTPRICE = "price";
    private static final String KEY_PRODUCTTOTALPRICE = "total";
    private static final String Key_EMPCODE = "empcode";


    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " TEXT," + KEY_NAME + " TEXT," + KEY_PRODUCTQTY + " TEXT," + KEY_PRODUCTPRICE + " TEXT," + KEY_PRODUCTTOTALPRICE + " TEXT," + Key_EMPCODE + " TEXT" + " )";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }


    public void addContact(DatabaseGetset getsetImagedata) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, getsetImagedata.getPid());
        values.put(KEY_NAME, getsetImagedata.getPname());
        values.put(KEY_PRODUCTQTY, getsetImagedata.getPqty());
        values.put(KEY_PRODUCTPRICE, getsetImagedata.getPprice());
        values.put(KEY_PRODUCTTOTALPRICE, getsetImagedata.getTotalprice());
        values.put(Key_EMPCODE, getsetImagedata.getEmpcode());

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public DatabaseGetset getContact(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID,
                        KEY_NAME, KEY_PRODUCTQTY, KEY_PRODUCTPRICE, KEY_PRODUCTTOTALPRICE, Key_EMPCODE}, Key_EMPCODE + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DatabaseGetset getsetImagedata = new DatabaseGetset((cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));

        return getsetImagedata;

    }

    // Getting All Contacts
    public List<DatabaseGetset> getAllContacts() {
        List<DatabaseGetset> getsetImagedataList = new ArrayList<DatabaseGetset>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY name";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DatabaseGetset getsetImagedata = new DatabaseGetset();
                getsetImagedata.setPid(cursor.getString(0));
                getsetImagedata.setPname(cursor.getString(1));
                getsetImagedata.setPqty(cursor.getString(2));
                getsetImagedata.setPprice(cursor.getString(3));
                getsetImagedata.setTotalprice(cursor.getString(4));
                getsetImagedata.setEmpcode(cursor.getString(5));

                getsetImagedataList.add(getsetImagedata);
            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return contact list
        return getsetImagedataList;

    }

    // Deleting single getsetImagedata
    public void deleteparticular(DatabaseGetset getsetImagedata) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ? AND " + Key_EMPCODE + " = ?",
                new String[]{String.valueOf(getsetImagedata.getPid()), String.valueOf(getsetImagedata.getEmpcode())});
        db.close();
    }

    public void deleteContactall(DatabaseGetset getsetImagedata) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, Key_EMPCODE + " = ?",
                new String[]{String.valueOf(getsetImagedata.getPid())});
        db.close();
    }

    public int updateContact(String getsetImagedata, String getsettotal, String id, String eid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCTQTY, getsetImagedata);
        values.put(KEY_PRODUCTTOTALPRICE, getsettotal);

        // updating row
        return db.update(TABLE_CONTACTS,
                values,
                KEY_ID + " = ? AND " + Key_EMPCODE + " = ?",
                new String[]{String.valueOf(id), String.valueOf(eid)});


    }

    public List<DatabaseGetset> getAllempContacts(String id) {
        List<DatabaseGetset> getsetImagedataList = new ArrayList<DatabaseGetset>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE " + Key_EMPCODE + " = '" + id + "'";


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DatabaseGetset getsetImagedata = new DatabaseGetset();
                getsetImagedata.setPid(cursor.getString(0));
                getsetImagedata.setPname(cursor.getString(1));
                getsetImagedata.setPqty(cursor.getString(2));
                getsetImagedata.setPprice(cursor.getString(3));
                getsetImagedata.setTotalprice(cursor.getString(4));
                getsetImagedata.setEmpcode(cursor.getString(5));

                getsetImagedataList.add(getsetImagedata);
            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return contact list
        return getsetImagedataList;

    }


}

package beta.customers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import beta.customers.Customer;

/**
 * Created by omar on 3/20/16.
 */
public class DBHandler extends SQLiteOpenHelper {
    Context mContext;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "customers.db";
    private static final String TABLE_CUSTOMERS_DETAILS = "CUSTOMERS_DETAILS";
    private static final String TABLE_LINES = "LINES";

    public static final String _ID = "_id";
    public static final String NAME = "NAME";
    public static final String ADDRESS = "ADDRESS";
    public static final String LINE = "LINE";
    public static final String PHONE = "PHONE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String PICTURE_PATH = "PICTURE_PATH";

    private static final String LINE_ID = "_id";
    private static final String LINE_NAME = "NAME";

    String createCustomerTable;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createCustomerTable = "CREATE TABLE " + TABLE_CUSTOMERS_DETAILS + "(" +
                _ID + " TEXT PRIMARY KEY, " +
                NAME + "  TEXT, " +
                ADDRESS + "  TEXT, " +
                LINE + "  TEXT, " +
                PHONE + "  TEXT, " +
                LONGITUDE + "  TEXT, " +
                LATITUDE + "  TEXT, " +
                PICTURE_PATH + "  TEXT);";

        String createLinesTable = "CREATE TABLE " + TABLE_LINES +" ( " +
                LINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                LINE_NAME + " TEXT" +
                "); ";

        db.execSQL(createCustomerTable);
        db.execSQL(createLinesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINES);
        onCreate(db);
    }

    public void addCustomer(Customer customer) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(_ID, customer.code);
        values.put(NAME, customer.name);
        values.put(ADDRESS, customer.address);
        values.put(LINE, customer.line);
        values.put(PHONE, customer.phone);
        values.put(LONGITUDE, customer.locationLong);
        values.put(LATITUDE, customer.locationLat);
        values.put(PICTURE_PATH, customer.picturePath);

        if(db.insert(TABLE_CUSTOMERS_DETAILS, null, values) != -1)
            Toast.makeText(mContext, "New customer added", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mContext, "Error adding customer", Toast.LENGTH_SHORT).show();

        db.close();
    }

    public void replaceCustomer(Customer customer, String lastCode) {

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_CUSTOMERS_DETAILS, _ID + " = '" + lastCode + "'", null);
        } catch (Exception e) {
            Log.e("replaceCustomer", e.toString());
        }

        db.close();

        addCustomer(customer);
    }

    public boolean codeIsFound(String code) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " + _ID +" FROM " + TABLE_CUSTOMERS_DETAILS +
                " WHERE " + _ID + " = '" + code + "'";
        Cursor c = db.rawQuery(query, null);
        boolean flag = !(c.getCount() == 0);
        c.close();
        db.close();
        return flag;
    }

    public List<Customer> getCustomers() {

        String query = "SELECT * FROM " + TABLE_CUSTOMERS_DETAILS;

        SQLiteDatabase db =  getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        List<Customer> result = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()) {

            Customer newC = new Customer();

            newC.code = c.getString(c.getColumnIndex(_ID));
            newC.address = c.getString(c.getColumnIndex(ADDRESS));
            newC.name = c.getString(c.getColumnIndex(NAME));
            newC.line = c.getString(c.getColumnIndex(LINE));
            newC.phone = c.getString(c.getColumnIndex(PHONE));
            newC.picturePath = c.getString(c.getColumnIndex(PICTURE_PATH));
            newC.locationLat = c.getString(c.getColumnIndex(LATITUDE));
            newC.locationLong = c.getString(c.getColumnIndex(LONGITUDE));

            result.add(newC);
            c.moveToNext();
        }
        c.close();
        db.close();
        return result;
    }

    public void addLine (String line) {
        ContentValues values = new ContentValues();
        values.put(LINE_NAME, line);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_LINES, null, values);
        db.close();
    }

    public List<String> getLines() {
        List<String> result = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_LINES;

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();
        while(!c.isAfterLast()) {
            result.add(c.getString(c.getColumnIndex(LINE_NAME)));
            c.moveToNext();
        }

        c.close();
        db.close();

        return result;
    }
}

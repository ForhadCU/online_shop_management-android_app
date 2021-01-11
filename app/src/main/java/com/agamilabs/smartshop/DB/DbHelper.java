package com.agamilabs.smartshop.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.agamilabs.smartshop.Models.InvoiceItem;
import com.agamilabs.smartshop.Models.InvoiceModel;


import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartShop_db";
    private static final String TABLE_1 = "Selected_product_list_table";
    private static final int DATABASE_VERSION = 3;

    private static final String dID = "_id";
    private static final String dProductName = "Product_name";
    private static final String dCustomer = "customer";
    private static final String dDate = "date";
    private static final String dProductQuantity = "Product_quantity";
    private static final String dProductPrice = "Product_price";
    private static final String dDiscount = "discount";
    private static final String dTotalBill = "total_bill";
//    private static final String dProductImgUrl = "Product_imgUrl";


    private static final String dCREATE_TABLE = "CREATE TABLE " + TABLE_1 + " (" + dID + " INTEGER PRIMARY KEY AUTOINCREMENT , "+dCustomer+" TEXT,"+dDate+" TEXT, " + dProductName + " TEXT  NOT NULL, " +
            "" + dProductQuantity + " VARCHAR(255)," + dProductPrice + " VARCHAR(255), "+dDiscount+" VARCHAR(255), "+dTotalBill+" VARCHAR(255))";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(dCREATE_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_1);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }



    public long mAddSelectedProduct( String customer, String date, String productName, String productQuantity, String productPrice, String discount, String totalBill){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(dCustomer, customer);
        contentValues.put(dDate, date);
        contentValues.put(dProductName, productName);
        contentValues.put(dProductQuantity, productQuantity);
        contentValues.put(dProductPrice, productPrice);
        contentValues.put(dDiscount, discount);
        contentValues.put(dTotalBill, totalBill);
//        contentValues.put(dProductImgUrl, productImgUrl);

        long id = sqLiteDatabase.insert(TABLE_1, null, contentValues);
        return id;
    }

    public List<InvoiceItem> showSelectedProductsInScannerActivity()
    {
        String sql = "select Product_name, Product_quantity, total_bill from "+ TABLE_1;
        SQLiteDatabase db = this.getReadableDatabase();
        List<InvoiceItem> storeProductDetails = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {

            do {
                String productName = cursor.getString(cursor.getColumnIndex(dProductName));
                String productQuantity = cursor.getString(cursor.getColumnIndex(dProductQuantity));
                String totalBill = cursor.getString(cursor.getColumnIndex(dTotalBill));

                storeProductDetails.add(new InvoiceItem(productName, Double.parseDouble(productQuantity), Double.parseDouble(totalBill)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storeProductDetails;
    }
    public List<InvoiceItem> invoiceItemTable()
    {
        String sql = "select Product_name, Product_price, Product_quantity, total_bill from "+ TABLE_1;
        SQLiteDatabase db = this.getReadableDatabase();
        List<InvoiceItem> storeData = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(dProductName));
                String price = cursor.getString(cursor.getColumnIndex(dProductPrice));
                String qty = cursor.getString(cursor.getColumnIndex(dProductQuantity));
                String total = cursor.getString(cursor.getColumnIndex(dTotalBill));
                storeData.add(new InvoiceItem(name,Double.parseDouble(qty), Double.parseDouble(price),  Double.parseDouble(total)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return storeData;

    }

    public ArrayList<InvoiceItem> showAllSelectedProductsInInvoiceActivity()
    {
        String sql = "select * from "+ TABLE_1;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<InvoiceItem> storeProductDetails = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {

            do {
                String customer = cursor.getString(1);
                String date = cursor.getString(2);
                String productName = cursor.getString(3);
                String productQuantity = cursor.getString(4);
                String productPrice = cursor.getString(5);
                String discount = cursor.getString(6);
                String totalBill = cursor.getString(7);

//                storeProductDetails.add(new InvoiceItem(customer, date, productName, Double.parseDouble(productQuantity), Double.parseDouble(productPrice), Double.parseDouble(discount), Double.parseDouble(totalBill)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storeProductDetails;
    }
    public void mDeleteProductList()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_1);
    }
}

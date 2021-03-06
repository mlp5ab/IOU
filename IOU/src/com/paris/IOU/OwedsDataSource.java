package com.paris.IOU;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MichaelParis
 * Date: 12/16/12
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class OwedsDataSource {

    //Database fields
    private SQLiteDatabase database;
    private MySQLiteOwedHelper dbHelper;
    private String[] allColumns = { MySQLiteOwedHelper.COLUMN_ID,
            MySQLiteOwedHelper.COLUMN_NAME, MySQLiteOwedHelper.COLUMN_OWEDVAL,
            MySQLiteOwedHelper.COLUMN_DESC, MySQLiteOwedHelper.COLUMN_DATECREATED};

    public OwedsDataSource( Context context ) {
        dbHelper = new MySQLiteOwedHelper(context);
    }

    public void open() throws SQLException {
       database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Owed createOwed(String name, double oweAmount, String desc) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteOwedHelper.COLUMN_NAME, name);
        values.put(MySQLiteOwedHelper.COLUMN_OWEDVAL, oweAmount);
        values.put(MySQLiteOwedHelper.COLUMN_DESC, desc);

        //GET CURRENT DATE TO ADD TO TABLE
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String strDate = sdf.format(new Date());
        values.put(MySQLiteOwedHelper.COLUMN_DATECREATED, strDate);

        long insertId = database.insert(MySQLiteOwedHelper.TABLE_OWEDS, null,
                values);
        Cursor cursor = database.query(MySQLiteOwedHelper.TABLE_OWEDS,
                allColumns, MySQLiteOwedHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Owed newOwed = cursorToOwed(cursor);
        cursor.close();
        return newOwed;
    }

    public void updateOwed(Owed owed, double amount) {
        String strFilter = "_id=" + owed.getId();
        ContentValues values = new ContentValues();
        values.put(MySQLiteOwedHelper.COLUMN_NAME, owed.getName());
        values.put(MySQLiteOwedHelper.COLUMN_OWEDVAL, amount);
        values.put(MySQLiteOwedHelper.COLUMN_DESC, owed.getDescription());
        values.put(MySQLiteOwedHelper.COLUMN_DATECREATED, owed.getDateTime());
        database.update(MySQLiteOwedHelper.TABLE_OWEDS, values, strFilter, null);
    }

    public void deleteOwed(Owed owed) {
        long id = owed.getId();
        System.out.println("Owed deleted with id: " + id);
        database.delete(MySQLiteOwedHelper.TABLE_OWEDS, MySQLiteOwedHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void deleteAllOweds() {
        Log.w("OwedsDataSource", "Delete all oweds called");
        Cursor cursor = database.query(MySQLiteOwedHelper.TABLE_OWEDS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while( !cursor.isAfterLast()) {
            Owed owed = cursorToOwed(cursor);
            long id = owed.getId();
            database.delete(MySQLiteOwedHelper.TABLE_OWEDS, MySQLiteOwedHelper.COLUMN_ID +
            " = " + id, null);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public List<Owed> getAllOwed() {
        List<Owed> oweds = new ArrayList<Owed>();

        Cursor cursor = database.query(MySQLiteOwedHelper.TABLE_OWEDS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while( !cursor.isAfterLast()) {
            Owed owed = cursorToOwed(cursor);
            oweds.add(owed);
            cursor.moveToNext();
        }

        //Make sure to close cursor
        cursor.close();
        return oweds;
    }

    public Owed cursorToOwed(Cursor cursor) {
        Owed owed = new Owed();
        owed.setId(cursor.getLong(0));
        owed.setName(cursor.getString(1));
        owed.setOwedAmount(cursor.getDouble(2));
        owed.setDescription(cursor.getString(3));
        owed.setDateTime(cursor.getString(4));
        return owed;
    }

}

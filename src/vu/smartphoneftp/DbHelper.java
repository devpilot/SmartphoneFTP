package vu.smartphoneftp;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "smartphoneFTP";

	// servers table name
	private static final String TABLE_ACCOUNTS = "accounts";

	// Accounts Table Columns names
	private static final String KEY_ID = "_id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_HOST = "host";
	private static final String KEY_PORT = "port";
	private static final String KEY_USER = "user";
	private static final String KEY_PASS = "pass";

	// Account table create statement
	private static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE "
			+ TABLE_ACCOUNTS + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE + " TEXT,"
			+ KEY_HOST + " TEXT," + KEY_PORT + " NUMERIC," + KEY_USER
			+ " TEXT," + KEY_PASS + " TEXT)";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Create table
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_ACCOUNT_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);

		// Create tables again
		onCreate(db);
	}

	// Adding new account
	public void addServer(Server server) {
		SQLiteDatabase db = this.getWritableDatabase(); // Open database
		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, server.getTitle()); // connection Name
		values.put(KEY_HOST, server.getHost()); // server hostname
		values.put(KEY_PORT, server.getPort()); // server port
		values.put(KEY_USER, server.getUsername()); // server username
		values.put(KEY_PASS, server.getPassword()); // server password
		
		// Inserting Row
		db.insert(TABLE_ACCOUNTS, null, values);
		db.close(); // Closing database
	}

	// Get all accounts id and name
	public List<Server> getServers() {
		SQLiteDatabase db = this.getReadableDatabase(); // Open database

		List<Server> serverList = new ArrayList<Server>();
		// Select All Query
		String selectQuery = "SELECT "+ KEY_ID +","+ KEY_TITLE +" FROM " + TABLE_ACCOUNTS;

		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Server serv = new Server();
				 serv.set_id(Integer.parseInt(cursor.getString(0)));
				 serv.setTitle(cursor.getString(1));
				// Adding server to list
				serverList.add(serv);
			} while (cursor.moveToNext());
			cursor.close();
		}
		db.close();
		// return server list
		return serverList;
	}
	
	// Deleting single connection
	public void deleteServer(Server server) {
	SQLiteDatabase db = this.getWritableDatabase();
	db.delete(TABLE_ACCOUNTS, KEY_ID + " = ?",
	new String[]{String.valueOf(server.get_id())});
	db.close();
	}
}

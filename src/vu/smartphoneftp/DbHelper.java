package vu.smartphoneftp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "smartphoneFTP";

	// Contacts table name
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
	
}

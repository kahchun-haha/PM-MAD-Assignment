package com.example.horapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper{

    private volatile static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context){
        if(sInstance==null){
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }




/*  Constructor should be private to prevent direct instantiation.
    Make a call to the static method "getInstance()"       */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Database Info
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Users";


    // Table Values
    private static final String TABLE_CONTACTS = "Contacts";
    private static final String CONTACT_ID = "contactID";
    private static final String PHONE = "phone_number";


    @Override
    public void onOpen(SQLiteDatabase db) {
        System.out.println("is open? "+db.isOpen());
    }

    public void onCreate(SQLiteDatabase db) {
        System.out.println("Is database opened? onCreate()" + db.isOpen());
        String SQL_CREATE_ENTRIES = "CREATE TABLE  " + TABLE_CONTACTS + "("
                + CONTACT_ID + " INTEGER PRIMARY KEY, "
                + PHONE + " VARCHAR(20) NOT NULL)";
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i,int i1) {
        System.out.println("Is database opened? onUpgrade()" + db.isOpen());
        String drop_table = "DROP TABLE IF EXISTS Contacts";
        db.execSQL(drop_table);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public long addEntry(){
        SQLiteDatabase db = sInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PHONE,"012-5239724");
        long id = db.insert(TABLE_CONTACTS,null,values);

        //db.close();
        return id;
    }
}

/*  onCreate() will be invoked only when getWritableDatabase() or getReadableDatabase() is executed
    onCreate() is called when the database is created for the FIRST time.
    If a database already exists on disk with the same DATABASE_NAME,
    this method will NOT be called.  */

 /* onUpgrade() is called when the database needs to be upgraded.
    This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    but the DATABASE_VERSION is different than the version of the database that exists on disk.
*/

// Don't need Content Provider to use an SQLite database if the use is entirely within own application

/*
   Singleton pattern restricts the instantiation of a class and ensures that only one instance
   of the class exists in the Java Virtual Machine.
   The static getInstance() method ensures that only one instance of DatabaseHelper
   will ever exist at any given time. If the sInstance object has not been initialized,
   one will be created. If one has already been created then it will simply be returned
*/


/*
    The primary reason for performing long-running tasks in a background thread is to keep
    the UI responsive during those tasks. If your database operation is initiated by a user action,
    such as a button click, and doesn't significantly delay the UI from being displayed or
    interacted with, it's generally acceptable to perform it on the main UI thread.
 */
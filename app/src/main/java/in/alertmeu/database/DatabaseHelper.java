package in.alertmeu.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.alertmeu.utils.MyApplicatio;

import static in.alertmeu.utils.Constant.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alertmeu.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper databaseHelper;

    private DatabaseHelper() {
        super(MyApplicatio.getInstance(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance() {

        if (databaseHelper == null) {
            synchronized (DatabaseHelper.class) { //thread safe singleton
                if (databaseHelper == null)
                    databaseHelper = new DatabaseHelper();
            }
        }

        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TBL_BUSINESS_CATEGORY = "CREATE TABLE " + TABLE_TBL_BUSINESS_CATEGORY + "("
                + CID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ID + " INTEGER NOT NULL UNIQUE, "
                + ADS_PRICING + " REAL, "
                + COUNTRY_CODE + " TEXT, " //nullable
                + CURRENCY_SIGN + " TEXT, " //nullable
                + DISCOUNT + " REAL, " //nullable
                + NOTIFICATION_CHARGE + " REAL, " //nullable
                + PRICE_FORMULA + " TEXT, " //nullable
                + ROUNDING_SCALE + " INTEGER, " //nullable
                + SPECIAL_MESSAGE + " TEXT, " //nullable
                + SPECIAL_MESSAGE_HINDI + " TEXT, " //nullable
                + CATEGORY_NAME + " TEXT, " //nullable
                + CATEGORY_NAME_HINDI + " TEXT, " //nullable
                + IMAGE_PATH + " TEXT, " //nullable
                + STATUS + " INTEGER, "
                + SYNC_STATUS + " INTEGER, "
                + CREATE_AT + " TEXT " //nullable
                + ")";
        String CREATE_TBL_BUSINESS_SUBCATEGORY = "CREATE TABLE " + TABLE_TBL_BUSINESS_SUBCATEGORY + "("
                + CID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ID + " INTEGER NOT NULL UNIQUE, "
                + BC_ID + " INTEGER, "
                + SUBCATEGORY_NAME + " TEXT, " //nullable
                + SUBCATEGORY_NAME_HINDI + " TEXT, " //nullable
                + IMAGE_PATH + " TEXT, " //nullable
                + STATUS + " INTEGER, "
                + SYNC_STATUS + " INTEGER, "
                + CREATE_AT + " TEXT " //nullable
                + ")";
        String CREATE_TBL_USER_SUBCATGEORY = "CREATE TABLE " + TABLE_TBL_USER_SUBCATGEORY + "("
                + CID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ID + " INTEGER NOT NULL UNIQUE, "
                + USER_ID + " INTEGER, "
                + SUBBC_ID + " INTEGER, " //nullable
                + MAINCAT_ID + " INTEGER, " //nullable
                + STATUS + " INTEGER, "
                + SYNC_STATUS + " INTEGER, "
                + CREATE_AT + " TEXT " //nullable
                + ")";
        db.execSQL(CREATE_TBL_BUSINESS_CATEGORY);
        db.execSQL(CREATE_TBL_BUSINESS_SUBCATEGORY);
        db.execSQL(CREATE_TBL_USER_SUBCATGEORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TBL_BUSINESS_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TBL_BUSINESS_SUBCATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TBL_USER_SUBCATGEORY);
        // Create tables again
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        //enable foreign key constraints like ON UPDATE CASCADE, ON DELETE CASCADE
        db.execSQL("PRAGMA foreign_keys=ON;");
    }
}

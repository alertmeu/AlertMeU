package in.alertmeu.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import in.alertmeu.models.MainCatModeDAO;
import in.alertmeu.models.SubCatModeDAO;
import in.alertmeu.utils.Constant;

import static in.alertmeu.utils.Constant.*;


public class DatabaseQueryClass {
    public List<MainCatModeDAO> getAllMainCat() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Constant.TABLE_TBL_BUSINESS_CATEGORY, null, null, null, null, null, "category_name ASC,category_name_hindi ASC", null);

            /**
             // If you want to execute raw query then uncomment below 2 lines. And comment out above line.

             String SELECT_QUERY = String.format("SELECT %s, %s, %s, %s, %s FROM %s", Config.COLUMN_STUDENT_ID, Config.COLUMN_STUDENT_NAME, Config.COLUMN_STUDENT_REGISTRATION, Config.COLUMN_STUDENT_EMAIL, Config.COLUMN_STUDENT_PHONE, Config.TABLE_STUDENT);
             cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
             */

            if (cursor != null)
                if (cursor.moveToFirst()) {
                    List<MainCatModeDAO> milestoneList = new ArrayList<>();
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Constant.ID));
                        String category_name = cursor.getString(cursor.getColumnIndex(Constant.CATEGORY_NAME));
                        String category_name_hindi = cursor.getString(cursor.getColumnIndex(Constant.CATEGORY_NAME_HINDI));
                        String checked_status = "" + getSumTotalQValue(id);
                        String image_path = cursor.getString(cursor.getColumnIndex(Constant.IMAGE_PATH));
                        milestoneList.add(new MainCatModeDAO("" + id, category_name, category_name_hindi, checked_status, image_path));
                    } while (cursor.moveToNext());
                    Comparator<MainCatModeDAO> comparator = new Comparator<MainCatModeDAO>() {

                        @Override
                        public int compare(MainCatModeDAO object1, MainCatModeDAO object2) {
                            return Integer.parseInt(object2.getChecked_status()) - Integer.parseInt(object1.getChecked_status());
                        }
                    };
                    Collections.sort(milestoneList, comparator);
                    return milestoneList;
                }
        } catch (Exception e) {
            //Logger.d("Exception: " + e.getMessage());
            //  Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            // sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public int getSumTotalQValue(int mid) {
        int sum = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {
            int status = 1;
            String sumQuery = "select count(" + USER_ID + ") as Total from " + TABLE_TBL_USER_SUBCATGEORY + " where " + MAINCAT_ID + "= " + mid + " and " + STATUS + " = " + status + "";
            cursor = sqLiteDatabase.rawQuery(sumQuery, null);
            if (cursor.moveToFirst())
                sum = cursor.getInt(cursor.getColumnIndex("Total"));
        } catch (Exception e) {
            //Logger.d("Exception: " + e.getMessage());
            //  Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            //  sqLiteDatabase.close();
        }
        return sum;
    }

    public List<SubCatModeDAO> getUserSubCatCat(int user_id, int bcid) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Constant.TABLE_TBL_BUSINESS_SUBCATEGORY, null, BC_ID + " =?", new String[]{String.valueOf(bcid)}, null, null, null, null);

            /**
             // If you want to execute raw query then uncomment below 2 lines. And comment out above line.

             String SELECT_QUERY = String.format("SELECT %s, %s, %s, %s, %s FROM %s", Config.COLUMN_STUDENT_ID, Config.COLUMN_STUDENT_NAME, Config.COLUMN_STUDENT_REGISTRATION, Config.COLUMN_STUDENT_EMAIL, Config.COLUMN_STUDENT_PHONE, Config.TABLE_STUDENT);
             cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
             */

            if (cursor != null)
                if (cursor.moveToFirst()) {
                    List<SubCatModeDAO> milestoneList = new ArrayList<>();
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Constant.ID));
                        int bc_id = cursor.getInt(cursor.getColumnIndex(BC_ID));
                        String subcategory_name = cursor.getString(cursor.getColumnIndex(Constant.SUBCATEGORY_NAME));
                        String subcategory_name_hindi = cursor.getString(cursor.getColumnIndex(Constant.SUBCATEGORY_NAME_HINDI));
                        String checked_status = "" + getUserSubCatCheck(user_id, id);
                        String image_path = cursor.getString(cursor.getColumnIndex(Constant.IMAGE_PATH));
                        milestoneList.add(new SubCatModeDAO("" + id, "" + bc_id, subcategory_name, subcategory_name_hindi, checked_status, image_path));
                    } while (cursor.moveToNext());

                    return milestoneList;
                }
        } catch (Exception e) {
            //Logger.d("Exception: " + e.getMessage());
            //  Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            // sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public int getUserSubCatCheck(int user_id, int mid) {
        int sum = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {
            int status = 1;
            String sumQuery = "select count(" + USER_ID + ") as Total from " + TABLE_TBL_USER_SUBCATGEORY + " where " + SUBBC_ID + "= " + mid + " and " + USER_ID + " = " + user_id + "" + " and " + STATUS + " = " + status + "";
            cursor = sqLiteDatabase.rawQuery(sumQuery, null);
            if (cursor.moveToFirst())
                sum = cursor.getInt(cursor.getColumnIndex("Total"));
        } catch (Exception e) {
            //Logger.d("Exception: " + e.getMessage());
            //  Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            // sqLiteDatabase.close();
        }
        return sum;
    }

    public int getUserCheck(int user_id) {
        int sum = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            String sumQuery = "select count(" + USER_ID + ") as Total from " + TABLE_TBL_USER_SUBCATGEORY + " where " + USER_ID + "= " + user_id + " and " + STATUS + " =1";
            cursor = sqLiteDatabase.rawQuery(sumQuery, null);
            if (cursor.moveToFirst())
                sum = cursor.getInt(cursor.getColumnIndex("Total"));
        } catch (Exception e) {
            //Logger.d("Exception: " + e.getMessage());
            //  Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            // sqLiteDatabase.close();
        }
        return sum;
    }

    public long UpdateUserSubCat(int sid, int maincat_id, int user_id, int status) {
        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        contentValues.put(SYNC_STATUS, 0);
        try {
            if (getUserSubCatCheck(sid) > 0) {
                rowCount = sqLiteDatabase.update(Constant.TABLE_TBL_USER_SUBCATGEORY, contentValues, Constant.SUBBC_ID + " = ? ", new String[]{String.valueOf(sid)});
            } else {
                Date today = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                String dateToStr = format.format(today);
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(Constant.ID, getMaxNo() + 1);
                contentValues1.put(Constant.USER_ID, user_id);
                contentValues1.put(Constant.SUBBC_ID, sid);
                contentValues1.put(Constant.MAINCAT_ID, maincat_id);
                contentValues1.put(Constant.STATUS, status);
                contentValues1.put(Constant.SYNC_STATUS, 0);
                contentValues1.put(Constant.CREATE_AT, dateToStr);
                rowCount = sqLiteDatabase.insertOrThrow(Constant.TABLE_TBL_USER_SUBCATGEORY, null, contentValues1);

            }
        } catch (SQLiteException e) {
            // Logger.d("Exception: " + e.getMessage());
            // Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            // sqLiteDatabase.close();
        }

        return rowCount;
    }

    public long UpdateUserAllSubCat(int status, int user_id) {
        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        contentValues.put(SYNC_STATUS, 0);
        try {
            rowCount = sqLiteDatabase.update(Constant.TABLE_TBL_USER_SUBCATGEORY, contentValues, USER_ID + " = ? ", new String[]{String.valueOf(user_id)});

        } catch (SQLiteException e) {
            // Logger.d("Exception: " + e.getMessage());
            // Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            // sqLiteDatabase.close();
        }

        return rowCount;
    }

    public int getUserSubCatCheck(int subbc_id) {
        int sum = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {

            String sumQuery = "select count(" + USER_ID + ") as Total from " + TABLE_TBL_USER_SUBCATGEORY + " where " + SUBBC_ID + "= " + subbc_id;
            cursor = sqLiteDatabase.rawQuery(sumQuery, null);
            if (cursor.moveToFirst())
                sum = cursor.getInt(cursor.getColumnIndex("Total"));
        } catch (Exception e) {
            //Logger.d("Exception: " + e.getMessage());
            //  Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            // sqLiteDatabase.close();
        }
        return sum;
    }

    public int getMaxNo() {
        int sum = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {
            String sumQuery = "select Max(" + ID + ") as Total from " + TABLE_TBL_USER_SUBCATGEORY;
            cursor = sqLiteDatabase.rawQuery(sumQuery, null);
            if (cursor.moveToFirst())
                sum = cursor.getInt(cursor.getColumnIndex("Total"));
        } catch (Exception e) {
            //Logger.d("Exception: " + e.getMessage());
            //  Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            // sqLiteDatabase.close();
        }
        return sum;
    }

    //syncing data
    public Cursor getUnsyncedCatUpdate() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + Constant.TABLE_TBL_USER_SUBCATGEORY + " WHERE " + SYNC_STATUS + " = 0 ";
        Cursor c = sqLiteDatabase.rawQuery(sql, null);
        return c;
    }
}

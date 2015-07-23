package us.bridgeses.minder_placesplugin.provider;

import us.bridgeses.minder_placesplugin.provider.PlacesContract.PlacesEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Manages a local database for place data
 */
public class PlacesDatabaseHelper extends SQLiteOpenHelper {
    /**
     * If the database schema is changed, the database version must be
     * incremented.
     */
    protected static final int DATABASE_VERSION = 2;

    /**
     * Database name.
     */
    public static final String DATABASE_NAME =
            "places.db";

    /**
     * Constructor for PlaceDatabaseHelper.
     *
     * @param context is the calling context
     */
    public PlacesDatabaseHelper(Context context) {
        super(context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION);
    }

    /**
     * Hook method called when Database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Define an SQL string that creates a table to hold Places.
        // Each Place has a list of LongForms in Json.
        if (db == null){
            throw new IllegalStateException("DB does not exist");
        }
        final String SQL_CREATE_PLACE_TABLE =
                "CREATE TABLE "
                        + PlacesEntry.TABLE_NAME + " ("
                        + PlacesEntry._ID + " INTEGER PRIMARY KEY, "
                        + PlacesEntry.COLUMN_NAME + " TEXT NOT NULL, "
                        + PlacesEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                        + PlacesEntry.COLUMN_LONGITUDE + " REAL NOT NULL, "
                        + PlacesEntry.COLUMN_LATITUDE + " REAL NOT NULL, "
                        + PlacesEntry.COLUMN_DISPLAY_ADDRESS + " TEXT NOT NULL "
                        + " );";

        // Create the table.
        db.execSQL(SQL_CREATE_PLACE_TABLE);
    }

    /**
     * Hook method called when Database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "
                + PlacesEntry.TABLE_NAME);
        onCreate(db);
    }
}

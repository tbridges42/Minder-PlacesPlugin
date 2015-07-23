package us.bridgeses.minder_placesplugin.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

/**
 * Created by Tony on 7/14/2015.
 */
public class PlacesDatabaseHelperTest extends AndroidTestCase {

    private RenamingDelegatingContext context;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getContext(), "test_");
    }

    public void testCreateDB() throws Exception {
        PlacesDatabaseHelper dbHelper = new PlacesDatabaseHelper(context);
        assertNotNull(dbHelper);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());
        db.close();
        assertFalse(db.isOpen());
        dbHelper.close();
    }

    public void testInsertandGetData() throws Exception {
        PlacesDatabaseHelper dbHelper = new PlacesDatabaseHelper(context);
        assertNotNull(dbHelper);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());

        ContentValues values = new ContentValues();
        values.put(PlacesContract.PlacesEntry.COLUMN_NAME,"Test name");
        values.put(PlacesContract.PlacesEntry.COLUMN_DESCRIPTION, "Test Description");
        values.put(PlacesContract.PlacesEntry.COLUMN_LATITUDE, 0.000277777778d);
        values.put(PlacesContract.PlacesEntry.COLUMN_LONGITUDE, 0.000277777778d);
        values.put(PlacesContract.PlacesEntry.COLUMN_DISPLAY_ADDRESS, "Test Address");
        long id = db.insert(PlacesContract.PlacesEntry.TABLE_NAME, null, values);

        Cursor cursor = db.query(PlacesContract.PlacesEntry.TABLE_NAME, null, "_id = ?",
                new String[] { Long.toString( id )}, null, null, null);
        assertTrue(cursor.moveToFirst());
        assertEquals(cursor.getString(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_NAME)),
                "Test name");
        assertEquals(cursor.getString(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_DESCRIPTION)),
                "Test Description");
        assertEquals(cursor.getDouble(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_LATITUDE)),
                0.000277777778d);
        assertEquals(cursor.getDouble(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_LONGITUDE)),
                0.000277777778d);
        assertEquals(cursor.getString(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_DISPLAY_ADDRESS)),
                "Test Address");

        cursor.close();
        db.close();
        assertFalse(db.isOpen());
        dbHelper.close();
    }
}
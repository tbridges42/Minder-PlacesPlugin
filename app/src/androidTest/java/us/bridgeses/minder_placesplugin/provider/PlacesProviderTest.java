package us.bridgeses.minder_placesplugin.provider;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;


import java.io.File;

/**
 * Test for PlacesProvider
 */
public class PlacesProviderTest extends ProviderTestCase2 {

    MockContentResolver mResolver;

    public PlacesProviderTest() {
        super(PlacesProvider.class, PlacesContract.CONTENT_AUTHORITY);
    }

    @Override
    protected  void setUp() throws Exception {
        super.setUp();
        mResolver = getMockContentResolver();
    }

    public void testInsertAndDelete() throws Exception {
        ContentValues values = new ContentValues();
        values.put(PlacesContract.PlacesEntry.COLUMN_NAME,"Test name");
        values.put(PlacesContract.PlacesEntry.COLUMN_DESCRIPTION, "Test Description");
        values.put(PlacesContract.PlacesEntry.COLUMN_LATITUDE, 0.000277777778d);
        values.put(PlacesContract.PlacesEntry.COLUMN_LONGITUDE, 0.000277777778d);
        values.put(PlacesContract.PlacesEntry.COLUMN_DISPLAY_ADDRESS, "Test Address");
        Uri address = mResolver.insert(PlacesContract.PlacesEntry.CONTENT_URI, values);
        String[] args = new String[1];
        args[0] = address.getLastPathSegment();
        assertEquals(1, mResolver.delete(PlacesContract.PlacesEntry.CONTENT_URI,
                "_id = ?",
                args));
    }

    public void testGetTypeItems() throws Exception {
        String type = mResolver.getType(PlacesContract.PlacesEntry.CONTENT_URI);
        assertEquals(PlacesContract.PlacesEntry.CONTENT_ITEMS_TYPE, type);
    }

    public void testGetTypeItem() throws Exception {
        String type = mResolver.getType(PlacesContract.PlacesEntry.buildPlaceUri(1l));
        assertEquals(PlacesContract.PlacesEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testInsertAndQuery() throws Exception {
        ContentValues values = new ContentValues();
        values.put(PlacesContract.PlacesEntry.COLUMN_NAME,"Test name");
        values.put(PlacesContract.PlacesEntry.COLUMN_DESCRIPTION, "Test Description");
        values.put(PlacesContract.PlacesEntry.COLUMN_LATITUDE, 0.000277777778d);
        values.put(PlacesContract.PlacesEntry.COLUMN_LONGITUDE, 0.000277777778d);
        values.put(PlacesContract.PlacesEntry.COLUMN_DISPLAY_ADDRESS, "Test Address");
        mResolver.insert(PlacesContract.PlacesEntry.CONTENT_URI, values);

        Cursor cursor = mResolver.query(PlacesContract.PlacesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

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
    }

    public void testBadInsertAndQuery() throws Exception {
        ContentValues values = new ContentValues();
        values.put(PlacesContract.PlacesEntry.COLUMN_NAME,"Test name");
        values.put(PlacesContract.PlacesEntry.COLUMN_DESCRIPTION, "Test Description");
        values.put(PlacesContract.PlacesEntry.COLUMN_LATITUDE, 0.000277777778d);
        values.put(PlacesContract.PlacesEntry.COLUMN_LONGITUDE, 0.000277777778d);
        values.put(PlacesContract.PlacesEntry.COLUMN_DISPLAY_ADDRESS, "Test Address");
        try {
            mResolver.insert(Uri.withAppendedPath(PlacesContract.PlacesEntry.CONTENT_URI,
                    "/root"), values);
            fail("Allowed incorrect URI");
        } catch (UnsupportedOperationException e) {
            assertEquals("Unknown uri: " + Uri.withAppendedPath(PlacesContract.PlacesEntry.CONTENT_URI,
                    "/root"), e.getMessage());
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
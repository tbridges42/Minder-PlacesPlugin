package us.bridgeses.minder_placesplugin.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PlacesProvider extends ContentProvider {
    /**
     * Debugging tag used by the Android logger.
     */
    @SuppressWarnings("unused")
    private static final String TAG =
            PlacesProvider.class.getSimpleName();

    Context context;

    private PlacesDatabaseHelper dbHelper;

    /**
     * The code that is returned when a URI for more than 1 items is
     * matched against the given components.  Must be positive.
     */
    private static final int PLACES = 100;

    /**
     * The code that is returned when a URI for exactly 1 item is
     * matched against the given components.  Must be positive.
     */
    private static final int PLACE = 101;

    /**
     * The URI Matcher used by this content provider.
     */
    private static final UriMatcher sUriMatcher =
            buildUriMatcher();

    /**
     * Helper method to match each URI to the PLACE integers
     * constant defined above.
     *
     * @return UriMatcher
     */
    private static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code
        // to return when a match is found.  The code passed into the
        // constructor represents the code to return for the rootURI.
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher =
                new UriMatcher(UriMatcher.NO_MATCH);

        // For each type of URI that is added, a corresponding code is
        // created.
        matcher.addURI(PlacesContract.CONTENT_AUTHORITY,
                PlacesContract.PATH_PLACE,
                PLACES);
        matcher.addURI(PlacesContract.CONTENT_AUTHORITY,
                PlacesContract.PATH_PLACE
                        + "/#",
                PLACE);
        return matcher;
    }

    public PlacesProvider() {
    }

    public PlacesProvider(Context context){
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(PlacesContract.PlacesEntry.TABLE_NAME,
                selection,
                selectionArgs);
        getContext().getContentResolver().notifyChange(uri,
                null);
        return result;
    }

    @Override
    public String getType(Uri uri) {
        // Use Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        // Match the id returned by UriMatcher to return appropriate
        // MIME_TYPE.
        switch (match) {
            case PLACES:
                return PlacesContract.PlacesEntry.CONTENT_ITEMS_TYPE;
            case PLACE:
                return PlacesContract.PlacesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: "
                        + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Create and/or open a database that will be used for reading
        // and writing. Once opened successfully, the database is
        // cached, so you can call this method every time you need to
        // write to the database.
        final SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        Uri returnUri;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert a new
        // row.
        switch (sUriMatcher.match(uri)) {
            case PLACES:
                long id = db.insert(PlacesContract.PlacesEntry.TABLE_NAME,
                        null,
                        values);

                // Check if a new row is inserted or not.
                if (id > 0)
                    returnUri =
                            PlacesContract.PlacesEntry.buildPlaceUri(id);
                else
                    throw new android.database.SQLException
                            ("Failed to insert row into "
                                    + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "
                        + uri);
        }

        // Notifies registered observers that a row was inserted.
        getContext().getContentResolver().notifyChange(uri,
                null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        dbHelper =
                new PlacesDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor result;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Match the id returned by UriMatcher to query appropriate
        // rows.
        switch (sUriMatcher.match(uri)) {
            case PLACES:
                result = db.query(PlacesContract.PlacesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PLACE:
                // Selection clause that matches row id with id passed
                // from Uri.
                final String rowId =
                        ""
                                + PlacesContract.PlacesEntry._ID
                                + " = '"
                                + ContentUris.parseId(uri)
                                + "'";

                result = db.query(PlacesContract.PlacesEntry.TABLE_NAME,
                        projection,
                        rowId,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "
                        + uri);
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // Create and/or open a database that will be used for reading
        // and writing. Once opened successfully, the database is
        // cached, so you can call this method every time you need to
        // write to the database.
        final SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        int rowsUpdated;

        // Try to match against the path in a uri.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If a match occurs update the
        // appropriate rows.
        switch (sUriMatcher.match(uri)) {
            case PLACE:
                // Updates the rows in the Database and returns no of rows
                // updated.
                rowsUpdated = db.update(PlacesContract.PlacesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case PLACES:
                // Updates the rows in the Database and returns no of rows
                // updated.
                rowsUpdated = db.update(PlacesContract.PlacesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "
                        + uri);
        }

        // Notifies registered observers that rows were updated.
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri,
                    null);
        return rowsUpdated;
    }
}

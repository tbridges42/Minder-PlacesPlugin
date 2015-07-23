package us.bridgeses.minder_placesplugin.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the Place database.
 */
public class PlacesContract {
    /**
     * The "Content authority" is a name for the entire content
     * provider, similar to the relationship between a domain name and
     * its website.  A convenient string to use for the content
     * authority is the package name for the app, which must be unique
     * on the device.
     */
    public static final String CONTENT_AUTHORITY =
            "us.bridgeses.places_provider";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's that apps
     * will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://"
                    + CONTENT_AUTHORITY);

    /**
     * Possible paths (appended to base content URI for possible
     * URI's), e.g., content://vandy.mooc/place/ is a valid path for
     * Place data. However, content://vandy.mooc/givemeroot/ will
     * fail since the ContentProvider hasn't been given any
     * information on what to do with "givemeroot".
     */
    public static final String PATH_PLACE =
            PlacesEntry.TABLE_NAME;

    /**
     * Inner class that defines the contents of the Place table.
     */
    public static final class PlacesEntry implements BaseColumns {
        /**
         * Use BASE_CONTENT_URI to create the unique URI for Place
         * Table that apps will use to contact the content provider.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendPath(PATH_PLACE).build();

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 0..x items.
         */
        public static final String CONTENT_ITEMS_TYPE =
                "vnd.android.cursor.dir/"
                        + CONTENT_AUTHORITY
                        + "/"
                        + PATH_PLACE;

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 1 item.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/"
                        + CONTENT_AUTHORITY
                        + "/"
                        + PATH_PLACE;

        /**
         * Name of the database table.
         */
        public static final String TABLE_NAME =
                "places_table";

        /**
         * Columns to store Data of each Place Expansion.
         */
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_DISPLAY_ADDRESS = "display_address";

        /**
         * Return a Uri that points to the row containing a given id.
         *
         * @param id the id of the row to be pointed to
         * @return Uri with path to the row
         */
        public static Uri buildPlaceUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI,
                    id);
        }
    }
}

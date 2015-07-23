package us.bridgeses.minder_placesplugin.provider;

import android.net.Uri;
import android.test.AndroidTestCase;


/**
 * Unit test for PlacesEntry
 */
public class PlacesEntryTest extends AndroidTestCase {

    public void testBuildPlaceUri() throws Exception {
        assertEquals("URI builder failed", PlacesContract.PlacesEntry.buildPlaceUri(5l),
                Uri.parse("content://us.bridgeses.places_provider/places_table/5"));
    }
}
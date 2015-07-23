package us.bridgeses.minder_placesplugin.provider;


import android.database.Cursor;

/**
 * Created by Tony on 7/20/2015.
 */
public class PlacesModel {
    private String name;
    private String description;
    private String address;
    private double longitude;
    private double latitude;
    private long id;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public long getId() {
        return id;
    }

    public PlacesModel(String name, String description, String address,
                       double longitude, double latitude, long id) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
    }

    public PlacesModel(Cursor cursor) {
        cursor.moveToFirst();
        this.name = cursor.getString(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_NAME));
        this.description = cursor.getString(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_DESCRIPTION));
        this.address = cursor.getString(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_DISPLAY_ADDRESS));
        this.longitude = cursor.getDouble(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_LONGITUDE));
        this.latitude = cursor.getDouble(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_LATITUDE));
        this.id = cursor.getLong(cursor.getColumnIndex(PlacesContract.PlacesEntry._ID));
    }
}

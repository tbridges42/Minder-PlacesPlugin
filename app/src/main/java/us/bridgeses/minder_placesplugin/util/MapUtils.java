package us.bridgeses.minder_placesplugin.util;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import us.bridgeses.minder_placesplugin.R;
import us.bridgeses.minder_placesplugin.provider.PlacesContract;

/**
 * Created by Tony on 7/19/2015.
 */
public class MapUtils implements GoogleMap.OnMapClickListener {

    private Context context;
    public Marker marker;
    private GoogleMap googleMap;
    private View detailView;
    private TextView addressText;

    public MapUtils(Context context,
                    Marker marker,
                    GoogleMap googleMap,
                    View detailView){
        this.context = context;
        this.marker = marker;
        this.googleMap = googleMap;
        this.detailView = detailView;
        addressText = (TextView)detailView.findViewById(R.id.address);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("Geo Plugin", "Entered onMapClick");
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addresses.size() == 0){
                return;
            }
            Address address = addresses.get(0);
            setMarker(latLng);
            StringBuilder builder = new StringBuilder();
            builder.append(address.getAddressLine(0));
            for (int i=1; i < address.getMaxAddressLineIndex(); i++) {
                builder.append(", ");
                builder.append(address.getAddressLine(i));
            }
            addressText.setText(builder.toString());
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMarker(LatLng location) {
        if (marker != null){
            marker.remove();
            marker = null;
        }
        marker = googleMap.addMarker(new MarkerOptions().position(location));
        marker.setDraggable(true);
    }

    public void initMap(Cursor locations) {
        locations.moveToFirst();
        LatLng location = new LatLng(locations.getDouble(locations.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_LATITUDE)),
                locations.getDouble(locations.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_LONGITUDE)));
        setMarker(location);
        if (addressText != null){
            addressText.setText(locations.getString(locations.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_DISPLAY_ADDRESS)));
        }
    }

    public void moveTo(LatLng location) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location,10);
        googleMap.animateCamera(update);
    }
}

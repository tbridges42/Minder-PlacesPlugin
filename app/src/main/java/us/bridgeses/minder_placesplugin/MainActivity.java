package us.bridgeses.minder_placesplugin;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import us.bridgeses.minder_placesplugin.provider.PlacesContract;
import us.bridgeses.minder_placesplugin.provider.PlacesModel;
import us.bridgeses.minder_placesplugin.provider.PlacesProvider;
import us.bridgeses.minder_placesplugin.util.MapUtils;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        View.OnClickListener, MainActivityFragment.OnAddressSelectedListener,
        PlacePickerFragment.OnPlacePickedListener{

    private GoogleMap googleMap;
    private Marker marker;
    private TextView addressText;
    private MenuItem addView;
    private MapUtils mapUtils;
    private Cursor location;

    private void addMap(@Nullable PlacesModel place){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        PlacePickerFragment mapFragment = new PlacePickerFragment();
        mapFragment.setPlace(place);
        mapFragment.setOnPlacePickedListener(this);
        transaction.addToBackStack("Map");
        transaction.replace(R.id.fragment, mapFragment, "Tag");
        transaction.commit();
    }

    private void addList(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        MainActivityFragment fragment = new MainActivityFragment();
        fragment.setOnAddressSelectedListener(this);
        transaction.addToBackStack("List");
        transaction.replace(R.id.fragment, fragment, "List");
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //addressText = (TextView) findViewById(R.id.address);
        //findViewById(R.id.ok).setOnClickListener(this);
        //addMap(null);
        addList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        /**/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Geo Plugin", "Entered onMapReady");
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mapUtils = new MapUtils(this,
                marker,
                googleMap,
                findViewById(R.id.button_container));
        googleMap.setOnMapClickListener(mapUtils);
        if (location != null){
            mapUtils.initMap(location);
        }
    }

    private void addPlace(){
        ContentValues values = new ContentValues();
        values.put(PlacesContract.PlacesEntry.COLUMN_DISPLAY_ADDRESS,String.valueOf(addressText.getText()));
        values.put(PlacesContract.PlacesEntry.COLUMN_LATITUDE, marker.getPosition().latitude);
        values.put(PlacesContract.PlacesEntry.COLUMN_LONGITUDE, marker.getPosition().longitude);
        values.put(PlacesContract.PlacesEntry.COLUMN_NAME, "Test");
        values.put(PlacesContract.PlacesEntry.COLUMN_DESCRIPTION, "Test Description");
        getContentResolver().insert(PlacesContract.PlacesEntry.CONTENT_URI, values);
        Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.ok:{
                addPlace();
            }
            case R.id.cancel: {
                getFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void onAddressSelected(PlacesModel place) {
        addMap(place);
    }

    @Override
    public void PlacePicked() {
        addList();
    }

    @Override
    public void Cancelled() {
        addList();
    }
}

package us.bridgeses.minder_placesplugin;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import us.bridgeses.minder_placesplugin.provider.PlacesContract;
import us.bridgeses.minder_placesplugin.provider.PlacesModel;
import us.bridgeses.minder_placesplugin.util.MapUtils;

/**
 * Created by Tony on 7/19/2015.
 */
public class PlacePickerFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener,
        View.OnFocusChangeListener, Animation.AnimationListener, SearchView.OnQueryTextListener {

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("Geo Plugin", "Entered onQueryTextSubmit");
        searchView.clearFocus();
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(query,1);
            if (addresses.size() == 0){
                return false;
            }
            Address address = addresses.get(0);
            LatLng location = new LatLng(address.getLatitude(),address.getLongitude());
            mapUtils.setMarker(location);
            StringBuilder builder = new StringBuilder();
            builder.append(address.getAddressLine(0));
            for (int i=1; i < address.getMaxAddressLineIndex(); i++) {
                builder.append(", ");
                builder.append(address.getAddressLine(i));
            }
            ((TextView)infoContainer.findViewById(R.id.address)).setText(builder.toString());
            mapUtils.moveTo(location);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public interface OnPlacePickedListener {
        void PlacePicked();
        void Cancelled();
    }

    Activity activity;
    private MapUtils mapUtils;
    private View detailView;
    private View infoContainer;
    private float height;
    private PlacesModel place;
    private OnPlacePickedListener listener;
    private SearchView searchView;

    public void setPlace(PlacesModel place) {
        this.place = place;
        if (mapUtils != null){
            mapUtils.setMarker(new LatLng(place.getLatitude(),
                    place.getLongitude()));
            mapUtils.moveTo(new LatLng(place.getLatitude(),
                    place.getLongitude()));
        }
        if (infoContainer != null) {
            ((TextView)infoContainer.findViewById(R.id.address)).setText(place.getAddress());
            ((EditText)infoContainer.findViewById(R.id.detail_view_name)).setText(place.getName());
            ((EditText)infoContainer.findViewById(R.id.detail_view_description)).setText(place.getDescription());
        }
    }

    public void setOnPlacePickedListener(OnPlacePickedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Geo Plugin", "Entered onMapReady");
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mapUtils = new MapUtils(activity,
                null,
                googleMap,
                activity.findViewById(R.id.button_container));
        googleMap.setOnMapClickListener(mapUtils);
        if (place != null) {
            if (mapUtils != null){
                mapUtils.setMarker(new LatLng(place.getLatitude(),
                        place.getLongitude()));
                mapUtils.moveTo(new LatLng(place.getLatitude(),
                        place.getLongitude()));
            }
            if (infoContainer != null) {
                ((TextView)infoContainer.findViewById(R.id.address)).setText(place.getAddress());
                ((EditText)infoContainer.findViewById(R.id.detail_view_name)).setText(place.getName());
                ((EditText)infoContainer.findViewById(R.id.detail_view_description)).setText(place.getDescription());
            }
        }
    }

    private void addMap(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        MapFragment mapFragment = MapFragment.newInstance();
        transaction.addToBackStack("Map");
        transaction.add(R.id.map_frame, mapFragment, "Tag");
        transaction.commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map, menu);
        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_picker, container, false);
        addMap();
        detailView = view.findViewById(R.id.detail_view);
        infoContainer = view.findViewById(R.id.info_container);
        final ViewTreeObserver observer= infoContainer.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                height = infoContainer.findViewById(R.id.detail_view).getHeight();
                Log.d("Picker", Float.toString(height));
                infoContainer.findViewById(R.id.detail_view).setVisibility(View.GONE);
                infoContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        view.findViewById(R.id.button_container).setOnClickListener(this);
        view.findViewById(R.id.ok).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        view.findViewById(R.id.detail_view_name).setOnFocusChangeListener(this);
        view.findViewById(R.id.detail_view_description).setOnFocusChangeListener(this);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        height = infoContainer.findViewById(R.id.detail_view).getHeight();
        Log.d("Picker", Float.toString(height));
        //infoContainer.findViewById(R.id.detail_view).setVisibility(View.GONE);
    }

    private void toggleDetails(){
        boolean visible = detailView.isShown();
        RotateAnimation rotate;
        TranslateAnimation slide;
        TranslateAnimation displace;
        if (visible) {
            rotate = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            slide = new TranslateAnimation(0,0,0,100);
            slide.setAnimationListener(this);
            displace = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, 0, Animation.ABSOLUTE, height);
        }
        else {
            rotate = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            slide = new TranslateAnimation(0,0,100,0);
            detailView.setVisibility(View.VISIBLE);
            displace = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, height, Animation.ABSOLUTE, 0);
        }

        displace.setDuration(250l);
        displace.setRepeatCount(0);
        infoContainer.findViewById(R.id.button_container).startAnimation(displace);
        slide.setDuration(250l);
        slide.setRepeatCount(0);
        slide.setFillAfter(true);
        detailView.startAnimation(slide);
        rotate.setDuration(250l);
        rotate.setRepeatCount(0);
        rotate.setFillAfter(true);
        infoContainer.findViewById(R.id.drawer_indicator).startAnimation(rotate);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.button_container: {
                toggleDetails();
                break;
            }
            case R.id.ok: {
                addPlace();
                if (listener != null) {
                    listener.PlacePicked();
                }
                break;
            }
            case R.id.cancel: {
                if (listener != null) {
                    listener.Cancelled();
                }
                break;
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if ((v instanceof EditText) && !hasFocus) {
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        detailView.setVisibility(View.GONE);
        detailView.getAnimation().setAnimationListener(null);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void addPlace(){
        ContentValues values = new ContentValues();
        values.put(PlacesContract.PlacesEntry.COLUMN_DISPLAY_ADDRESS,
                String.valueOf(((TextView) infoContainer.findViewById(R.id.address)).getText()));
        values.put(PlacesContract.PlacesEntry.COLUMN_LATITUDE, mapUtils.marker.getPosition().latitude);
        values.put(PlacesContract.PlacesEntry.COLUMN_LONGITUDE, mapUtils.marker.getPosition().longitude);
        values.put(PlacesContract.PlacesEntry.COLUMN_NAME,
                String.valueOf(((EditText) infoContainer.findViewById(R.id.detail_view_name)).getText()));
        values.put(PlacesContract.PlacesEntry.COLUMN_DESCRIPTION,
                String.valueOf(((EditText) infoContainer.findViewById(R.id.detail_view_description)).getText()));
        if (place == null){
            activity.getContentResolver().insert(PlacesContract.PlacesEntry.CONTENT_URI, values);
        }
        else {
            activity.getContentResolver().update(PlacesContract.PlacesEntry.CONTENT_URI, values,
                    "_id = ?", new String[]{Long.toString(place.getId())});
        }
        Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show();
    }
}

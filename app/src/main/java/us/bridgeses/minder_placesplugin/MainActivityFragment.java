package us.bridgeses.minder_placesplugin;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import us.bridgeses.minder_placesplugin.provider.PlacesContract;
import us.bridgeses.minder_placesplugin.provider.PlacesModel;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ExpandableListView.OnGroupExpandListener, ExpandableListView.OnChildClickListener{

    public interface OnAddressSelectedListener {
        void onAddressSelected(PlacesModel place);
    }

    Context context;
    ExpandableListView list;
    PlacesTreeAdapter adapter;
    private int selection = -1;
    private OnAddressSelectedListener listener;

    public static final String[] groupDisplayColumns = new String[] {
            PlacesContract.PlacesEntry.COLUMN_NAME
    };

    public static final int[] groupDisplayViews = new int[] {
            R.id.list_name
    };

    public static final String[] childDisplayColumns = new String[] {
            PlacesContract.PlacesEntry.COLUMN_DESCRIPTION,
            PlacesContract.PlacesEntry.COLUMN_DISPLAY_ADDRESS
    };

    public static final int[] childDisplayViews = new int[] {
            R.id.detail_description,
            R.id.detail_address
    };

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Loader<Cursor> loader = getLoaderManager().getLoader(0);
        if ((loader != null) && (!loader.isReset())) {
            getLoaderManager().restartLoader(0, null, this);
        }
        else {
            getLoaderManager().initLoader(0, null, this);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_new: {
                if (listener != null) {
                    listener.onAddressSelected(null);
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        list = (ExpandableListView)view.findViewById(R.id.list);
        adapter = new PlacesTreeAdapter(context,
                R.layout.place_item,
                groupDisplayColumns,
                groupDisplayViews,
                R.layout.place_detail,
                childDisplayColumns,
                childDisplayViews);
        list.setAdapter(adapter);
        list.setOnGroupExpandListener(this);
        list.setOnChildClickListener(this);
        return view;
    }

    public void setOnAddressSelectedListener(OnAddressSelectedListener listener){
        this.listener = listener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(context,
                PlacesContract.PlacesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.setGroupCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onGroupExpand(int groupPosition) {
        if (selection != groupPosition){
            if (selection != -1){
                list.collapseGroup(selection);
            }
            selection = groupPosition;
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (listener != null){
            // TODO: Move off main thread
            Cursor place = context.getContentResolver().query(PlacesContract.PlacesEntry.CONTENT_URI,
                    null,
                    "_id = ?",
                    new String[]{String.valueOf(id)},
                    null);
            PlacesModel model = new PlacesModel(place);
            listener.onAddressSelected(model);
        }
        Log.d("onChildClick",Long.toString(id));
        return false;
    }

    // --------------------------------------------------------------------------

    private class PlacesTreeAdapter extends SimpleCursorTreeAdapter{

        private Cursor childCursor;
        private GoogleMap googleMap;
        LatLng location;

        public PlacesTreeAdapter(Context context, int collapsedGroupLayout,
                                 String[] groupFrom, int[] groupTo, int childLayout,
                                 String[] childFrom, int[] childTo) {
            super(context, null, collapsedGroupLayout, groupFrom, groupTo,
                    childLayout, childFrom, childTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            childCursor = context.getContentResolver().query(PlacesContract.PlacesEntry.CONTENT_URI,
                    null,
                    "_id = ?",
                    new String[]{ groupCursor.getString(groupCursor.getColumnIndex(
                            PlacesContract.PlacesEntry._ID
                    )) },
                    null);
            Log.d("ListFragment",groupCursor.getString(groupCursor.getColumnIndex(
                    PlacesContract.PlacesEntry._ID)));
            return childCursor;
        }

        /*@Override
        protected void bindChildView(@NonNull View view, Context context, @NonNull Cursor cursor, boolean isLastChild) {
            super.bindChildView(view,context,cursor,isLastChild);
            view.setOnClickListener((MainActivity)context); // TODO: this is hacky, fix it
            MapView mapView = (MapView)view.findViewById(R.id.mapview);
            location = new LatLng(cursor.getDouble(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_LONGITUDE)));
            mapView.getMapAsync(this);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            this.googleMap = googleMap;
            Marker marker = googleMap.addMarker(new MarkerOptions().position(location));
        }*/
    }
}

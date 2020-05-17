package space.sociallydistanced;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "search field" ;
    GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    PlacesClient placesClient;

    String placeID;
    JSONObject results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apikey = "";

        if(!Places.isInitialized()) {
            Places.initialize(MainActivity.this, apikey);
        }


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteSupportFragment.setOnPlaceSelectedListener(
                new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(@NonNull Place place) {
                        placeID = place.getId();
                        new GetJSONTask().execute(placeID);
                        Log.d(TAG, "it worked!!!!! 4am");
                        Log.d(TAG, String.valueOf(results));

                        final LatLng latLng = place.getLatLng();
                        map.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    }

                    @Override
                    public void onError(@NonNull Status status) {
                    }
                }
        );

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(53, -3), 5);
        map.animateCamera(center);
    }

    // Uses AsyncTask to create a task away from the main UI thread(For Avoid
    // NetworkOnMainThreadException). This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the
    // connection
    // has been established, the AsyncTask downloads the contents of the data as
    // an InputStream. Than, the InputStream is converted into a string, which
    // is
    // displayed in the TextView by the AsyncTask's onPostExecute method. Which
    // called after doInBackgroud Complete
    private class GetJSONTask extends AsyncTask {
        private ProgressDialog pd;


        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String data = Utility.getData((String) objects[0]);
                onPostExecute(data);
                Log.d(TAG, "data: " + data);
                results = new JSONObject(data);
                Log.d(TAG, "json: " + results);
                return data;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(MainActivity.this, "", "Loading", true,
                    false); // Create and show Progress dialog
        }

        protected void onPostExecute(String result) throws JSONException {
            pd.dismiss();
        }

    }

}

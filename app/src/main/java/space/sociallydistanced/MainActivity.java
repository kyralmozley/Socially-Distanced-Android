package space.sociallydistanced;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteFragment;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    Context context = this;

    private static final String TAG = "search field" ;
    GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    PlacesClient placesClient;
    LinearLayout linearLayout;

    String placeID;
    JSONObject results;
    public JSONObject json;

    Boolean done = false;
    int prediction = -1;

    // define colour scheme
    int red = Color.parseColor("#E14A4E");
    int orange = Color.parseColor("#EE8632");
    int yellow = Color.parseColor("#F7C117");
    int lime = Color.parseColor("#9CBD38");
    int green = Color.parseColor("#4FB373");

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apikey = new APIKey().getAPIKey();

        if(!Places.isInitialized()) {
            Places.initialize(MainActivity.this, apikey);
        }

        linearLayout = (LinearLayout) this.findViewById(R.id.overlay);
        final TextView predictionView = (TextView) findViewById(R.id.distanceText);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        final Marker[] marker = new Marker[1];
        placesClient = Places.createClient(this);


        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.getView().setBackgroundColor(Color.WHITE);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteSupportFragment.setCountry("GB");

        autocompleteSupportFragment.setOnPlaceSelectedListener(

                new PlaceSelectionListener() {

                    @Override
                    public void onPlaceSelected(@NonNull Place place) {
                        linearLayout.setVisibility(LinearLayout.GONE);
                        predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_round));
                        predictionView.setText("");

                        placeID = place.getId();
                        new GetJSONTask().execute(placeID);

                        final LatLng latLng = place.getLatLng();

                        if(marker[0] != null) {
                            // only want to show one marker
                            marker[0].remove();
                        }
                        marker[0] = map.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        // get data
                        while (!done) {
                            try {
                                TimeUnit.MICROSECONDS.sleep(200);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                        linearLayout.setVisibility(LinearLayout.VISIBLE);

                        switch (prediction) {
                            case -1:

                            case 0:
                                predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.greenbackground));
                                predictionView.setText("You can safely social distance at this location");
                                break;
                            case 1:
                                predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.limebackground));
                                predictionView.setText("You should be able to safely social distance at this location");
                                break;
                            case 2:
                                predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.yellowbackground));
                                predictionView.setText("You may be able to safely social distance at this location");
                                break;
                            case 3:
                                predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.orangebackground));
                                predictionView.setText("It is unlikely that you will be able to safely social distance");
                                break;
                            case 4:
                                predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.redbackground));
                                predictionView.setText("You cannot safely social distance at this location");
                                break;
                        }

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

    private class GetJSONTask extends AsyncTask {
        private ProgressDialog pd;

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String data = Utility.getData((String) objects[0]);
                results = new JSONObject(data);
                onPostExecute(data);
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
            Log.d(TAG, "response" + results);
            done = true;
            informationHandler informationHandler = new informationHandler();
            prediction = informationHandler.getRaiting(results);
        }

    }


}

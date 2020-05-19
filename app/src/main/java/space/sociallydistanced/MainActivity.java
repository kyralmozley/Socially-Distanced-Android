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
import android.icu.text.IDNA;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.security.keystore.UserNotAuthenticatedException;
import android.speech.tts.TextToSpeech;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.CameraPosition;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    Context context = this;

    private static final String TAG = "doesThisWork" ;
    GoogleMap map;
    SupportMapFragment mapFragment;
    PlacesClient placesClient;
    LinearLayout linearLayout;
    LinearLayout linearLayout2;
    LinearLayout linearLayout3;
    LinearLayout linearLayout4;

    BarChart barChart;
    BarData barData;
    MyBarDataSet barDataSet;
    ArrayList barEntries;

    String placeID;
    JSONObject results;

    Boolean done = false;
    int prediction = -1;
    int queue = -1;

    informationHandler informationHandler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apikey = new APIKey().getAPIKey();

        if(!Places.isInitialized()) {
            Places.initialize(MainActivity.this, apikey);
        }

        linearLayout = (LinearLayout) this.findViewById(R.id.overlay);
        linearLayout2 = (LinearLayout) this.findViewById(R.id.overlay2);
        linearLayout3 = (LinearLayout) this.findViewById(R.id.feedbackButtons);
        linearLayout4 = (LinearLayout) this.findViewById(R.id.noButtons);

        final TextView predictionView = (TextView) findViewById(R.id.distanceText);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        final Marker[] marker = new Marker[1];
        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteSupportFragment.setCountry("GB");

        // define all of the buttons
        Button feedbackButtonYes = (Button) findViewById(R.id.feedbackYes);
        Button feedbackButtonNo = (Button) findViewById(R.id.feedbackNo);
        Button feedback1 = (Button) findViewById(R.id.feedback1);
        Button feedback2 = (Button) findViewById(R.id.feedback2);
        Button feedback3 = (Button) findViewById(R.id.feedback3);
        Button feedback4 = (Button) findViewById(R.id.feedback4);
        Button feedback5 = (Button) findViewById(R.id.feedback5);

        // define all of the buttons on click functions
        feedbackButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedbackYesClick();
            }
        });
        feedbackButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedbackClickNo();
            }
        });
        feedback1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback(1);
            }
        });
        feedback2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback(2);
            }
        });
        feedback3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback(3);
            }
        });
        feedback4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback(4);
            }
        });
        feedback5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback(5);
            }
        });


                autocompleteSupportFragment.setOnPlaceSelectedListener(

                        new PlaceSelectionListener() {

                            @Override
                            public void onPlaceSelected(@NonNull Place place) {
                                linearLayout.setVisibility(LinearLayout.GONE);
                                linearLayout2.setVisibility(LinearLayout.GONE);

                                predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_round));
                                predictionView.setText("");

                                placeID = place.getId();
                                new GetJSONTask().execute(placeID);

                                final LatLng latLng = place.getLatLng();

                                if (marker[0] != null) {
                                    // only want to show one marker
                                    marker[0].remove();
                                }
                                marker[0] = map.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                                //super duper hacky to get it to be slightly off center
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude - 0.004, latLng.longitude), 15));

                                // get data
                                while (!done) {
                                    try {
                                        TimeUnit.MICROSECONDS.sleep(100);

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                resetFeedbackButtons();
                                linearLayout.setVisibility(LinearLayout.VISIBLE);
                                linearLayout2.setVisibility(LinearLayout.VISIBLE);

                                if (queue == -1) {
                                    switch (prediction) {
                                        // no queue data, show social distancing prediction.
                                        case -1:
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.closedbackground));
                                            predictionView.setTextColor(Color.parseColor("#FFFFFF"));
                                            predictionView.setText("This location seems to be closed");
                                            break;
                                        case 0:
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.greenbackground));
                                            predictionView.setTextColor(Color.parseColor("#FFFFFF"));
                                            predictionView.setText("You can safely social distance at this location");
                                            break;
                                        case 1:
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.limebackground));
                                            predictionView.setTextColor(Color.parseColor("#000000"));
                                            predictionView.setText("You should be able to safely social distance at this location");
                                            break;
                                        case 2:
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.yellowbackground));
                                            predictionView.setTextColor(Color.parseColor("#000000"));
                                            predictionView.setText("You may be able to safely social distance at this location");
                                            break;
                                        case 3:
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.orangebackground));
                                            predictionView.setTextColor(Color.parseColor("#000000"));
                                            predictionView.setText("It is unlikely that you will be able to safely social distance");
                                            break;
                                        case 4:
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.redbackground));
                                            predictionView.setTextColor(Color.parseColor("#FFFFFF"));
                                            predictionView.setText("You cannot safely social distance at this location");
                                            break;
                                    }
                                } else {
                                    switch (queue) {
                                        case 0:
                                            predictionView.append("There is little to no queue for entry");
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.greenbackground));
                                            predictionView.setTextColor(Color.parseColor("#FFFFFF"));
                                            break;
                                        case 1:
                                            predictionView.append("It is unlikely that you will have to queue for entry");
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.limebackground));
                                            predictionView.setTextColor(Color.parseColor("#000000"));
                                            break;
                                        case 2:
                                            predictionView.append("There is likely to be a short queue for entry");
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.yellowbackground));
                                            predictionView.setTextColor(Color.parseColor("#000000"));
                                            break;
                                        case 3:
                                            predictionView.append("You will have to queue for entry");
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.orangebackground));
                                            predictionView.setTextColor(Color.parseColor("#000000"));
                                            break;
                                        case 4:
                                            predictionView.append("There is likely a long queue for entry");
                                            predictionView.setBackground(ContextCompat.getDrawable(context, R.drawable.redbackground));
                                            predictionView.setTextColor(Color.parseColor("#FFFFFF"));
                                            break;
                                    }
                                }

                                barChart = findViewById(R.id.barchart);
                                try {
                                    getEntries();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                barDataSet = new MyBarDataSet(barEntries, "");
                                barDataSet.setDrawValues(false);

                                barDataSet.setColors(ContextCompat.getColor(context, R.color.myRed),
                                        ContextCompat.getColor(context, R.color.myOrange),
                                        ContextCompat.getColor(context, R.color.myLime),
                                        ContextCompat.getColor(context, R.color.myYellow),
                                        ContextCompat.getColor(context, R.color.myGreen));

                                barData = new BarData(barDataSet);
                                barChart.setData(barData);

                                XAxis xAxis = barChart.getXAxis();
                                xAxis.setDrawLabels(true);
                                xAxis.setDrawGridLines(false);
                                xAxis.setEnabled(true);
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setLabelCount(11, false);

                                YAxis yAxis = barChart.getAxisLeft();
                                yAxis.setDrawLabels(false);
                                yAxis.setDrawGridLines(false);
                                yAxis.setEnabled(false);

                                YAxis yAxis1 = barChart.getAxisRight();
                                yAxis1.setDrawGridLines(false);
                                yAxis1.setDrawLabels(false);
                                yAxis1.setEnabled(false);

                                barChart.getDescription().setEnabled(false);
                                barChart.getLegend().setEnabled(false);
                                barChart.setDrawBorders(false);
                                barChart.setDrawGridBackground(false);
                                barChart.setDragEnabled(false);
                                barChart.setScaleEnabled(false);
                                barChart.setPinchZoom(false);
                                barChart.setAutoScaleMinMaxEnabled(true);

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
        private ProgressDialog pd = new ProgressDialog(MainActivity.this);

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                done = false;
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
            pd.setMessage("Fetching Results...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();

        }

        protected void onPostExecute(String result) throws JSONException {
            Log.d(TAG, "response" + results);
            done = true;
            informationHandler = new informationHandler(results);
            prediction = informationHandler.getRaiting();
            queue = informationHandler.getQueue();
            pd.dismiss();

        }

    }

    private void resetFeedbackButtons() {
        TextView tv = (TextView) findViewById(R.id.feedback);
        tv.setText("Does this look right?");
        LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        tv.setLayoutParams(textParam);
        linearLayout3.setVisibility(View.VISIBLE);
        linearLayout4.setVisibility(View.GONE);
    }

    public void onFeedbackYesClick() {
        TextView tv = (TextView) findViewById(R.id.feedback);
        linearLayout3.setVisibility(View.GONE);

        LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);

        tv.setLayoutParams(textParam);
        tv.setText("Thanks for your feedback!");
        /*
        TODO: Check Location?
        TODO: SEND FEEDBACK!!!
         */
    }

    public void onFeedbackClickNo() {
        TextView tv = (TextView) findViewById(R.id.feedback);
        tv.setText("Which level looks right?");
        linearLayout3.setVisibility(View.GONE);
        linearLayout4.setVisibility(View.VISIBLE);
    }
    private void feedback(int number) {
        //TODO: actoually send feedback
        Log.d(TAG, "Feedback recieved" + number);
        linearLayout4.setVisibility(View.GONE);
        TextView tv = (TextView) findViewById(R.id.feedback);
        tv.setText("Thanks for your feedback!");
    }

    private void getEntries() throws JSONException {
        informationHandler = new informationHandler(results);
        ArrayList<Integer> values = informationHandler.getForecast();

        barEntries = new ArrayList<>();

        for(int i = 0; i < 24; i++) {
            barEntries.add(new BarEntry(i, values.get(i)));
        }
    }

    class MyBarDataSet extends BarDataSet {

        public MyBarDataSet(List<BarEntry> yVals, String label) {
            super(yVals, label);
        }
        @Override
        public int getColor(int index) {
            try {
                informationHandler.getForecast();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(getEntryForIndex(index).getY() > 70)
                return mColors.get(0);
            else if(getEntryForIndex(index).getY() > 50)
                return mColors.get(1);
            else if(getEntryForIndex(index).getY() > 30)
                return mColors.get(2);
            else if(getEntryForIndex(index).getY() > 10)
                return  mColors.get(3);
            else
                return mColors.get(4);
        }
    }

}

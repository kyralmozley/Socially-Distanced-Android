package space.sociallydistanced;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    Context context = this;

    private static final String TAG = "MyMainActivity" ;
    GoogleMap map;
    SupportMapFragment mapFragment;
    PlacesClient placesClient;

    String placeID;
    String placeName;

    FetchResults results;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apikey = new APIKey().getAPIKey();

        if(!Places.isInitialized()) {
            Places.initialize(MainActivity.this, apikey);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        final Marker[] marker = new Marker[1];
        placesClient = Places.createClient(this);

        /**
         * implement maps autocomplete search function
         */
        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteSupportFragment.setCountry("GB");

        /**
         * define all of the buttons and their respective onclick functions
         * (There is probably a nicer/more consise way to do this...)
         */
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


                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onPlaceSelected(@NonNull Place place) {
                                placeID = place.getId();
                                placeName = place.getName();
                                resetFeedbackButtons();
                                /**
                                 * Call Async task
                                 * Once finished this gets all of the data for the place selected
                                 */
                                new GetJSONTask().execute(place.getId());

                                final LatLng latLng = place.getLatLng();

                                if (marker[0] != null) {
                                    // only want to show one marker
                                    marker[0].remove();
                                }
                                marker[0] = map.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                                //super duper hacky to get it to be slightly off center
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude - 0.002, latLng.longitude), 15));

                            }

                            @Override
                            public void onError(@NonNull Status status) {
                                new onError(MainActivity.this);
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


    private class GetJSONTask extends AsyncTask<String, Void, Void> {

        private ProgressDialog dialog;

        /**\
         * Task to do in background:
         * Make API Call
         * Fetch results to then show
         * @param params placeId
         * @return nothing
         */
        protected Void doInBackground(String... params) {
            Log.d(TAG, "background");
            try {
                results = new FetchResults(params[0]);
                Log.d(TAG,
                        "Raiting " + results.getRaiting() +
                                " Forecast: " + results.getForecast() +
                                " Queue: " + results.getQueue() +
                                "Open Hours: " + results.getOpenHours() +
                                "Is open: " + results.getIsOpen()
                );

            } catch (IOException e) {
                e.printStackTrace();
                new onError(MainActivity.this);
            } catch (JSONException e) {
                e.printStackTrace();
                new onError(MainActivity.this);
            }
            return null;
        }

        /**
         * on post execute need to show all of the relevant views
         * dismiss the dialog box
         * @param result
         */
        protected void onPostExecute(Void result) {
            Log.d(TAG, "post");
            try {
                loadDisplay();
            } catch (JSONException e) {
                e.printStackTrace();
                new onError(MainActivity.this);
            }
            dialog.dismiss();
        }

        /**
         * show dialog box
         */
        protected void onPreExecute() {
            Log.d(TAG, "pre");
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Fetching Results");
            dialog.show();
        }


    }

    public void loadDisplay() throws JSONException {
        /**
         * Set the layout cards to visisble
         */
        LinearLayout overlayLayout = findViewById(R.id.overlay2);
        overlayLayout.setVisibility(View.VISIBLE);

        overlayLayout.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.noButtons).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.feedbackButtons).setVisibility(View.VISIBLE);
                    findViewById(R.id.noButtons).setVisibility(View.GONE);
                }
            }

        });

        LinearLayout raitingLayout = findViewById(R.id.overlay);
        raitingLayout.setVisibility(View.VISIBLE);

        TextView predictionView = (TextView) findViewById(R.id.distanceText);
        predictionView.setText("");
        /**
         * Update text to that given by results
         */
        TextView tv = (TextView) findViewById(R.id.placename);
        TextView tv2 = (TextView) findViewById(R.id.opentimes);
        tv.setText(placeName);
        String opentimes = results.getOpenHours();
        if(opentimes == null) {
            tv2.setVisibility(View.GONE);
        } else {
            tv2.setText(opentimes);
            tv2.setVisibility(View.VISIBLE);
        }
        int queue = results.getQueue();
        int prediction = results.getRaiting();

        /**
         * Decide which colours&messages to show based on value returned
         */
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

        /**
         * Create Bar Chart
         * Add all of the data
         * And make layout nice
         */
        BarChart barChart = findViewById(R.id.barchart);
        ArrayList<Integer> values = results.getForecast();
        ArrayList barEntries = new ArrayList<>();
        for(int i=0; i<24;i++) {
            barEntries.add(new BarEntry(i, values.get(i)));
        }
        MyBarDataSet barDataSet = new MyBarDataSet(barEntries, "");
        barDataSet.setDrawValues(false);
        barDataSet.setColors(ContextCompat.getColor(context, R.color.myRed),
                ContextCompat.getColor(context, R.color.myRedOrange),
                ContextCompat.getColor(context, R.color.myOrange),
                ContextCompat.getColor(context, R.color.myOrangeYellow),
                ContextCompat.getColor(context, R.color.myYellow),
                ContextCompat.getColor(context, R.color.myYellowLime),
                ContextCompat.getColor(context, R.color.myLime),
                ContextCompat.getColor(context, R.color.myLimeGreen),
                ContextCompat.getColor(context, R.color.myGreen));

        BarData barData = new BarData(barDataSet);
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

        barChart.animateY(2000);


    }

    /**
     * Reset feedback buttons to make them invisble / have no text after use
     */
    private void resetFeedbackButtons() {
        TextView tv = (TextView) findViewById(R.id.feedback);
        tv.setText("Does this look right?");
        LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        tv.setLayoutParams(textParam);
        this.findViewById(R.id.feedbackButtons).setVisibility(View.VISIBLE);
        this.findViewById(R.id.noButtons).setVisibility(View.GONE);
    }

    /**
     * If yes: then make API call to send positive feedback
     * Show Thanks message
     */
    public void onFeedbackYesClick() {
        TextView tv = (TextView) findViewById(R.id.feedback);
        findViewById(R.id.feedbackButtons).setVisibility(View.GONE);

        LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);

        FeedbackHandler fh = new FeedbackHandler();
        fh.sendPositiveFeedback(placeID);

        tv.setLayoutParams(textParam);
        tv.setText("Thanks for your feedback!");
    }

    /**
     * If no: then ask the user to supply which level they believe is correct
     */
    public void onFeedbackClickNo() {
        TextView tv = (TextView) findViewById(R.id.feedback);
        tv.setText("Which level looks right?");
        this.findViewById(R.id.feedbackButtons).setVisibility(View.GONE);
        this.findViewById(R.id.noButtons).setVisibility(View.VISIBLE);
    }

    /**
     * Once user has supplied number, we make negative feedback API call
     * @param number
     */
    private void feedback(int number) {
        Log.d(TAG, "Feedback recieved" + number);
        this.findViewById(R.id.noButtons).setVisibility(View.GONE);
        TextView tv = (TextView) findViewById(R.id.feedback);
        tv.setText("Thanks for your feedback!");
        FeedbackHandler fh = new FeedbackHandler();
        fh.sendNegativeFeedback(placeID, number);

    }



}

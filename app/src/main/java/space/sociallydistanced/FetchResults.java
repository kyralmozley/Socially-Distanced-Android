package space.sociallydistanced;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to process the data returned from the API call
 */
public class FetchResults {
    private JSONObject results;

    /**
     * Initialise class to call the API call, and store JSON response
     * @param placeId
     * @throws IOException
     * @throws JSONException
     */
    public FetchResults(String placeId) throws IOException, JSONException {
        results = new JSONObject(Utility.getData(placeId));

    }

    /**
     * Function to return whether or not place is open
     * @return True if open, False if not
     * @throws JSONException can never actually occur because if we don't know if a place is open I return true
     */
    public Boolean getIsOpen() throws JSONException {
        return (Boolean) results.get("open");
    }

    /**
     * Function to get the predicted raiting of the location (int 0-4)
     * @return int raiting
     * @throws JSONException will never occur because I always return a raiting
     */
    public int getRaiting() throws JSONException {
        if(!getIsOpen()) {
            return -1;
        }
        return (int) results.get("prediction");
    }

    /**
     * Get current queue ranking, int 0-4 if queue, or -1 if not relevant
     * @return int raiting
     * @throws JSONException yet again, wont occur
     */
    public int getQueue() throws JSONException {
        return (int) results.get("queue");
    }

    /**
     * Get the forecast prediction for the day, to be able to turn it into a graph
     * @return array of 24 integers (forecast for each hour)
     * @throws JSONException
     */
    public ArrayList<Integer> getForecast() throws JSONException {
        ArrayList<Integer> data = new ArrayList<Integer>();

        JSONArray response = results.getJSONArray("graphPoints");
        for(int i = 0; i < response.length(); i++) {
            data.add(response.getInt(i));
        }

        return data;

    }

    /**
     * String construction for open hours (Open today 9:15 till 18:00) for example
     * Or returns null if error / no open hours
     * @return String of open hours phrase
     */
    public String getOpenHours() {

        try {
            JSONArray response = results.getJSONArray("openhours");
            /**
             * Returned in the format [[8, 30], [17, 30]]
             * So need to split this
             */

            JSONArray open = response.getJSONArray(0);
            JSONArray closed = response.getJSONArray(1);

            String[] openString = new String[]{open.getString(0), open.getString(1)};
            String[] closedString = new String[]{closed.getString(0), closed.getString(1)};

            String dontShow[][] = new String[][]{
                    {"0", "00"}, {"23", "59"}
            };
            if (Arrays.equals(openString, dontShow[0]) && Arrays.equals(closedString, dontShow[1]))
                return null;
            return "Open today " + openString[0] + ":" + openString[1] + " till " + closedString[0] + ":" + closedString[1];


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

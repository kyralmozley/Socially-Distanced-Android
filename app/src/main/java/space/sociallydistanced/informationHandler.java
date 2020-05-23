package space.sociallydistanced;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class informationHandler {
    private JSONObject results;

    public informationHandler(JSONObject obj) {
        results = obj;
    }


    public int getRaiting() throws JSONException {
        if(!isOpen()) {
            return -1;
        }
        int raiting = (int) results.get("prediction");
        return raiting;
    }

    public Boolean isOpen() throws JSONException {
        return (Boolean) results.get("open");
    }

    public int getQueue() throws JSONException {
        return (int) results.get("queue");
    }

    public ArrayList<Integer> getForecast() throws JSONException {
        ArrayList<Integer> data = new ArrayList<Integer>();

        JSONArray response = results.getJSONArray("graphPoints");
        for(int i = 0; i < response.length(); i++) {
            data.add(response.getInt(i));
        }

        return data;

    }

    public String getOpenHours() {
        try {
            JSONArray response = results.getJSONArray("openhours");

            JSONArray open = response.getJSONArray(0);
            JSONArray closed = response.getJSONArray(1);

            String[] openString = new String[]{open.getString(0), open.getString(1)};
            String[] closedString = new String[]{closed.getString(0), closed.getString(1)};

            String dontShow[][] = new String[][]{
                    {"0", "00"}, {"23", "59"}
            };
            if(Arrays.equals(openString, dontShow[0]) && Arrays.equals(closedString, dontShow[1]))
                return null;
            return "Open today " + openString[0] + ":" + openString[1] + " till " + closedString[0] + ":" + closedString[1];


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

            /*

            String[][] allDay = new String[][] {{"0","00"}, {"23", "59"}};
            Log.d("got here", String.valueOf(openHours));
            if(openHours.equals(allDay))
                return null;
            return "Open Today " + openHours[0][0] + ":" + openHours[0][1]
                    + " till " + openHours[1][0] + ":" + openHours[1][1];

             */

    }

}

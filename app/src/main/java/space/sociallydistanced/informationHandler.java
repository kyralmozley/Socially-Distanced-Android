package space.sociallydistanced;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        Log.d("Graph", "Got here");
        for(int i = 0; i < response.length(); i++) {
            data.add(response.getInt(i));
        }

        return data;

    }

}

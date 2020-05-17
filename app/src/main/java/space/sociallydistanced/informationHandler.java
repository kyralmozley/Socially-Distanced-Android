package space.sociallydistanced;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

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

    public int[] getForecast() throws JSONException {
        return (int[]) results.get("graphPoints");
    }

}

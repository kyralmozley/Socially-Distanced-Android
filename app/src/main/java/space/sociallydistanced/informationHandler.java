package space.sociallydistanced;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class informationHandler {
    public int getRaiting(JSONObject results) throws JSONException {
        int raiting = (int) results.get("prediction");
        Log.d(TAG, "got raiting" + raiting);
        return raiting;
    }
}

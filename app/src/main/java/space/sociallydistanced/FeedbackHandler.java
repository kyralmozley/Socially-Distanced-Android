package space.sociallydistanced;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class FeedbackHandler {
    private static final String TAG = "feedback";

    public static void sendPositiveFeedback(String placeID) throws IOException { //positive?placeId=placeId
        Log.d(TAG, "positive feedback");
        URL url = new URL("https://ec2.sociallydistanced.space/api/feedback/positive?placeId=" + placeID);
        //String data =;
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        try {
            //connection.getOutputStream().write(data.getBytes("UTF-8"));
            Log.d(TAG, String.valueOf(connection.getResponseCode()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void sendNegativeFeedback(String placeID, int level) throws IOException {
        Log.d(TAG, "negative feedback" + level);
        URL url = new URL("https://ec2.sociallydistanced.space/api/feedback/negative?");
        String data = "placeId=" + placeID +"&level=" + level;
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        try {
            connection.getOutputStream().write(data.getBytes("UTF-8"));
            Log.d(TAG, String.valueOf(connection.getResponseCode()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}

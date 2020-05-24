package space.sociallydistanced;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class feedbackHandler {
    private static final String TAG = "feedback";

    public static void sendPositiveFeedback(String placeID) throws IOException { //positive?placeId=placeId
        URL url = new URL("https://ec2.sociallydistanced.space/api/feedback/positive?");
        String data = "placeId=" + placeID;
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

    public static void sendNegativeFeedback(String placeID, int level) throws IOException {
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

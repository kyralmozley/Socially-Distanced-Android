package space.sociallydistanced;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import javax.net.ssl.HttpsURLConnection;

public class Utility {
    private static String TAG = "Utility Log";

    /**
     * Make API get request to socaillydistanced.space
     * This returns a JSON of predictions made and opening data
     * @param placeId the placeId as defined by Google Maps
     * @return returns the JSON format as a string
     * @throws IOException
     */
    public static String getData(String placeId) throws IOException {

        InputStream is = null;
        try {
            URL url = new URL("https://ec2.sociallydistanced.space/api/main/place?placeId=" + placeId);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();
            Log.d(TAG, "The response is " + connection.getResponseCode());
            is = connection.getInputStream();

            String contentAsString = readInputStream(is);

            return contentAsString;
        } finally {
            if(is != null) {
                is.close();
            }
        }

    }

    /**
     * Helper function to read the input stream from connection
     * @param is input stream
     * @return string of input
     * @throws IOException
     */
    private static String readInputStream(InputStream is) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(is, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer)))
            writer.write(buffer, 0, n);
        return writer.toString();
    }
}

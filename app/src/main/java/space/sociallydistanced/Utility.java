package space.sociallydistanced;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import javax.net.ssl.HttpsURLConnection;

public class Utility {
    private static String TAG = "Utility Log";

    public static String getData(String myurl) throws IOException {
        InputStream is = null;

        try {

            URL url = new URL("https://ec2.sociallydistanced.space/api/main/place?placeId=" + myurl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();
            Log.d(TAG, "The response is " + connection.getResponseCode());
            is = connection.getInputStream();

            String contentAsString = readInputStream(is);
            //String contentAsString = "{\"prediction\":2,\"placeId\":\"ChIJpzm3HB4bdkgR5BytQRbMiCc\",\"graphPoints\":[0,0,0,0,0,0,0,0,0,0,0,45,74,99,99,99,99,0,0,0,0,0,0,0],\"open\":true,\"queue\":3}";
            return contentAsString;
        } finally {
            if(is != null) {
                is.close();
            }
        }
    }

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

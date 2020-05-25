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

    public static String getData(String myurl) throws IOException {

        InputStream is = null;
        try {
            /*
            URL url = new URL("https://ec2.sociallydistanced.space/api/main/place?placeId=" + myurl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();
            Log.d(TAG, "The response is " + connection.getResponseCode());
            is = connection.getInputStream();

            String contentAsString = readInputStream(is);

            */
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String[] randomContent = {
                    "{\"prediction\":4,\"placeId\":\"ChIJpzm3HB4bdkgR5BytQRbMiCc\",\"graphPoints\":[0,0,0,0,0,0,0,0,0,0,0,45,74,99,99,99,99,0,0,0,0,0,0,0],\"open\":true,\"queue\":3,\"openhours\":[[\"8\", \"30\"], [\"17\", \"30\"]]}",
                    "{\"prediction\":4,\"placeId\":\"ChIJhRoYKUkFdkgRDL20SU9sr9E\",\"graphPoints\":[0,0,0,0,0,0,0,0,0,4,12,25,35,44,61,75,90,95,81,72,58,32,12,1],\"open\":true,\"queue\":1, \"openhours\":[[\"12\", \"30\"], [\"17\", \"30\"]]}",
                    "{\"prediction\":4,\"placeId\":\"ChIJKZQaXxwbdkgRWLo89tC-_V8\",\"graphPoints\":[29,37,40,44,48,52,54,58,57,52,53,49,47,45,44,41,47,44,34,36,31,30,30,0],\"open\":true,\"queue\":-1,\"openhours\":[[\"0\", \"00\"], [\"23\", \"59\"]]}"
            };

            String contentAsString = randomContent[(int)(Math.random() * 3)];


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

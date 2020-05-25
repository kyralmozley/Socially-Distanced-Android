package space.sociallydistanced;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FeedbackHandler {
    private static final String TAG = "feedback";

    /**
     * Send the positive feedback using post API request to sociallydistanced.space
     * @param placeID placeId as defined by Google
     */
    public void sendPositiveFeedback(String placeID) {
        new SendPosFeedback().execute(placeID);
    }

    private class SendPosFeedback extends AsyncTask<String, Void, Void> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This will normally run on a background thread. But to better
         * support testing frameworks, it is recommended that this also tolerates
         * direct execution on the foreground thread, as part of the {@link #execute} call.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param strings The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL("https://ec2.sociallydistanced.space/api/feedback/positive?placeId=" + strings[0]);

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write("");
                writer.close();
                Log.d(TAG, String.valueOf(connection.getResponseCode()));
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Send negative feedback (with correct current raiting) based on
     * user score. Send as api post request to socaillydistanced.space
     * @param placeID as defined by Google
     * @param level correct raiting level, defined by user
     */
    public void sendNegativeFeedback(String placeID, int level) {
        new SendNegFeedback().execute(placeID, String.valueOf(level));

    }

    private class SendNegFeedback extends AsyncTask<String, Void, Void> {
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This will normally run on a background thread. But to better
         * support testing frameworks, it is recommended that this also tolerates
         * direct execution on the foreground thread, as part of the {@link #execute} call.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param strings The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL("https://ec2.sociallydistanced.space/api/feedback/negative?placeId=" + strings[0] +"&level=" + strings[1]);

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write("");
                writer.close();
                Log.d(TAG, String.valueOf(connection.getResponseCode()));
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

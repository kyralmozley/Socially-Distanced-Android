package space.sociallydistanced;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;

public class onError {

    public onError(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        activity.findViewById(R.id.overlay2).setVisibility(View.GONE);
        activity.findViewById(R.id.overlay).setVisibility(View.GONE);


        builder.setMessage("Oops! Something went wrong, please try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        activity.findViewById(R.id.overlay2).setVisibility(View.GONE);
                        activity.findViewById(R.id.overlay).setVisibility(View.GONE);
                    }
                });

        builder.show();
    }
}

package org.paidaki.wordsearcher.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDialog {

    private AlertDialog error;

    public ErrorDialog(Context context) {
        error = new AlertDialog.Builder(context).create();

        error.setTitle("Error");
        error.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    public void showAndWait(String message) {
        error.setMessage(message);
        error.show();
    }
}

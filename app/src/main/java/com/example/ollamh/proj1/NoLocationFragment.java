package com.example.ollamh.proj1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by woody on 7/17/17.
 */

public class NoLocationFragment extends DialogFragment {
    boolean pause=false;
    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Location Error"); //set the title, the message, and the view fromthe xml
        builder.setMessage("Please Turn On Location And Exit Application");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               getActivity().finish();


            }
        });


        return builder.create();
    }


}

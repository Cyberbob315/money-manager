package com.example.iceman.project.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.iceman.project.activity.AddActivity;

/**
 * Created by iceman on 23/10/2016.
 */

public class SimpleDialog  {
    public static void showDialog(Context context, String tilte, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(tilte);
        builder.setMessage(message);
        builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public static boolean confirmDialog(Context context, String tilte, String message) {
        final boolean[] isDelete = new boolean[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(tilte);
        builder.setMessage(message);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDelete[0] = true;
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDelete[0] = false;
                dialog.dismiss();
            }
        });

        builder.create().show();
        Log.d("Thong bao",isDelete[0]+"");
        return isDelete[0];
    }
}

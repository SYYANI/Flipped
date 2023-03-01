package cn.shu.heartsound.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import cn.shu.heartsound.R;


public class ProgressDialogCreator {

    /**
     * 创建ProgressDialog
     *
     * @param activity
     * @return
     */
    public Dialog GetProgressDialogCreator(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(activity.getLayoutInflater().inflate(R.layout.progressdialog, (ViewGroup) activity.findViewById(R.id.progressDialog)));
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        dialog.getWindow().setLayout(point.x / 3, point.y / 6);
        return dialog;
    }
}

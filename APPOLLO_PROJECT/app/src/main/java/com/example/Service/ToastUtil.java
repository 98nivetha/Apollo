package com.example.Service;// ToastUtil.java
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {

    public static void showRedToast(Context context, String message, int durationInMillis) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View view = toast.getView();

        if (view != null) {
            view.setBackgroundColor(Color.argb(255, 253, 236, 234));

            TextView text = view.findViewById(android.R.id.message);
            if (text != null) {
                text.setTextColor(Color.argb(255, 211, 47, 47));
                text.setTypeface(null, Typeface.BOLD);
                text.setTextSize(16);
            }
        }

        toast.show();
        if (durationInMillis > 3500) {
            new Handler(Looper.getMainLooper()).postDelayed(toast::show, 3500);
        }

        new Handler(Looper.getMainLooper()).postDelayed(toast::cancel, durationInMillis);
    }


    public static void showGreenToast(Context context, String message, int durationInMillis) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View view = toast.getView();

        if (view != null) {
            view.setBackgroundColor(Color.argb(255, 223, 240, 216));
            TextView text = view.findViewById(android.R.id.message);
            if (text != null) {
                text.setTextColor(Color.argb(255, 76, 175, 80));
                text.setTextSize(16);
            }
        }

        toast.show();
        if (durationInMillis > 3500) {
            new Handler(Looper.getMainLooper()).postDelayed(toast::show, 3500);
        }
        new Handler(Looper.getMainLooper()).postDelayed(toast::cancel, durationInMillis);
    }

    public static void showBlueToast(Context context, String message, int durationInMillis) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View view = toast.getView();

        if (view != null) {
            view.setBackgroundColor(Color.argb(255, 227, 242, 253));
            TextView text = view.findViewById(android.R.id.message);
            if (text != null) {
                text.setTextColor(Color.argb(255, 33, 150, 243));
                text.setTypeface(null, Typeface.BOLD); // Make text bold
                text.setTextSize(16);
            }
        }

        toast.show();
        if (durationInMillis > 3500) {
            new Handler(Looper.getMainLooper()).postDelayed(toast::show, 3500);
        }
        new Handler(Looper.getMainLooper()).postDelayed(toast::cancel, durationInMillis);
    }
}

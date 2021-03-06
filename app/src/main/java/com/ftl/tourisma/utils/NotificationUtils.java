package com.ftl.tourisma.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Vinay on 6/13/2017.
 */

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();
    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, Intent intent) {
        showNotificationMessage(title, message, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        // notification icon
        final int icon = R.drawable.appicon1;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Bitmap bitmap = getBitmapFromURL(imageUrl);
                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, resultPendingIntent, alarmSound);
                }
            }
        } else {
            playNotificationSound();
            showSmallNotification(mBuilder, icon, title, message, resultPendingIntent, alarmSound);
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, PendingIntent resultPendingIntent, Uri alarmSound) {
        Notification notification;
        notification = mBuilder
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.appicon1)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MyTorismaApplication.mAtomicInteger.incrementAndGet(), notification);
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.drawable.appicon1)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MyTorismaApplication.mAtomicInteger.incrementAndGet(), notification);
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

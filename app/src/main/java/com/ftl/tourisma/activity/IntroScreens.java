package com.ftl.tourisma.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.SignUpLoginFragmentActivity;
import com.ftl.tourisma.utils.Constants;
import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by Vinay on 2/28/2017.
 */

public class IntroScreens extends AppIntro {

    private static SharedPreferences mPreferences;
    int noOfTimesCalled = 0;
    private SharedPreferences.Editor mEditor;

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
    }

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new firstSlide());
        addSlide(new secondSlide());
        addSlide(new thirdSlide());
        setFlowAnimation();
        setScrollDurationFactor(1);
    }

    @Override
    public void onSkipPressed() {
        if (noOfTimesCalled % 2 == 0) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Tutorial End message"))
                    .setCancelable(false)
                    .setPositiveButton(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Tutorial End Continue"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Tutorial End Cancel"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent in = new Intent(IntroScreens.this, SignUpLoginFragmentActivity.class);
                            startActivity(in);
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Tutorial End Title"));
            alert.show();
        }
        noOfTimesCalled++;
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        if (noOfTimesCalled % 2 == 0) {
            Intent in = new Intent(IntroScreens.this, SignUpLoginFragmentActivity.class);
            startActivity(in);
            finish();
        }
        noOfTimesCalled++;
    }

    public void getStarted() {

    }

    @Override
    public void onSlideChanged() {

    }

    @SuppressLint("ValidFragment")
    public static class firstSlide extends Fragment {

        public firstSlide() {
            //Requires empty constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.intro_location, container, false);
            TextView location_txt = (TextView) v.findViewById(R.id.location_txt);
            TextView loc_txt = (TextView) v.findViewById(R.id.loc_txt);
            loc_txt.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Tutorial message-2"));
            if (Constants.language.equals("arabic")) {
                location_txt.setText("مواقع");
//                loc_txt.setText(getResources().getString(R.string.tutorial_location_txt_arabic));
            } else if (Constants.language.equals("russian")) {
                location_txt.setText("Ваше местоположение");
//                loc_txt.setText(getResources().getString(R.string.tutorial_location_txt_russian));
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.location_new, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            ImageView mImageView = (ImageView) v.findViewById(R.id.location_img);
            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.location_new, imageWidth, imageHeight);
            mImageView.setImageBitmap(bitmap);
            return v;
        }
    }

    @SuppressLint("ValidFragment")
    public static class secondSlide extends Fragment {

        public secondSlide() {
            //Requires empty constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.intro_notification, container, false);
            TextView notify_txt = (TextView) v.findViewById(R.id.notify_txt);
            TextView notification_txt = (TextView) v.findViewById(R.id.notification_txt);
            notification_txt.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Tutorial Message-1"));
            if (Constants.language.equals("arabic")) {
                notify_txt.setText("إخطارات");
//                notification_txt.setText(getResources().getString(R.string.tutorial_notification_txt_arabic));
            } else if (Constants.language.equals("russian")) {
                notify_txt.setText("Уведомления");
//                notification_txt.setText(getResources().getString(R.string.tutorial_notification_txt_russian));
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.notifications_new, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            ImageView mImageView = (ImageView) v.findViewById(R.id.notification_img);
            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.notifications_new, imageWidth, imageHeight);
            mImageView.setImageBitmap(bitmap);
            return v;
        }
    }

    @SuppressLint("ValidFragment")
    public static class thirdSlide extends Fragment {

        public thirdSlide() {
            //Requires empty constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.intro_bluetooth, container, false);
            TextView blue_txt = (TextView) v.findViewById(R.id.blue_txt);
            TextView bluetooth_txt = (TextView) v.findViewById(R.id.bluetooth_txt);
            bluetooth_txt.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Tutorial message-3"));
            if (Constants.language.equals("arabic")) {
                blue_txt.setText("بلوتوث");
//                bluetooth_txt.setText(getResources().getString(R.string.tutorial_bluetooth_txt_arabic));
            } else if (Constants.language.equals("russian")) {
                blue_txt.setText("Bluetooth");
//                bluetooth_txt.setText(getResources().getString(R.string.tutorial_bluetooth_txt_russian));
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.bluetooth_new, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            ImageView mImageView = (ImageView) v.findViewById(R.id.bluetooth_img);
            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.bluetooth_new, imageWidth, imageHeight);
            mImageView.setImageBitmap(bitmap);
            return v;
        }
    }
}

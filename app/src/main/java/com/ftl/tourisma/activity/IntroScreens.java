package com.ftl.tourisma.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.SignUpLoginFragmentActivity;
import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by Vinay on 2/28/2017.
 */

public class IntroScreens extends AppIntro {

    int noOfTimesCalled = 0;

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
            Intent in = new Intent(IntroScreens.this, SignUpLoginFragmentActivity.class);
            startActivity(in);
            finish();
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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.location_intro, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            ImageView mImageView = (ImageView) v.findViewById(R.id.location_img);
            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.location_intro, imageWidth, imageHeight);
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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.notification_intro, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            ImageView mImageView = (ImageView) v.findViewById(R.id.notification_img);
            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.notification_intro, imageWidth, imageHeight);
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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.bluetooth_intro, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            ImageView mImageView = (ImageView) v.findViewById(R.id.bluetooth_img);
            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.bluetooth_intro, imageWidth, imageHeight);
            mImageView.setImageBitmap(bitmap);
            return v;
        }
    }
}

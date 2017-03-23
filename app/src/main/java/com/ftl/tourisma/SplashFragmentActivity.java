package com.ftl.tourisma;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.utils.Constants;

/**
 * Created by fipl11111 on 22-Feb-16.
 */
public class SplashFragmentActivity extends FragmentActivity {

    Thread mThread;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_splash);
        final Bundle bundle = getIntent().getExtras();
        mPreferences = getSharedPreferences(Constants.mPref, 0);
        final ImageView imgCloud = (ImageView) findViewById(R.id.imgCloud);
        final ImageView imgSplash1 = (ImageView) findViewById(R.id.imgSplash1);
        final ImageView imgSplash2 = (ImageView) findViewById(R.id.imgSplash2);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);


        String type = null;
        if (bundle != null) {
            type = bundle.getString("type");
        }
        if (type != null) {

            if (mPreferences.getString("User_Id", "").length() != 0 && mPreferences.getString("User_Email", "").length() != 0) {
                Constants.mFromSelectLocation = 0;

                Intent mIntent = new Intent(SplashFragmentActivity.this, MainActivity.class);
//                                    Intent mIntent = new Intent(SplashFragmentActivity.this, SelectLocationFragmentActivity.class);
                mIntent.putExtra("beacon", bundle);
                startActivity(mIntent);
                finish();
//                                }
            } else {


                Intent mIntent = new Intent(SplashFragmentActivity.this, MainActivity.class);
//                                    Intent mIntent = new Intent(SplashFragmentActivity.this, SelectLocationFragmentActivity.class);
                mIntent.putExtra("beacon", bundle);
                startActivity(mIntent);
                finish();
            }
        } else {

            final TranslateAnimation animation11 = new TranslateAnimation(10, -100, 0, 0);
            animation11.setDuration(7000);
            animation11.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //imgCloud.startAnimation(animation1);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgCloud.startAnimation(animation11);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            imgCloud.startAnimation(animation11);

            final Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new LinearInterpolator()); //add this
            fadeIn.setDuration(1000);


            final Animation fadeIn1 = new AlphaAnimation(0, 1);
            fadeIn1.setInterpolator(new LinearInterpolator()); //add this
            fadeIn1.setDuration(1000);

            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    imgSplash1.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgSplash2.startAnimation(fadeIn1);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            imgSplash1.startAnimation(fadeIn);


            fadeIn1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    imgSplash2.setVisibility(View.VISIBLE);
//                imgSplash1.setVisibility(View.GONE);


                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                if (mPreferences.getString("User_Id", "").length() != 0 && mPreferences.getString("User_Email", "").length() != 0) {
                                    Constants.mFromSelectLocation = 0;

                                    Intent mIntent = new Intent(SplashFragmentActivity.this, MainActivity.class);
//                                    Intent mIntent = new Intent(SplashFragmentActivity.this, SelectLocationFragmentActivity.class);
                                    mIntent.putExtra("beacon", bundle);
                                    startActivity(mIntent);
                                    finish();
//                                }
                                } else {

                                    String type = null;
                                    if (bundle != null) {
                                        type = bundle.getString("type");
                                    }
                                    if (type != null) {
                                        Intent mIntent = new Intent(SplashFragmentActivity.this, MainActivity.class);
//                                    Intent mIntent = new Intent(SplashFragmentActivity.this, SelectLocationFragmentActivity.class);
                                        mIntent.putExtra("beacon", bundle);
                                        startActivity(mIntent);
                                        finish();
                                    } else {
                                        Intent mIntent = new Intent(SplashFragmentActivity.this, LanguageFragmentActivity.class);
                                        startActivity(mIntent);
                                        finish();
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mThread.start();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

       /* try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ftl.tourisma", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
//                Log.d("System out", "keyhash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/
        if (mThread != null)
            mThread.interrupt();
    }
}

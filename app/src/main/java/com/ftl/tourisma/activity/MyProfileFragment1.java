package com.ftl.tourisma.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ftl.tourisma.LanguageFragmentActivity;
import com.ftl.tourisma.LoginFragmentActivity;
import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.adapters.CropOptionAdapter;
import com.ftl.tourisma.custom_views.NormalEditText;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.models.CropOption;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CircleImageView;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.Utils;
import com.github.clans.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fipl11111 on 25-Feb-16.
 */
public class MyProfileFragment1 extends Fragment implements View.OnClickListener, post_sync.ResponseHandler {
    private static final int PIC_CROP = 3;
    private static final String TAG = "MyProfileFragment_New";
    private static final int CROP_FROM_CAMERA = 2;
    private static int TAKE_PICTURE = 1, SELECT_PICTURE = 0;
    private static int RESULT_LOAD = 1;
    public String selectedImagePath = "", path, imgName = "";
    long timeForImgname;
    String[] languages = {
            "English",
            "Русский",
            "العربية",
    };
    MainActivity yourLocationFragmentActivity;
    String img_Decodable_Str;
    Bitmap photo = null;
    Uri currImageURI;
    private NormalTextView tv_profile_name, tv_profile_address, tv_profile_email, tv_profile_language, tv_address, tv_profile_email1;
    private Spinner tv_profile_language1;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private ScrollView sv_my_profile1, sv_my_profile2;
    private FloatingActionButton fb_edit, fb_edit1;
    private NormalEditText edt_profile_name1, edt_address1;
    private FloatLabeledEditText fledt_profile_name1, fledt_address1;
    private File f;
    private Dialog dialog;
    private ProgressBar progressbar;
    private CircleImageView cv_my_profile, cv_my_profile1;
    private RelativeLayout ll_about_us;
    private ImageView iv_close_header1, iv_about_us;
    private WebView wb_about_us;
    private NormalTextView txt_email, tv_sign_up;
    private NormalTextView tv_profile_address1, floatableAddress, tv_about, button_save_changes, tv_logout;
    private boolean image = false;
    private int lan_id;
    private ImageView imgSun1, imgSun;
    private int mCurrRotation;
    private Animation rotation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainTabHostFragment.lastPage = 2;
        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile, container, false);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, languages);
        ll_about_us = (RelativeLayout) view.findViewById(R.id.ll_about_us);
        tv_profile_address1 = (NormalTextView) view.findViewById(R.id.tv_profile_address1);
        tv_profile_address1.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "changeprofile"));
        tv_profile_name = (NormalTextView) view.findViewById(R.id.tv_profile_name);
        imgSun = (ImageView) view.findViewById(R.id.imgSun);
        imgSun1 = (ImageView) view.findViewById(R.id.imgSun1);
        rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        imgSun.startAnimation(rotation);
        imgSun1.startAnimation(rotation);
        tv_profile_name.setText(mPreferences.getString("User_Name", ""));
        tv_profile_address = (NormalTextView) view.findViewById(R.id.tv_profile_address);
        tv_profile_address.setText(mPreferences.getString(Preference.Pref_State, "") + ", " + mPreferences.getString(Preference.Pref_Country, ""));
        tv_address = (NormalTextView) view.findViewById(R.id.tv_address);
        tv_address.setText(mPreferences.getString("mAddress", ""));
        tv_address.setText(mPreferences.getString("User_Address", ""));
        tv_address.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "addrestost"));
        tv_profile_address.setText(mPreferences.getString("User_Address", ""));
        tv_profile_email = (NormalTextView) view.findViewById(R.id.tv_profile_email);
        tv_profile_email.setText(mPreferences.getString("User_Email", ""));
        tv_profile_email1 = (NormalTextView) view.findViewById(R.id.tv_profile_email1);
        tv_profile_email1.setText(mPreferences.getString("User_Email", ""));
        tv_profile_language = (NormalTextView) view.findViewById(R.id.tv_profile_language);
        tv_profile_language1 = (Spinner) view.findViewById(R.id.tv_profile_language1);
        tv_profile_language1.setAdapter(adapter);
        tv_profile_language1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tv_profile_language1.setSelection(position);
                if (position == 0) {
                    lan_id = 6;
                } else if (position == 1) {
                    lan_id = 7;
                } else {
                    lan_id = 8;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        if (mPreferences.getString("Lan_Id", "").equalsIgnoreCase("6")) {
            tv_profile_language.setText("English");
            tv_profile_language1.setSelection(0);
            lan_id = 6;
        } else if (mPreferences.getString("Lan_Id", "").equalsIgnoreCase("7")) {
            tv_profile_language.setText("Русский");
            tv_profile_language1.setSelection(1);
            lan_id = 7;
        } else if (mPreferences.getString("Lan_Id", "").equalsIgnoreCase("8")) {
            tv_profile_language.setText("العربية");
            tv_profile_language1.setSelection(2);
            lan_id = 8;
        }

        iv_about_us = (ImageView) view.findViewById(R.id.iv_about_us);
        iv_about_us.setOnClickListener(this);
        sv_my_profile1 = (ScrollView) view.findViewById(R.id.sv_my_profile1);
        sv_my_profile2 = (ScrollView) view.findViewById(R.id.sv_my_profile2);
        edt_profile_name1 = (NormalEditText) view.findViewById(R.id.edt_profile_name1);
        edt_profile_name1.setHint(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "nametost"));
        edt_profile_name1.setText(mPreferences.getString("User_Name", ""));
        fledt_profile_name1 = (FloatLabeledEditText) view.findViewById(R.id.fledt_profile_name1);
        fledt_profile_name1.setHint(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "nametost"));
        edt_address1 = (NormalEditText) view.findViewById(R.id.edt_address1);
        edt_address1.setHint(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "addrestost"));
        edt_address1.setText(mPreferences.getString("mAddress", ""));
        edt_address1.setText(mPreferences.getString("User_Address", ""));
        txt_email = (NormalTextView) view.findViewById(R.id.txt_email);
        floatableAddress = (NormalTextView) view.findViewById(R.id.floatableAddress);
        fledt_address1 = (FloatLabeledEditText) view.findViewById(R.id.fledt_address1);
        txt_email.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "emailtost"));
        floatableAddress.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "addrestost"));
        fledt_address1.setHint(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "addrestost"));
        fb_edit = (FloatingActionButton) view.findViewById(R.id.fb_edit);
        fb_edit.setOnClickListener(this);
        fb_edit1 = (FloatingActionButton) view.findViewById(R.id.fb_edit1);
        fb_edit1.setOnClickListener(this);
        NormalTextView tv_language = (NormalTextView) view.findViewById(R.id.tv_language);
        tv_language.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Language"));
        NormalTextView tv_language1 = (NormalTextView) view.findViewById(R.id.tv_language1);
        tv_language1.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Language"));
        button_save_changes = (NormalTextView) view.findViewById(R.id.button_save_changes);
        button_save_changes.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "savechanges"));
        button_save_changes.setOnClickListener(this);
        cv_my_profile = (CircleImageView) view.findViewById(R.id.cv_my_profile);
        cv_my_profile1 = (CircleImageView) view.findViewById(R.id.cv_my_profile1);
        cv_my_profile1.setOnClickListener(this);

        if (mPreferences.getString("fb_id", "").length() != 0) {
            String imageUrl = "https://graph.facebook.com/" + mPreferences.getString("fb_id", "") + "/picture?type=large";
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .into(cv_my_profile);
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .into(cv_my_profile1);
        } else {
            if (mPreferences.getString("User_ProfilePic", "").length() != 0 && !mPreferences.getString("User_ProfilePic", "").equalsIgnoreCase("null")) {
                String imageUrl = Constants.IMAGE_URL1 + mPreferences.getString("User_ProfilePic", "");
                Picasso.with(getActivity())
                        .load(imageUrl)
                        .into(cv_my_profile);
                Picasso.with(getActivity())
                        .load(imageUrl)
                        .into(cv_my_profile1);
            }
        }

        tv_sign_up = (NormalTextView) view.findViewById(R.id.txtTitle);
        tv_about = (NormalTextView) view.findViewById(R.id.tv_about);
        tv_about.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "about"));
        tv_logout = (NormalTextView) view.findViewById(R.id.tv_logout);
        tv_logout.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "logout"));
        tv_logout.setOnClickListener(this);
        tv_about.setOnClickListener(this);
        tv_sign_up.setText(R.string.about_us);
        iv_close_header1 = (ImageView) view.findViewById(R.id.img_close);
        iv_close_header1.setOnClickListener(this);
        wb_about_us = (WebView) view.findViewById(R.id.wb_about_us);

        if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
            tv_profile_name.setText("Guest");
            tv_profile_email.setText("info@mytourisma.com");
            tv_logout.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Login"));
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (rotation != null) {
                imgSun.startAnimation(rotation);
                imgSun1.startAnimation(rotation);
            }
        } else {
            if (rotation != null) {
                rotation.cancel();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == iv_about_us | v == tv_about) {
            sv_my_profile1.setVisibility(View.GONE);
            ll_about_us.setVisibility(View.VISIBLE);
            pageInfo();
        } else if (v == tv_logout) {
            if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                Constants.mStatic = 0;
                Intent mIntent = new Intent(getActivity(), LoginFragmentActivity.class);
                startActivity(mIntent);
                getActivity().finish();
            } else {
                Constants.mStatic = 0;
                mEditor.clear().commit();
                Prefs.putInt(Constants.user_id, 0);
                Intent mIntent = new Intent(getActivity(), LanguageFragmentActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
            }
        } else if (v == fb_edit) {
            if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                yourLocationFragmentActivity.showGuestSnackToast();
            } else {
                sv_my_profile1.setVisibility(View.GONE);
                sv_my_profile2.setVisibility(View.VISIBLE);
                if (tv_profile_language.getText().toString().equalsIgnoreCase("English")) {
                    tv_profile_language1.setSelection(0);
                } else if (tv_profile_language.getText().toString().equalsIgnoreCase("Russian")) {
                    tv_profile_language1.setSelection(1);
                } else if (tv_profile_language.getText().toString().equalsIgnoreCase("Arabic")) {
                    tv_profile_language1.setSelection(2);
                }
            }
        } else if (v == fb_edit1) {
            sv_my_profile1.setVisibility(View.VISIBLE);
            sv_my_profile2.setVisibility(View.GONE);
        } else if (v == button_save_changes) {
            if (edt_profile_name1.getText().toString().length() == 0) {
                SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NAME")));
            } else if (edt_address1.getText().toString().length() == 0) {
                SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "ADDRESS")));
            } else if (edt_address1.getText().toString().contains("  ")) {
                SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "space_validation")));
            } else {
                editProfileCall();
            }
        } else if (v == cv_my_profile1) {
            dialog();
        } else if (v == iv_close_header1) {
            sv_my_profile1.setVisibility(View.VISIBLE);
            ll_about_us.setVisibility(View.GONE);
        }
    }

    private void editProfileCall() {
        if (image == true) {
            mEditor.putString("User_ProfilePic", "").commit();
            imgName = "";
        }
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=EditProfile";
            String json = "[{\"User_Email\":\"" + mPreferences.getString("User_Email", "") + "\",\"User_Name\":\"" + edt_profile_name1.getText().toString() + "\",\"User_ProfilePic\":\"" + imgName + "\",\"User_Address\":\"" + edt_address1.getText().toString() + "\",\"User_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"User_Longi\":\"" + mPreferences.getString("longitude2", "") + "\",\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\",\"User_About\":\"" + "" + "\",\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\"}]";
            new post_sync(getActivity(), "EditProfile", MyProfileFragment1.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void editProfileResponse(String resultString) {
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
                        JSONArray jsonArray1 = jsonObject.optJSONArray("UserDetails");
                        JSONObject jsonObject1 = jsonArray1.optJSONObject(0);
                        mEditor.putString("User_Name", jsonObject1.optString("User_Name")).commit();
                        mEditor.putString("User_Address", jsonObject1.optString("User_Address")).commit();
                        mEditor.putString("mAddress", jsonObject1.optString("User_Address")).commit();
                        mEditor.putString("User_ProfilePic", jsonObject1.optString("User_ProfilePic")).commit();
                        sv_my_profile1.setVisibility(View.VISIBLE);
                        sv_my_profile2.setVisibility(View.GONE);
                        if (!String.valueOf(lan_id).equalsIgnoreCase(mPreferences.getString("Lan_Id", ""))) {
                            mEditor.putString("Lan_Id", String.valueOf(lan_id)).commit();
                            Constants.mStatic = 2;
                            Constants.mStaticFavCall = 0;
                            Constants.mStaticNearCall = 0;
                            Constants.mFromSelectLocation = 0;
                            Intent mIntent = new Intent(getActivity(), MainActivity.class);
                            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mIntent);
                            getActivity().finish();
                        }
                        SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "PROFILEUPDATE")));
                        tv_profile_name.setText(mPreferences.getString("User_Name", ""));
                        tv_address.setText(mPreferences.getString("mAddress", ""));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("EditProfile")) {
                editProfileResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    public void dialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_gallery);
        dialog.setCancelable(false);
        NormalTextView gal = (NormalTextView) dialog.findViewById(R.id.gal);
        NormalTextView camera = (NormalTextView) dialog.findViewById(R.id.camera);
        NormalTextView remove = (NormalTextView) dialog.findViewById(R.id.remove);
        remove.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "remove"));
        gal.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "gallery"));
        ImageView closebtn = (ImageView) dialog.findViewById(R.id.closebtn);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        gal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // To open up a gallery browser
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dispatchTakePictureIntent(TAKE_PICTURE);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv_my_profile1.setImageResource(R.drawable.profile_pic);
                image = true;
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    //Newly added code for image setting starts here
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {

            if (requestCode == 1) {
                // currImageURI is the global variable I?m using to hold the content:
                currImageURI = data.getData();
                doCrop();
            } else if (requestCode == CROP_FROM_CAMERA) {
                currImageURI = data.getData();
                Bundle extras = data.getExtras();
                if (extras != null) {
                    photo = extras.getParcelable("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                }
                cv_my_profile1.setImageBitmap(photo);
                cv_my_profile.setImageBitmap(photo);
            }
        }
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(getActivity(), "Can not find image crop app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(currImageURI);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);
                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();
                    co.title = getActivity().getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getActivity().getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }
                CropOptionAdapter adapter = new CropOptionAdapter(getActivity(), cropOptions);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (currImageURI != null) {
                            getActivity().getContentResolver().delete(currImageURI, null, null);
                            currImageURI = null;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        timeForImgname = System.currentTimeMillis();
        imgName = "img_" + timeForImgname + ".jpg";
        f = new File(Environment.getExternalStorageDirectory(), imgName);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(cameraIntent, 1);
    }

    public String getPath1(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        Cursor cursor = null;
        int currentapiVersion = Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.KITKAT) {
            @SuppressLint({"NewApi", "LocalSuppress"}) String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] projection = {MediaStore.Images.Media.DATA};
            String whereClause = MediaStore.Images.Media._ID + "=?";
            cursor = getActivity().getContentResolver().query(getUri(), projection, whereClause, new String[]{id}, null);
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = getActivity().managedQuery(uri, projection, null, null, null);
        }

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    private Bitmap ImageCompression(Bitmap bmpPic) {
        int MAX_IMAGE_SIZE = 1024 * 1024; // max final file size
        // Bitmap bmpPic = BitmapFactory.decodeFile(imgDecodableString);
        int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
        int streamLength = MAX_IMAGE_SIZE;
        while (streamLength >= MAX_IMAGE_SIZE) {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            compressQuality -= 5;
            Log.e(TAG, "ImageCompression -> Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            Log.e(TAG, "ImageCompression -> Size: " + streamLength);
        }
        return bmpPic;
    }

    private void pageInfo() {
        wb_about_us.getSettings().setLoadWithOverviewMode(true);
        wb_about_us.loadUrl("http://ec2-54-93-117-123.eu-central-1.compute.amazonaws.com/cmsdetail.php?pagename=aboutus&Lan_Id=" + mPreferences.getString("Lan_Id", ""));
        wb_about_us.getSettings().setUseWideViewPort(true);
        wb_about_us.getSettings().setDefaultFixedFontSize(23);
        wb_about_us.getSettings().setDefaultFontSize(23);
        wb_about_us.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("Email:")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("info@mytourisma.com"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@mytourisma.com"});
                    emailIntent.setType("text/plain");
                    final PackageManager pm = getActivity().getPackageManager();
                    final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                    ResolveInfo best = null;
                    for (final ResolveInfo info : matches)
                        if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                            best = info;
                    if (best != null)
                        emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                    getActivity().startActivity(emailIntent);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Language");
        menu.add(0, 0, 0, "English");
        menu.add(0, 1, 0, "Russian");
        menu.add(0, 2, 0, "Arabic");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 0) {
            String language = item.getTitle().toString();
            tv_profile_language1.setSelection(item.getGroupId());
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        yourLocationFragmentActivity = (MainActivity) getActivity();
    }

    class RetrieveFeedTask extends AsyncTask<String, String, String> {
        private Exception exception;

        protected void onPreExecute() {
            dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
            progressbar = new ProgressBar(getActivity());
            progressbar.setBackgroundResource(R.drawable.progress_background);
            dialog.addContentView(progressbar, new LinearLayout.LayoutParams(40, 40));
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            dialog.show();
        }

        protected String doInBackground(String... urls) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Log.i("System out", "selectedImagePath : " + selectedImagePath);
                timeForImgname = System.currentTimeMillis();
                imgName = /* myPref.optString("path", "") */"img" + timeForImgname + ".jpg";
                byte[] data = bos.toByteArray();
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(Constants.IMG_URL + "?");
                String[] split;
                ByteArrayBody bab = new ByteArrayBody(data, imgName);
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                Log.i("System out", "2");
                reqEntity.addPart("action", new StringBody("imageUpload"));
                reqEntity.addPart("imagePath", new StringBody("user"));
                reqEntity.addPart("imageField", bab);
                postRequest.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(postRequest);
                Log.i("System out", "5" + response);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();
                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                String ResponseString = s.toString();
                try {
                    JSONArray jarray = new JSONArray(s.toString());
                    JSONObject jobj = jarray.getJSONObject(0);
                    selectedImagePath = jobj.getString("fileName");
                } catch (Exception e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            dialog.dismiss();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}

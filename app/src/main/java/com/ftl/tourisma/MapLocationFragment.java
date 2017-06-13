package com.ftl.tourisma;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.GPSTracker;
import com.ftl.tourisma.utils.JSONParser;
import com.ftl.tourisma.utils.Utils;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harpalsinh on 27-Feb-2016.
 */
public class MapLocationFragment extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    List<LatLng> pontos = null;
    private TextView tv_map_location;
    private SupportMapFragment map_direction1;
    private GoogleMap map;
    private String latitude, longitude;
    private Double latitude1, longitude1;
    private Marker marker, marker1;
    private ImageView iv_close_header1;
    private TextView tv_cate_name;
    private String placeName, dist;
    private FloatingActionButton fab1_single, fab2_single;
    private String direction;
    private GPSTracker gpsTracker;
    private String mDirection = "";
    private String imgName = "";
    private MapWrapperLayout mapWrapper;
    private ViewGroup infoWindow;
    private SharedPreferences mPreferences;
    private TextView tv_pin_address;
    private String address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.direction);

        mPreferences = getSharedPreferences(Constants.mPref, 0);

        Intent mIntent = getIntent();
        placeName = mIntent.getStringExtra("placeName");
        dist = mIntent.getStringExtra("dist");
        latitude = mIntent.getStringExtra("latitude");
        longitude = mIntent.getStringExtra("longitude");
        mDirection = mIntent.getStringExtra("mDirection");
        imgName = mIntent.getStringExtra("imgName");
        address = mIntent.getStringExtra("address");

        initialisation();

        gpsTracker = new GPSTracker(MapLocationFragment.this);
        if (mPreferences.getString("latitude1", "").equalsIgnoreCase("0.0") || mPreferences.getString("longitude1", "").equalsIgnoreCase("0.0") || mPreferences.getString("latitude1", "").equalsIgnoreCase("") || mPreferences.getString("longitude1", "").equalsIgnoreCase("")) {
            if (gpsTracker.canGetLocation()) {
                gpsTracker.getLocation();
                mPreferences.edit().putString("latitude1", String.valueOf(gpsTracker.getLatitude())).commit();
                mPreferences.edit().putString("latitude1", String.valueOf(gpsTracker.getLongitude())).commit();
            } else {
                gpsTracker.showSettingsAlert();
            }
        }



        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            fab1_single.setImageResource(R.drawable.gps_icon_map_selected);
        } else {
            fab1_single.setImageResource(R.drawable.gps_icon_map);
        }

    }

    private void initialisation() {
        tv_pin_address = (TextView) findViewById(R.id.tv_pin_address);
        tv_pin_address.setText(address.toString());
        fab1_single = (FloatingActionButton) findViewById(R.id.fab1_single);
        fab1_single.setOnClickListener(this);
        fab2_single = (FloatingActionButton) findViewById(R.id.fab2_single);
        fab2_single.setOnClickListener(this);
        tv_cate_name = (TextView) findViewById(R.id.tv_cate_name);
        iv_close_header1 = (ImageView) findViewById(R.id.img_close);
        iv_close_header1.setOnClickListener(this);
//        map_direction1 = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.map_direction1)).getMap();
        map_direction1 = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_direction1);
        map_direction1.getMapAsync(this);
        mapWrapper = (MapWrapperLayout) findViewById(R.id.mapWrapper);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.map_pin_popup, null);
                final TextView infoTitle = (TextView) infoWindow.findViewById(R.id.infoTitle);
                infoTitle.setText(placeName);
                mapWrapper.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == iv_close_header1) {
            finish();
        } else if (v == fab2_single) {
            fab2_single.setImageResource(R.drawable.direction_icon_map);
            map.clear();
            getSingleLocation1();
            new GetDirection().execute();
        }
    }

    private void getSingleLocation() {
        try {
            tv_cate_name.setText(placeName);
            map.clear();
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", ""))), 15));
            try {
                latitude1 = Double.parseDouble(latitude);
                longitude1 = Double.parseDouble(longitude);
            } catch (NumberFormatException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                Log.e("System out", e.getMessage());
            }
            try {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                map.setMyLocationEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setCompassEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(true);
                map.setTrafficEnabled(true);
                View marker1 = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pin_popup, null);
                final ImageView pin_image = (ImageView) marker1.findViewById(R.id.pin_image);
                marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude1, longitude1))
                        .snippet("" + 0)
                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker1))));
                b.include(new LatLng(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", ""))));
                b.include(new LatLng(latitude1, longitude1));
            } catch (Exception e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                Log.e("System out", e.getMessage());
            }
            LatLngBounds bounds = b.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150, 150, 3);
            map.animateCamera(cu);
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e("System out", "IllegalStateException " + e.getMessage());
        }
    }

    private void getSingleLocation1() {
        try {
            tv_cate_name.setText(placeName);
            map.clear();
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", ""))), 15));
            try {
                latitude1 = Double.parseDouble(latitude);
                longitude1 = Double.parseDouble(longitude);
            } catch (NumberFormatException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                Log.e("System out", e.getMessage());
            }
            try {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                map.setMyLocationEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setCompassEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(true);
                map.setTrafficEnabled(true);
                View marker1 = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pin_popup, null);
                final ImageView pin_image = (ImageView) marker1.findViewById(R.id.pin_image);

                marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude1, longitude1))
                        .snippet("" + 0)
                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker1))));

                this.marker1 = map.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", ""))))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                b.include(this.marker1.getPosition());
                b.include(marker.getPosition());

            } catch (Exception e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                Log.e("System out", e.getMessage());
            }
            LatLngBounds bounds = b.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 400, 400, 3);
            map.animateCamera(cu);
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e("System out", "IllegalStateException " + e.getMessage());
        }
    }

    // Convert a view to bitmap
    public Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (mDirection.equalsIgnoreCase("yes")) {
            getSingleLocation1();
            fab2_single.setImageResource(R.drawable.direction_icon_map);
            new GetDirection().execute();
        } else if (mDirection.equalsIgnoreCase("no")) {
            getSingleLocation();
        }
    }

    // This Map Direction.
    class GetDirection extends AsyncTask<String, String, List<LatLng>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        protected List<LatLng> doInBackground(String... args) {
            String origin = mPreferences.getString("latitude1", "") + "," + mPreferences.getString("longitude1", "");
            String destination = latitude1 + "," + longitude1;
            String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin="
                    + origin + "&destination=" + destination + "&sensor=false";
            try {
                JSONParser json_parser = new JSONParser();
                String jsonOutput = json_parser.makeServiceCall(stringUrl, "GET");
                JSONObject jsonObject = new JSONObject(jsonOutput);
                if (jsonObject.length() > 0) {
                    String status = "";
                    if (jsonObject.has("status")) {
                        status = jsonObject.optString("status");
                        if (status.equalsIgnoreCase("ZERO_RESULTS")) {
                            return pontos;
                        } else {
                            JSONArray routesArray = jsonObject
                                    .getJSONArray("routes");
                            if (routesArray.length() > 0) {
                                // Grab the first route
                                JSONObject route = routesArray.getJSONObject(0);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyline = poly.getString("points");
                                pontos = decodePoly(polyline);
                            }
                        }
                    } else {
                        return pontos;
                    }
                } else {
                    return pontos;
                }
            } catch (Exception e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                Log.e("System out", e.getMessage());
            }
            return pontos;
        }

        protected void onPostExecute(List<LatLng> result) {
            super.onPostExecute(result);
            if (result != null) {
                for (int i = 0; i < pontos.size() - 1; i++) {
                    LatLng src = pontos.get(i);
                    LatLng dest = pontos.get(i + 1);
                    try {
                        // here is where it will draw the polyline in your map
                        Polyline line = map
                                .addPolyline(new PolylineOptions()
                                        .add(new LatLng(src.latitude,
                                                        src.longitude),
                                                new LatLng(dest.latitude,
                                                        dest.longitude))
                                        .width(6).color(Color.BLUE)
                                        .geodesic(true));
                    } catch (NullPointerException e) {
                        // Tracking exception
                        MyTorismaApplication.getInstance().trackException(e);
                        Log.e("System out", "NullPointerException onPostExecute: "
                                + e.toString());
                    } catch (Exception e2) {
                        Log.e("System out",
                                "Exception onPostExecute: " + e2.toString());
                    }
                }
                try {
                } catch (IllegalArgumentException e) {
                    Log.e("System out", e.getMessage());
                }
            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        SnackbarManager.show(Snackbar.with(MapLocationFragment.this).color(Utils.getColor(MapLocationFragment.this, R.color.mBlue)).text("Route not found."));
                    }
                });
            }
        }
    }
}

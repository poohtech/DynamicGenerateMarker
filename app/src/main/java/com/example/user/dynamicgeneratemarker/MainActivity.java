package com.example.user.dynamicgeneratemarker;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    // Google Map
    private GoogleMap googleMap;

    private Location loc;
    private LocationManager LC;
    private double latitude;
    private double longitude;

    /* for add multiple markers */
    private ArrayList<LocationBean> pinsArrayList;
    private String imageFile;
    private MarkerOptions currentLocMarker;
    private DownloadImageAsync downloadImageAsync;
    private ArrayList<ImageBean> imgArray;
    private ImageBean imageBean;
    private double pinLat;
    private double pinLon;
    private String name, color;
    private LocationBean locBean;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        MapsInitializer.initialize(this);

        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initilizeMap() {
        if (googleMap == null) {

            // Gets to GoogleMap from the MapView and does initialization stuff
            googleMap = mapView.getMap();

            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable MyLocation Button in the Map
            googleMap.setMyLocationEnabled(true);

            // Zooming Buttons
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            // Compass Functionality
            googleMap.getUiSettings().setCompassEnabled(true);

            // Map Rotate Gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            getCurrentLocation();

            pinsArrayList = new ArrayList<LocationBean>();
            locBean = new LocationBean();
            locBean.name = "Riddhi";
            locBean.latitude = "23.064483";
            locBean.longitude = "72.551289";
            locBean.color = "FFA900";
            pinsArrayList.add(locBean);

            locBean = new LocationBean();
            locBean.name = "Nancy";
            locBean.latitude = "23.053751";
            locBean.longitude = "72.276492";
            locBean.color = "7FFF00";
            pinsArrayList.add(locBean);

            locBean = new LocationBean();
            locBean.name = "Vaishali";
            locBean.latitude = "23.070901";
            locBean.longitude = "72.563418";
            locBean.color = "FF007F";
            pinsArrayList.add(locBean);

			/* fill ArrayList of Marker */
            if (pinsArrayList != null && pinsArrayList.size() > 0) {

                imgArray = new ArrayList<ImageBean>();

                for (int i = 0; i < pinsArrayList.size(); i++) {

                    System.out.println("=======pinsArrayList.size()====="
                            + pinsArrayList.size());

                    pinLat = Double
                            .parseDouble(pinsArrayList.get(i).latitude);
                    pinLon = Double
                            .parseDouble(pinsArrayList.get(i).longitude);
                    name = pinsArrayList.get(i).name;
                    color = pinsArrayList.get(i).color;

                    File file = new File(Environment.getExternalStorageDirectory()
                            .getPath() + "/DynamicMarker" + File.separator
                            + "pin_" + name.charAt(0) + ".png");
                    System.out.println("%%%%%%%%%%%%file.exists()%%%%%%%"
                            + file.exists());
                    if (file.exists()) {
                        System.out
                                .println("---------file.exists():::pinLat:---------"
                                        + pinLat);
                        System.out
                                .println("---------file.exists():::pinLon:---------"
                                        + pinLon);

                        imageFile = Environment.getExternalStorageDirectory()
                                .getPath() + "/DynamicMarker" + File.separator
                                + "pin_" + name.charAt(0) + ".png";
                        if (currentLocMarker == null) {
                            currentLocMarker = new MarkerOptions();
                        }
                        currentLocMarker.title(name);
                        currentLocMarker.position(
                                new LatLng(pinLat, pinLon)).icon(
                                BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory
                                                .decodeFile(imageFile)));
                        googleMap.addMarker(currentLocMarker);
                    } else {
                        System.out
                                .println("---------not.exists():::pinLat:---------"
                                        + pinLat);
                        System.out
                                .println("---------not.exists():::pinLon:---------"
                                        + pinLon);
                        System.out
                                .println("---------not.exists():::ImagePath:---------"
                                        + Environment.getExternalStorageDirectory()
                                        .getPath() + "/DynamicMarker"
                                        + File.separator
                                        + "pin_" + name.charAt(0) + ".png");


                        imageBean = new ImageBean();
                        imageBean.image_path = Environment.getExternalStorageDirectory()
                                .getPath() + "/DynamicMarker"
                                + File.separator + "pin_" + name.charAt(0) + ".png";
                        imageBean.name = name;
                        imageBean.img_latitude = pinLat;
                        imageBean.img_longitude = pinLon;
                        imageBean.color = color;
                        imgArray.add(imageBean);
                    }

                }
                System.out
                        .println("$$$$$$$$$$$$$$$$imgArray.size()$$$$$$$$$$:::::"
                                + imgArray.size());
                if (imgArray.size() > 0) {
                    downloadImageAsync = new DownloadImageAsync(true);
                    downloadImageAsync.execute();
                }
            } else {
                // create marker on current position
                MarkerOptions marker = new MarkerOptions().position(new LatLng(
                        latitude, longitude));
                // adding marker
                googleMap.addMarker(marker);

            }

            // Moving Camera to a Location with animation
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(11).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(MainActivity.this, "Sorry! unable to create maps",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* Get Current Location from location Listener */
    private void getCurrentLocation() {

        if (googleMap.getMyLocation() != null) {
            latitude = (double) googleMap.getMyLocation().getLatitude();
            longitude = (double) googleMap.getMyLocation().getLongitude();
        } else {

            LC = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            loc = LC.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (loc != null) {
                latitude = (double) loc.getLatitude();
                longitude = (double) loc.getLongitude();
            } else {

                loc = LC.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (loc != null) {
                    loc = LC.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    latitude = (double) loc.getLatitude();
                    longitude = (double) loc.getLongitude();
                } else {
                    Toast.makeText(MainActivity.this, "Location not available",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    /**
     * To download marker
     */
    public class DownloadImageAsync extends AsyncTask<Void, String, Void> {

        private String source;

        public DownloadImageAsync(Boolean showProgress) {
            if (showProgress) {
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... args) {
            File file;
            int count;
            URL url = null;
            HttpURLConnection connection = null;
            InputStream input = null;
            OutputStream output = null;
            try {

                for (int i = 0; i < imgArray.size(); i++) {

                    file = new File(imgArray.get(i).image_path);

                    if (!file.exists()) {
                        /** create user folder into sdcard */
                        Utils.verifyUserPath();

                        source = Utils.FILE_PICTURE_URL + "1.1%7C0%7C"
                                + imgArray.get(i).color
                                + "%7C25%7Cb%7C"
                                + imgArray.get(i).name.charAt(0);
                        url = new URL(source);

                        System.out.println("@@@@@@@@@@@@@:::::URL :: " + url);

                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Accept-Language",
                                "en-us,en;q=0.5");
                        connection.setDoInput(true);

                        if (HttpURLConnection.HTTP_OK == connection
                                .getResponseCode()) {

                            input = new BufferedInputStream(url.openStream());
                            output = new FileOutputStream(
                                    imgArray.get(i).image_path.toString());

                            byte data[] = new byte[2048];

                            while ((count = input.read(data)) != -1) {
                                System.out.println(" :: " + count);

                                output.write(data, 0, count);
                            }
                            output.flush();
                        } else {
                            System.out.println("Status : "
                                    + connection.getResponseCode());
                        }
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();
                System.out.println(this.getClass() + "" + e);
                System.out.println(this.getClass() + "" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            System.out
                    .println("*******onPostExecute:::::::imgArray.size()**********"
                            + imgArray.size());
            isSetMarker(imgArray);

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

        }
    }

    public void isSetMarker(ArrayList<ImageBean> imgArray) {
        ArrayList<ImageBean> localArrayList = new ArrayList<ImageBean>();
        System.out.println("*******isSetMarker::::::: " + imgArray.size());
        for (int i = 0; i < imgArray.size(); i++) {
            File f = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/DynamicMarker" + File.separator + "pin_"
                    + imgArray.get(i).name.charAt(0) + ".png");
            System.out.println("*******isSetMarker:::::::f.exists()*********"
                    + f.exists());
            if (f.exists()) {
                if (currentLocMarker == null) {
                    currentLocMarker = new MarkerOptions();
                }
                System.out
                        .println("*******isSetMarker:::::::imgArray.get(i).img_latitude********"
                                + imgArray.get(i).img_latitude);
                System.out
                        .println("*******isSetMarker::::::imgArray.get(i).img_longitude*******"
                                + imgArray.get(i).img_longitude);
                System.out
                        .println("*******Before --------- isSetMarker::::imgArray.get(i).imagePath******"
                                + imgArray.get(i).image_path);

                currentLocMarker.title(imgArray.get(i).name);
                currentLocMarker.position(
                        new LatLng(imgArray.get(i).img_latitude, imgArray
                                .get(i).img_longitude)).icon(
                        BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                .decodeFile(imgArray.get(i).image_path)));
                googleMap.addMarker(currentLocMarker);
            } else {
                localArrayList.add(imgArray.get(i));
            }
        }
        System.out
                .println("*******isSetMarker:::::::localArrayList.size()**********"
                        + localArrayList.size());
        if (localArrayList.size() > 0) {
            isSetMarker(localArrayList);
        }

    }
}

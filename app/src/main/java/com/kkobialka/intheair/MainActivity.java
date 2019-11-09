package com.kkobialka.intheair;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.emredavarci.circleprogressbar.CircleProgressBar;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Line;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tuyenmonkey.mkloader.MKLoader;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    ImageView icon;
    LinearLayout textViewFoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        icon = (ImageView) findViewById(R.id.icon);
        textViewFoot = (LinearLayout) findViewById(R.id.text_foot);
        icon.setVisibility(View.GONE);
        textViewFoot.setVisibility(View.GONE);

        Dexter.withActivity(this).withPermissions(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted()){
                            buildLocationRequest();
                            buildLocationCallback();
                        }

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {

                                    if (location != null) {

                                        icon.setVisibility(View.VISIBLE);
                                        textViewFoot.setVisibility(View.VISIBLE);
                                        Animation myAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.my_animation);
                                        icon.startAnimation(myAnimation);
                                        textViewFoot.startAnimation(myAnimation);

                                        final Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                                        Thread thread = new Thread() {
                                            public void run() {
                                                try {
                                                    sleep(3000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    startActivity(intent);
                                                    finish();
                                                }

                                            }
                                        };
                                        thread.start();

                                    } else {

                                        icon.setVisibility(View.VISIBLE);
                                        textViewFoot.setVisibility(View.VISIBLE);
                                        Animation myAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.my_animation);
                                        icon.startAnimation(myAnimation);
                                        textViewFoot.startAnimation(myAnimation);

                                        final Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                                        Thread thread = new Thread() {
                                            public void run() {
                                                try {
                                                    sleep(3000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    startActivity(intent);
                                                    finish();
                                                }

                                            }
                                        };
                                        thread.start();

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                        dialog.setMessage("For better service please enable location using Google Play Services");
                                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                        dialog.show();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            return;
                        }
                        else {
                            icon.setVisibility(View.VISIBLE);
                            textViewFoot.setVisibility(View.VISIBLE);
                            Animation myAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.my_animation);
                            icon.startAnimation(myAnimation);
                            textViewFoot.startAnimation(myAnimation);

                            final Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                            Thread thread = new Thread() {
                                public void run() {
                                    try {
                                        sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } finally {
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            };
                            thread.start();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        icon.setVisibility(View.VISIBLE);
                        textViewFoot.setVisibility(View.VISIBLE);
                        Animation myAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.my_animation);
                        icon.startAnimation(myAnimation);
                        textViewFoot.startAnimation(myAnimation);

                        final Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                        Thread thread = new Thread() {
                            public void run() {
                                try {
                                    sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } finally {
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        };
                        thread.start();

                        Toast.makeText(MainActivity.this, "Location permissions denied", Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setMessage("For better service please enable location using Google Play Services");
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                    }
                }).check();

    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
    }

}

package com.kkobialka.intheair;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Map layout
    private GoogleMap mMap;

    TextView textViewCity, textViewAqi, textViewPm25, textViewCo, textViewNo2, textViewO3, textViewSo2, textViewPm10;

    TextView textViewPressure, textViewTemp, textViewDescription, textViewHumidity,
            textViewWind, textViewSunrise, textViewSunset, textHome, textMap, textSearch, textInfo;

    BottomSheetBehavior bottomSheetBehavior;

    ImageView imageViewArrow, imageMap, imageSearch, imageHome, imageInfo;

    ValueLineChart mCubicValueLineChart;

    Button buttonTemp, buttonPressure, buttonHumidity, buttonWind;

    FusedLocationProviderClient fusedLocationProviderClient;

    RelativeLayout mapLayout, infoLayout, searchLayout, homeLayout;

    LinearLayout buttonHome, buttonSearch, buttonMap, buttonInfo, buttonSettings;

    NestedScrollView nestedScrollView;

    //Search layout
    FloatingActionButton floatingActionButton;

    private ArrayList<ItemView> itemViewArrayList;

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    EditText editCity;

    //Home layout
    TextView textPm25, textCo, textNo2, textO3, textPm10, textSo2, textCityName, textDescriptionMain,
            textTempNow, textPressureNow, textHumidityNow, textWindNow, textSunriseNow, textSunsetNow,
            textLocation; //textDescriptionMain

    FusedLocationProviderClient fusedLocationProviderClientMain; //fusedLocationProviderClientMain
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    LinearLayout relativeView, linearViewPm25, linearViewPm10, linearViewNo2, linearViewO3, linearViewSo2, linearViewCo;

    CircleProgressBar progressBarPm10, progressBarPm25, progressBarCo, progressBarNo2, progressBaro3, progressBarSo2;

    MKLoader loadingIndicator;

    LinearLayout weatherNowView, buttonsView, buttonsViewDetailsMain;

    HorizontalScrollView horizontalScrollView;

    ArcProgress arcProgress;

    ValueLineChart mCubicValueLineChartMain; //mCubicValueLineChartMain

    Button buttonTempMain, buttonPressureMain, buttonHumidityMain, buttonWindMain; //buttonTempMain, buttonPressureMain, buttonHumidityMain, buttonWindMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Map Layout

        textViewCity = (TextView) findViewById(R.id.city_text);
        textViewAqi = (TextView) findViewById(R.id.aqi_text);
        textViewPm25 = (TextView) findViewById(R.id.pm25_text);
        textViewCo = (TextView) findViewById(R.id.co_text);
        textViewNo2 = (TextView) findViewById(R.id.no2_text);
        textViewO3 = (TextView) findViewById(R.id.o3_text);
        textViewSo2 = (TextView) findViewById(R.id.so2_text);
        textViewPm10 = (TextView) findViewById(R.id.pm10_text);
        textViewPressure = (TextView) findViewById(R.id.text_pressure);
        textViewTemp = (TextView) findViewById(R.id.text_temp);
        textViewDescription = (TextView) findViewById(R.id.text_description);
        textViewHumidity = (TextView) findViewById(R.id.text_humidity);
        textViewWind = (TextView) findViewById(R.id.text_wind);
        textViewSunrise = (TextView) findViewById(R.id.text_sunrise);
        textViewSunset = (TextView) findViewById(R.id.text_sunset);

        imageViewArrow = (ImageView) findViewById(R.id.image_arrow);

        mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

        buttonTemp = (Button) findViewById(R.id.button_temp);
        buttonPressure = (Button) findViewById(R.id.button_pressure);
        buttonHumidity = (Button) findViewById(R.id.button_humidity);
        buttonWind = (Button) findViewById(R.id.button_wind);

        mapLayout = (RelativeLayout) findViewById(R.id.map_layout);
        infoLayout = (RelativeLayout) findViewById(R.id.info_layout);
        searchLayout = (RelativeLayout) findViewById(R.id.search_layout);
        homeLayout = (RelativeLayout) findViewById(R.id.home_layout);

        nestedScrollView = (NestedScrollView) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        imageViewArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_up));
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        imageViewArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down));
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //Search Layout
        loadData();

        editCity = (EditText) findViewById(R.id.edit_city);

        buildRecyclerView();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editCity.length() < 1) {
                    Toast.makeText(MapsActivity.this, "Please type city name", Toast.LENGTH_SHORT).show();
                }

                insertItem(0);
            }
        });

        //Home Layout
        Dexter.withActivity(this).withPermissions(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            buildLocationRequest();
                            buildLocationCallback();
                        }

                        if (ActivityCompat.checkSelfPermission(MapsActivity.this, ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        fusedLocationProviderClientMain = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
                        fusedLocationProviderClientMain.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        fusedLocationProviderClientMain.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location != null) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();

                                    getAqiInformationMain(latitude, longitude);

                                    getWeatherForecastMain(latitude, longitude);

                                    getWeatherNow(latitude, longitude);

                                } else {

                                    //London
                                    //latitude = 51.5073509;
                                    //longitude = -0.1277583;

                                    //Shanghai
                                    //latitude = 31.267401;
                                    //longitude = 121.522179;

                                    //Tokyo
                                    //latitude = 35.652832;
                                    //longitude = 139.839478;

                                    //Delhi
                                    //latitude = 28.644800;
                                    //longitude = 77.216721;

                                    //Warszawa
                                    double latitude = 52.22977;
                                    double longitude = 21.01178;

                                    getAqiInformationMain(latitude, longitude);

                                    getWeatherForecastMain(latitude, longitude);

                                    getWeatherNow(latitude, longitude);

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
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
                                Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        Toast.makeText(MapsActivity.this, "Location permissions denied", Toast.LENGTH_SHORT).show();

                        double latitude = 52.22977;
                        double longitude = 21.01178;

                        getAqiInformationMain(latitude, longitude);

                        getWeatherForecastMain(latitude, longitude);

                        getWeatherNow(latitude, longitude);

                        AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                        dialog.setMessage("For better service please enable location using Google Play Services");
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                    }
                }).check();

        arcProgress = (ArcProgress) findViewById(R.id.arc_progress);

        loadingIndicator = (MKLoader) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        textPm25 = (TextView) findViewById(R.id.text_pm25);
        textCo = (TextView) findViewById(R.id.text_co);
        textNo2 = (TextView) findViewById(R.id.text_no2);
        textO3 = (TextView) findViewById(R.id.text_o3);
        textPm10 = (TextView) findViewById(R.id.text_pm10);
        textSo2 = (TextView) findViewById(R.id.text_so2);
        textCityName = (TextView) findViewById(R.id.text_city_name);
        textDescriptionMain = (TextView) findViewById(R.id.text_description_main);
        textTempNow = (TextView) findViewById(R.id.text_temp_now);
        textPressureNow = (TextView) findViewById(R.id.text_press_now);
        textWindNow = (TextView) findViewById(R.id.text_wind_now);
        textHumidityNow = (TextView) findViewById(R.id.text_humidity_now);
        textSunriseNow = (TextView) findViewById(R.id.text_sunrise_now);
        textSunsetNow = (TextView) findViewById(R.id.text_sunset_now);
        textLocation = (TextView) findViewById(R.id.text_location);

        relativeView = (LinearLayout) findViewById(R.id.relative_view);

        progressBarPm10 = (CircleProgressBar) findViewById(R.id.progress_bar_pm10);
        progressBarPm25 = (CircleProgressBar) findViewById(R.id.progress_bar_pm25);
        progressBarCo = (CircleProgressBar) findViewById(R.id.progress_bar_co);
        progressBarNo2 = (CircleProgressBar) findViewById(R.id.progress_bar_no2);
        progressBaro3 = (CircleProgressBar) findViewById(R.id.progress_bar_o3);
        progressBarSo2 = (CircleProgressBar) findViewById(R.id.progress_bar_so2);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scroll_view);

        mCubicValueLineChartMain = (ValueLineChart) findViewById(R.id.cubiclinechart_main);
        weatherNowView = (LinearLayout) findViewById(R.id.weather_now_view);
        buttonsView = (LinearLayout) findViewById(R.id.buttons_view);
        buttonsViewDetailsMain = (LinearLayout) findViewById(R.id.buttons_view_details_main);

        buttonsView.setVisibility(View.GONE);
        buttonsViewDetailsMain.setVisibility(View.GONE);
        mCubicValueLineChartMain.setVisibility(View.GONE);
        weatherNowView.setVisibility(View.GONE);
        relativeView.setVisibility(View.GONE);
        horizontalScrollView.setVisibility(View.GONE);

        buttonTempMain = (Button) findViewById(R.id.button_temp_main);
        buttonPressureMain = (Button) findViewById(R.id.button_pressure_main);
        buttonHumidityMain = (Button) findViewById(R.id.button_humidity_main);
        buttonWindMain = (Button) findViewById(R.id.button_wind_main);

        linearViewPm25 = (LinearLayout) findViewById(R.id.linear_view_pm25);
        linearViewPm25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                final View view2 = factory.inflate(R.layout.alert_pm25, null);
                dialog.setView(view2);
                dialog.setPositiveButton("Go to Wikipedia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonUrl("https://en.wikipedia.org/wiki/Particulates");
                    }
                });
                dialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });

        linearViewPm10 = (LinearLayout) findViewById(R.id.linear_view_pm10);
        linearViewPm10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                final View view2 = factory.inflate(R.layout.alert_pm10, null);
                dialog.setView(view2);
                dialog.setPositiveButton("Go to Wikipedia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonUrl("https://en.wikipedia.org/wiki/Particulates");
                    }
                });
                dialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });

        linearViewNo2 = (LinearLayout) findViewById(R.id.linear_view_no2);
        linearViewNo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                final View view2 = factory.inflate(R.layout.alert_no2, null);
                dialog.setView(view2);
                dialog.setPositiveButton("Go to Wikipedia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonUrl("https://en.wikipedia.org/wiki/Nitrogen_dioxide");
                    }
                });
                dialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });

        linearViewO3 = (LinearLayout) findViewById(R.id.linear_view_o3);
        linearViewO3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                final View view2 = factory.inflate(R.layout.alert_o3, null);
                dialog.setView(view2);
                dialog.setPositiveButton("Go to Wikipedia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonUrl("https://en.wikipedia.org/wiki/Ozone");
                    }
                });
                dialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });

        linearViewSo2 = (LinearLayout) findViewById(R.id.linear_view_so2);
        linearViewSo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                final View view2 = factory.inflate(R.layout.alert_so2, null);
                dialog.setView(view2);
                dialog.setPositiveButton("Go to Wikipedia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonUrl("https://en.wikipedia.org/wiki/Sulfur_dioxide");
                    }
                });
                dialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });

        linearViewCo = (LinearLayout) findViewById(R.id.linear_view_co);
        linearViewCo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                final View view2 = factory.inflate(R.layout.alert_co, null);
                dialog.setView(view2);
                dialog.setPositiveButton("Go to Wikipedia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonUrl("https://en.wikipedia.org/wiki/Carbon_monoxide");
                    }
                });
                dialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });

        //Bottom Nav
        buttonHome = (LinearLayout) findViewById(R.id.button_home);
        buttonMap = (LinearLayout) findViewById(R.id.button_map);
        buttonSearch = (LinearLayout) findViewById(R.id.button_search);
        buttonInfo = (LinearLayout) findViewById(R.id.button_info);
        buttonSettings = (LinearLayout) findViewById(R.id.button_settings);

        imageHome = (ImageView) findViewById(R.id.image_home);
        imageMap = (ImageView) findViewById(R.id.image_map);
        imageSearch = (ImageView) findViewById(R.id.image_search);
        imageInfo = (ImageView) findViewById(R.id.image_info);

        textHome = (TextView) findViewById(R.id.text_home);
        textMap = (TextView) findViewById(R.id.text_map);
        textSearch = (TextView) findViewById(R.id.text_search);
        textInfo = (TextView) findViewById(R.id.text_info);

        imageHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_selected));
        imageMap.setImageDrawable(getResources().getDrawable(R.drawable.ic_map));
        imageInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_info));
        imageSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        textHome.setTextColor(getResources().getColor(R.color.colorAccent));
        textMap.setTextColor(getResources().getColor(R.color.colorTextLight));
        textSearch.setTextColor(getResources().getColor(R.color.colorTextLight));
        textInfo.setTextColor(getResources().getColor(R.color.colorTextLight));

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeLayout.setVisibility(View.VISIBLE);
                infoLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.GONE);
                imageHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_selected));
                imageMap.setImageDrawable(getResources().getDrawable(R.drawable.ic_map));
                imageInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_info));
                imageSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
                textHome.setTextColor(getResources().getColor(R.color.colorAccent));
                textMap.setTextColor(getResources().getColor(R.color.colorTextLight));
                textSearch.setTextColor(getResources().getColor(R.color.colorTextLight));
                textInfo.setTextColor(getResources().getColor(R.color.colorTextLight));
            }
        });

        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeLayout.setVisibility(View.GONE);
                infoLayout.setVisibility(View.VISIBLE);
                mapLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.GONE);
                imageHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_home));
                imageMap.setImageDrawable(getResources().getDrawable(R.drawable.ic_map));
                imageInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_info_selected));
                imageSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
                textHome.setTextColor(getResources().getColor(R.color.colorTextLight));
                textMap.setTextColor(getResources().getColor(R.color.colorTextLight));
                textSearch.setTextColor(getResources().getColor(R.color.colorTextLight));
                textInfo.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeLayout.setVisibility(View.GONE);
                infoLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
                imageHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_home));
                imageMap.setImageDrawable(getResources().getDrawable(R.drawable.ic_map_selected));
                imageInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_info));
                imageSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
                textHome.setTextColor(getResources().getColor(R.color.colorTextLight));
                textMap.setTextColor(getResources().getColor(R.color.colorAccent));
                textSearch.setTextColor(getResources().getColor(R.color.colorTextLight));
                textInfo.setTextColor(getResources().getColor(R.color.colorTextLight));
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeLayout.setVisibility(View.GONE);
                infoLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
                imageHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_home));
                imageMap.setImageDrawable(getResources().getDrawable(R.drawable.ic_map));
                imageInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_info));
                imageSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_selected));
                textHome.setTextColor(getResources().getColor(R.color.colorTextLight));
                textMap.setTextColor(getResources().getColor(R.color.colorTextLight));
                textSearch.setTextColor(getResources().getColor(R.color.colorAccent));
                textInfo.setTextColor(getResources().getColor(R.color.colorTextLight));
            }
        });

    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE HH:mm");
        String formatted = sdf.format(date);
        return formatted;

    }

    public static String convertUnixToHourShort(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //Map Layout methods
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("Maps Activity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Maps Activity", "Can't find style. Error: ", e);
        }

        fusedLocationProviderClientMain = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        fusedLocationProviderClientMain.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng currentLocation = new LatLng(latitude, longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

                    String latUrl = String.valueOf(latitude);
                    String lonUrl = String.valueOf(longitude);

                    getAqiInformation(latUrl, lonUrl);

                    getWeatherInformation(latUrl, lonUrl);

                    getWeatherForecast(latUrl, lonUrl);
                } else {

                    double latitude = 52.22977;
                    double longitude = 21.01178;

                    LatLng currentLocation = new LatLng(latitude, longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

                    String latUrl = String.valueOf(latitude);
                    String lonUrl = String.valueOf(longitude);

                    getAqiInformation(latUrl, lonUrl);

                    getWeatherInformation(latUrl, lonUrl);

                    getWeatherForecast(latUrl, lonUrl);

                    Toast.makeText(MapsActivity.this, "Unable to get current location. Random location: Warsaw", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mMap.clear();
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                getStationsInfo();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LatLng position = marker.getPosition();

                String latUrl = String.valueOf(position.latitude);
                String lonUrl = String.valueOf(position.longitude);

                getAqiInformation(latUrl, lonUrl);

                getWeatherInformation(latUrl, lonUrl);

                getWeatherForecast(latUrl, lonUrl);

                return false;
            }
        });
    }

    private void getStationsInfo() {

        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        LatLng nearLeft = visibleRegion.nearLeft;
        LatLng nearRight = visibleRegion.nearRight;
        LatLng farLeft = visibleRegion.farLeft;
        LatLng farRight = visibleRegion.farRight;

        double north = farLeft.latitude;
        double south = nearRight.latitude;
        double east = nearLeft.longitude;
        double west = farRight.longitude;

        
        //1: south, 2: east, 3: north, 4: west
        //lat1, lon1, lat2, lon2

        String basicUrl = "";
        String lat1 = String.valueOf(south);
        String lon1 = String.valueOf(east);
        String lat2 = String.valueOf(north);
        String lon2 = String.valueOf(west);
        final String appId = "";

        String url = basicUrl + lat1 + "," + lon1 + "," + lat2 + "," + lon2 + appId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        String aqi = jsonObject.getString("aqi");
                        double lat = jsonObject.getDouble("lat");
                        double lon = jsonObject.getDouble("lon");

                        LatLng station = new LatLng(lat, lon);
                        //mMap.addMarker(new MarkerOptions().position(station).title("AQI: " + aqi));

                        if (!aqi.contains("-")) {

                            final int aqiResult = Integer.valueOf(aqi);

                            if (aqiResult < 51) {
                                MarkerOptions options = new MarkerOptions().position(station).title("AQI: " + aqi);
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                mMap.addMarker(options);
                            } else if (aqiResult >= 51 && aqiResult < 101) {
                                MarkerOptions options2 = new MarkerOptions().position(station).title("AQI: " + aqi);
                                options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                mMap.addMarker(options2);
                            } else if (aqiResult >= 101 && aqiResult < 151) {
                                MarkerOptions options3 = new MarkerOptions().position(station).title("AQI: " + aqi);
                                options3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                mMap.addMarker(options3);
                            } else if (aqiResult >= 151 && aqiResult < 201) {
                                MarkerOptions options4 = new MarkerOptions().position(station).title("AQI: " + aqi);
                                options4.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                mMap.addMarker(options4);
                            } else if (aqiResult >= 201 && aqiResult < 301) {
                                MarkerOptions options5 = new MarkerOptions().position(station).title("AQI: " + aqi);
                                options5.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                mMap.addMarker(options5);
                            } else if (aqiResult > 300) {
                                MarkerOptions options6 = new MarkerOptions().position(station).title("AQI: " + aqi);
                                options6.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                mMap.addMarker(options6);
                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        queue.add(jsonObjectRequest);
    }

    private void getWeatherForecast(String latUrl, String lonUrl) {

        mCubicValueLineChart.clearChart();

        //String ulrtest = "";

        String basicUrl = "";
        String appId = "";

        String url = basicUrl + "lat=" + latUrl + "&lon=" + lonUrl + appId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    final ValueLineSeries series = new ValueLineSeries();
                    series.setColor(getResources().getColor(R.color.colorGraph));

                    JSONArray list = response.getJSONArray("list");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        int dateStamp = jsonObject.getInt("dt");
                        JSONObject main = jsonObject.getJSONObject("main");
                        double temp = main.getDouble("temp");

                        series.addPoint(new ValueLinePoint(convertUnixToHour(Long.parseLong(String.valueOf(dateStamp))), (float) temp));

                    }

                    mCubicValueLineChart.addSeries(series);
                    mCubicValueLineChart.startAnimation();

                    buttonTemp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonTemp.setBackground(getResources().getDrawable(R.drawable.ripple_button_active));
                            buttonTemp.setTextColor(getResources().getColor(R.color.colorAccent));

                            buttonPressure.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonPressure.setTextColor(getResources().getColor(R.color.colorText));
                            buttonHumidity.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonHumidity.setTextColor(getResources().getColor(R.color.colorText));
                            buttonWind.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonWind.setTextColor(getResources().getColor(R.color.colorText));

                            series.setColor(getResources().getColor(R.color.colorGraph));
                            mCubicValueLineChart.clearChart();

                            mCubicValueLineChart.addSeries(series);
                            mCubicValueLineChart.startAnimation();
                        }
                    });

                    final ValueLineSeries seriesPressure = new ValueLineSeries();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        int dateStamp = jsonObject.getInt("dt");
                        JSONObject main = jsonObject.getJSONObject("main");
                        double pressure = main.getDouble("pressure");

                        seriesPressure.addPoint(new ValueLinePoint(convertUnixToHour(Long.parseLong(String.valueOf(dateStamp))), (float) pressure));
                    }

                    buttonPressure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonPressure.setBackground(getResources().getDrawable(R.drawable.ripple_button_active));
                            buttonPressure.setTextColor(getResources().getColor(R.color.colorAccent));

                            buttonTemp.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonTemp.setTextColor(getResources().getColor(R.color.colorText));
                            buttonHumidity.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonHumidity.setTextColor(getResources().getColor(R.color.colorText));
                            buttonWind.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonWind.setTextColor(getResources().getColor(R.color.colorText));

                            seriesPressure.setColor(getResources().getColor(R.color.colorGraph));
                            mCubicValueLineChart.clearChart();

                            mCubicValueLineChart.addSeries(seriesPressure);
                            mCubicValueLineChart.startAnimation();
                        }
                    });

                    final ValueLineSeries seriesHumidity = new ValueLineSeries();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        int dateStamp = jsonObject.getInt("dt");
                        JSONObject main = jsonObject.getJSONObject("main");
                        double humidity = main.getDouble("humidity");

                        seriesHumidity.addPoint(new ValueLinePoint(convertUnixToHour(Long.parseLong(String.valueOf(dateStamp))), (float) humidity));
                    }

                    buttonHumidity.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonHumidity.setBackground(getResources().getDrawable(R.drawable.ripple_button_active));
                            buttonHumidity.setTextColor(getResources().getColor(R.color.colorAccent));

                            buttonTemp.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonTemp.setTextColor(getResources().getColor(R.color.colorText));
                            buttonPressure.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonPressure.setTextColor(getResources().getColor(R.color.colorText));
                            buttonWind.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonWind.setTextColor(getResources().getColor(R.color.colorText));

                            seriesHumidity.setColor(getResources().getColor(R.color.colorGraph));
                            mCubicValueLineChart.clearChart();

                            mCubicValueLineChart.addSeries(seriesHumidity);
                            mCubicValueLineChart.startAnimation();
                        }
                    });

                    final ValueLineSeries seriesWind = new ValueLineSeries();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        int dateStamp = jsonObject.getInt("dt");
                        JSONObject main = jsonObject.getJSONObject("wind");
                        double wind = main.getDouble("speed");

                        seriesWind.addPoint(new ValueLinePoint(convertUnixToHour(Long.parseLong(String.valueOf(dateStamp))), (float) wind));
                    }

                    buttonWind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonWind.setBackground(getResources().getDrawable(R.drawable.ripple_button_active));
                            buttonWind.setTextColor(getResources().getColor(R.color.colorAccent));

                            buttonTemp.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonTemp.setTextColor(getResources().getColor(R.color.colorText));
                            buttonPressure.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonPressure.setTextColor(getResources().getColor(R.color.colorText));
                            buttonHumidity.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonHumidity.setTextColor(getResources().getColor(R.color.colorText));

                            seriesWind.setColor(getResources().getColor(R.color.colorGraph));
                            mCubicValueLineChart.clearChart();

                            mCubicValueLineChart.addSeries(seriesWind);
                            mCubicValueLineChart.startAnimation();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void getWeatherInformation(String latUrl, String lonUrl) {

        

        String basicUrl2 = "";
        String appId2 = "";

        String url2 = basicUrl2 + "lat=" + latUrl + "&lon=" + lonUrl + appId2;

        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main = response.getJSONObject("main");
                    JSONArray weather = response.getJSONArray("weather");
                    JSONObject weatherOne = weather.getJSONObject(0);
                    JSONObject sys = response.getJSONObject("sys");
                    JSONObject wind = response.getJSONObject("wind");

                    String city = response.getString("name");
                    int pressure = main.getInt("pressure");
                    String humidity = String.valueOf(main.getInt("humidity"));
                    String description = weatherOne.getString("description");
                    String temp = String.valueOf(main.getInt("temp"));
                    String sunrise = String.valueOf(sys.getInt("sunrise"));
                    String sunset = String.valueOf(sys.getInt("sunset"));
                    int windSpeed = wind.getInt("speed");

                    textViewPressure.setText(pressure + " hpa");
                    textViewHumidity.setText(humidity + "%");
                    textViewDescription.setText(description);
                    textViewTemp.setText(temp + "°C");
                    textViewSunrise.setText(convertUnixToHourShort(Long.parseLong(sunrise)));
                    textViewSunset.setText(convertUnixToHourShort(Long.parseLong(sunset)));
                    textViewWind.setText(windSpeed + " m/s");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue queue2 = Volley.newRequestQueue(MapsActivity.this);
        queue2.add(jsonObjectRequest2);
    }

    private void getAqiInformation(String latUrl, String lonUrl) {

        

        String basicUrl = "";
        String appId = "";

        String url = basicUrl + latUrl + ";" + lonUrl + appId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONObject city = data.getJSONObject("city");
                    JSONObject iaqi = data.getJSONObject("iaqi");

                    int coV = 0;
                    int no2V = 0;
                    int o3V = 0;
                    int pm10V = 0;
                    int pm25V = 0;
                    int so2V = 0;
                    int tV = 0;
                    int pV = 0;

                    //CO
                    if (iaqi.has("co")) {

                        JSONObject co = iaqi.getJSONObject("co");
                        coV = co.optInt("v");
                        textViewCo.setText(String.valueOf(coV));

                    } else {

                        textViewCo.setText("N/D");

                    }

                    //NO2
                    if (iaqi.has("no2")) {

                        JSONObject no2 = iaqi.getJSONObject("no2");
                        no2V = no2.optInt("v");
                        textViewNo2.setText(String.valueOf(no2V));

                    } else {

                        textViewNo2.setText("N/D");

                    }

                    //O3
                    if (iaqi.has("o3")) {
                        JSONObject o3 = iaqi.getJSONObject("o3");
                        o3V = o3.optInt("v");
                        textViewO3.setText(String.valueOf(o3V));

                    } else {

                        textViewO3.setText("N/D");

                    }

                    //PM10
                    if (iaqi.has("pm10")) {

                        JSONObject pm10 = iaqi.getJSONObject("pm10");
                        pm10V = pm10.optInt("v");
                        textViewPm10.setText(String.valueOf(pm10V));

                    } else {

                        textViewPm10.setText("N/D");

                    }

                    //PM25
                    if (iaqi.has("pm25")) {

                        JSONObject pm25 = iaqi.getJSONObject("pm25");
                        pm25V = pm25.optInt("v");
                        textViewPm25.setText(String.valueOf(pm25V));

                    } else {
                        textViewPm25.setText("N/D");
                    }

                    //SO2
                    if (iaqi.has("so2")) {

                        JSONObject so2 = iaqi.getJSONObject("so2");
                        so2V = so2.optInt("v");
                        textViewSo2.setText(String.valueOf(so2V));

                    } else {

                        textViewSo2.setText("N/D");
                    }

                    int aqi = data.optInt("aqi");
                    String cityName = city.optString("name");

                    //AQI + city name
                    textViewCity.setText(cityName);

                    //Text color change
                    if (aqi < 51) {
                        textViewAqi.setText("Good");
                        textViewAqi.setTextColor(getResources().getColor(R.color.colorGood));
                        textViewCo.setTextColor(getResources().getColor(R.color.colorGood));
                        textViewNo2.setTextColor(getResources().getColor(R.color.colorGood));
                        textViewO3.setTextColor(getResources().getColor(R.color.colorGood));
                        textViewPm25.setTextColor(getResources().getColor(R.color.colorGood));
                        textViewPm10.setTextColor(getResources().getColor(R.color.colorGood));
                        textViewSo2.setTextColor(getResources().getColor(R.color.colorGood));
                    }
                    if (aqi >= 51 && aqi < 101) {
                        textViewAqi.setText("Moderate");
                        textViewAqi.setTextColor(getResources().getColor(R.color.colorModerate));
                        textViewCo.setTextColor(getResources().getColor(R.color.colorModerate));
                        textViewNo2.setTextColor(getResources().getColor(R.color.colorModerate));
                        textViewO3.setTextColor(getResources().getColor(R.color.colorModerate));
                        textViewPm25.setTextColor(getResources().getColor(R.color.colorModerate));
                        textViewPm10.setTextColor(getResources().getColor(R.color.colorModerate));
                        textViewSo2.setTextColor(getResources().getColor(R.color.colorModerate));
                    }
                    if (aqi >= 101 && aqi < 151) {
                        textViewAqi.setText("Unhealthy for Sensitive Groups");
                        textViewAqi.setTextColor(getResources().getColor(R.color.colorSensitive));
                        textViewCo.setTextColor(getResources().getColor(R.color.colorSensitive));
                        textViewNo2.setTextColor(getResources().getColor(R.color.colorSensitive));
                        textViewO3.setTextColor(getResources().getColor(R.color.colorSensitive));
                        textViewPm25.setTextColor(getResources().getColor(R.color.colorSensitive));
                        textViewPm10.setTextColor(getResources().getColor(R.color.colorSensitive));
                        textViewSo2.setTextColor(getResources().getColor(R.color.colorSensitive));
                    }
                    if (aqi >= 151 && aqi < 201) {
                        textViewAqi.setText("Unhealthy");
                        textViewAqi.setTextColor(getResources().getColor(R.color.colorUnhealthy));
                        textViewCo.setTextColor(getResources().getColor(R.color.colorUnhealthy));
                        textViewNo2.setTextColor(getResources().getColor(R.color.colorUnhealthy));
                        textViewO3.setTextColor(getResources().getColor(R.color.colorUnhealthy));
                        textViewPm25.setTextColor(getResources().getColor(R.color.colorUnhealthy));
                        textViewPm10.setTextColor(getResources().getColor(R.color.colorUnhealthy));
                        textViewSo2.setTextColor(getResources().getColor(R.color.colorUnhealthy));
                    }
                    if (aqi >= 201 && aqi < 301) {
                        textViewAqi.setText("Very unhealthy");
                        textViewAqi.setTextColor(getResources().getColor(R.color.colorVeryUnhealthy));
                        textViewCo.setTextColor(getResources().getColor(R.color.colorVeryUnhealthy));
                        textViewNo2.setTextColor(getResources().getColor(R.color.colorVeryUnhealthy));
                        textViewO3.setTextColor(getResources().getColor(R.color.colorVeryUnhealthy));
                        textViewPm25.setTextColor(getResources().getColor(R.color.colorVeryUnhealthy));
                        textViewPm10.setTextColor(getResources().getColor(R.color.colorVeryUnhealthy));
                        textViewSo2.setTextColor(getResources().getColor(R.color.colorVeryUnhealthy));
                    }
                    if (aqi > 300) {
                        textViewAqi.setText("Hazardous");
                        textViewAqi.setTextColor(getResources().getColor(R.color.colorHazardous));
                        textViewCo.setTextColor(getResources().getColor(R.color.colorHazardous));
                        textViewNo2.setTextColor(getResources().getColor(R.color.colorHazardous));
                        textViewO3.setTextColor(getResources().getColor(R.color.colorHazardous));
                        textViewPm25.setTextColor(getResources().getColor(R.color.colorHazardous));
                        textViewPm10.setTextColor(getResources().getColor(R.color.colorHazardous));
                        textViewSo2.setTextColor(getResources().getColor(R.color.colorHazardous));
                    }

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        queue.add(jsonObjectRequest);

    }

    //Search Layout methods
    private void clearText() {
        editCity.setText("");
    }

    private void insertItem(final int position) {

        

        String basicUrl = "";
        String cityName = editCity.getText().toString();
        String appId = "";

        String url = basicUrl + cityName + appId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONObject city = data.getJSONObject("city");
                    JSONObject iaqi = data.getJSONObject("iaqi");

                    String cityName = city.optString("name");

                    itemViewArrayList.add(position, new ItemView(editCity.getText().toString(), cityName));

                    adapter.notifyItemInserted(position);

                    saveData();

                    clearText();

                    Toast.makeText(MapsActivity.this, "Added: " + cityName, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemViewArrayList);
        editor.putString("city list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("city list", null);
        Type type = new TypeToken<ArrayList<ItemView>>() {
        }.getType();
        itemViewArrayList = gson.fromJson(json, type);

        if (itemViewArrayList == null) {
            itemViewArrayList = new ArrayList<>();
        }
    }

    private void removeItem(int position) {
        itemViewArrayList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void buildRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.canScrollHorizontally();
        adapter = new MyAdapter(itemViewArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
                saveData();
            }

            @Override
            public void onRefreshClick(final int position) {

            }
        });

    }

    //Home Layout methods
    private void getWeatherNow(double latitude, double longitude) {

        

        String basicUrl2 = "";
        String appId2 = "";

        String url2 = basicUrl2 + "lat=" + latitude + "&lon=" + longitude + appId2;

        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main = response.getJSONObject("main");
                    JSONArray weather = response.getJSONArray("weather");
                    JSONObject weatherOne = weather.getJSONObject(0);
                    JSONObject sys = response.getJSONObject("sys");
                    JSONObject wind = response.getJSONObject("wind");

                    String city = response.getString("name");
                    int pressure = main.getInt("pressure");
                    String humidity = String.valueOf(main.getInt("humidity"));
                    String description = weatherOne.getString("description");
                    String temp = String.valueOf(main.getInt("temp"));
                    String sunrise = String.valueOf(sys.getInt("sunrise"));
                    String sunset = String.valueOf(sys.getInt("sunset"));
                    int windSpeed = wind.getInt("speed");

                    textLocation.setText(city + ", " + description);
                    textPressureNow.setText(pressure + " hpa");
                    textHumidityNow.setText(humidity + "%");
                    textTempNow.setText(temp + "°C");
                    textSunriseNow.setText(convertUnixToHourShort(Long.parseLong(sunrise)));
                    textSunsetNow.setText(convertUnixToHourShort(Long.parseLong(sunset)));
                    textWindNow.setText(windSpeed + " m/s");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue queue2 = Volley.newRequestQueue(MapsActivity.this);
        queue2.add(jsonObjectRequest2);
    }

    private void getWeatherForecastMain(double latitude, double longitude) {

        

        String basicUrl = "";
        String latUrl = String.valueOf(latitude);
        String lonUrl = String.valueOf(longitude);
        String appId = "";

        String url = basicUrl + "lat=" + latUrl + "&lon=" + lonUrl + appId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    final ValueLineSeries series = new ValueLineSeries();
                    series.setColor(getResources().getColor(R.color.colorGraph));

                    JSONArray list = response.getJSONArray("list");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        int dateStamp = jsonObject.getInt("dt");
                        JSONObject main = jsonObject.getJSONObject("main");
                        double temp = main.getDouble("temp");

                        series.addPoint(new ValueLinePoint(convertUnixToHour(Long.parseLong(String.valueOf(dateStamp))), (float) temp));

                    }

                    mCubicValueLineChartMain.addSeries(series);
                    mCubicValueLineChartMain.startAnimation();

                    buttonTempMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonTempMain.setBackground(getResources().getDrawable(R.drawable.ripple_button_active));
                            buttonTempMain.setTextColor(getResources().getColor(R.color.colorAccent));

                            buttonPressureMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonPressureMain.setTextColor(getResources().getColor(R.color.colorText));
                            buttonHumidityMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonHumidityMain.setTextColor(getResources().getColor(R.color.colorText));
                            buttonWindMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonWindMain.setTextColor(getResources().getColor(R.color.colorText));

                            series.setColor(getResources().getColor(R.color.colorGraph));
                            mCubicValueLineChartMain.clearChart();

                            mCubicValueLineChartMain.addSeries(series);
                            mCubicValueLineChartMain.startAnimation();
                        }
                    });

                    final ValueLineSeries seriesPressure = new ValueLineSeries();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        int dateStamp = jsonObject.getInt("dt");
                        JSONObject main = jsonObject.getJSONObject("main");
                        double pressure = main.getDouble("pressure");

                        seriesPressure.addPoint(new ValueLinePoint(convertUnixToHour(Long.parseLong(String.valueOf(dateStamp))), (float) pressure));
                    }

                    buttonPressureMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonPressureMain.setBackground(getResources().getDrawable(R.drawable.ripple_button_active));
                            buttonPressureMain.setTextColor(getResources().getColor(R.color.colorAccent));

                            buttonTempMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonTempMain.setTextColor(getResources().getColor(R.color.colorText));
                            buttonHumidityMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonHumidityMain.setTextColor(getResources().getColor(R.color.colorText));
                            buttonWindMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonWindMain.setTextColor(getResources().getColor(R.color.colorText));

                            seriesPressure.setColor(getResources().getColor(R.color.colorGraph));
                            mCubicValueLineChartMain.clearChart();

                            mCubicValueLineChartMain.addSeries(seriesPressure);
                            mCubicValueLineChartMain.startAnimation();
                        }
                    });

                    final ValueLineSeries seriesHumidity = new ValueLineSeries();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        int dateStamp = jsonObject.getInt("dt");
                        JSONObject main = jsonObject.getJSONObject("main");
                        double humidity = main.getDouble("humidity");

                        seriesHumidity.addPoint(new ValueLinePoint(convertUnixToHour(Long.parseLong(String.valueOf(dateStamp))), (float) humidity));
                    }

                    buttonHumidityMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonHumidityMain.setBackground(getResources().getDrawable(R.drawable.ripple_button_active));
                            buttonHumidityMain.setTextColor(getResources().getColor(R.color.colorAccent));

                            buttonTempMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonTempMain.setTextColor(getResources().getColor(R.color.colorText));
                            buttonPressureMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonPressureMain.setTextColor(getResources().getColor(R.color.colorText));
                            buttonWindMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonWindMain.setTextColor(getResources().getColor(R.color.colorText));

                            seriesHumidity.setColor(getResources().getColor(R.color.colorGraph));
                            mCubicValueLineChartMain.clearChart();

                            mCubicValueLineChartMain.addSeries(seriesHumidity);
                            mCubicValueLineChartMain.startAnimation();
                        }
                    });

                    final ValueLineSeries seriesWind = new ValueLineSeries();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        int dateStamp = jsonObject.getInt("dt");
                        JSONObject main = jsonObject.getJSONObject("wind");
                        double wind = main.getDouble("speed");

                        seriesWind.addPoint(new ValueLinePoint(convertUnixToHour(Long.parseLong(String.valueOf(dateStamp))), (float) wind));
                    }

                    buttonWindMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonWindMain.setBackground(getResources().getDrawable(R.drawable.ripple_button_active));
                            buttonWindMain.setTextColor(getResources().getColor(R.color.colorAccent));

                            buttonTempMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonTempMain.setTextColor(getResources().getColor(R.color.colorText));
                            buttonPressureMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonPressureMain.setTextColor(getResources().getColor(R.color.colorText));
                            buttonHumidityMain.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                            buttonHumidityMain.setTextColor(getResources().getColor(R.color.colorText));

                            seriesWind.setColor(getResources().getColor(R.color.colorGraph));
                            mCubicValueLineChartMain.clearChart();

                            mCubicValueLineChartMain.addSeries(seriesWind);
                            mCubicValueLineChartMain.startAnimation();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);

    }

    public void buttonUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void getAqiInformationMain(double latitude, double longitude) {

        

        String basicUrl = "";
        String latUrl = String.valueOf(latitude);
        String lonUrl = String.valueOf(longitude);
        String appId = "";

        String url = basicUrl + latUrl + ";" + lonUrl + appId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONObject city = data.getJSONObject("city");
                    JSONObject iaqi = data.getJSONObject("iaqi");

                    int coV = 0;
                    int no2V = 0;
                    int o3V = 0;
                    int pm10V = 0;
                    int pm25V = 0;
                    int so2V = 0;
                    int tV = 0;
                    int pV = 0;

                    //CO
                    if (iaqi.has("co")) {
                        JSONObject co = iaqi.getJSONObject("co");
                        coV = co.optInt("v");
                        textCo.setText(String.valueOf(coV));
                        progressBarCo.setProgress(coV);
                        progressBarCo.setMaxValue(10f);
                        Double value = new Double(coV);
                        double progressPercent = (value / 10) * 100;
                        int progressPercentResult = Integer.valueOf((int) progressPercent);
                        progressBarCo.setText(String.valueOf(progressPercentResult));
                    } else {
                        textCo.setText("N/D");
                    }

                    //NO2
                    if (iaqi.has("no2")) {
                        JSONObject no2 = iaqi.getJSONObject("no2");
                        no2V = no2.optInt("v");
                        textNo2.setText(String.valueOf(no2V));
                        progressBarNo2.setProgress(no2V);
                        progressBarNo2.setMaxValue(40f);
                        Double value2 = new Double(no2V);
                        double progressPercent2 = (value2 / 40) * 100;
                        int progressPercentResult2 = Integer.valueOf((int) progressPercent2);
                        progressBarNo2.setText(String.valueOf(progressPercentResult2));
                    } else {
                        textNo2.setText("N/D");
                    }

                    //O3
                    if (iaqi.has("o3")) {
                        JSONObject o3 = iaqi.getJSONObject("o3");
                        o3V = o3.optInt("v");
                        textO3.setText(String.valueOf(o3V));
                        progressBaro3.setProgress(o3V);
                        progressBaro3.setMaxValue(100f);
                        Double value3 = new Double(o3V);
                        double progressPercent3 = (value3 / 100) * 100;
                        int progressPercentResult3 = Integer.valueOf((int) progressPercent3);
                        progressBaro3.setText(String.valueOf(progressPercentResult3));
                    } else {
                        textO3.setText("N/D");
                    }

                    //PM10
                    if (iaqi.has("pm10")) {
                        JSONObject pm10 = iaqi.getJSONObject("pm10");
                        pm10V = pm10.optInt("v");
                        textPm10.setText(String.valueOf(pm10V));
                        progressBarPm10.setProgress(pm10V);
                        progressBarPm10.setMaxValue(50f);
                        Double value4 = new Double(pm10V);
                        double progressPercent4 = (value4 / 50) * 100;
                        int progressPercentResult4 = Integer.valueOf((int) progressPercent4);
                        progressBarPm10.setText(String.valueOf(progressPercentResult4));
                    } else {
                        textPm10.setText("N/D");
                    }

                    //PM25
                    if (iaqi.has("pm25")) {
                        JSONObject pm25 = iaqi.getJSONObject("pm25");
                        pm25V = pm25.optInt("v");
                        textPm25.setText(String.valueOf(pm25V));
                        progressBarPm25.setProgress(pm25V);
                        progressBarPm25.setMaxValue(25f);
                        Double value5 = new Double(pm25V);
                        double progressPercent5 = (value5 / 25) * 100;
                        int progressPercentResult5 = Integer.valueOf((int) progressPercent5);
                        progressBarPm25.setText(String.valueOf(progressPercentResult5));
                    } else {
                        textPm25.setText("N/D");
                    }

                    //SO2
                    if (iaqi.has("so2")) {
                        JSONObject so2 = iaqi.getJSONObject("so2");
                        so2V = so2.optInt("v");
                        textSo2.setText(String.valueOf(so2V));
                        progressBarSo2.setProgress(so2V);
                        progressBarSo2.setMaxValue(20f);
                        Double value6 = new Double(so2V);
                        double progressPercent6 = (value6 / 20) * 100;
                        int progressPercentResult6 = Integer.valueOf((int) progressPercent6);
                        progressBarSo2.setText(String.valueOf(progressPercentResult6));
                    } else {
                        textSo2.setText("N/D");
                    }

                    loadingIndicator.setVisibility(View.GONE);
                    relativeView.setVisibility(View.VISIBLE);
                    horizontalScrollView.setVisibility(View.VISIBLE);
                    buttonsView.setVisibility(View.VISIBLE);
                    buttonsViewDetailsMain.setVisibility(View.VISIBLE);
                    mCubicValueLineChartMain.setVisibility(View.VISIBLE);
                    weatherNowView.setVisibility(View.VISIBLE);

                    int aqi = data.optInt("aqi");
                    String cityName = city.optString("name");

                    //AQI + city name
                    textCityName.setText("Nearest AQI Station: " + cityName);
                    arcProgress.setProgress(aqi);

                    //AQI description
                    if (aqi < 51) {
                        textDescriptionMain.setText("Good");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorGood));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorGood));
                    } else if (aqi >= 51 && aqi < 101) {
                        textDescriptionMain.setText("Moderate");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorModerate));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorModerate));
                    } else if (aqi >= 101 && aqi < 151) {
                        textDescriptionMain.setText("Unhealthy for Sensitive Groups");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorSensitive));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorSensitive));
                    } else if (aqi >= 151 && aqi < 201) {
                        textDescriptionMain.setText("Unhealthy");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorUnhealthy));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorUnhealthy));
                    } else if (aqi >= 201 && aqi < 301) {
                        textDescriptionMain.setText("Very unhealthy");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorVeryUnhealthy));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorVeryUnhealthy));
                    } else if (aqi > 300) {
                        textDescriptionMain.setText("Hazardous");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorHazardous));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorHazardous));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);

    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
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

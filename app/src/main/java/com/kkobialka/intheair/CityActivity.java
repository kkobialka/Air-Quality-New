package com.kkobialka.intheair;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.tuyenmonkey.mkloader.MKLoader;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CityActivity extends AppCompatActivity {

    TextView textPm25, textCo, textNo2, textO3, textPm10, textSo2, textCityName, textDescription,
            textTempNow, textPressureNow, textHumidityNow, textWindNow, textSunriseNow, textSunsetNow,
            textLocation;

    LinearLayout relativeView;

    RelativeLayout mainLayout;

    CircleProgressBar progressBarPm10, progressBarPm25, progressBarCo, progressBarNo2, progressBaro3, progressBarSo2;

    MKLoader loadingIndicator;

    LinearLayout imageMap, imageSearch, imageInfo, weatherNowView, buttonsView, buttonsViewDetails;

    HorizontalScrollView horizontalScrollView;

    ArcProgress arcProgress;

    ValueLineChart mCubicValueLineChart;

    Button buttonTemp, buttonPressure, buttonHumidity, buttonWind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        arcProgress = (ArcProgress) findViewById(R.id.arc_progress);

        loadingIndicator = (MKLoader) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        imageMap = (LinearLayout) findViewById(R.id.button_map);
        imageSearch = (LinearLayout) findViewById(R.id.button_search);
        imageInfo = (LinearLayout) findViewById(R.id.button_info);

        textPm25 = (TextView) findViewById(R.id.text_pm25);
        textCo = (TextView) findViewById(R.id.text_co);
        textNo2 = (TextView) findViewById(R.id.text_no2);
        textO3 = (TextView) findViewById(R.id.text_o3);
        textPm10 = (TextView) findViewById(R.id.text_pm10);
        textSo2 = (TextView) findViewById(R.id.text_so2);
        textCityName = (TextView) findViewById(R.id.text_city_name);
        textDescription = (TextView) findViewById(R.id.text_description);
        textTempNow = (TextView) findViewById(R.id.text_temp_now);
        textPressureNow = (TextView) findViewById(R.id.text_press_now);
        textWindNow = (TextView) findViewById(R.id.text_wind_now);
        textHumidityNow = (TextView) findViewById(R.id.text_humidity_now);
        textSunriseNow = (TextView) findViewById(R.id.text_sunrise_now);
        textSunsetNow = (TextView) findViewById(R.id.text_sunset_now);
        textLocation = (TextView) findViewById(R.id.text_location);

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        relativeView = (LinearLayout) findViewById(R.id.relative_view);

        progressBarPm10 = (CircleProgressBar) findViewById(R.id.progress_bar_pm10);
        progressBarPm25 = (CircleProgressBar) findViewById(R.id.progress_bar_pm25);
        progressBarCo = (CircleProgressBar) findViewById(R.id.progress_bar_co);
        progressBarNo2 = (CircleProgressBar) findViewById(R.id.progress_bar_no2);
        progressBaro3 = (CircleProgressBar) findViewById(R.id.progress_bar_o3);
        progressBarSo2 = (CircleProgressBar) findViewById(R.id.progress_bar_so2);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scroll_view);

        mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);
        weatherNowView = (LinearLayout) findViewById(R.id.weather_now_view);
        buttonsView = (LinearLayout) findViewById(R.id.buttons_view);
        buttonsViewDetails = (LinearLayout) findViewById(R.id.buttons_view_details);

        buttonsView.setVisibility(View.GONE);
        buttonsViewDetails.setVisibility(View.GONE);
        mCubicValueLineChart.setVisibility(View.GONE);
        weatherNowView.setVisibility(View.GONE);
        relativeView.setVisibility(View.GONE);
        horizontalScrollView.setVisibility(View.GONE);

        buttonTemp = (Button) findViewById(R.id.button_temp);
        buttonPressure = (Button) findViewById(R.id.button_pressure);
        buttonHumidity = (Button) findViewById(R.id.button_humidity);
        buttonWind = (Button) findViewById(R.id.button_wind);

        getIncomingIntent();

    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("city_name")) {
            String getCityName = getIntent().getStringExtra("city_name");

            getAqiInformation(getCityName);

            getWeatherNow(getCityName);

            getWeatherForecast(getCityName);

        }
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

    private void getWeatherForecast(String getCityName) {

        String basicUrl = "";
        String appId = "";

        String url = basicUrl + getCityName + appId;

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
                Toast.makeText(CityActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void getWeatherNow(String getCityName) {

        

        String basicUrl2 = "";
        String appId2 = "";

        String url2 = basicUrl2 + getCityName + appId2;

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
                Toast.makeText(CityActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue queue2 = Volley.newRequestQueue(CityActivity.this);
        queue2.add(jsonObjectRequest2);

    }

    private void getAqiInformation(String getCityName) {
        

        String basicUrl = "";
        String appId = "";

        String url = basicUrl + getCityName + appId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
                    buttonsViewDetails.setVisibility(View.VISIBLE);
                    mCubicValueLineChart.setVisibility(View.VISIBLE);
                    weatherNowView.setVisibility(View.VISIBLE);

                    int aqi = data.optInt("aqi");
                    String cityName = city.optString("name");

                    //AQI + city name
                    textCityName.setText("Nearest AQI Station: " + cityName);
                    arcProgress.setProgress(aqi);
                    arcProgress.animate();

                    //AQI description
                    if (aqi < 51) {
                        textDescription.setText("Good");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorGood));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorGood));
                    } else if (aqi >= 51 && aqi < 101) {
                        textDescription.setText("Moderate");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorModerate));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorModerate));
                    } else if (aqi >= 101 && aqi < 151) {
                        textDescription.setText("Unhealthy for Sensitive Groups");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorSensitive));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorSensitive));
                    } else if (aqi >= 151 && aqi < 201) {
                        textDescription.setText("Unhealthy");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorUnhealthy));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorUnhealthy));
                    } else if (aqi >= 201 && aqi < 301) {
                        textDescription.setText("Very unhealthy");
                        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorVeryUnhealthy));
                        arcProgress.setTextColor(getResources().getColor(R.color.colorVeryUnhealthy));
                    } else if (aqi > 300) {
                        textDescription.setText("Hazardous");
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
                Toast.makeText(CityActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

}

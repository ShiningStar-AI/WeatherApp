package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private SearchView searchView;
    private TextView tvCityName, tvTemperature, tvWeatherCondition, tvHumidity, tvWindSpeed, tvSunrise;
    private ImageView ivWeatherIcon;

    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // Weather API
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    // IMPORTANT: You need to get your own API key from OpenWeatherMap
    String apiKey = BuildConfig.API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        initViews();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up search view listener
        setupSearchView();

        // Set up the location callback
        setupLocationCallback();

        // Get weather for current location
        requestLocationUpdates();
    }

    private void initViews() {
        searchView = findViewById(R.id.searchView);
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherCondition = findViewById(R.id.tvWeatherCondition);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        tvSunrise = findViewById(R.id.tvSunrise);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchWeatherByCity(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                // We only need one update, so remove the callback
                fusedLocationClient.removeLocationUpdates(locationCallback);
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        fetchWeatherByCoordinates(location.getLatitude(), location.getLongitude());
                        return; // Exit after handling the first valid location
                    }
                }
                // Fallback if loop completes without a valid location
                Toast.makeText(MainActivity.this, "Could not get location. Showing default.", Toast.LENGTH_SHORT).show();
                fetchWeatherByCity("New York");
            }
        };
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show();
            fetchWeatherByCity("London");
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied. Showing default weather.", Toast.LENGTH_SHORT).show();
                fetchWeatherByCity("Paris");
            }
        }
    }

    private void fetchWeatherByCity(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=metric";
        fetchWeatherData(url);
    }

    private void fetchWeatherByCoordinates(double lat, double lon) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
        fetchWeatherData(url);
    }

    private void fetchWeatherData(String url) {
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch weather", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String responseBody = response.body().string();
                    runOnUiThread(() -> updateUI(responseBody));
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "City not found", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void updateUI(String responseBody) {
        try {
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
            String cityName = jsonObject.get("name").getAsString();
            JsonObject main = jsonObject.getAsJsonObject("main");
            double temp = main.get("temp").getAsDouble();
            int humidity = main.get("humidity").getAsInt();
            JsonObject weather = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject();
            String condition = weather.get("description").getAsString();
            String iconCode = weather.get("icon").getAsString();
            JsonObject wind = jsonObject.getAsJsonObject("wind");
            double windSpeed = wind.get("speed").getAsDouble();
            JsonObject sys = jsonObject.getAsJsonObject("sys");
            long sunriseTimestamp = sys.get("sunrise").getAsLong();
            String sunriseTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(sunriseTimestamp * 1000));

            tvCityName.setText(cityName);
            tvTemperature.setText(String.format(Locale.getDefault(), "%.0f°C", temp));
            tvWeatherCondition.setText(capitalize(condition));
            tvHumidity.setText(String.format(Locale.getDefault(), "%d%%", humidity));
            tvWindSpeed.setText(String.format(Locale.getDefault(), "%.1f m/s", windSpeed));
            tvSunrise.setText(sunriseTime);

            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";
            Glide.with(this).load(iconUrl).into(ivWeatherIcon);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates when the app is not visible to save battery
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}

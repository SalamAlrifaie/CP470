package com.example.androidassignments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;

public class WeatherForecast extends AppCompatActivity {

    private Spinner citySpinner;
    private ImageView weatherImageView;
    private TextView currentTemperatureTextView;
    private TextView minTemperatureTextView;
    private TextView maxTemperatureTextView;
    private ProgressBar progressBar;
    private TextView lastUpdatedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather_forecast);

        // Initialize the views
        citySpinner = findViewById(R.id.citySpinner);
        weatherImageView = findViewById(R.id.currentWeather);
        currentTemperatureTextView = findViewById(R.id.currentTemp);
        minTemperatureTextView = findViewById(R.id.minTemp);
        maxTemperatureTextView = findViewById(R.id.maxTemp);
        progressBar = findViewById(R.id.progressBar);
        lastUpdatedTextView = findViewById(R.id.lastUpdated);

        // Debugging null views
        if (citySpinner == null) {
            Log.e("WeatherForecast", "citySpinner is null!");
        }
        if (currentTemperatureTextView == null) {
            Log.e("WeatherForecast", "currentTemperatureTextView is null!");
        }
        if (minTemperatureTextView == null) {
            Log.e("WeatherForecast", "minTemperatureTextView is null!");
        }
        if (maxTemperatureTextView == null) {
            Log.e("WeatherForecast", "maxTemperatureTextView is null!");
        }
        if (weatherImageView == null) {
            Log.e("WeatherForecast", "weatherImageView is null!");
        }
        if (progressBar == null) {
            Log.e("WeatherForecast", "progressBar is null!");
        }
        if (lastUpdatedTextView == null) {
            Log.e("WeatherForecast", "lastUpdatedTextView is null!");
        }

        // Check network availability
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Populate the Spinner
        populateCitySpinner();

        // Set default selection (Ottawa)
        citySpinner.setSelection(getCityPosition("Ottawa"));

        // Set OnItemSelectedListener
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstSelection = true; // To prevent initial trigger

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = parent.getItemAtPosition(position).toString();
                Log.i("WeatherForecast", "Selected city: " + selectedCity);

                // Skip the initial selection if needed
                if (isFirstSelection) {
                    isFirstSelection = false;
                    // rigger data fetch for the default city
                    String weatherUrl = buildWeatherUrl(selectedCity);
                    new ForecastQuery().execute(weatherUrl);
                    return;
                }

                // Generate the URL with the selected city
                String weatherUrl = buildWeatherUrl(selectedCity);
                // Execute the ForecastQuery AsyncTask with the new URL
                new ForecastQuery().execute(weatherUrl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle cases where no selection is made
                Log.e("WeatherForecast", "No city selected.");
            }
        });
    }

    /**
     * Checks network availability.
     *
     * @return true if network is available, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Log.e("WeatherForecast", "ConnectivityManager is null!");
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Populates the Spinner with the list of Canadian cities.
     */
    private void populateCitySpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.canadian_cities, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the Spinner
        if (citySpinner != null) {
            citySpinner.setAdapter(adapter);
        }
    }

    /**
     * Finds the position of a city in the Spinner.
     *
     * @param cityName Name of the city to find
     * @return Position index of the city, or 0 if not found
     */
    private int getCityPosition(String cityName) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) citySpinner.getAdapter();
        if (adapter != null) {
            return adapter.getPosition(cityName);
        }
        return 0;
    }

    /**
     * Builds the weather API URL for the given city.
     *
     * @param cityName Name of the city
     * @return Formatted API URL with the city name
     */
    private String buildWeatherUrl(String cityName) {
        // Replace spaces with '+' for URL encoding
        String encodedCityName = cityName.replace(" ", "+");
        return "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + ",ca&APPID=79cecf493cb6e52d25bb7b7050ff723c&mode=xml&units=metric";
    }

    /**
     * AsyncTask to fetch weather data, parse XML, download/load weather icon, and update UI.
     */
    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        private String currentTemp;
        private String minTemp;
        private String maxTemp;
        private String weatherIcon;
        private Bitmap weatherImage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressBar != null) {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                Log.e("ForecastQuery", "ProgressBar is null in onPreExecute.");
            }
        }

        @Override
        protected String doInBackground(String... args) {
            String urlString = args[0]; // Dynamic URL based on selected city
            InputStream stream = null;

            try {
                // Establish HTTP Connection
                publishProgress(25); // Progress at 25% after initiating connection

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000); // 10 seconds
                connection.setConnectTimeout(15000); // 15 seconds
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                int responseCode = connection.getResponseCode();
                Log.d("ForecastQuery", "HTTP Response Code: " + responseCode);

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    InputStream errorStream = connection.getErrorStream();
                    String errorMessage = convertStreamToString(errorStream);
                    Log.e("ForecastQuery", "API Error Response: " + errorMessage);
                    return "Error: " + errorMessage;
                }

                // Parse XML Response
                stream = connection.getInputStream();
                parseXML(stream);

                publishProgress(50); // Progress at 50% after parsing temperature data

                // Handle Weather Icon Image
                if (weatherIcon != null && !weatherIcon.isEmpty()) {
                    String iconFileName = weatherIcon + ".png";
                    if (fileExistance(iconFileName)) {
                        // Image exists locally, load from storage
                        Log.i("ForecastQuery", "Image " + iconFileName + " found locally. Loading from storage.");
                        weatherImage = loadImageFromStorage(iconFileName);
                        Log.i("ForecastQuery", "Image " + iconFileName + " loaded from local storage.");
                    } else {
                        // Image does not exist, download and save
                        Log.i("ForecastQuery", "Image " + iconFileName + " not found locally. Downloading.");
                        String iconUrlString = "https://openweathermap.org/img/w/" + weatherIcon + ".png";
                        Bitmap downloadedImage = downloadImage(iconUrlString);
                        if (downloadedImage != null) {
                            saveImageToStorage(downloadedImage, iconFileName);
                            weatherImage = downloadedImage;
                            Log.i("ForecastQuery", "Image " + iconFileName + " downloaded and saved locally.");
                        } else {
                            Log.e("ForecastQuery", "Failed to download image " + iconFileName + ".");
                            weatherImage = null;
                        }
                    }
                    publishProgress(100); // Progress at 100% after handling image
                } else {
                    Log.e("ForecastQuery", "Weather icon code is null or empty.");
                    publishProgress(100); // Even if icon is missing, finish progress
                }

                return "Success";

            } catch (Exception e) {
                Log.e("ForecastQuery", "Exception in doInBackground: ", e);
                return "Error: " + e.getMessage();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e) {
                        Log.e("ForecastQuery", "Error closing InputStream: ", e);
                    }
                }
            }
        }

        /**
         * Parses the XML input stream to extract temperature data and weather icon code.
         *
         * @param stream InputStream from the HTTP connection
         * @throws Exception if an error occurs during parsing
         */
        private void parseXML(InputStream stream) throws Exception {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                if (eventType == XmlPullParser.START_TAG) {
                    if ("temperature".equals(tagName)) {
                        String tempValue = parser.getAttributeValue(null, "value");
                        String tempMin = parser.getAttributeValue(null, "min");
                        String tempMax = parser.getAttributeValue(null, "max");
                        currentTemp = tempValue + "°C";
                        minTemp = tempMin + "°C";
                        maxTemp = tempMax + "°C";
                        Log.d("ForecastQuery", "Parsed Temperature - Current: " + currentTemp + ", Min: " + minTemp + ", Max: " + maxTemp);
                    } else if ("weather".equals(tagName)) {
                        weatherIcon = parser.getAttributeValue(null, "icon");
                        Log.d("ForecastQuery", "Parsed Weather Icon: " + weatherIcon);
                    }
                }
                eventType = parser.next();
            }
        }

        /**
         * Converts an InputStream to a String.
         *
         * @param stream InputStream to convert
         * @return String representation of the InputStream
         * @throws Exception if an error occurs during reading
         */
        private String convertStreamToString(InputStream stream) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        }

        /**
         * Checks if a file exists in the local storage.
         *
         * @param fname Name of the file to check
         * @return true if the file exists, false otherwise
         */
        public boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            boolean exists = file.exists();
            Log.i("ForecastQuery", "Checking existence of " + fname + ": " + exists);
            return exists;
        }

        /**
         * Loads a Bitmap image from local storage.
         *
         * @param fname Name of the image file
         * @return Bitmap object if successful, null otherwise
         */
        private Bitmap loadImageFromStorage(String fname) {
            FileInputStream fis = null;
            try {
                fis = openFileInput(fname);
                Bitmap bm = BitmapFactory.decodeStream(fis);
                fis.close();
                return bm;
            } catch (FileNotFoundException e) {
                Log.e("ForecastQuery", "File not found: " + fname, e);
                return null;
            } catch (Exception e) {
                Log.e("ForecastQuery", "Error loading image: " + fname, e);
                return null;
            }
        }

        /**
         * Downloads a Bitmap image from the given URL.
         *
         * @param imageUrl URL of the image to download
         * @return Bitmap object if successful, null otherwise
         */
        private Bitmap downloadImage(String imageUrl) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();
                Log.d("ForecastQuery", "Image HTTP Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                    input.close();
                } else {
                    Log.e("ForecastQuery", "Failed to download image from " + imageUrl);
                }
            } catch (Exception e) {
                Log.e("ForecastQuery", "Exception while downloading image: ", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return bitmap;
        }

        /**
         * Saves a Bitmap image to local storage.
         *
         * @param image   Bitmap image to save
         * @param fname   Name of the file to save as
         */
        private void saveImageToStorage(Bitmap image, String fname) {
            FileOutputStream outputStream = null;
            try {
                outputStream = openFileOutput(fname, Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
                Log.i("ForecastQuery", "Image " + fname + " saved to local storage.");
            } catch (Exception e) {
                Log.e("ForecastQuery", "Exception while saving image: " + fname, e);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Exception e) {
                        Log.e("ForecastQuery", "Error closing FileOutputStream: ", e);
                    }
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (progressBar != null) {
                // Set the ProgressBar visibility to VISIBLE
                progressBar.setVisibility(View.VISIBLE);
                // Update the ProgressBar's progress to the passed value
                progressBar.setProgress(values[0]);
                Log.i("ForecastQuery", "Progress updated to: " + values[0] + "%");
            } else {
                Log.e("ForecastQuery", "ProgressBar is null in onProgressUpdate.");
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Hide the ProgressBar by setting visibility to INVISIBLE
            if (progressBar != null) {
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                Log.e("ForecastQuery", "ProgressBar is null in onPostExecute.");
            }

            if (result.equals("Success")) {
                // Update the TextViews with the fetched temperature data
                if (currentTemperatureTextView != null) {
                    currentTemperatureTextView.setText("Current: " + currentTemp);
                } else {
                    Log.e("ForecastQuery", "currentTemperatureTextView is null in onPostExecute.");
                }

                if (minTemperatureTextView != null) {
                    minTemperatureTextView.setText("Min: " + minTemp);
                } else {
                    Log.e("ForecastQuery", "minTemperatureTextView is null in onPostExecute.");
                }

                if (maxTemperatureTextView != null) {
                    maxTemperatureTextView.setText("Max: " + maxTemp);
                } else {
                    Log.e("ForecastQuery", "maxTemperatureTextView is null in onPostExecute.");
                }

                // Update the ImageView with the fetched or loaded weather icon
                if (weatherImageView != null) {
                    if (weatherImage != null) {
                        weatherImageView.setImageBitmap(weatherImage);
                    } else {
                        // If weatherImage is null, set a placeholder image
                        weatherImageView.setImageResource(R.drawable.add);
                        Log.e("ForecastQuery", "weatherImage is null in onPostExecute.");
                    }
                } else {
                    Log.e("ForecastQuery", "weatherImageView is null in onPostExecute.");
                }

                // Update Last Updated Time
                if (lastUpdatedTextView != null) {

                    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault());
                    String currentDateTime = dateFormat.format(new Date());

                    lastUpdatedTextView.setText("Last Updated: " + currentDateTime);
                } else {
                    Log.e("ForecastQuery", "lastUpdatedTextView is null in onPostExecute.");
                }

            } else if (result.startsWith("Error:")) {
                // Handle errors by displaying the error message in the currentTemperatureTextView
                if (currentTemperatureTextView != null) {
                    currentTemperatureTextView.setText(result);
                }

                // Clear the other temperature TextViews
                if (minTemperatureTextView != null) {
                    minTemperatureTextView.setText("");
                }

                if (maxTemperatureTextView != null) {
                    maxTemperatureTextView.setText("");
                }

                // Set a placeholder image in the ImageView
                if (weatherImageView != null) {
                    weatherImageView.setImageResource(R.drawable.add);
                }

                Log.e("ForecastQuery", "Error fetching data: " + result);
            }
        }

    }
}

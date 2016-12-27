package commonsware.com.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView result;
    TextView celsiusText;
    DecimalFormat twoDigits = new DecimalFormat("#.##");
    ImageView resultImage;
    ArrayList<String> weatherInfoList = new ArrayList<>();

    public void showMoreInfo(View view){
        Intent moreInfoIntent = new Intent(getApplicationContext(), moreInfoActivity.class);

        ArrayList<String> dummyStringList = new ArrayList<>();
        dummyStringList.add("first line");
        dummyStringList.add("second line");
        dummyStringList.add("third line");

        moreInfoIntent.putStringArrayListExtra("moreInfo", weatherInfoList);

        startActivity(moreInfoIntent);
    }
    public void findWeather(View view){
        EditText cityInput = (EditText)findViewById(R.id.cityInput);
        String cityName = null;

        // hide the keyboard
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityInput.getWindowToken(), 0);

        try {
            cityName = URLEncoder.encode(cityInput.getText().toString(), "UTF-8");
            if (cityName.equals("")){
                Toast.makeText(getApplicationContext(), "Using default city: london", Toast.LENGTH_SHORT).show();
                cityName = "london";
            }
                String url = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=5c7c917f2eedbebf85086c5fab2569d2";
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        } catch (UnsupportedEncodingException e) {
            Log.i("No connection 1", "No internet");
            Toast.makeText(getApplicationContext(), "Please enter a valid city", Toast.LENGTH_SHORT);


        }
        Log.i("User input: ", cityName);

    }

    public class DownloadTask extends AsyncTask<String, Void, String>{
        //5c7c917f2eedbebf85086c5fab2569d2


        @Override
        protected String doInBackground(String... urls) {
            StringBuilder resultSb = new StringBuilder();
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while (data != -1){
                    resultSb.append((char)data);
                    data = reader.read();
                }
                return resultSb.toString();
            } catch (Exception e) {
                Log.i("NO Internet 2", "error");// error is here
                //Toast.makeText(MainActivity.this, "Please enter a valid city", Toast.LENGTH_SHORT).show();

            }
            return null;
        }

        protected void onPostExecute(String value){
            super.onPostExecute(value);

            Map<String, Integer> mainDescription = new HashMap<>();
            mainDescription.put("clear sky", R.drawable.clear_sky);
            mainDescription.put("few clouds", R.drawable.few_clouds);
            mainDescription.put("scattered clouds", R.drawable.scattered_clouds);
            mainDescription.put("broken clouds", R.drawable.broken_clouds);
            mainDescription.put("shower rain", R.drawable.shower_rain);
            mainDescription.put("rain", R.drawable.rain);
            mainDescription.put("light rain", R.drawable.shower_rain);
            mainDescription.put("thunderstorm", R.drawable.thunderstorm);
            mainDescription.put("snow", R.drawable.snow);
            mainDescription.put("mist", R.drawable.mist);
            weatherInfoList.clear();
            try {
                String message = "";
                Double celsiusValue = 0.0;

                JSONObject jsonObject = new JSONObject(value);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather content", weatherInfo + "");
                JSONArray jsonArray = new JSONArray(weatherInfo);
                String city = jsonObject.getString("name");
                Log.i("City: ", city);


                //for (int i = 0; i < jsonArray.length(); i++){
                for (int i = 0; i < 1; i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    //weatherInfoList.add(main);
                    weatherInfoList.add(description);
                    if (!main.equals("") && !description.equals("")) {
                        message += city + "\n$: " + description + "\n";
                        int imageResourceName = 0;
                        if (mainDescription.containsKey(description))
                            imageResourceName = mainDescription.get(description);
                        else
                            imageResourceName = R.drawable.default_image;
                        resultImage.setImageResource(imageResourceName);
                    }
                }

                String temperatureInfo = jsonObject.getString("main");
                JSONObject tempObj = new JSONObject(temperatureInfo);
                String kelvinString = tempObj.getString("temp");
                String humidity = tempObj.getString("humidity");
                String pressure = tempObj.getString("pressure");
                String minTemperature = tempObj.getString("temp_min");
                String maxTemperature = tempObj.getString("temp_max");
                Log.i("TEMPERATURE: " , kelvinString);
                celsiusValue = Double.parseDouble(kelvinString) - 273.15;

                String countryInfo = jsonObject.getString("sys");
                JSONObject countryObj = new JSONObject(countryInfo);
                String country = countryObj.getString("country");
                Log.i("Country: ", country );

                String coordInfo = jsonObject.getString("coord");
                JSONObject coordObj = new JSONObject(coordInfo);
                String latitude = coordObj.getString("lat");
                String longitude = coordObj.getString("lon");

                String dateInfo = jsonObject.getString("dt");
                Log.i("Date time", dateInfo);
                Log.i("Local time", getLocalDate(dateInfo));

                weatherInfoList.add(city);
                weatherInfoList.add(country);
                weatherInfoList.add(kelvinString);
                weatherInfoList.add(latitude);
                weatherInfoList.add(longitude);
                weatherInfoList.add(minTemperature);
                weatherInfoList.add(maxTemperature);
                weatherInfoList.add(humidity);
                weatherInfoList.add(pressure);
                Log.i("list: ", weatherInfoList.toString());


                if (message.equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter a different city", Toast.LENGTH_SHORT).show();
                else {
                    result.setText(message);
                    celsiusText
                            .setText(Html.fromHtml(twoDigits.format(celsiusValue) + "<sup><small>o</small></sup><small>c</small>"));
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Weather info not found", Toast.LENGTH_SHORT);
            }


        }
    }

    public String getLocalDate(String epochTime){
        Date UTCDate = new Date(Long.parseLong(epochTime));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
        String result = "";
        try {
            //System.out.println("local format: " + simpleDateFormat.format(UTCDate));
            //System.out.println("local Date: " + simpleDateFormat.parse(simpleDateFormat.format(UTCDate)));
            result = String.valueOf(simpleDateFormat.parse(simpleDateFormat.format(UTCDate)));
        } catch (Exception ex) {
            result = "Date could not be calculated.";
            //Log.i("INVALID DATE", "Date could not be calculated.");
        }
        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        result = (TextView)findViewById(R.id.resultTextView);
        celsiusText = (TextView)findViewById(R.id.temperatureTextView);
        resultImage = (ImageView)findViewById(R.id.resultImage);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package commonsware.com.weatherapp;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity {
    TextView result;
    TextView celsiusText;
    DecimalFormat twoDigits = new DecimalFormat("#.##");
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
            Toast.makeText(getApplicationContext(), "Please enter a valid city", Toast.LENGTH_SHORT);
        }
        Log.i("User input: ", cityName);

    }

    public class DownloadTask extends AsyncTask<String, Void, String>{
        //5c7c917f2eedbebf85086c5fab2569d2


        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while (data != -1){
                    result.append((char)data);
                    data = reader.read();
                }
                return result.toString();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Please enter a valid city", Toast.LENGTH_SHORT).show();

            }
            return null;
        }

        protected void onPostExecute(String value){
            super.onPostExecute(value);
            try {
                String message = "";
                Double celsiusValue = 0.0;

                JSONObject jsonObject = new JSONObject(value);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather content", weatherInfo + "");
                JSONArray jsonArray = new JSONArray(weatherInfo);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    if (!main.equals("") && !description.equals(""))
                        message += main + ": " + description + "\n";
                }

                String temperatureInfo = jsonObject.getString("main");
                JSONObject tempObj = new JSONObject(temperatureInfo);
                String kelvinString = tempObj.getString("temp");
                Log.i("TEMPERATURE: " , kelvinString);
                celsiusValue = Double.parseDouble(kelvinString) - 273.15;



                if (message.equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter a different city", Toast.LENGTH_SHORT).show();
                else {
                    result.setText(message);
                    celsiusText.setText(Html.fromHtml(twoDigits.format(celsiusValue) + "<sup><small>o</small></sup><small>c</small>"));
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Weather info not found", Toast.LENGTH_SHORT);
            }


        }
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

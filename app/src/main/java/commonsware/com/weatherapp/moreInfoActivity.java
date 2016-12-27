package commonsware.com.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class moreInfoActivity extends AppCompatActivity {
    DecimalFormat twoDigits = new DecimalFormat("#.##");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
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

        Intent intent = getIntent();
        ArrayList<String> moreInfoList = intent.getStringArrayListExtra("moreInfo");
        String conditionResult = moreInfoList.get(0);
        String cityCountry = moreInfoList.get(1) + ", " + moreInfoList.get(2);
        String kelvinResult = moreInfoList.get(3);
        Double celsiusValue = Double.parseDouble(kelvinResult) - 273.15;
        String latitudeResult = moreInfoList.get(4);
        String longResult = moreInfoList.get(5);
        String minMaxTemp = moreInfoList.get(6) + "/" + moreInfoList.get(7);

        String humidityResult = moreInfoList.get(8);
        String pressureResult = moreInfoList.get(9);

        ((TextView)findViewById(R.id.locationTextView)).setText(cityCountry);
        ((TextView)findViewById(R.id.temperatureTextView))
                .setText(Html.fromHtml(twoDigits.format(celsiusValue) + "<sup><small>o</small></sup><small>c</small>"));

        ((TextView)findViewById(R.id.conditionTextView)).setText(conditionResult);
        ((TextView)findViewById(R.id.latitudeTextView)).setText(latitudeResult);
        ((TextView)findViewById(R.id.longitudeTextView)).setText(longResult);
        ((TextView)findViewById(R.id.minMaxTextView)).setText(minMaxTemp);
        ((TextView)findViewById(R.id.pressureTextView)).setText(pressureResult);
        ((TextView)findViewById(R.id.humidityTextView)).setText(humidityResult);
        Log.i("arraylist value", moreInfoList.toString());

        // dummy
        //TextView firstText = (TextView)findViewById(R.id.firstLine);
        //firstText.setText(moreInfoList.toString());
    }

}

/*
                weatherInfoList.add(kelvinString);
                weatherInfoList.add(latitude);
                weatherInfoList.add(longitude);
                weatherInfoList.add(minTemperature);
                weatherInfoList.add(maxTemperature);
                weatherInfoList.add(humidity);
                weatherInfoList.add(pressure);
 */
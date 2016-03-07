package com.example.imam.networkwebservice;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imam.networkwebservice.Model.Cuaca;

import org.json.JSONException;

public class MainActivity extends Activity {

    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;

    private TextView hum;
    private ImageView imgView;
	private ImageView imageView1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String city = "London,UK";

        cityText = (TextView) findViewById(R.id.cityText);
        condDescr = (TextView) findViewById(R.id.condDescr);
        temp = (TextView) findViewById(R.id.temp);
        hum = (TextView) findViewById(R.id.hum);
        press = (TextView) findViewById(R.id.press);
        windSpeed = (TextView) findViewById(R.id.windSpeed);
        windDeg = (TextView) findViewById(R.id.windDeg);
        imgView = (ImageView) findViewById(R.id.condIcon);

		if (cekKoneksi()){
			JSONWeatherTask task = new JSONWeatherTask();
			task.execute(new String[]{city});
		}else{
			try {
                Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

    }

	private boolean cekKoneksi(){
		ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
		if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
			return false;
		}
		return true;
	}


    private class JSONWeatherTask extends AsyncTask<String, Void, Cuaca> {

		@Override
		protected Cuaca doInBackground(String... params) {
			Cuaca weather = new Cuaca();
			String data = ( (new HttpClient()).getWeatherData(params[0]));
			try {
				weather = JsonParsing.getWeather(data);

				weather.iconData = ( (new HttpClient()).getImage(weather.currentCondition.getIcon()));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return weather;

	}


	@Override
		protected void onPostExecute(Cuaca weather) {
			super.onPostExecute(weather);

			if (weather.iconData != null) {
				Bitmap img = weather.iconData;
				imgView.setImageBitmap(img);
			}

			cityText.setText(weather.lokasi.getCity() + "," + weather.lokasi.getCountry());
			condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
			temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "C");
			hum.setText("" + weather.currentCondition.getHumidity() + "%");
			press.setText("" + weather.currentCondition.getPressure() + " hPa");
			windSpeed.setText("" + weather.wind.getSpeed() + " mps");
			windDeg.setText("" + weather.wind.getDeg() + "Deg");

		}


    }


}

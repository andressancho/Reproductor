package andressancho.com.reproductor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();
                int n=0;
                while (data != -1){
                    if (n>6) {
                        char current = (char) data;
                        result += current;
                        data = inputStreamReader.read();
                    }
                    else{
                        n++;
                    }
                }
                result=result.substring(9,result.length()-2);
                Log.i("info",result);
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                Log.i("jsonObject", jsonObject.toString());

                JSONObject main = new JSONObject(jsonObject.getString("message"));
                Log.i("main", main.toString());
                main= new JSONObject(main.getString("body"));
                main= new JSONObject(main.getString("lyrics"));

                String lyrics =  main.getString("lyrics_body");
                Log.i("Letra", lyrics);



                textView.setText(lyrics);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.letratx);


        refreshWeather();
    }
    public void refreshWeather(){
        String url = "https://api.musixmatch.com/ws/1.1/track.lyrics.get?format=jsonp&callback=callback&track_id=145935375&apikey=addd84d4810727be2426f860c52fc614";
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }
}

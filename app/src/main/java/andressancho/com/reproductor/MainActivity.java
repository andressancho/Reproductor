package andressancho.com.reproductor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.TagLostException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    ImageView cover;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    boolean isActivePlaying = false;
    SeekBar advanceSeekBar;
    Song[] songs={new Song(145935375,R.raw.closer)};
    TextView[] images= new TextView[10];
    String idm="";
    String urlAPI;
    ListView lista;
    boolean tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.letratx);
        cover=findViewById(R.id.cover);
        lista=lista= findViewById(R.id.lista);
        advanceSeekBar = findViewById(R.id.seekBar);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        inicializarCanciones();
        inicializarSeekbars();

        /*new Timer().scheduleAtFixedRate(new Timer(){
            @Override
            public void run(){
                advanceSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        }, 0 ,1000);*/

    }
    public void refreshWeather(){
        String url = "https://api.musixmatch.com/ws/1.1/track.lyrics.get?format=jsonp&callback=callback&track_id=145935375&apikey=addd84d4810727be2426f860c52fc614";
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    public void stopClicked(View view){
        mediaPlayer.pause();
    }
    public void playClicked(View view){
        mediaPlayer.start();
    }
    public void inicializarSeekbars(){

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        SeekBar volumeSeekBar = findViewById(R.id.volumeSk);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);

        //Manejo de avance


        int duration = mediaPlayer.getDuration();
        final int advance = mediaPlayer.getCurrentPosition();
        int progress = mediaPlayer.getCurrentPosition();

        advanceSeekBar.setMax(duration);
        advanceSeekBar.setProgress(progress);


        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("volume", Integer.toString(i));
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                //mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        advanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("volume", Integer.toString(i));
                mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public void cambiarCancion(int id){
        mediaPlayer = MediaPlayer.create(this, id);
        int duration = mediaPlayer.getDuration();
        final int advance = mediaPlayer.getCurrentPosition();
        int progress = mediaPlayer.getCurrentPosition();

        advanceSeekBar.setMax(duration);
        advanceSeekBar.setProgress(progress);
    }
    public void inicializarCanciones() {
        mediaPlayer = MediaPlayer.create(this, R.raw.closer);
        idm = Integer.toString(songs[0].getMusixId());
        urlAPI = "https://api.musixmatch.com/ws/1.1/track.get?format=jsonp&callback=callback&track_id=" + idm + "&apikey=addd84d4810727be2426f860c52fc614";
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(urlAPI);
        downloadTask = new DownloadTask();
        downloadTask.execute("https://api.musixmatch.com/ws/1.1/track.get?format=jsonp&callback=callback&track_id=145935376&apikey=addd84d4810727be2426f860c52fc614");
        ImageDownloadTask dt= new ImageDownloadTask();
        dt.execute("http://www.youredm.com/wp-content/uploads/2016/08/The-Chainsmokers-Closer-2016-Official.jpg");


    }
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
                    while (data != -1) {
                        if (n > 6) {
                            char current = (char) data;
                            result += current;
                            data = inputStreamReader.read();
                        } else {
                            n++;
                        }
                    }
                    result = result.substring(9, result.length() - 2);


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
                main= new JSONObject(main.getString("track"));

                songs[0].setRating(Double.parseDouble(main.getString("track_rating")));
                songs[0].setArtist(main.getString("artist_name"));
                songs[0].setUrlImage(main.getString("track_share_url"));
                String lyrics =  main.getString("track_share_url");
                Log.i("Letra", lyrics);



                textView.setText(lyrics);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
    public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {


            try {
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            songs[0].setImage(result);
            cover.setImageBitmap(result);




        }

    }
}

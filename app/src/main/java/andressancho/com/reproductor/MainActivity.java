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
import android.widget.AdapterView;
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
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView textView,canciontv,artista;
    ImageView cover,rating;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    boolean isActivePlaying = false;
    SeekBar advanceSeekBar;
    Song[] songs;
    TextView[] images= new TextView[10];
    String idm="";
    String urlAPI;
    ListView lista;
    int cancionActual=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.letratx);
        canciontv = findViewById(R.id.canciontv);
        artista = findViewById(R.id.artista);
        rating = findViewById(R.id.rating);
        cover = findViewById(R.id.cover);
        lista = lista = findViewById(R.id.lista);
        advanceSeekBar = findViewById(R.id.seekBar);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        try {
            inicializarCanciones();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        inicializarSeekbars();
        cancionActual=0;
        cambiarCancion();



    }
    public void stopClicked(View view){
        mediaPlayer.pause();
    }
    public void prevClicked(View view){
        mediaPlayer.stop();
        if (cancionActual==0){
            cancionActual=songs.length-1;
        }
        else{
            cancionActual--;
        }
        cambiarCancion();
        mediaPlayer.start();
    }
    public void nextClicked(View view){
        mediaPlayer.stop();
        if (cancionActual==songs.length-1){
            cancionActual=0;
        }
        else{
            cancionActual++;
        }
        cambiarCancion();
        mediaPlayer.start();
    }
    public void playClicked(View view){
        mediaPlayer.start();
        Log.d("HOLA",Integer.toString(mediaPlayer.getDuration()));
        textView.animate().translationYBy(-2500f).setDuration(mediaPlayer.getDuration());//mediaPlayer.getDuration());

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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(advanceSeekBar.getProgress());
                //actualizar();
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                advanceSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        },0,1000);


    }
    public void actualizar(){
        int e = advanceSeekBar.getProgress();
        Log.d("Progress:", Integer.toString(e));
        textView.animate().translationYBy(-100f).setDuration(e/10);
    }
    public void cambiarCancion(){
        mediaPlayer = MediaPlayer.create(this, songs[cancionActual].getSongId());
        int duration = mediaPlayer.getDuration();
        final int advance = mediaPlayer.getCurrentPosition();
        int progress = mediaPlayer.getCurrentPosition();

        advanceSeekBar.setMax(duration);
        advanceSeekBar.setProgress(progress);

        cover.setImageBitmap(songs[cancionActual].getImage());
        canciontv.setText(songs[cancionActual].getNombre());
        artista.setText(songs[cancionActual].getArtist());
        rating();
        textView.setText(songs[cancionActual].getLyrics());

    }
    public void rating(){
        double r=songs[cancionActual].getRating();
        if(r==1){
            rating.setImageResource(R.drawable.uno);
        }
        else if(r==2){
            rating.setImageResource(R.drawable.dos);
        }
        else if(r==3){
            rating.setImageResource(R.drawable.tres);
        }
        else if(r==4){
            rating.setImageResource(R.drawable.cuatro);
        }
        else if(r==5){
            rating.setImageResource(R.drawable.cinco);
        }
    }
    public void inicializarCanciones() throws ExecutionException, InterruptedException, JSONException {
        cancionActual=0;
        songs= new Song[]{new Song(145935375, R.raw.closer,"http://www.youredm.com/wp-content/uploads/2016/08/The-Chainsmokers-Closer-2016-Official.jpg",getResources().getString(R.string.closerSong)),
                          new Song(142895505, R.raw.river,"https://djbooth.net/.image/t_share/MTUzNDg5NDEyNjg4NTg1OTI2/eminem-revivaljpg.jpg",getResources().getString(R.string.riverSong)),
                          new Song(129998728, R.raw.swish,"https://orig00.deviantart.net/28af/f/2017/139/9/a/swish_swish__feat__nicki_minaj__by_dianalovaticd3m1-db9sbeb.jpg",getResources().getString(R.string.swishSong)),
                          new Song(131977903, R.raw.nomoney,"https://upload.wikimedia.org/wikipedia/en/thumb/4/44/NoMoney.jpeg/220px-NoMoney.jpeg",getResources().getString(R.string.noMoneySong)),
                          new Song(84358379, R.raw.youknow,"https://images-na.ssl-images-amazon.com/images/I/41c-aWSV2sL._SS500.jpg",getResources().getString(R.string.ykyliSong)),
                          new Song(115865506, R.raw.shots,"http://radioactivodj.com/wp-content/uploads/2015/05/IMAGINE-DRAGONS-SHOTS-BROILER-REMIX.jpg",getResources().getString(R.string.shotsSong)),
                          new Song(73239939, R.raw.shots,"https://s.mxmcdn.net/images-storage/albums/7/6/1/1/0/9/30901167_350_350.jpg",getResources().getString(R.string.ratherSong))};

        mediaPlayer = MediaPlayer.create(this, songs[cancionActual].getSongId());
        for(int x=0;x< songs.length;x++){
            cancionActual=x;
            ImageDownloadTask dt= new ImageDownloadTask();
            songs[x].setImage(dt.execute(songs[x].getUrlImage()).get());
            DownloadTask downloadTask = new DownloadTask();
            idm=Integer.toString(songs[x].getMusixId());
            urlAPI = "https://api.musixmatch.com/ws/1.1/track.get?format=jsonp&callback=callback&track_id=" + idm + "&apikey=addd84d4810727be2426f860c52fc614";

            JSONObject jsonObject = new JSONObject(downloadTask.execute(urlAPI).get());
            JSONObject main = new JSONObject(jsonObject.getString("message"));
            main= new JSONObject(main.getString("body"));
            main= new JSONObject(main.getString("track"));

            songs[x].setRating(Double.parseDouble(main.getString("track_rating")));
            songs[x].setArtist(main.getString("artist_name"));
            songs[x].setNombre(main.getString("track_name"));
        }

        SongAdapter adapter= new SongAdapter(this,R.layout.listview_item_row,songs);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                textView.animate().cancel();
                textView.setY(0);
                textView.animate().translationYBy(-2500f).setDuration(mediaPlayer.getDuration());//mediaPlayer.getDuration());
                mediaPlayer.stop();
                cancionActual=i;
                cambiarCancion();
                mediaPlayer.start();
            }
        });



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


        }

    }
}

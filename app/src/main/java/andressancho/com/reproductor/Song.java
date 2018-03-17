package andressancho.com.reproductor;

import android.graphics.Bitmap;

/**
 * Created by Usuario on 16/03/2018.
 */

public class Song {
    private int musixId;
    private int songId;
    private String lyrics;
    private double rating;
    private String urlImage;
    private Bitmap image;
    private String artist;
    private String nombre;

    public int getMusixId() {
        return musixId;
    }

    public void setMusixId(int musixId) {
        this.musixId = musixId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    Song(int idM, int id){
        musixId=idM;
        songId=id;

    }

}


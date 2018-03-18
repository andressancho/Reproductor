package andressancho.com.reproductor;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Created by Usuario on 17/03/2018.
 */

public class SongAdapter extends ArrayAdapter<Song> {
    Context context;
    int layoutResourceId;
    Song[] data=null;

    public SongAdapter(@NonNull Context context, int resource, @NonNull Song[] objects) {
        super(context, resource, objects);
        this.context=context;
        layoutResourceId=resource;
        data=objects;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View row=convertView;
        SongsHolder holder=null;
        if(row==null){
            LayoutInflater inflater=((Activity)context).getLayoutInflater();
            row=inflater.inflate(layoutResourceId,parent,false);
            holder= new SongsHolder();
            holder.image=row.findViewById(R.id.imagen);
            row.setTag(holder);
        }else{
            holder=(SongsHolder)row.getTag();
        }
        Song s=data[position];
        holder.image.setImageBitmap(s.getImage());

        return row;

    }
    static class SongsHolder{
        ImageView image;
    }
}

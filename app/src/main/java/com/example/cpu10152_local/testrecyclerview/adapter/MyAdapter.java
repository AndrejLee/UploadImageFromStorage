package com.example.cpu10152_local.testrecyclerview.adapter;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cpu10152_local.testrecyclerview.ImageCache;
import com.example.cpu10152_local.testrecyclerview.R;
import com.example.cpu10152_local.testrecyclerview.entity.Movie;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by cpu10152-local on 03/04/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Movie> movies;
    private Context context;
    //    private SparseArray<Bitmap> photoPools = new SparseArray<>();
    private ImageCache bitmapCache;

    public MyAdapter(Context context) {
        this.context = context;
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 8;
        bitmapCache = new ImageCache(cacheSize);
    }

    public void setMovies(List<Movie> list) {
        movies = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.title.setText(movies.get(position).getTitle());
        holder.des.setText(movies.get(position).getDescription());

//        Bitmap picture = photoPools.get(position);
        Bitmap picture = bitmapCache.get(position);

        if (picture != null) {
            holder.picture.setImageBitmap(picture);
        } else {
            new AsyncTask<MyViewHolder, Void, Bitmap>() {
                MyViewHolder v;

                @Override
                protected Bitmap doInBackground(MyViewHolder... strings) {
                    v = strings[0];
                    URL url = null;
                    Bitmap bmp = null;
                    try {
                        url = new URL(movies.get(position).getUrl());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return bmp;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    if (bitmap != null) {
//                        photoPools.put(position, bitmap);
                        bitmapCache.put(position, bitmap);
                        v.picture.setImageBitmap(bitmap);
                    }
                }
            }.execute(holder);
        }


    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
    }

    final class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        ImageView picture;
        TextView title;
        TextView des;

        public MyViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            picture = itemView.findViewById(R.id.iv_picture);
            title = itemView.findViewById(R.id.tv_title);
            des = itemView.findViewById(R.id.tv_description);

        }
    }
}

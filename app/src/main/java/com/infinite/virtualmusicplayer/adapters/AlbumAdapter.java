package com.infinite.virtualmusicplayer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.AlbumDetails;
import com.infinite.virtualmusicplayer.model.Music;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {

    private final Context mContext;
    private ArrayList<Music> albumFiles;
    View view;

    public AlbumAdapter(Context mContext, ArrayList<Music> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.album_name.setText(albumFiles.get(position).getAlbum());
        holder.album_name.setSelected(true);

        byte[] image = getAlbumArt(albumFiles.get(position).getPath());
        if (image != null){
            Glide.with(mContext)
                    .load(image).placeholder(R.drawable.album_art)
                    .into(holder.album_image);
        }
        else {
            Glide.with(mContext)
                    .load(R.drawable.album_art)
                    .into(holder.album_image);
        }


        holder.itemView.setOnClickListener(view -> {
            try {
                String albumTitle = albumFiles.get(position).getAlbum();
                String albumYear = albumFiles.get(position).getYear();
                Intent intent = new Intent(mContext, AlbumDetails.class);
                intent.putExtra("albumName", albumTitle);
                intent.putExtra("albumTitle", albumTitle);
                intent.putExtra("albumYear", albumYear);
                mContext.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        TextView album_name;
        ImageView album_image;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_name = itemView.findViewById(R.id.album_name);
            album_image = itemView.findViewById(R.id.album_image);
        }
    }


    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        try {
            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return art;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<Music> musicArrayList)
    {
        albumFiles = new ArrayList<>();
        albumFiles.addAll(musicArrayList);
        notifyDataSetChanged();
    }
}

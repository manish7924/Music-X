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
import com.infinite.virtualmusicplayer.model.Music;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.ArtistDetails;

import java.io.IOException;
import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyArtistViewHolder> {

    private final Context mContext;
    private ArrayList<Music> artistFiles;
    View view;

    public ArtistAdapter(Context mContext, ArrayList<Music> artistFiles) {
        this.mContext = mContext;
        this.artistFiles = artistFiles;
    }

    @NonNull
    @Override
    public MyArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.artist_item, parent, false);
        return new MyArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyArtistViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.artist_name.setText(artistFiles.get(position).getArtist());
        holder.artist_name.setSelected(true);

        byte[] image = getAlbumArt(artistFiles.get(position).getPath());
        if (image != null){
            holder.artist_image.setPadding(0, 0, 0, 0);
            Glide.with(mContext).asBitmap().placeholder(R.drawable.artist_art)
                    .load(image)
                    .into(holder.artist_image);
        }
        else {
            holder.artist_image.setPadding(30, 30, 30, 30);
            Glide.with(mContext).asBitmap()
                    .load(R.drawable.artist_art)
                    .into(holder.artist_image);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String artistTitle = artistFiles.get(position).getArtist();
                    Intent intent = new Intent(mContext, ArtistDetails.class);
                    intent.putExtra("artistName", artistTitle);
                    intent.putExtra("artistTitle", artistTitle);
                    mContext.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistFiles.size();
    }

    public class MyArtistViewHolder extends RecyclerView.ViewHolder{

        TextView artist_name;
        ImageView artist_image;
        public MyArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artist_name = itemView.findViewById(R.id.artist_name);
            artist_image = itemView.findViewById(R.id.artist_image);
        }
    }


    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return art;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<Music> musicArrayList)
    {
        artistFiles = new ArrayList<>();
        artistFiles.addAll(musicArrayList);
        notifyDataSetChanged();
    }
}
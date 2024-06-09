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
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.R;

import java.io.IOException;
import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    public static ArrayList<Music> favouriteFiles;
    private final Context context;

    public FavouriteAdapter(Context context, ArrayList<Music> favouriteFiles)
    {
        FavouriteAdapter.favouriteFiles = favouriteFiles;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favourite_item, parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.favSongName.setText(FavouriteAdapter.favouriteFiles.get(position).getTitle());
        holder.favSongName.setSelected(true);

        byte[] image = getAlbumArt(favouriteFiles.get(position).getPath());
        if (image != null){
            Glide.with(context).asBitmap().placeholder(R.drawable.music_note_placeholder)
                    .load(image)
                    .into(holder.favSongImg);
        }
        else {
            Glide.with(context).asBitmap()
                    .load(R.drawable.music_note)
                    .into(holder.favSongImg);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, MusicPlayerActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("favIntent","FavIntent");
                    context.startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Exception"+ e, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



    @Override
    public int getItemCount() {
        return favouriteFiles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView favSongName;
        ImageView favSongImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            favSongName = itemView.findViewById(R.id.songNameFav);
            favSongImg = itemView.findViewById(R.id.songImgFav);


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


}

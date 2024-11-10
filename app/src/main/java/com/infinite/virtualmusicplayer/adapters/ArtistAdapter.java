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
import com.infinite.virtualmusicplayer.activities.ArtistDetails;
import com.infinite.virtualmusicplayer.model.Music;

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
            Glide.with(mContext)
                    .load(image)
                    .override(400,400)
                    .circleCrop()
                    .placeholder(R.drawable.artist_art)
                    .into(holder.artist_image);
        }
        else {
            Glide.with(mContext)
                    .load(R.drawable.artist_art)
                    .placeholder(R.drawable.artist_art)
                    .into(holder.artist_image);
        }


        holder.itemView.setOnClickListener(view -> {
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


    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        byte[] art = null;
        try {
            retriever.setDataSource(uri);
            art = retriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
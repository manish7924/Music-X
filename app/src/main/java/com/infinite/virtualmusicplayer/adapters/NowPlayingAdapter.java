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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.model.Music;

import java.util.ArrayList;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.MyHolder> {

    private Context mContext;
    static ArrayList<Music> nowPlayingSongFiles;
    View view;

    public NowPlayingAdapter(Context mContext, ArrayList<Music> nowPlayingSongFiles) {
        this.mContext = mContext;
        NowPlayingAdapter.nowPlayingSongFiles = nowPlayingSongFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.up_next_items, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.up_next_song.setText(nowPlayingSongFiles.get(position).getTitle());
        holder.up_next_song.setSelected(true);
        holder.up_next_artist.setText(nowPlayingSongFiles.get(position).getArtist());
        holder.up_next_artist.setSelected(false);



        byte[] image = getAlbumArt(nowPlayingSongFiles.get(position).getPath());
        if (image != null){
            Glide.with(mContext)
                    .load(image).placeholder(R.drawable.music_note_placeholder)
                    .into(holder.up_next_art);
            holder.up_next_art.setPadding(0, 0, 0, 0);
        }
        else {
            holder.up_next_art.setPadding(12, 12, 12, 12);
            Glide.with(mContext)
                    .load(R.drawable.music_note)
                    .into(holder.up_next_art);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MusicPlayerActivity.class);
                intent.putExtra("sender","albumDetails");
                intent.putExtra("position", position);
                mContext.startActivity(intent);

            }
        });





    }

    @Override
    public int getItemCount() {
        return nowPlayingSongFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView up_next_song, up_next_artist;
        ImageView up_next_art, up_next_drag;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            up_next_song = itemView.findViewById(R.id.up_next_title);
            up_next_artist = itemView.findViewById(R.id.up_next_artist);
            up_next_drag = itemView.findViewById(R.id.up_next_drag);
            up_next_art = itemView.findViewById(R.id.up_next_art);
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
}



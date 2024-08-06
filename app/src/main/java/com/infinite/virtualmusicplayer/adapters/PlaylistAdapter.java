package com.infinite.virtualmusicplayer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.model.Music;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {

    public static ArrayList<Music> playlistList;
    private Context context;


    public PlaylistAdapter(Context context, ArrayList<Music> playlistList)
    {
        PlaylistAdapter.playlistList = playlistList;
        this.context = context;
    }


    @NonNull
    @Override
    public PlaylistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_view, parent, false);

        return new PlaylistAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        holder.playlistSongName.setText(Music.Playlist.name);
//        holder.playlistSongName.setText(PlaylistFragment.musicPlaylist.getRef().get(position).getName());
        holder.playlistSongName.setSelected(true);

        byte[] image = getAlbumArt(playlistList.get(position).getPath());
//        if (image != null){
//            Glide.with(context).asBitmap().placeholder(R.drawable.favourite_on)
//                    .load(image)
//                    .into(holder.playlistSongImg);
//        }
//        else {
//            Glide.with(context).asBitmap()
//                    .load(R.drawable.album_art)
//                    .into(holder.playlistSongImg);
//        }
//        if (PlaylistFragment.musicPlaylist.getRef().get(position).getPlaylist().size() > 0) {
//            Glide.with(context)
//                    .load(image)
//                    .apply(RequestOptions.placeholderOf(R.drawable.music_note_placeholder).centerCrop())
//                    .into(holder.playlistSongImg);
//        }



//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, PlaylistDetails.class);
//            intent.putExtra("index", position);
//            ContextCompat.startActivity(context, intent, null);
//        });


//        holder.playlistDeleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ArrayList<Music> myFiles = new ArrayList<>();
//                new MaterialAlertDialogBuilder(context)
//                        .setTitle(playlistList.get(position).getAlbum())
//                        .setMessage("Do you want to Delete this Playlist ?")
//                        .setPositiveButton("Yes", (dialog, which) -> {
//                            // Exit the app
//                            PlaylistFragment.musicPlaylist.getRef().remove(position);
//                            refreshPlaylist(playlistList);
//                            dialog.dismiss();
//                        })
//                        .setNegativeButton("No", (dialog, which) -> {
//                            // Exit the app
//                            dialog.dismiss();
//                        })
//                        .show();
//
//            }
//        });

    }



    @Override
    public int getItemCount() {
        return playlistList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout cardView;
        TextView playlistSongName;
        ImageView playlistSongImg;
        ImageButton playlistDeleteBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.relative_layout_cardView);
            playlistSongName = itemView.findViewById(R.id.playlistSongName);
            playlistSongImg = itemView.findViewById(R.id.playlistSongImg);
            playlistDeleteBtn = itemView.findViewById(R.id.playlistDeleteBtn);


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
    public void refreshPlaylist(ArrayList<Music> playlistArrayList) {
        playlistList = new ArrayList<>();
        playlistList.addAll(playlistArrayList);
        notifyDataSetChanged();
    }

}

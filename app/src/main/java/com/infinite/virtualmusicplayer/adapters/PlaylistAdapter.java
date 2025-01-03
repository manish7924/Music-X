package com.infinite.virtualmusicplayer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.activities.PlaylistDetails;
import com.infinite.virtualmusicplayer.fragments.PlaylistFragment;
import com.infinite.virtualmusicplayer.model.Music;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {

    public static ArrayList<Music.Playlist> playlistList;
    private Context context;


    public PlaylistAdapter(Context context, ArrayList<Music.Playlist> playlistList)
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

        holder.playlistSongName.setText(playlistList.get(position).name);
//        holder.playlistSongName.setText(PlaylistFragment.musicPlaylist.ref.get(position).name);
        holder.playlistSongName.setSelected(true);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaylistDetails.class);
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });

        holder.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.menuBtn);
                popupMenu.getMenuInflater().inflate(R.menu.playlist_item_menu, popupMenu.getMenu());

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.add_songs) {
                            Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (id == R.id.delete) {

                            new MaterialAlertDialogBuilder(context)
                                    .setTitle(playlistList.get(position).name)
                                    .setIcon(R.drawable.ic_playlist_outlined)
                                    .setMessage("Do you want to delete this playlist ?")
                                    .setPositiveButton("Delete", (dialog, which) -> {

//                                        delete the playlist
                                        PlaylistFragment.musicPlaylist.ref.remove(position);
                                        refreshPlaylist();

                                        dialog.dismiss();

                                    })
                                    .setNegativeButton("Cancel", (dialog, which) -> {
                                        // Exit the app
                                        dialog.dismiss();
                                    })
                                    .show();
                            return true;
                        }


                        return true;
                    }
                });

            }
        });

//        byte[] image = getAlbumArt(playlistList.get(position).getPath());
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



    }



    @Override
    public int getItemCount() {
        return playlistList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cardView;
        TextView playlistSongName;
        ImageView playlistSongImg, menuBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.relative_layout_cardView);
            playlistSongName = itemView.findViewById(R.id.playlistSongName);
            playlistSongImg = itemView.findViewById(R.id.playlistSongImg);
            menuBtn = itemView.findViewById(R.id.playlist_menu);

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
    public void refreshPlaylist() {
        playlistList = new ArrayList<>();
        playlistList.addAll(PlaylistFragment.musicPlaylist.ref);
        notifyDataSetChanged();
    }

}

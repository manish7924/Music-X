package com.infinite.virtualmusicplayer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.Favourite;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.fragments.PlaylistFragment;
import com.infinite.virtualmusicplayer.model.Music;

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
            Glide.with(context)
                    .load(image)
                    .override(400,400)
                    .placeholder(R.drawable.music_note_placeholder)
                    .into(holder.favSongImg);
        }
        else {
            Glide.with(context).asBitmap()
                    .load(R.drawable.music_note)
                    .override(200,200)
                    .into(holder.favSongImg);
        }


        holder.itemView.setOnClickListener(view -> {
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

        });

        holder.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.menuBtn);
                popupMenu.getMenuInflater().inflate(R.menu.favourite_item_menu, popupMenu.getMenu());

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.remove) {

                            Favourite.favouriteSongs.remove(position);
                            return true;
                        }


                        return true;
                    }
                });

            }
        });

    }



    @Override
    public int getItemCount() {
        return favouriteFiles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView favSongName;
        ImageView favSongImg, menuBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            favSongName = itemView.findViewById(R.id.songNameFav);
            favSongImg = itemView.findViewById(R.id.songImgFav);
            menuBtn = itemView.findViewById(R.id.favourite_menu);



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


}

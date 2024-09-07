package com.infinite.virtualmusicplayer.adapters;

import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.formatMB;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isFav;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.millisecondsToTime;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.ArtistDetails;
import com.infinite.virtualmusicplayer.activities.Favourite;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.model.Music;

import java.io.File;
import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {

    private final Context mContext;
    public static ArrayList<Music> albumSongFiles;
    int fIndex = -1;
    View view;

    public AlbumDetailsAdapter(Context mContext, ArrayList<Music> albumSongFiles) {
        this.mContext = mContext;
        AlbumDetailsAdapter.albumSongFiles = albumSongFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.album_song_name.setText(albumSongFiles.get(position).getTitle());
        holder.songArtist.setText(albumSongFiles.get(position).getArtist());
        holder.songDuration.setText("• "+millisecondsToTime(Integer.parseInt(albumSongFiles.get(position).getDuration())));



        byte[] image = getAlbumArt(albumSongFiles.get(position).getPath());
        if (image != null){
            Glide.with(mContext)
                    .load(image).placeholder(R.drawable.music_note)
                    .into(holder.album_song_image);
            holder.album_song_image.setPadding(0, 0, 0, 0);
        }
        else {
            holder.album_song_image.setPadding(12, 12, 12, 12);
            Glide.with(mContext)
                    .load(R.drawable.music_note)
                    .into(holder.album_song_image);
        }


        holder.album_song_image.setOnClickListener(view -> showSongDetails(position));


        holder.itemView.setOnClickListener(view -> {
            if (isLoop || isShuffle){
                if (isLoop){
                    isLoop = false;
                }
                if (isShuffle){
                    isShuffle = false;
                }
            }
            try {
                Intent intent = new Intent(mContext, MusicPlayerActivity.class);
                intent.putExtra("albumSender","albumDetails");
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
            }

        });



        holder.menu_more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                PopupMenu popupMenu = new PopupMenu(mContext, view);
//                popupMenu.getMenuInflater().inflate(R.menu.album_menu_more, popupMenu.getMenu());
//                popupMenu.show();
//                popupMenu.setOnMenuItemClickListener((item) -> {
//                    if (item.getItemId() == R.id.add_to_playlist) {
//                        Toast.makeText(mContext, "Coming soon", Toast.LENGTH_SHORT).show();
//                    }
//                    else if (item.getItemId() == R.id.go_to_artist) {
//                        goToArtist();
//                    }
//                    else if (item.getItemId() == R.id.go_to_folder) {
//                        Toast.makeText(mContext, "Coming soon", Toast.LENGTH_SHORT).show();
//                    }
////                    else if (item.getItemId() == R.id.equalizer) {
//////                        showEqualizer();
////                    }
//                    else if (item.getItemId() == R.id.share) {
//                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                        shareIntent.setType("audio/*");
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(albumSongFiles.get(position).getPath()));
//                        mContext.startActivity(Intent.createChooser(shareIntent, "Sharing Music File via"));
//                    }
//                    else if (item.getItemId() == R.id.delete_from_device) {
//                        deleteMusicFile(position, view);
//                    }
//                    else if (item.getItemId() == R.id.details) {
//                        showSongDetails(position);
//                    }
//                    return true;
//                });

                showMenuMoreBottomSheet();

            }


            private void showMenuMoreBottomSheet() {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.menu_more_bottom_sheet_layout);

                fIndex = Music.favouriteChecker(albumSongFiles.get(position).getId());

                ImageView selectedSongImg = dialog.findViewById(R.id.selectedSongImg);
                ImageView addToFavourite = dialog.findViewById(R.id.addToFavBtn);
                TextView nowPlayingSongName = dialog.findViewById(R.id.nowPlayingSongName);
                TextView nowPlayingSongArtist = dialog.findViewById(R.id.nowPlayingSongArtist);
                TextView nowPlayingSongDuration = dialog.findViewById(R.id.nowPlayingSongDuration);



                if (isFav){
                    addToFavourite.setImageResource(R.drawable.favourite_on);
                }else {
                    addToFavourite.setImageResource(R.drawable.ic_favorite);
                }


                LinearLayout addToPlaylist = dialog.findViewById(R.id.add_to_playlist);
                LinearLayout goToAlbum = dialog.findViewById(R.id.go_to_album);
                LinearLayout goToArtist = dialog.findViewById(R.id.go_to_artist);
                LinearLayout goToFolder = dialog.findViewById(R.id.go_to_folder);
                LinearLayout goToFavourite = dialog.findViewById(R.id.go_to_favourite);
                LinearLayout share = dialog.findViewById(R.id.share);
                LinearLayout deleteFromDevice = dialog.findViewById(R.id.delete_from_device);
                LinearLayout details = dialog.findViewById(R.id.details);


                addToPlaylist.setOnClickListener(view -> Toast.makeText(mContext, "Coming Soon", Toast.LENGTH_SHORT).show());

                addToFavourite.setOnClickListener(view -> {

                    if (isFav) {
                        isFav = false;
                        addToFavourite.setImageResource(R.drawable.ic_favorite);
                        Favourite.favouriteSongs.remove(albumSongFiles.get(position));
                        Toast.makeText(mContext, "Removed from favourite", Toast.LENGTH_SHORT).show();

                    } else {
                        isFav = true;
                        addToFavourite.setImageResource(R.drawable.favourite_on);
                        Favourite.favouriteSongs.add(albumSongFiles.get(position));
                        Toast.makeText(mContext, "Added to favourite", Toast.LENGTH_SHORT).show();

                    }
                });


                goToAlbum.setOnClickListener(view -> {
//                        goToAlbum();
                    Toast.makeText(mContext, "Already in Album", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

                goToArtist.setOnClickListener(view -> {
                    goToArtist();
                    dialog.dismiss();
                });

                goToFolder.setOnClickListener(view -> {
                    Toast.makeText(mContext, "Coming Soon", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

                goToFavourite.setOnClickListener(view -> {
                    Intent intent = new Intent(mContext, Favourite.class);
                    mContext.startActivity(intent);
                    dialog.dismiss();
                });

                share.setOnClickListener(view -> {
                    shareFiles();
                    dialog.dismiss();
                });

                deleteFromDevice.setOnClickListener(view -> {
                    deleteMusicFile(position, view);
//                        dialog.dismiss();
                });

                selectedSongImg.setOnClickListener(view -> showSongDetails(position));

                details.setOnClickListener(view -> {
                    showSongDetails(position);
                    dialog.dismiss();
                });


                nowPlayingSongName.setText(albumSongFiles.get(position).getTitle());
                nowPlayingSongName.setSelected(true);
                nowPlayingSongArtist.setText(albumSongFiles.get(position).getArtist());
                nowPlayingSongDuration.setText("• "+millisecondsToTime(Integer.parseInt(albumSongFiles.get(position).getDuration())));


                byte[] nowPlayingImage = getAlbumArt(albumSongFiles.get(position).getPath());
                if (nowPlayingImage != null){
                    selectedSongImg.setPadding(0, 0, 0, 0);
                    Glide.with(mContext)
                            .load(nowPlayingImage).placeholder(R.drawable.music_note)
                            .into(selectedSongImg);
                }
                else {
                    selectedSongImg.setPadding(12, 12, 12, 12);
                    Glide.with(mContext)
                            .load(R.drawable.music_note).centerInside()
                            .into(selectedSongImg);
                }


                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);

            }

//            private void goToAlbum() {

//                String albumTitle = albumSongFiles.get(position).getAlbum();
//                String albumYear = albumSongFiles.get(position).getYear();
//                Intent intent = new Intent(mContext, AlbumDetails.class);
//                intent.putExtra("albumName", albumTitle);
//                intent.putExtra("albumTitle", albumTitle);
//                intent.putExtra("albumYear", albumYear);
//                mContext.startActivity(intent);
//            }


            private void goToArtist() {
                String artistTitle = albumSongFiles.get(position).getArtist();
                Intent intent = new Intent(mContext, ArtistDetails.class);
                intent.putExtra("artistName", artistTitle);
                intent.putExtra("artistTitle", artistTitle);
                mContext.startActivity(intent);
            }

            private void shareFiles() {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("audio/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(albumSongFiles.get(position).getPath()));
                mContext.startActivity(Intent.createChooser(shareIntent, "Sharing Music File via"));
            }


        });



    }


    @SuppressLint("SetTextI18n")
    private void showSongDetails(int position) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.song_details_popup);

//        TextView TitleView = dialog.findViewById(R.id.titleView);
        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
        TextView artistTextView = dialog.findViewById(R.id.artistTextView);
        TextView albumTextView = dialog.findViewById(R.id.albumTextView);
        TextView yearTextView = dialog.findViewById(R.id.yearTextView);
        TextView durationTextView = dialog.findViewById(R.id.durationTextView);
        TextView sizeTextView = dialog.findViewById(R.id.sizeTextView);
        TextView pathTitleTextView = dialog.findViewById(R.id.pathTitleTextView);
        TextView pathTextView = dialog.findViewById(R.id.pathTextView);

        titleTextView.setText("Title : " + albumSongFiles.get(position).getTitle());
        artistTextView.setText("Artist : " + albumSongFiles.get(position).getArtist());
        albumTextView.setText("Album : " + albumSongFiles.get(position).getAlbum());
        yearTextView.setText("Year : "+ albumSongFiles.get(position).getYear());
        durationTextView.setText("Duration : " + millisecondsToTime(Integer.parseInt(albumSongFiles.get(position).getDuration())));
        sizeTextView.setText("Size : "+ formatMB(Integer.parseInt(albumSongFiles.get(position).getSize())));
        pathTitleTextView.setText("File path: ");
        pathTextView.setText(albumSongFiles.get(position).getPath());


        // Close button
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());
//
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void deleteMusicFile(int position, View view){

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(albumSongFiles.get(position).getId()));
        File file = new File(albumSongFiles.get(position).getPath());
        boolean delete = file.delete();

        if (file.exists()){
            delete = !delete;
            if (delete) {
                try {
                    mContext.getContentResolver().delete(contentUri, null, null);
                    albumSongFiles.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, albumSongFiles.size());
                    Snackbar.make(view, "File Deleted From device" ,Snackbar.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
//                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, e.toString() ,Snackbar.LENGTH_SHORT).show();

                }
            }
            else {
                Snackbar.make(view, "File Can't be Deleted" ,Snackbar.LENGTH_SHORT).show();
            }
        }
        else {
            Snackbar.make(view, "File doesn't exist" ,Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return albumSongFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView album_song_name, songArtist, songDuration;
        ImageView album_song_image, menu_more_btn;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_song_name = itemView.findViewById(R.id.music_file_name);
            album_song_image = itemView.findViewById(R.id.music_img);
            menu_more_btn = itemView.findViewById(R.id.menu_more);
            songArtist = itemView.findViewById(R.id.music_artist_name);
            songDuration = itemView.findViewById(R.id.music_duration);
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

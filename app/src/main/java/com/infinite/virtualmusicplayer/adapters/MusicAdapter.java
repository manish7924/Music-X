package com.infinite.virtualmusicplayer.adapters;

import static com.infinite.virtualmusicplayer.activities.MainActivity.albums;
import static com.infinite.virtualmusicplayer.activities.MainActivity.artists;
import static com.infinite.virtualmusicplayer.activities.MainActivity.tracks;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validAlbums;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validArtists;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validSongs;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.formatMB;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isFav;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.lightVibrantColor;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.loop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.millisecondsToTime;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.shuffle;

import static java.util.Objects.*;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.AlbumDetails;
import com.infinite.virtualmusicplayer.activities.ArtistDetails;
import com.infinite.virtualmusicplayer.activities.Favourite;
import com.infinite.virtualmusicplayer.activities.MainActivity;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.model.Music;
import java.io.File;
import java.util.ArrayList;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private final Context context;
    public static ArrayList<Music> songFiles;
    View view;

    int fIndex = -1;

    public MusicAdapter(Context context, ArrayList<Music> songFiles)
    {
        this.context = context;
        MusicAdapter.songFiles = songFiles;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.songName.setText(songFiles.get(position).getTitle());
        holder.songArtist.setText(songFiles.get(position).getArtist());
        holder.songDuration.setText("• "+millisecondsToTime(Integer.parseInt(songFiles.get(position).getDuration())));


        byte[] image = getAlbumArt(songFiles.get(position).getPath());
        if (image != null){
            Glide.with(context)
                    .load(image)
                    .override(200,200)
                    .circleCrop()
                    .placeholder(R.drawable.music_note)
                    .into(holder.albumArt);

        }
        else {
            Glide.with(context)
                    .load(R.drawable.music_note)
                    .placeholder(R.drawable.music_note)
                    .into(holder.albumArt);

        }


        holder.albumArt.setOnClickListener(view -> showSongDetails(position));


        holder.itemView.setOnClickListener(view -> {
            if (isLoop || isShuffle){
                lightVibrantColor = Color.WHITE;
                if (isLoop) {
                    isLoop = false;
                    loop.setImageResource(R.drawable.repeat_off);
                }
                if (isShuffle) {
                    isShuffle = false;
                    shuffle.setImageResource(R.drawable.shuffle_off);
                }
            }

            try {

                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);

            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context, "Exception"+ e, Toast.LENGTH_SHORT).show();
            }

        });

        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                menu bottom sheet dialog
                showMenuMoreBottomSheet();

            }

            private void showMenuMoreBottomSheet() {
                final BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.more_bottom_sheet_layout);

                fIndex = Music.favouriteChecker(songFiles.get(position).getId());

                ImageView selectedSongImg = dialog.findViewById(R.id.selectedSongImg);
                ImageView addToFavourite = dialog.findViewById(R.id.addToFavBtn);
                TextView nowPlayingSongName = dialog.findViewById(R.id.nowPlayingSongName);
                TextView nowPlayingSongArtist = dialog.findViewById(R.id.nowPlayingSongArtist);
                TextView nowPlayingSongDuration = dialog.findViewById(R.id.nowPlayingSongDuration);


                if (isFav){
                    if (addToFavourite == null) throw new AssertionError();
                    addToFavourite.setImageResource(R.drawable.favourite_on);
                }else {
                    if (addToFavourite == null) throw new AssertionError();
                    addToFavourite.setImageResource(R.drawable.ic_favorite);
                }

                LinearLayout addToPlaylist = dialog.findViewById(R.id.add_to_playlist);
                LinearLayout goToAlbum = dialog.findViewById(R.id.go_to_album);
                LinearLayout goToArtist = dialog.findViewById(R.id.go_to_artist);
                LinearLayout goToFolder = dialog.findViewById(R.id.go_to_folder);
                LinearLayout goToFavourite = dialog.findViewById(R.id.go_to_favourite);
                LinearLayout editTags = dialog.findViewById(R.id.edit_tags);
                LinearLayout share = dialog.findViewById(R.id.share);
                LinearLayout deleteFromDevice = dialog.findViewById(R.id.delete_from_device);
                LinearLayout details = dialog.findViewById(R.id.details);


                if (addToPlaylist == null) throw new AssertionError();
                addToPlaylist.setOnClickListener(view -> {
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
                });

                addToFavourite.setOnClickListener(view -> {
                    if (isFav) {
                        isFav = false;
                        addToFavourite.setImageResource(R.drawable.ic_favorite);
                        Favourite.favouriteSongs.remove(songFiles.get(position));
                        Toast.makeText(context, "Removed from favourite", Toast.LENGTH_SHORT).show();

                    } else {
                        isFav = true;
                        addToFavourite.setImageResource(R.drawable.favourite_on);
                        Favourite.favouriteSongs.add(songFiles.get(position));
                        Toast.makeText(context, "Added to favourite", Toast.LENGTH_SHORT).show();

                    }
                });

                if (goToAlbum == null) throw new AssertionError();
                goToAlbum.setOnClickListener(view -> {
                   goToAlbum();
                   dialog.dismiss();
                });

                if (goToArtist == null) throw new AssertionError();
                goToArtist.setOnClickListener(view -> {
                    goToArtist();
                    dialog.dismiss();
                });

                if (goToFolder == null) throw new AssertionError();
                goToFolder.setOnClickListener(view -> {
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
                });

                if (goToFavourite == null) throw new AssertionError();
                goToFavourite.setOnClickListener(view -> {
                    Intent intent = new Intent(context, Favourite.class);
                    context.startActivity(intent);
                    dialog.dismiss();
                });

                if (editTags == null) throw new AssertionError();
                editTags.setOnClickListener(view -> {
                    showEditTagsDialog(position);
                    dialog.dismiss();
                });

                if (share == null) throw new AssertionError();
                share.setOnClickListener(view -> {
                    shareFiles();
                    dialog.dismiss();
                });

                if (deleteFromDevice == null) throw new AssertionError();
                deleteFromDevice.setOnClickListener(view -> {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Do you want to delete this file ?")
                            .setIcon(R.drawable.delete)
                            .setMessage("This is permanent and cannot be undone.")
                            .setPositiveButton("Delete", (dialogInterface, which) -> {
//                                delete
                                deleteMusicFile(position, view);
                                dialog.dismiss();

                            })
                            .setNegativeButton("Cancel", (dialogInterface, which) -> {
                                // Exit the app
                                dialog.dismiss();
                            })
                            .show();
                });

                if (selectedSongImg == null) throw new AssertionError();
                selectedSongImg.setOnClickListener(view -> {
                    showSongDetails(position);
                    dialog.dismiss();
                });

                if (details == null) throw new AssertionError();
                details.setOnClickListener(view -> {
                    showSongDetails(position);
                    dialog.dismiss();
                });


                if (nowPlayingSongName == null) throw new AssertionError();
                nowPlayingSongName.setText(songFiles.get(position).getTitle());
                if (nowPlayingSongArtist == null) throw new AssertionError();
                nowPlayingSongArtist.setText(songFiles.get(position).getArtist());
                if (nowPlayingSongDuration == null) throw new AssertionError();
                nowPlayingSongDuration.setText("• "+millisecondsToTime(Integer.parseInt(songFiles.get(position).getDuration())));


                byte[] nowPlayingImage = getAlbumArt(songFiles.get(position).getPath());
                if (nowPlayingImage != null){
                    Glide.with(context)
                            .load(nowPlayingImage)
                            .circleCrop()
                            .placeholder(R.drawable.music_note)
                            .into(selectedSongImg);
                    selectedSongImg.setPadding(0, 0, 0, 0);
                }
                else {
                    selectedSongImg.setPadding(12, 12, 12, 12);
                    Glide.with(context)
                            .load(R.drawable.music_note)
                            .centerInside()
                            .into(selectedSongImg);
                }


                dialog.show();
                requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);

            }



//            private void showEqualizer() {
//                try {
//                    Intent eqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
//                    eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService.mediaPlayer.getAudioSessionId());
//                    eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
//                    eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
//                    startActivityForResult(eqIntent, 13);
//                }
//                catch (Exception e){
//                    Toast.makeText(context, "Equalizer Not Found", Toast.LENGTH_SHORT).show();
//                }
//            }

            private void goToArtist() {
                String artistTitle = songFiles.get(position).getArtist();
                Intent intent = new Intent(context, ArtistDetails.class);
                intent.putExtra("artistName", artistTitle);
                intent.putExtra("artistTitle", artistTitle);
                context.startActivity(intent);
            }

            private void goToAlbum() {
                String albumTitle = songFiles.get(position).getAlbum();
                String albumYear = songFiles.get(position).getYear();
                Intent intent = new Intent(context, AlbumDetails.class);
                intent.putExtra("albumName", albumTitle);
                intent.putExtra("albumTitle", albumTitle);
                intent.putExtra("albumYear", albumYear);
                context.startActivity(intent);
            }

            private void shareFiles() {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("audio/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(songFiles.get(position).getPath()));
                context.startActivity(Intent.createChooser(shareIntent, "Sharing Music File via"));
            }
        });

    }

    private void showEditTagsDialog(int position) {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_tag_editor, null);

        // Initialize the EditText fields
        EditText editTitle = dialogView.findViewById(R.id.edit_title);
        EditText editArtist = dialogView.findViewById(R.id.edit_artist);
        EditText editAlbum = dialogView.findViewById(R.id.edit_album);
        EditText editYear = dialogView.findViewById(R.id.edit_year);

        // Set current metadata values to EditText fields
        editTitle.setText(songFiles.get(position).getTitle());
        editArtist.setText(songFiles.get(position).getArtist());
        editAlbum.setText(songFiles.get(position).getAlbum());
        editYear.setText(songFiles.get(position).getYear());

        // Build the dialog
        new MaterialAlertDialogBuilder(context)
                .setTitle("Edit Song Info")
                .setIcon(R.drawable.edit_tags)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Retrieve updated values from the user
                    String newTitle = editTitle.getText().toString();
                    String newArtist = editArtist.getText().toString();
                    String newAlbum = editAlbum.getText().toString();
                    String newYear = editYear.getText().toString();

                    // Get the path of the song
                    String songPath = songFiles.get(position).getPath();
                    Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(songFiles.get(position).getId()));

                    // Update the song metadata
                    Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
//                    updateSongMetadata(songUri, newTitle, newArtist, newAlbum, newYear, position);

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

//    private void updateSongMetadata(Uri songUri, String newTitle, String newArtist, String newAlbum, String newYear, int position) {
//        try {
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Audio.Media.TITLE, newTitle);
//            values.put(MediaStore.Audio.Media.ARTIST, newArtist);
//            values.put(MediaStore.Audio.Media.ALBUM, newAlbum);
//            values.put(MediaStore.Audio.Media.YEAR, newYear);
//
//            // Update the MediaStore with the new metadata
//            int rowsUpdated = context.getContentResolver().update(songUri, values, null, null);
//
//            if (rowsUpdated > 0) {
//                // Update the metadata in the adapter's file list
//                songFiles.get(position).setTitle(newTitle);
//                songFiles.get(position).setArtist(newArtist);
//                songFiles.get(position).setAlbum(newAlbum);
//                songFiles.get(position).setYear(newYear);
//
//                // Notify the adapter about the changes
//                notifyItemChanged(position);
//                Toast.makeText(context, "Metadata updated successfully", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, "Failed to update metadata", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, "Error updating metadata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }

//    @SuppressLint("NotifyDataSetChanged")
//    private void updateSongMetadata(String songPath, String newTitle, String newArtist, String newAlbum, String newYear) {
//        // ContentValues to store the new metadata
//        ContentValues values = new ContentValues();
//
//        // Add new metadata values to ContentValues
//        if (!newTitle.isEmpty()) values.put(MediaStore.Audio.Media.TITLE, newTitle);
//        if (!newArtist.isEmpty()) values.put(MediaStore.Audio.Media.ARTIST, newArtist);
//        if (!newAlbum.isEmpty()) values.put(MediaStore.Audio.Media.ALBUM, newAlbum);
//        if (!newYear.isEmpty()) values.put(MediaStore.Audio.Media.YEAR, newYear);
//
//        // Uri of the song to be updated
//        Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(new File(songPath).getName()));
//
//        // Update the media store with new metadata
//        int rowsUpdated = context.getContentResolver().update(songUri, values, null, null);
//
//        if (rowsUpdated > 0) {
//            Toast.makeText(context, "Song metadata updated", Toast.LENGTH_SHORT).show();
//            notifyDataSetChanged();
//        } else {
//            Toast.makeText(context, "Failed to update song metadata", Toast.LENGTH_SHORT).show();
//        }
//    }

    @SuppressLint("SetTextI18n")
    private void showSongDetails(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        titleTextView.setText("Title : " + songFiles.get(position).getTitle());
        artistTextView.setText("Artist : " + songFiles.get(position).getArtist());
        albumTextView.setText("Album : " + songFiles.get(position).getAlbum());
        yearTextView.setText("Year : "+ songFiles.get(position).getYear());
        durationTextView.setText("Duration : " + millisecondsToTime(Integer.parseInt(songFiles.get(position).getDuration())));
        sizeTextView.setText("Size : "+ formatMB(Integer.parseInt(songFiles.get(position).getSize())));
        pathTitleTextView.setText("File path: ");
        pathTextView.setText(songFiles.get(position).getPath());


        // Close button
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());


        ImageView songImg = dialog.findViewById(R.id.coverImg);



        byte[] nowPlayingImage = getAlbumArt(songFiles.get(position).getPath());
        if (nowPlayingImage != null){
            Glide.with(context)
                    .load(nowPlayingImage)
                    .placeholder(R.drawable.music_note)
                    .into(songImg);
            songImg.setPadding(0, 0, 0, 0);
        }
        else {
            songImg.setPadding(12, 12, 12, 12);
            Glide.with(context)
                    .load(R.drawable.music_note)
                    .centerInside()
                    .into(songImg);
        }

        dialog.show();
        requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }



    @SuppressLint("NotifyDataSetChanged")
    private void deleteMusicFile(int position, View view){

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(songFiles.get(position).getId()));
        File file = new File(songFiles.get(position).getPath());
        ContentResolver contentResolver = context.getContentResolver();

        // Check if the file exists before attempting to delete
        if (file.exists()) {
            boolean isDelete = file.delete(); // Attempt to delete the file

            try {
                if (isDelete) {
                    int deletedRows = contentResolver.delete(contentUri, null, null);
                    if (deletedRows > 0) {
                        songFiles.remove(position);
                        notifyItemRemoved(position);
                        validSongs = Music.checkAndSetValidSongs(tracks);
                        validAlbums = Music.checkAndSetValidSongs(albums);
                        validArtists = Music.checkAndSetValidSongs(artists);
                        notifyDataSetChanged();
//                        Snackbar.make(view, "File Deleted From device", Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(context, "File Deleted From device", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(context, "Failed to delete file from device", Toast.LENGTH_SHORT).show();
                }
            }catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(context, "Permission issue: Grant the manage all files access permission " + e, Toast.LENGTH_SHORT).show();
            }
        }

        else {
            Toast.makeText(context, "File doesn't exist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return songFiles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView songName, songArtist, songDuration;
        ImageView albumArt, menuMore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.music_file_name);
            songArtist = itemView.findViewById(R.id.music_artist_name);
            songDuration = itemView.findViewById(R.id.music_duration);
            albumArt = itemView.findViewById(R.id.music_img);
            menuMore = itemView.findViewById(R.id.menu_more);


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
        songFiles = new ArrayList<>();
        songFiles.addAll(musicArrayList);
        notifyDataSetChanged();
    }
}

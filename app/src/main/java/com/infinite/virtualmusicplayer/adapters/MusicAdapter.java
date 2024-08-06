package com.infinite.virtualmusicplayer.adapters;

import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.formatMB;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isFav;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.lightVibrantColor;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.millisecondsToTime;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.infinite.virtualmusicplayer.activities.AlbumDetails;
import com.infinite.virtualmusicplayer.activities.ArtistDetails;
import com.infinite.virtualmusicplayer.activities.Favourite;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.model.Music;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    public static ArrayList<Music> mFiles;
    private final Context context;

    int fIndex = -1;

    public MusicAdapter(Context context, ArrayList<Music> mFiles)
    {
        MusicAdapter.mFiles = mFiles;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.songName.setText(mFiles.get(position).getTitle());
        holder.songArtist.setText(mFiles.get(position).getArtist());
        holder.songDuration.setText("• "+millisecondsToTime(Integer.parseInt(mFiles.get(position).getDuration())));


        byte[] image = getAlbumArt(mFiles.get(position).getPath());
        if (image != null){
            Glide.with(context)
                    .load(image).placeholder(R.drawable.music_note_placeholder2)
                    .into(holder.albumArt);

        }
        else {
//                holder.albumArt.setPadding(12, 12, 12, 12);
            Glide.with(context)
                    .load(R.drawable.music_note_placeholder2)
                    .into(holder.albumArt);

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (isLoop || isShuffle){
                    lightVibrantColor = Color.WHITE;
                    if (isLoop){
                        isLoop = false;
                    }
                }

                try {
                    Intent intent = new Intent(context, MusicPlayerActivity.class);
                    intent.putExtra("position", position);
                    context.startActivity(intent);


                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Exception"+ e.toString(), Toast.LENGTH_SHORT).show();
                }


            }
        });

        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                menu bottom sheet dialog
                showMenuMoreBottomSheet();
            }

            private void showMenuMoreBottomSheet() {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.menu_more_bottom_sheet_layout);

                fIndex = Music.favouriteChecker(mFiles.get(position).getId());

                ImageView nowPlayingSongImg = dialog.findViewById(R.id.nowPlayingSongImg);
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



                addToPlaylist.setOnClickListener(view -> {
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
                });

                addToFavourite.setOnClickListener(view -> {

                    if (isFav) {
                        isFav = false;
                        addToFavourite.setImageResource(R.drawable.ic_favorite);
                        Favourite.favouriteSongs.remove(mFiles.get(position));
                        Toast.makeText(context, "Removed from favourite", Toast.LENGTH_SHORT).show();

                    } else {
                        isFav = true;
                        addToFavourite.setImageResource(R.drawable.favourite_on);
                        Favourite.favouriteSongs.add(mFiles.get(position));
                        Toast.makeText(context, "Added to favourite", Toast.LENGTH_SHORT).show();

                    }
                });

                goToAlbum.setOnClickListener(view -> {
                   goToAlbum();
                   dialog.dismiss();
                });

                goToArtist.setOnClickListener(view -> {
                    goToArtist();
                    dialog.dismiss();
                });

                goToFolder.setOnClickListener(view -> {
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
                });

                goToFavourite.setOnClickListener(view -> {
                    Intent intent = new Intent(context, Favourite.class);
                    context.startActivity(intent);
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

                details.setOnClickListener(view -> {
                    showSongDetails(position);
                    dialog.dismiss();
                });


                nowPlayingSongName.setText(mFiles.get(position).getTitle());
                nowPlayingSongArtist.setText(mFiles.get(position).getArtist());
                nowPlayingSongDuration.setText("• "+millisecondsToTime(Integer.parseInt(mFiles.get(position).getDuration())));


                byte[] nowPlayingImage = getAlbumArt(mFiles.get(position).getPath());
                if (nowPlayingImage != null){
                    Glide.with(context)
                            .load(nowPlayingImage).placeholder(R.drawable.music_note)
                            .into(nowPlayingSongImg);
                    nowPlayingSongImg.setPadding(0, 0, 0, 0);
                }
                else {
                    nowPlayingSongImg.setPadding(12, 12, 12, 12);
                    Glide.with(context)
                            .load(R.drawable.music_note)
                            .into(nowPlayingSongImg);
                }


                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                String artistTitle = mFiles.get(position).getArtist();
                Intent intent = new Intent(context, ArtistDetails.class);
                intent.putExtra("artistName", artistTitle);
                intent.putExtra("artistTitle", artistTitle);
                context.startActivity(intent);
            }

            private void goToAlbum() {
                String albumTitle = mFiles.get(position).getAlbum();
                String albumYear = mFiles.get(position).getYear();
                Intent intent = new Intent(context, AlbumDetails.class);
                intent.putExtra("albumName", albumTitle);
                intent.putExtra("albumTitle", albumTitle);
                intent.putExtra("albumYear", albumYear);
                context.startActivity(intent);
            }

            private void shareFiles() {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("audio/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mFiles.get(position).getPath()));
                context.startActivity(Intent.createChooser(shareIntent, "Sharing Music File via"));
            }
        });

    }


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

        titleTextView.setText("Title : " + mFiles.get(position).getTitle());
        artistTextView.setText("Artist : " + mFiles.get(position).getArtist());
        albumTextView.setText("Album : " + mFiles.get(position).getAlbum());
        yearTextView.setText("Year : "+ mFiles.get(position).getYear());
        durationTextView.setText("Duration : " + millisecondsToTime(Integer.parseInt(mFiles.get(position).getDuration())));
        sizeTextView.setText("Size : "+ formatMB(Integer.parseInt(mFiles.get(position).getSize())));
        pathTitleTextView.setText("File path: ");
        pathTextView.setText(mFiles.get(position).getPath());


        // Close button
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }



    private void deleteMusicFile(int position, View view){

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId()));
        File file = new File(mFiles.get(position).getPath());
        boolean delete = file.delete();

        if (file.exists()){
//            Toast.makeText(context, "File exists", Toast.LENGTH_SHORT).show();
            delete = !delete;
            if (delete) {

                try {
                    context.getContentResolver().delete(contentUri, null, null);
                    mFiles.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mFiles.size());
                    Snackbar.make(view, "File Deleted From device" ,Snackbar.LENGTH_SHORT).show();
                } catch (Exception e){
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
        return mFiles.size();
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
        mFiles = new ArrayList<>();
        mFiles.addAll(musicArrayList);
        notifyDataSetChanged();
    }
}

package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.musicFiles;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.adapters.MusicAdapter;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.R;

import java.util.Random;


public class SongsFragment extends Fragment {

    RecyclerView recyclerView;
    TextView noSongsFound;

    private ViewGroup alertdialog;
    int mainPosition = 0;
    static FloatingActionButton playMainBtn;
    static ImageView tracksShuffleBtn;
    static ImageView tracksSortBtn;
    
    TextView totalSongsTV;
    @SuppressLint("StaticFieldLeak")
    public static MusicAdapter musicAdapter;

    public SongsFragment() {
        // Required empty public constructor
    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        playMainBtn = view.findViewById(R.id.playMainBtn);
        tracksShuffleBtn = view.findViewById(R.id.tracksShuffleBtn);
        tracksSortBtn = view.findViewById(R.id.tracksSortBtn);
        totalSongsTV = view.findViewById(R.id.totalSongs);
        noSongsFound = view.findViewById(R.id.no_songs_found);


        musicAdapter = new MusicAdapter(getContext(), musicFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setHasFixedSize(true);

        String totalSongs = String.valueOf(musicFiles.size());

        totalSongsTV.setText(totalSongs + " Songs");



        playMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAllSongs();
            }
        });

        tracksShuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAllSongs();
            }
        });


//        tracksSortBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                showSortPopUpMenu();
//            }
//        });




//        networkStreamBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                customNetworkStreamDialog();
//            }
//        });


        //            if (musicService != null){
//                playMainBtn.setVisibility(View.INVISIBLE);
//            }
//            else {
//                playMainBtn.setVisibility(View.VISIBLE);
//            }


//            ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(musicAdapter);
//            scaleInAnimationAdapter.setDuration(40);
//            scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
//            scaleInAnimationAdapter.setFirstOnly(true);
//            recyclerView.setAdapter(scaleInAnimationAdapter);




        if (!(musicFiles.size() < 1))
        {
            noSongsFound.setVisibility(View.GONE);
            int cacheSize = musicFiles.size();

            recyclerView.setItemViewCacheSize(cacheSize);
//        recyclerView.setHorizontalScrollBarEnabled(true);
            recyclerView.setVerticalScrollBarEnabled(true);
            recyclerView.setScrollbarFadingEnabled(true);
            recyclerView.setScrollContainer(true);

        }
        else {
            playMainBtn.setVisibility(View.INVISIBLE);
            noSongsFound.setVisibility(View.VISIBLE);
        }
        return view;
    }

//    private void showSortPopUpMenu() {
//        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();
//
//        PopupMenu moreMenu2 = new PopupMenu(getContext(), tracksSortBtn);
//        moreMenu2.getMenuInflater().inflate(R.menu.sort_menu_song_frag, moreMenu2.getMenu());
//
//        moreMenu2.show();
//
//        // PopUp Menu click listener
//        moreMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            //                    @SuppressLint({"NonConstantResourceId", "RestrictedApi"})
//            @SuppressLint("QueryPermissionsNeeded")
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id = item.getItemId();
//
//                if (id == R.id.by_name) {
//                    if (musicService != null){
//                        if (musicService.isPlaying()){
//                            musicService.pause();
//
//                            try {
//                                editor.putString("sorting", "sortByName");
//                                editor.apply();
//                                this.recreate();
//                            }catch (Exception e){
//                                e.printStackTrace();
//                                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        else {
//                            try {
//                                editor.putString("sorting", "sortByName");
//                                editor.apply();
//                                this.recreate();
//                            }catch (Exception e){
//                                e.printStackTrace();
//                                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                    } else {
//                        try {
//                            editor.putString("sorting", "sortByName");
//                            editor.apply();
//                            this.recreate();
//                        }catch (Exception e){
//                            e.printStackTrace();
//                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                } else if (id == R.id.by_date) {
//                    if (musicService != null){
//                        if (musicService.isPlaying()){
//                            musicService.pause();
//
//                            try {
//                                editor.putString("sorting", "sortByDate");
//                                editor.apply();
//                                this.recreate();
//                            }catch (Exception e){
//                                e.printStackTrace();
//                                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            try {
//                                editor.putString("sorting", "sortByDate");
//                                editor.apply();
//                                this.recreate();
//                            }catch (Exception e){
//                                e.printStackTrace();
//                                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                    else {
//                        try {
//                            editor.putString("sorting", "sortByDate");
//                            editor.apply();
//                            this.recreate();
//                        }catch (Exception e){
//                            e.printStackTrace();
//                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//
//                } else if (id == R.id.by_size) {
//                    if (musicService != null){
//                        if (musicService.isPlaying()){
//                            musicService.pause();
//
//                            try {
//                                editor.putString("sorting", "sortBySize");
//                                editor.apply();
//                                this.recreate();
//                            }catch (Exception e){
//                                e.printStackTrace();
//                                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            try {
//                                editor.putString("sorting", "sortBySize");
//                                editor.apply();
//                                this.recreate();
//                            }catch (Exception e){
//                                e.printStackTrace();
//                                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                    }
//                    else {
//                        try {
//                            editor.putString("sorting", "sortBySize");
//                            editor.apply();
//                            this.recreate();
//                        }catch (Exception e){
//                            e.printStackTrace();
//                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                }
//
//                return true;
//            }
//        });
//
//    }

//    private void customNetworkStreamDialog() {
//        View customDialog = LayoutInflater.from(getContext()).inflate(R.layout.network_stream_dialog, alertdialog, false);
//        new MaterialAlertDialogBuilder(requireContext())
//                .setView(customDialog)
//                .setTitle("Network Stream")
//                .setPositiveButton("Add", (dialog, which) -> {
//                    networkStreamUrlEditInput = alertdialog.findViewById(R.id.networkStream);
//                    // Exit the app
//                    networkStreamUrl = String.valueOf(networkStreamUrlEditInput.getText());
//
//                    if (!(networkStreamUrl.isEmpty()))
//                    {
//                        Toast.makeText(getContext(), networkStreamUrl, Toast.LENGTH_SHORT).show();
//                    }
////                    dialog.dismiss();
//                })
//                .setNegativeButton("cancel", (dialog, which) -> {
//                    // Exit the app
//                    dialog.dismiss();
//                })
//                .show();
//    }

    private void playAllSongs() {
        isShuffle = true;
        if (!isLoop) {
            mainPosition = getRandom(musicFiles.size() - 1);
        }
        if (!(musicFiles.size() < 1)){
            Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
            intent.putExtra("position", mainPosition);
            intent.putExtra("mainPlayIntent", "MainPlayIntent");
            startActivity(intent);

        }else {
            Toast.makeText(getContext(), "You hava not any song", Toast.LENGTH_SHORT).show();
        }



    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

}

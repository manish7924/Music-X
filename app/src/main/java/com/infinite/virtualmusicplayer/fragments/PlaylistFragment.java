package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.playlists;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validAlbums;
import static com.infinite.virtualmusicplayer.adapters.PlaylistAdapter.playlistList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.Favourite;
import com.infinite.virtualmusicplayer.adapters.AlbumAdapter;
import com.infinite.virtualmusicplayer.adapters.PlaylistAdapter;
import com.infinite.virtualmusicplayer.model.Music;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class PlaylistFragment extends Fragment {

    RecyclerView recyclerView;
    PlaylistAdapter playlistAdapter;
    LinearLayout favouriteLayout;

    TextView noPlaylistFound, noPlaylistFoundDes;
    ImageView noPlaylistImg;

//    private ViewGroup alertdialog;

    FloatingActionButton networkStreamBtn;
//    String networkStreamUrl;

//    public static Music.MusicPlaylist musicPlaylist = new Music.MusicPlaylist();

    TextInputEditText playlistNameEditText, yourNameEditText;
    FloatingActionButton addPlaylistBtn;

    public static Music.Playlist playlist = new Music.Playlist();
    public static Music.MusicPlaylist musicPlaylist = new Music.MusicPlaylist();



    public PlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        recyclerView = view.findViewById(R.id.playlistRv);
        networkStreamBtn = view.findViewById(R.id.networkStreamBtn);
        addPlaylistBtn = view.findViewById(R.id.addPlaylistBtn);
//        favouriteLayout = view.findViewById(R.id.favouriteLayout);
        noPlaylistFound = view.findViewById(R.id.no_playlist_found);
        noPlaylistImg = view.findViewById(R.id.no_playlist_found_img);
        noPlaylistFoundDes = view.findViewById(R.id.no_playlist_description);


        playlistAdapter = new PlaylistAdapter(getContext(), playlistList = musicPlaylist.ref);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(playlistAdapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setHorizontalScrollBarEnabled(true);


//        favouriteLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), Favourite.class);
//                startActivity(intent);
//            }
//        });


        addPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPlaylistCustomAlertDialog();
//                Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });


        networkStreamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showLinkDialog();
                Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show();

            }
        });



        if (!(playlists.isEmpty()))
        {
            noPlaylistImg.setVisibility(View.GONE);
            noPlaylistFound.setVisibility(View.GONE);
            noPlaylistFoundDes.setVisibility(View.GONE);

        }
        return view;
    }

//    private void showLinkDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Enter URL");
//
//        // Set up the input
//        final EditText input = new EditText(getContext());
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String url = input.getText().toString().trim();
//                if (!url.isEmpty()) {
//                    // Start Music Player Activity and play URL
//                    try {
//                        Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
//                        intent.putExtra("url", url);
//                        startActivity(intent);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }

    private void createPlaylistCustomAlertDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customDialog = inflater.inflate(R.layout.add_playlist_dialog, null);

        playlistNameEditText = customDialog.findViewById(R.id.playlistName);
        yourNameEditText = customDialog.findViewById(R.id.createdBy);

        new MaterialAlertDialogBuilder(requireContext())
                .setView(customDialog)
                .setTitle("New Playlist")
                .setPositiveButton("Create", (dialog, which) -> {

                    String playlistName = Objects.requireNonNull(playlistNameEditText.getText()).toString();
                    String createdBy = yourNameEditText.getText().toString();

                    if (!playlistName.isEmpty()) {
                        addPlaylist(playlistName, createdBy);
                    }
                    else {
                        Toast.makeText(getContext(), "Empty field not allowed", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Exit the app
                    dialog.dismiss();
                })
                .show();
    }


    private void addPlaylist(String name, String createdBy) {
        boolean playlistExists = false;

        // Check if the playlist already exists
        for (Music.Playlist playlist : musicPlaylist.ref) {
            if (name.equals(playlist.name)) {
                playlistExists = true;
                break;
            }
        }

        // If the playlist exists, show a toast message
        if (playlistExists) {
            Toast.makeText(getContext(), "Playlist Exist!!", Toast.LENGTH_SHORT).show();
        } else {
            // Create a new playlist
            Music.Playlist tempPlaylist = new Music.Playlist();
            tempPlaylist.name = name;
            tempPlaylist.myPlaylist = new ArrayList<>();
            tempPlaylist.createdBy = createdBy;

            // Get the current date and format it
            Date calendar = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            tempPlaylist.createdOn = sdf.format(calendar);

            // Add the new playlist to the list
            musicPlaylist.ref.add(tempPlaylist);


            noPlaylistImg.setVisibility(View.GONE);
            noPlaylistFound.setVisibility(View.GONE);
            noPlaylistFoundDes.setVisibility(View.GONE);

            // Refresh the adapter
            playlistAdapter.refreshPlaylist();
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
//        playlistAdapter.notifyDataSetChanged();

    }
}
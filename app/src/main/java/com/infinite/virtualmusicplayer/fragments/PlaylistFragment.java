package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.playlists;
import static com.infinite.virtualmusicplayer.adapters.PlaylistAdapter.playlistList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.infinite.virtualmusicplayer.activities.Favourite;
import com.infinite.virtualmusicplayer.model.Music;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.adapters.PlaylistAdapter;
import com.infinite.virtualmusicplayer.R;


public class PlaylistFragment extends Fragment {

    RecyclerView recyclerView;
    PlaylistAdapter playlistAdapter;
    LinearLayout favouriteLayout;

    private ViewGroup alertdialog;

    FloatingActionButton networkStreamBtn;
    String networkStreamUrl;

    public static Music.MusicPlaylist musicPlaylist = new Music.MusicPlaylist();

    TextInputEditText playlistNameEditText, yourNameEditText;
    FloatingActionButton addPlaylistBtn;


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
        favouriteLayout = view.findViewById(R.id.favouriteLayout);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setHorizontalScrollBarEnabled(true);

        favouriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Favourite.class);
                startActivity(intent);
            }
        });


        addPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                createPlaylistCustomAlertDialog();
                Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });


        networkStreamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showLinkDialog();
                Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show();

            }
        });



        if (!(playlists.size() < 1))
        {
            playlistAdapter = new PlaylistAdapter(getContext(), playlistList);
            recyclerView.setAdapter(playlistAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

//            createPlaylistCurstomAlertDialog();
        }
        return view;
    }

    private void showLinkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter URL");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = input.getText().toString().trim();
                if (!url.isEmpty()) {
                    // Start Music Player Activity and play URL
                    try {
                        Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

//    private void createPlaylistCustomAlertDialog() {
//        playlistNameEditText = customDialog.findViewById(R.id.playlistName);
//        yourNameEditText = customDialog.findViewById(R.id.createdBy);
//
//        new MaterialAlertDialogBuilder(requireContext())
//                .setView(customDialog)
//                .setTitle("New Playlist")
//                .setPositiveButton("Add", (dialog, which) -> {
//                    // Exit the app
////                    playlistName = playlistEditInput.getText().toString();
////                    createdByName = createdByEditInput.getText().toString();
////
//////                    if (!(playlistName.isEmpty()  &&  createdByName.isEmpty()))
//////                    {
//////                        addPlaylist(playlistName, createdByName);
//////                    }
////                    dialog.dismiss();
//
//                    String playlistName = playlistNameEditText.getText().toString();
//                    String createdBy = yourNameEditText.getText().toString();
//                    if (!playlistName.isEmpty() && !createdBy.isEmpty()) {
//                        addPlaylist(playlistName, createdBy);
//                    }
//                    dialog.dismiss();
//
//                })
//                .setNegativeButton("cancel", (dialog, which) -> {
//                    // Exit the app
//                    dialog.dismiss();
//                })
//                .show();
//    }


//    private void addPlaylist(String name, String createdBy) {
//        boolean playlistExists = false;
//        ArrayList<Music> myPlaylist = new ArrayList<>();
//        for (Music.Playlist playlist : musicPlaylist.getRef()) {
//            if (name.equals(playlist.getName())) {
//                playlistExists = true;
//                break;
//            }
//        }
//        if (playlistExists) {
//            Toast.makeText(getContext(), "Playlist Exist!!", Toast.LENGTH_SHORT).show();
//        } else {
//            Music.Playlist tempPlaylist = new Music.Playlist();
//            tempPlaylist.setName(name);
//            tempPlaylist.setPlaylist(new ArrayList<>());
//            tempPlaylist.setCreatedBy(createdBy);
//            Date calendar = Calendar.getInstance().getTime();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
//            tempPlaylist.setCreatedOn(sdf.format(calendar));
//            musicPlaylist.getRef().add(tempPlaylist);
//            playlistAdapter.refreshPlaylist(myPlaylist);
//        }
//    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
//        playlistAdapter.notifyDataSetChanged();

    }
}
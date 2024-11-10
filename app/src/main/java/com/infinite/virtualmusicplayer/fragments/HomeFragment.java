package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.artists;
import static com.infinite.virtualmusicplayer.activities.MainActivity.musicFiles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.infinite.virtualmusicplayer.activities.Favourite;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.adapters.ArtistAdapter;
import com.infinite.virtualmusicplayer.adapters.MusicAdapter;
import com.infinite.virtualmusicplayer.model.Music;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private ImageView historyBtn, favBtn, mostPlayedBtn, shuffleBtn, goToFav;
    private TextView userName;
    private EditText editName;
    private RecyclerView recentlyAddedRV, recentlyPlayedRV, recentArtistsRV, favouritesRV;
//    private MusicAdapter recentlyAddedAdapter, recentlyPlayedAdapter, recentArtistsAdapter, favoritesAdapter;
    private MusicAdapter recentlyAddedAdapter;
    private MusicAdapter recentlyPlayedAdapter;
    private ArtistAdapter recentArtistsAdapter;
    private ArtistAdapter favoritesAdapter;

//    private List<Music> recentlyAddedSongs, recentlyPlayedAlbums, recentArtists, favoriteSongs;

    private View dialogView;

    private RelativeLayout relativeLayout;



    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

//        RecyclerView
        recentlyAddedRV = view.findViewById(R.id.recently_added_songs_rv);
        recentlyPlayedRV = view.findViewById(R.id.recently_played_albums_rv);
        recentArtistsRV = view.findViewById(R.id.recent_artists_rv);
        favouritesRV = view.findViewById(R.id.favourites_rv);

        favBtn = view.findViewById(R.id.homeFavBtn);
        relativeLayout = view.findViewById(R.id.recent_favorites);
        goToFav = view.findViewById(R.id.go_to_favourite_recent);
        userName = view.findViewById(R.id.userName);
        dialogView = inflater.inflate(R.layout.dialog_user_name, null);

        editName = dialogView.findViewById(R.id.edit_name);


//        int cacheSize = musicFiles.size();
//        recentlyAddedRV.setHasFixedSize(true);
//        recentlyAddedRV.setItemViewCacheSize(cacheSize);
////        recyclerView.setHorizontalScrollBarEnabled(true);
//        recentlyAddedRV.setVerticalScrollBarEnabled(true);
//        recentlyAddedRV.setScrollbarFadingEnabled(true);
//        recentlyAddedRV.setScrollContainer(true);
//        recentlyAddedRV.setScrollIndicators(cacheSize);


//        Getting the saved preference
        SharedPreferences getSharedPref = getContext().getSharedPreferences("name", Context.MODE_PRIVATE);
        String value = getSharedPref.getString("str", "User Name");
        userName.setText(value);



//        setupRecyclerViews();


//        recentlyAddedRV.setLayoutManager(new GridLayoutManager(getContext(), 1));
//        recentlyAddedRV.setLayoutManager(new RecyclerView.LayoutManager() {
//            @Override
//            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//                return null;
//            }
//        });


        //        homeMusicAdapter = new HomeMusicAdapter(getContext(),musicFiles);
//        musicFiles = new ArrayList<>();
//        musicFiles.add(new Music("", "Tulsi Kumar Mashup", "Tulsi Kumar", "", "", "", "", ""));
//        home_musicRV.setAdapter(homeMusicAdapter);
//        home_musicRV.setLayoutManager(new LinearLayoutManager(getContext()));

//
//        artistAdapter = new ArtistAdapter(getContext(), artists);
//        home_artistsRV.setAdapter(artistAdapter);
////        home_artistsRV.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        home_artistsRV.setLayoutManager(new RecyclerView.LayoutManager() {
//            @Override
//            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//                return null;
//            }
//        });
////
//        albumAdapter = new AlbumAdapter(getContext(), albums);
//        home_albumsRV.setAdapter(albumAdapter);
////        home_albumsRV.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        home_albumsRV.setLayoutManager(new RecyclerView.LayoutManager() {
//            @Override
//            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//                return null;
//            }
//        });



        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    editName.setText(userName.getText());
                    showEditNameDialog();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });




        favBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), Favourite.class);
            startActivity(intent);
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Favourite.class);
                startActivity(intent);
            }
        });

        goToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Favourite.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void showEditNameDialog() {
        try {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Edit User Name")
                    .setIcon(R.drawable.edit_tags)
                    .setView(dialogView)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String newName = editName.getText().toString();

                        SharedPreferences shrd = getContext().getSharedPreferences("name", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shrd.edit();

                        editor.putString("str", newName);
                        editor.apply();

                        userName.setText(newName);

                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

//    private void setupRecyclerViews() {
//        // Setup for Recently Added Songs
////        musicFiles = new ArrayList<>();
//        recentlyAddedAdapter = new MusicAdapter(getContext(), musicFiles);
//        recentlyAddedRV.setAdapter(recentlyAddedAdapter);
//        recentlyAddedRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
//        // Setup for Recently Played Albums
////        musicFiles = new ArrayList<>();
//        recentlyPlayedAdapter = new MusicAdapter(getContext(), musicFiles);
//        recentlyPlayedRV.setAdapter(recentlyPlayedAdapter);
//        recentlyPlayedRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
//        // Setup for Recent Artists
////        artists = new ArrayList<>();
//        recentArtistsAdapter = new ArtistAdapter(getContext(), artists);
//        recentArtistsRV.setAdapter(recentArtistsAdapter);
//        recentArtistsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
//        // Setup for Favorites
////        artists = new ArrayList<>();
//        favoritesAdapter = new ArtistAdapter(getContext(), artists);
//        favouritesRV.setAdapter(favoritesAdapter);
//        favouritesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//  }


//    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
//            builder.setTitle("Edit User Name");
//            builder.setIcon(R.drawable.edit_tags);
//            builder.setView(dialogView);
//            builder.setPositiveButton("OK", (dialog, which) -> {
//                String newName = editName.getText().toString();
//
//                SharedPreferences shrd = getContext().getSharedPreferences("name", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = shrd.edit();
//
//                editor.putString("str", newName);
//                editor.apply();
//
//                userName.setText(newName);
//
//            });
//            builder.setNegativeButton("Cancel", null);
//
//            builder.show();
}
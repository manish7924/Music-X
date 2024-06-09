package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.musicFiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infinite.virtualmusicplayer.adapters.AlbumAdapter;
import com.infinite.virtualmusicplayer.adapters.ArtistAdapter;
import com.infinite.virtualmusicplayer.activities.Favourite;
import com.infinite.virtualmusicplayer.R;


public class HomeFragment extends Fragment {

    ImageView historyBtn, favBtn, mostPlayedBtn, shuffleBtn, goToFav;
    RecyclerView home_musicRV;
    RecyclerView home_albumsRV;
    RecyclerView home_artistsRV;

    RelativeLayout relativeLayout;
//    HomeMusicAdapter homeMusicAdapter;
    AlbumAdapter albumAdapter;
    ArtistAdapter artistAdapter;


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
        home_musicRV = view.findViewById(R.id.homeRV);
        favBtn = view.findViewById(R.id.homeFavBtn);
        relativeLayout = view.findViewById(R.id.recent_favorites);
        goToFav = view.findViewById(R.id.go_to_favourite_recent);

        home_albumsRV = view.findViewById(R.id.home_albums_RV);
        home_artistsRV = view.findViewById(R.id.home_artist_RV);


        int cacheSize = musicFiles.size();
        home_musicRV.setHasFixedSize(true);

        home_musicRV.setItemViewCacheSize(cacheSize);
//        recyclerView.setHorizontalScrollBarEnabled(true);
        home_musicRV.setVerticalScrollBarEnabled(true);
        home_musicRV.setScrollbarFadingEnabled(true);
        home_musicRV.setScrollContainer(true);
        home_musicRV.setScrollIndicators(cacheSize);

//        homeMusicAdapter = new HomeMusicAdapter(getContext(),musicFiles);
//        musicFiles = new ArrayList<>();
//        musicFiles.add(new Music("", "Tulsi Kumar Mashup", "Tulsi Kumar", "", "", "", "", ""));
//        home_musicRV.setAdapter(homeMusicAdapter);
//        home_musicRV.setLayoutManager(new LinearLayoutManager(getContext()));

        home_musicRV.setLayoutManager(new GridLayoutManager(getContext(), 1));
        home_musicRV.setLayoutManager(new RecyclerView.LayoutManager() {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return null;
            }
        });
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


//            ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(artistAdapter);
//            scaleInAnimationAdapter.setDuration(40);
//            scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
//            scaleInAnimationAdapter.setFirstOnly(true);
//            recyclerView.setAdapter(scaleInAnimationAdapter);




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


}
package com.infinite.virtualmusicplayer.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.infinite.virtualmusicplayer.R;

public class MusicPlayerBottomSheetFragment extends BottomSheetDialogFragment {

    private TextView songTitle, artistName;
    private SeekBar seekBar;
    private Button playPauseButton;

    public static MusicPlayerBottomSheetFragment newInstance() {
        return new MusicPlayerBottomSheetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_music_player, container, false);

//        songTitle = view.findViewById(R.id.songTitle);
//        artistName = view.findViewById(R.id.artistTitle);
//        seekBar = view.findViewById(R.id.seekBar);
//        playPauseButton = view.findViewById(R.id.playPauseBtn);
//
//        // Set initial data (e.g., song title, artist)
//        songTitle.setText("Your Song Title");
//        artistName.setText("Your Artist Name");
//
//        // Implement play/pause functionality
//        playPauseButton.setOnClickListener(v -> {
////            if (playPauseButton.getText().equals("Play")) {
////                playPauseButton.setText("Pause");
////                // TODO: Start playing music
////            } else {
////                playPauseButton.setText("Play");
////                // TODO: Pause music
////            }
//        });

        // TODO: Handle seekBar changes and music playback

        return view;
    }
}
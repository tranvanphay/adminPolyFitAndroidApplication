package com.hades.adminpolyfit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.hades.adminpolyfit.Constants.Constants;
import com.hades.adminpolyfit.R;

import java.util.Objects;

public class PlayVideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    YouTubePlayerView youTubePlayerView;
    int REQUEST_RELOAD_VIDEO = 999;
    Dialog dialogQuotes;
    String idVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogQuotes = new Dialog(PlayVideoActivity.this);
        dialogQuotes.setContentView(R.layout.layout_quotes);
        dialogQuotes.setCancelable(false);
        dialogQuotes.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            idVideo = bundle.getString("videoUrl");
        }
        Log.e("PhayTran", idVideo);
        if (idVideo.length() > 40 && idVideo.length() > 35) {
            idVideo = idVideo.substring(32);
        } else if (idVideo.length() > 20) {
            idVideo = idVideo.substring(17);
            Log.e("PhayTran", "link short");
        } else {
            idVideo = "NjzUc5vZ-34";
        }
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.playVideoView);
        youTubePlayerView.initialize(Constants.YOUTUBE_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo(idVideo);
        Log.e("PhayTran", "IDVideo ::: " + idVideo);
        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {
                youTubePlayer.play();
            }

            @Override
            public void onAdStarted() {
                dialogQuotes.show();
            }

            @Override
            public void onVideoStarted() {
                if (dialogQuotes.isShowing()) {
                    dialogQuotes.dismiss();
                }
            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(PlayVideoActivity.this, REQUEST_RELOAD_VIDEO);
        } else {
            Toast.makeText(this, "ERROR!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RELOAD_VIDEO) {
            youTubePlayerView.initialize(Constants.YOUTUBE_KEY, PlayVideoActivity.this);
        }
    }
}

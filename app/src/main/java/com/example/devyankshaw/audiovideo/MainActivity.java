package com.example.devyankshaw.audiovideo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener {

    //UI Components
    private VideoView myVideoView;
    private Button btnPlayVideo;
    private Button btnPlayMusic,btnPauseMusic;
    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    private SeekBar volumeSeekBar;
    private AudioManager audioManager;
    private SeekBar moveBackAndForthSeekBar;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myVideoView = findViewById(R.id.myVideoView);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);

        btnPauseMusic = findViewById(R.id.btnPauseMusic);
        btnPlayMusic = findViewById(R.id.btnPlayMusic);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        moveBackAndForthSeekBar = findViewById(R.id.seekBarMove);

       btnPlayVideo.setOnClickListener(MainActivity.this);
       btnPlayMusic.setOnClickListener(MainActivity.this);
       btnPauseMusic.setOnClickListener(MainActivity.this);



       mediaController = new MediaController(MainActivity.this);

       mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.music);//Sets the path our music and this mediaPlayer object is responsible for playing music
       audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);//Access the audio service

        int maximumVolumeOfUserDevice = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//Access the maximum volume of user's device
        int currentVolumeOfUserDevice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//Access the current volume of user's device

        volumeSeekBar.setMax(maximumVolumeOfUserDevice);
        volumeSeekBar.setProgress(currentVolumeOfUserDevice);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){//If user is interacting with the seekBar
                    //Toast.makeText(MainActivity.this, Integer.toString(progress), Toast.LENGTH_SHORT).show();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        moveBackAndForthSeekBar.setOnSeekBarChangeListener(MainActivity.this);
        moveBackAndForthSeekBar.setMax(mediaPlayer.getDuration());

        mediaPlayer.setOnCompletionListener(MainActivity.this);
    }

    @Override
    public void onClick(View buttonView) {

        switch (buttonView.getId()){
            case R.id.btnPlayVideo:
                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);//Sets the path our video

                myVideoView.setVideoURI(videoUri);
                myVideoView.setMediaController(mediaController);
                mediaController.setAnchorView(myVideoView);//Anchor mediaController on our video
                myVideoView.start();
                break;

            case R.id.btnPlayMusic:
                mediaPlayer.start();
                timer = new Timer();//Instantiate the timer object
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        moveBackAndForthSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }, 0, 1000);
                break;

            case R.id.btnPauseMusic:
                mediaPlayer.pause();
                timer.cancel();
                break;

        }




    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            //Toast.makeText(MainActivity.this,Integer.toString(progress), Toast.LENGTH_SHORT).show();
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mediaPlayer.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        timer.cancel();
        Toast.makeText(MainActivity.this, "Music is ended", Toast.LENGTH_SHORT).show();
    }
}

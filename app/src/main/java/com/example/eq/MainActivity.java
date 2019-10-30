package com.example.eq;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.ArrayList;

// 이퀄라이져 테스트 클래스입니다.

public class MainActivity extends AppCompatActivity {

    private Visualizer audioOutput = null;
    public float intensity = 0; //intensity is a value between 0 and 1. The intensity in this case is the system output volume
    MediaPlayer mMediaPlayer;
    Equalizer mEqualizer;
    Button getmusic;

    TextView log1;
    TextView log2;
    TextView log3;
    TextView log4;
    TextView log5;
    TextView log6;
    TextView log7;
    TextView log8;
    TextView log9;
    TextView log10;
    TextView log11;
    TextView log12;

    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getmusic = (Button)findViewById(R.id.button);
        pb = (ProgressBar)findViewById(R.id.progressBar);
        pb.setMax(30000);
        tedPermission(); // 권한 받아오기 !


        getmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 음악 가져오기

                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,1);

            }
        });

    }


    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setDeniedMessage("사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.")
                .setPermissions(Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.VIBRATE)
                .check();

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {

        if (requestCode == 1) { // 음악 데이터 끌어오기
            if (resultCode == RESULT_OK) {
                //the selected audio.
                Uri uri = data.getData();

                String[] projection = {MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DATA
                };

                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

                while (cursor.moveToNext()) {

                    Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+ cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

                    if(mMediaPlayer != null){
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mEqualizer.release();
                        mMediaPlayer = null;
                        mEqualizer = null;

                    }

                    mMediaPlayer = new MediaPlayer();

                    try {
                        mMediaPlayer.setDataSource(this, musicURI);
                        mMediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();

                    mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
                    mEqualizer.setEnabled(true);

                    LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.LinearLayoutEqual);
                    mLinearLayout.removeAllViews();
                    setupEqualizerFxAndUI ();
                }
                cursor.close();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void setupEqualizerFxAndUI(){

        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.LinearLayoutEqual);

        TextView equalizerHeading = new TextView(this);
        equalizerHeading.setText("Equalizer");
        equalizerHeading.setTextSize(20);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
        mLinearLayout.addView(equalizerHeading);

        log1 = (TextView)findViewById(R.id.log1);
        log2 = (TextView)findViewById(R.id.log2);
        log3 = (TextView)findViewById(R.id.log3);
        log4 = (TextView)findViewById(R.id.log4);
        log5 = (TextView)findViewById(R.id.log5);
        log6 = (TextView)findViewById(R.id.log6);
        log7 = (TextView)findViewById(R.id.log7);
        log8 = (TextView)findViewById(R.id.log8);
        log9 = (TextView)findViewById(R.id.log9);
        log10 = (TextView)findViewById(R.id.log10);
        log11 = (TextView)findViewById(R.id.log11);
        log12 = (TextView)findViewById(R.id.log12);

        short numberFrequencyBands = mEqualizer.getNumberOfBands();

        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];

        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];


        for(short i = 0; i < numberFrequencyBands; i++){
            final short equalizerBandIndex = i;

            TextView frequencyHeaderTextView = new TextView(this);
            frequencyHeaderTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            frequencyHeaderTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextView
                    .setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + "Hz");
            mLinearLayout.addView(frequencyHeaderTextView);

            LinearLayout seekBarRowLayout = new LinearLayout(this);
            seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView lowerEqualizerBandLevelTextView = new TextView(this);
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            lowerEqualizerBandLevelTextView.setText((lowerEqualizerBandLevel/100) + "dB");

            TextView upperEqualizerBandlevelTextView = new TextView(this);
            upperEqualizerBandlevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            upperEqualizerBandlevelTextView.setText((upperEqualizerBandLevel/100) + "dB");

            // seekbar

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;

            SeekBar seekBar = new SeekBar(this);

            seekBar.setId(i);

            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
            seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex));

            if(equalizerBandIndex == 0){
                log3.setText("Band 1 Gain : " + String.valueOf(lowerEqualizerBandLevel + mEqualizer.getBandLevel(equalizerBandIndex)));
            }
            if(equalizerBandIndex == 1){
                log5.setText("Band 2 Gain : " + String.valueOf(lowerEqualizerBandLevel + mEqualizer.getBandLevel(equalizerBandIndex)));
            }
            if(equalizerBandIndex == 2){
                log7.setText("Band 3 Gain : " + String.valueOf(lowerEqualizerBandLevel + mEqualizer.getBandLevel(equalizerBandIndex)));
            }
            if(equalizerBandIndex == 3){
                log9.setText("Band 4 Gain : " + String.valueOf(lowerEqualizerBandLevel + mEqualizer.getBandLevel(equalizerBandIndex)));
            }
            if(equalizerBandIndex == 4){
                log11.setText("Band 5 Gain : " + String.valueOf(lowerEqualizerBandLevel + mEqualizer.getBandLevel(equalizerBandIndex)));
            }

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mEqualizer.setBandLevel(equalizerBandIndex,(short)(progress+ lowerEqualizerBandLevel));
                    if(equalizerBandIndex == 0){
                        log3.setText("Band 1 Gain : " + String.valueOf((short)(progress+ lowerEqualizerBandLevel)));
                    }
                    if(equalizerBandIndex == 1){
                        log5.setText("Band 2 Gain : " + String.valueOf((short)(progress+ lowerEqualizerBandLevel)));
                    }
                    if(equalizerBandIndex == 2){
                        log7.setText("Band 3 Gain : " + String.valueOf((short)(progress+ lowerEqualizerBandLevel)));
                    }
                    if(equalizerBandIndex == 3){
                        log9.setText("Band 4 Gain : " + String.valueOf((short)(progress+ lowerEqualizerBandLevel)));
                    }
                    if(equalizerBandIndex == 4){
                        log11.setText("Band 5 Gain : " + String.valueOf((short)(progress+ lowerEqualizerBandLevel)));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    createVisualizer();
                }
            });

            seekBarRowLayout.addView(lowerEqualizerBandLevelTextView);
            seekBarRowLayout.addView(seekBar);
            seekBarRowLayout.addView(upperEqualizerBandlevelTextView);

            mLinearLayout.addView(seekBarRowLayout);

        }

        log1.setText("Number of bands is : "+ String.valueOf(mEqualizer.getNumberOfBands()));
        log2.setText("The range of gain is : " + String.valueOf(mEqualizer.getBandLevelRange()[0]) + "~" + String.valueOf(mEqualizer.getBandLevelRange()[1]));

//        log3.setText("Center frequency is :" + String.valueOf(mEqualizer.getCenterFreq((short) 0)));
        log4.setText("Band 1 range of frequency in 0 is : " + String.valueOf(mEqualizer.getBandFreqRange((short) 0)[0]) + " ~ " + String.valueOf(mEqualizer.getBandFreqRange((short) 0)[1]));

//        log5.setText("Center frequency is :" + String.valueOf(mEqualizer.getCenterFreq((short) 1)));
        log6.setText("Band 2 range of frequency in 0 is : " + String.valueOf(mEqualizer.getBandFreqRange((short) 1)[0]) + " ~ " + String.valueOf(mEqualizer.getBandFreqRange((short) 1)[1]));

//        log7.setText("Center frequency is :" + String.valueOf(mEqualizer.getCenterFreq((short) 2)));
        log8.setText("Band 3 range of frequency in 0 is : " + String.valueOf(mEqualizer.getBandFreqRange((short) 2)[0]) + " ~ " + String.valueOf(mEqualizer.getBandFreqRange((short) 2)[1]));

//        log9.setText("Center frequency is :" + String.valueOf(mEqualizer.getCenterFreq((short) 3)));
        log10.setText("Band 4 range of frequency in 0 is : " + String.valueOf(mEqualizer.getBandFreqRange((short) 3)[0]) + " ~ " + String.valueOf(mEqualizer.getBandFreqRange((short) 3)[1]));

//        log11.setText("Center frequency is :" + String.valueOf(mEqualizer.getCenterFreq((short) 4)));
        log12.setText("Band 5 range of frequency in 0 is : " + String.valueOf(mEqualizer.getBandFreqRange((short) 4)[0]) + " ~ " + String.valueOf(mEqualizer.getBandFreqRange((short) 4)[1]));
    }

    private void createVisualizer(){

        int rate = Visualizer.getMaxCaptureRate();

        if(audioOutput != null){
            audioOutput.release();
            audioOutput = null;
        }

        AudioManager audio = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_PLAY_SOUND);

        mMediaPlayer.setVolume(1f,1f);

        audioOutput = new Visualizer(mMediaPlayer.getAudioSessionId()); // get output audio stream
        audioOutput.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                intensity = ((float) waveform[0] + 128f) / 256;

                Log.d("ddddd",String.valueOf(100000f*intensity));

                if(30000 > Integer.parseInt(String.format("%.0f",100000f*intensity)))
                {
                    pb.setProgress(Integer.parseInt(String.format("%.0f",100000f*intensity)));
                    Log.e("ddddd",String.valueOf(Integer.parseInt(String.format("%.0f",100000f*intensity))));
                }
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

            }

        },rate , true, false); // waveform not freq data

        audioOutput.setEnabled(true);
    }


    protected void onPause(){
        super.onPause();

        if(isFinishing() && mMediaPlayer != null){
            mEqualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}

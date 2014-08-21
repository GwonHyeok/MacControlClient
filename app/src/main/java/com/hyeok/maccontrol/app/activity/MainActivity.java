package com.hyeok.maccontrol.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hyeok.maccontrol.app.R;
import com.hyeok.maccontrol.app.customview.VoiceGraph;
import com.hyeok.maccontrol.app.socket.SocketInit;

import java.util.ArrayList;


public class MainActivity extends Activity implements View.OnClickListener {
    private String IP = "192.168.0.9";
    private Button play_pause, prev_btn, next_btn, voice_btn, connect_btn;
    private ProgressBar voiceProgress;
    private EditText ip_edittext;
    private SocketInit socketInit;
    private SpeechRecognizer mRecognizer;
    public static LinearLayout connect_layout;
    private RelativeLayout relativeLayout;
    private Intent intent;
    private AudioManager audioManager;
    private VoiceGraph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //AudioManager Initialize
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        ViewInit();
        SocketInitialize(IP);
        SpeechRecoginzerInit();
    }

    @Override
    public void onClick(View v) {
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        if(v.getId() == play_pause.getId()) {
           new Thread() {
               @Override
                public void run() {
                   socketInit.SendCommand("playpause");
               }
           }.start();

        } else if(v.getId() == prev_btn.getId()) {
            new Thread() {
                @Override
                public void run() {
                    socketInit.SendCommand("prev");
                }
            }.start();
        } else if(v.getId() == next_btn.getId()) {
            new Thread() {
                @Override
                public void run() {
                    socketInit.SendCommand("next");
                }
            }.start();
        } else if(v.getId() == voice_btn.getId()) {
            graph.resetGraphValue();
            mRecognizer.startListening(intent);
        } else if(v.getId() == connect_btn.getId()) {
            if(ip_edittext.getText() != null)
            SocketInitialize(ip_edittext.getText().toString());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        Log.d(getClass().getName(), "Resume");
//        if(!socketInit.CheckConnection()) {
//            connect_layout.setVisibility(View.VISIBLE);
//            Log.d(getClass().getName(), "Visible");
//        } else {
//            connect_layout.setVisibility(View.GONE);
//            Log.d(getClass().getName(), "Gone");
//        }
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecognizer.destroy();
    }
    private void ViewInit() {
        play_pause = (Button)findViewById(R.id.play_pause);
        play_pause.setOnClickListener(this);
        prev_btn = (Button)findViewById(R.id.prev_btn);
        next_btn = (Button)findViewById(R.id.next_btn);
        voice_btn = (Button)findViewById(R.id.voice_detect);
        prev_btn.setOnClickListener(this);
        next_btn.setOnClickListener(this);
        voice_btn.setOnClickListener(this);
        connect_btn = (Button)findViewById(R.id.connect_btn);
        connect_btn.setOnClickListener(this);
        ip_edittext = (EditText)findViewById(R.id.ip_edittext);
        connect_layout = (LinearLayout)findViewById(R.id.connect_layout);
        voiceProgress = (ProgressBar)findViewById(R.id.voiceProgress);
        voiceProgress.setMax(12);
        relativeLayout = (RelativeLayout)findViewById(R.id.view);
        graph = (VoiceGraph)findViewById(R.id.voicegraph);
    }

    private void SpeechRecoginzerInit() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        android.speech.RecognitionListener RecognitionListener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                Log.v(getClass().toString(), "VoiceDetect");
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {
                voiceProgress.setProgress((int)v+2);
                graph.addGraphValue((v+2.0f)*10);
            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                Log.v(getClass().toString(), "EndVoice");
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> arrayList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for(String tmp : arrayList) {
                    Log.v(getClass().toString(), tmp);
                    if(tmp.equals("재생")) socketInit.SendCommand("playpause");
                    else if(tmp.contains("일시") && tmp.contains("정지")) { socketInit.SendCommand("stop"); return;}
                    else if(tmp.contains("다음") && tmp.contains("곡")) { socketInit.SendCommand("next"); return;}
                    else if(tmp.contains("이전") && tmp.contains("곡")) { socketInit.SendCommand("prev"); return;}
                    else if(tmp.contains("찾아줘")) { socketInit.SendCommand(tmp); return;}
                    else if(tmp.contains("실시간") && tmp.contains("검색어")) { socketInit.SendCommand("rtimekeyword"); return;}
                    else if(tmp.contains("시간")) { socketInit.SendCommand("currenttime"); return;}
                    else if(tmp.contains("틀어줘")) { socketInit.SendCommand(tmp); return;}
                    else if(tmp.contains("볼륨") && (tmp.contains("확") || tmp.contains("많이")) && (tmp.contains("줄여") || tmp.contains("낮춰"))) { socketInit.SendCommand("svoldown!"); return;}
                    else if(tmp.contains("볼륨") && (tmp.contains("확") || tmp.contains("많이")) && (tmp.contains("높여") || tmp.contains("올려"))) { socketInit.SendCommand("svolup!"); return;}
                    else if(tmp.contains("볼륨") && (tmp.contains("줄여") || tmp.contains("낮춰"))) { socketInit.SendCommand("svoldown"); return;}
                    else if(tmp.contains("볼륨") && (tmp.contains("높여") || tmp.contains("올려"))) { socketInit.SendCommand("svolup"); return;}
                    else if(tmp.contains("날씨")) { socketInit.SendCommand("weather"); return;}
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        };
        mRecognizer.setRecognitionListener(RecognitionListener);
        mRecognizer.startListening(intent);
    }


    private void SocketInitialize(final String ip) {
        new Thread() {
            @Override
            public void run(){
                socketInit = new SocketInit(ip);
            }
        }.start();
    }
}
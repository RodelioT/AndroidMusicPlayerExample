package ca.google.musicplayerexample;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    // Instance variables
    private MediaPlayer player; // Media player
    private ArrayList<Song> songs; // List of songs
    private int songPosn; // Song index/position
    private final IBinder musicBind = new MusicBinder();

    public void onCreate(){
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        // Configuring music player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK); //PARTIAL_WAKE_LOCK allows music playback to continue when the device goes to sleep
        player.setAudioStreamType(AudioManager.STREAM_MUSIC); // Sets the stream type

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        // Release resources when the service instance is unbound (when the user exits the app)
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    // Receives the song list from the MainActivity
    public void setList(ArrayList<Song> songList){
        songs = songList;
    }

    // Sets up a binding instance
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}

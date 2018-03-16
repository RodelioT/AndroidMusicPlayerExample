package ca.google.musicplayerexample;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;

// Handles the music playback
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    // Instance variables
    private MediaPlayer player; // Media player
    private ArrayList<Song> songs; // List of songs
    private int songPosn; // Song index/position
    private final IBinder musicBind = new MusicBinder();

    // Occurs when the MusicService is created
    public void onCreate(){
        super.onCreate(); // Creates the service
        songPosn=0; // Initialize position
        player = new MediaPlayer(); // Create the player

        initMusicPlayer();
    }

    // Initialize the music player
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
        //start music playback
        mp.start();
    }

    // Receives the song list from the MainActivity
    public void setList(ArrayList<Song> songList){
        songs = songList;
    }

    // Selects a song
    public void setSong(int songIndex){
        songPosn = songIndex;
    }

    // Sets up a binding instance
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong(){
        // Resets the media player
        player.reset();

        // Gets the song
        Song playSong = songs.get(songPosn);
        // Gets the ID of the song
        long currSong = playSong.getID();
        // Sets the Uri to the chosen song
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        // Attempting to set the data source of the media player to the song
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        // Uses an asynchronous method to play the song
        player.prepareAsync();
    }
}

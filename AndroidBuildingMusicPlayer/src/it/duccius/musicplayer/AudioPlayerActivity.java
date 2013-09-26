package it.duccius.musicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class AudioPlayerActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private  MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0; 
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<Audio> songsList = new ArrayList<Audio>();
	
	private String language = "ITA";
	private TextView textLanguage;	
	private String id_audioSD;
	private String point;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		textLanguage = (TextView) findViewById(R.id.textLanguage);
		
		// Language
		Intent intent = getIntent();		
	    try
	    {
	    	
	    	language = intent.getExtras().getString("language");
	    	id_audioSD = intent.getExtras().getString("id_audioSD");
	    	point = intent.getExtras().getString("point");
	    }
	    catch(Exception e)
	    {
	    	System.out.print(e);
	    }
		
		textLanguage.setText(language);
		
		// Mediaplayer
		mp = new MediaPlayer();
		songManager = new SongsManager(language);
		utils = new Utilities();
		
		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important				
		
		// Getting all songs list
		songsList = songManager.getPlayList(language);
		
		// By default play first song
		if (id_audioSD != null && id_audioSD != "")
		{
			playSong(Integer.parseInt(id_audioSD));
			
		}
//		playSong(0);
				
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check for already playing
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						// Changing button image to pause button
						btnPlay.setImageResource(R.drawable.btn_pause);
					}
				}
				
			}
		});
		
		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if(currentPosition + seekForwardTime <= mp.getDuration()){
					// forward song
					mp.seekTo(currentPosition + seekForwardTime);
				}else{
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});
		
		/**
		 * Backward button click event
		 * Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
					// forward song
					mp.seekTo(currentPosition - seekBackwardTime);
				}else{
					// backward to starting position
					mp.seekTo(0);
				}
				
			}
		});
		
		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				if(currentSongIndex < (songsList.size() - 1)){
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				}else{
					// play first song
					playSong(0);
					currentSongIndex = 0;
				}
				
			}
		});
		
		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}
				
			}
		});
		
		/**
		 * Button Click event for Repeat button
		 * Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isRepeat){
					isRepeat = false;
					Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}else{
					// make repeat to true
					isRepeat = true;
					Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isShuffle = false;
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}	
			}
		});
		
		/**
		 * Button Click event for Shuffle button
		 * Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isShuffle){
					isShuffle = false;
					Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}else{
					// make repeat to true
					isShuffle= true;
					Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isRepeat = false;
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}	
			}
		});
		
		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
//				startActivityForResult(i, 100);	
				i.putExtra("language", textLanguage.getText());
				startActivity(i);
				finish();
			}
		});
		
	}
		
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public void  playSong(int songIndex){
		// Play song
		try {
        	mp.reset();
			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			mp.prepare();
			mp.start();
			// Displaying Song title
			String songTitle = songsList.get(songIndex).get("songTitle");
        	songTitleLabel.setText(songTitle);
			
        	// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);
			
			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			
			// Updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   long totalDuration = mp.getDuration();
			   long currentDuration = mp.getCurrentPosition();
			  
			   // Displaying Total Duration time
			   songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   songProgressBar.setProgress(progress);
			   
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
		};
		
	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		mp.seekTo(currentPosition);
		
		// update timer progress again
		updateProgressBar();
    }

	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		// check for repeat is ON or OFF
		if(isRepeat){
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if(isShuffle){
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else{
			// no repeat or shuffle ON - play next song
			if(currentSongIndex < (songsList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}
		
	 public void download(final View view){
		 Intent intent = new Intent(this, Download.class);		    
		    startActivity(intent);
	 }
	 public void openMap(final View view)
	 {
// http://asnsblues.blogspot.it/2011/11/google-maps-query-string-parameters.html
		 
		 //------------------------------------------
//		 String url = "https://www.google.com/maps/ms?msid=206212653941099478857.0004e6e21f640cfa6a5fa&msa=0&ll=43.328999,11.32183&spn=0.002191,0.00408";
//		 url = "https://www.google.com/maps?f=d&amp;source=embed&amp;saddr=43.332139,11.314296&amp;daddr=Viale+Vittorio+Emanuele+II&amp;hl=it&amp;geocode=FSsylQIdeKSsAA%3BFfYllQId68CsAA&amp;aq=&amp;sll=43.330578,11.317935&amp;sspn=0.006197,0.016319&amp;t=h&amp;dirflg=w&amp;mra=ls&amp;ie=UTF8&amp;ll=43.330577,11.317938&amp;spn=0.003125,0.007283";
		 String urlPoint = "https://www.google.com/maps/ms?msid=206212653941099478857.0004e6e21f640cfa6a5fa&msa=0&t=h&z=19&ll="+point;
		 //String urlPoint = "https://www.google.com/maps/ms?msid=206212653941099478857.0004e6e21f640cfa6a5fa&msa=0&ll="+point+"&dg=feature&z=19";
		 //String urlPoint = "https://www.google.com/maps?msid=206212653941099478857.0004e6e21f640cfa6a5fa&msa=0&ll="+point+"";
		 Intent i = new Intent(this, WebviewActivity.class);
		 i.putExtra("urlPoint", urlPoint);
		 startActivity(i);
		 //---------------------------------
//		 Intent intent2 = new Intent(this, GeoActivity.class);		    
//		    startActivity(intent2);
//		 //--------------------------------------
//		 
//		 String saddr = "20.344,34.34";
//		 String daddr = "20.5666,45.345";
//		 
//		 String antiporto = "43.328945,11.321579";
//		 //https://www.google.com/maps?q=43.328945,11.321579&hl=it&ll=43.328951,11.321459&spn=0.002241,0.004128&sll=43.328763,11.321594&sspn=0.00112,0.002064&t=h&z=19
//		 //String gotTo= "https://www.google.com/maps?q=43.328945,11.321579&hl=it&ll="+antiporto+"&spn=0.002241,0.004128&sll=43.328763,11.321594&sspn=0.00112,0.002064&t=h&z=19";
//		 String gotTo= "https://www.google.com/maps?q=43.328945,11.321579&hl=it&ll="+antiporto+"&spn=0.002241,0.004128&sll=43.328763,11.321594&sspn=0.00112,0.002064&t=h&z=20";
////		 gotTo= "https://www.google.com/maps/ms?msid=206212653941099478857.0004e6e21f640cfa6a5fa&msa=0&iwloc=0004e6e23273981581522";
////		 gotTo= "https://www.google.com/maps/ms?msa=0&msid=206212653941099478857.0004e6e21f640cfa6a5fa&ie=UTF8&t=h&vpsrc=6&ll=43.329124,11.322522&spn=0.002191,0.00408&z=19&iwloc=0004e6e23273981581522&dg=feature";
//		 gotTo= "https://www.google.com/maps/ms?msid=206212653941099478857.0004e6e21f640cfa6a5fa&msa=0&ie=UTF8&t=h&z=18&vpsrc=0&iwloc=0004e6e23273981581522&dg=feature";
////		 gotTo= "http://mapsengine.google.com/map/embed?mid=zM51CwAknWDs.knM0ggWDvCVc";
//		 gotTo= "https://www.google.com/maps/ms?msa=0&msid=206212653941099478857.0004e6e21f640cfa6a5fa&ie=UTF8&ll=43.329121,11.322522&spn=0.002191,0.00408&t=h&z=19&vpsrc=6&iwloc=0004e6e23273981581522&dg=feature";
//		 //Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
//		 Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
//				 Uri.parse(gotTo));
//		 intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//		 
//		 startActivity(intent);
		 
	 }
	@Override
	 public void onDestroy(){
	 super.onDestroy();
	    mp.release();
	    // http://stackoverflow.com/questions/13854196/application-force-closed-when-exited-android
	    mHandler.removeCallbacks(mUpdateTimeTask);
	 }
	
}
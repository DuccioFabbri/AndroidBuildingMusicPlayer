package it.duccius.musicplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.os.Environment;

public class SongsManager {
	// SDCard Path
	//final String MEDIA_PATH = new String("/sdcard/");
	//final String MEDIA_PATH = new String("/storage/sdcard0/duccius/");    
	String MEDIA_PATH;
	
	
	private ArrayList<Audio> songsList = new ArrayList<Audio>();
	
	// Constructor
	public SongsManager(String lang){
		String appDir = ApplicationData.getAppName()+"/"+lang;
		File path = Environment.getExternalStoragePublicDirectory(appDir);
		MEDIA_PATH = path.toString();
	}
	
	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<Audio> getPlayList(String lang){
//		File home = new File(MEDIA_PATH + lang + "/");
		File home = new File(MEDIA_PATH);
		if (!home.exists())
			home.mkdirs(); 

		// Se la dir non esiste occorre crearl
			try{
			if (home.listFiles(new FileExtensionFilter()).length > 0) {
				int i=0 ;
				for (File file : home.listFiles(new FileExtensionFilter())) {
					Audio song = new Audio();
					song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
					song.put("songPath", file.getPath());
					song.setSongPositionInSd(i);
					i++;
					// Adding each song to SongList
					songsList.add(song);
				}
			}
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		// return songs list array
		return songsList;
	}
	public ArrayList<AudioGuide> getSdAudioList(String lang){
		ArrayList<AudioGuide> songsList = new ArrayList<AudioGuide>();
		File home = new File(MEDIA_PATH);
		if (!home.exists())
			home.mkdirs(); 

		// Se la dir non esiste occorre crearl
			try{
			if (home.listFiles(new FileExtensionFilter()).length > 0) {
				int i=0 ;
				for (File file : home.listFiles(new FileExtensionFilter())) {
					AudioGuide song = new AudioGuide();
					song.setTitle(file.getName().substring(0, (file.getName().length() - 4)));
					song.setPath(file.getPath());
					song.setSdPosition(i);
					i++;
					// Adding each song to SongList
					songsList.add(song);
				}
			}
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		// return songs list array
		return songsList;
	}
	
	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}

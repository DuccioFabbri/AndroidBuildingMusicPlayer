package it.duccius.download;
 
import it.duccius.musicplayer.ApplicationData;
import it.duccius.musicplayer.Audio;
import it.duccius.musicplayer.R;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import it.duccius.musicplayer.SongsManager;
import it.duccius.musicplayer.AudioGuide;

import it.duccius.adapters.AudioGuideAdapter;
 
public class ListViewMultipleSelectionActivity extends Activity implements
        OnClickListener {
    Button _button;
    ListView _listView;
    private Spinner _spnLanguage;
    private String _language = "ITA";
    
    ArrayAdapter<String> _adapter;
    ArrayList<AudioGuide> _guides =  new ArrayList<AudioGuide>();
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupLangSpinner();
        setupListView();
        setupButton();
        
    }
    public void loadGuideList()
	{		
		
		for (int i = 1; i < 5; i++) {
			AudioGuide song = new AudioGuide();
			song.setTitle("siena"+i );
			song.setPath("http://2.227.2.94:8080/audio/ITA/siena"+i+".mp3");
			song.setLang("ITA");
			song.setGeoPoint(ApplicationData.getPoints().get(i-1));
			// Adding each song to SongList
			_guides.add(song);
		}
		for (int i = 1; i < 2; i++) {
			AudioGuide song = new AudioGuide();
			song.setTitle("siena"+i );
			song.setPath("http://2.227.2.94:8080/audio/ENG/siena"+i+".mp3");
			song.setLang("ENG");
			song.setGeoPoint(ApplicationData.getPoints().get(i-1));
			// Adding each song to SongList
			_guides.add(song);
		}
		
	}
	private void setupButton() {
		_button = (Button) findViewById(R.id.testbutton);
        _button.setOnClickListener(this);
	}

	private void setupListView() {
		_listView = (ListView) findViewById(R.id.list);
		ArrayList<AudioGuide> sdAudios = getSdAudios();		    						
		
		loadGuideList();
		ArrayList<AudioGuide> audioDisponibiliServer= guideList(_language);
		ArrayList<String> sdAudiosStrings  = getSdAudioStrings(audioDisponibiliServer);
		
		_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, sdAudiosStrings);                	
        
        _listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        _listView.setAdapter(_adapter);
	}

	private ArrayList<String> getSdAudioStrings(ArrayList<AudioGuide> sdAudios) {
		ArrayList<String> sdAudiosStrings = new ArrayList<String>();
		for (AudioGuide audio: sdAudios)
		{
			sdAudiosStrings.add(audio.getTitle());
		}
		return sdAudiosStrings;
	}

	private ArrayList<AudioGuide> getSdAudios() {
		SongsManager plm = new SongsManager(_language);
		ArrayList<AudioGuide> sdAudio = plm.getSdAudioList(_language);
		return sdAudio;
	}
	public ArrayList<AudioGuide> guideList(String lang)
	{
		ArrayList<AudioGuide> langGuides= new ArrayList<AudioGuide>();

		for (AudioGuide audio: _guides) {
		    if (audio.getLang().contains(lang))
		    	langGuides.add(audio);
		}
		
		return langGuides;
	}
	
	private void setupLangSpinner() {
		_spnLanguage = (Spinner)findViewById(R.id.spnLanguage);
		ArrayAdapter<String> adapterLang = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_spinner_item,
        		ApplicationData.getLanguages()
        		);
        
		_spnLanguage.setAdapter(adapterLang);
		_spnLanguage.setSelection(adapterLang.getPosition(_language));
	}
 
    public void onClick(View v) {
        SparseBooleanArray checked = _listView.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
                selectedItems.add(_adapter.getItem(position).toString());
        }
 
        String[] outputStrArr = new String[selectedItems.size()];
 
        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr[i] = selectedItems.get(i);
        }
 
        Intent intent = new Intent(getApplicationContext(),
                ResultActivity.class);
 
        // Create a bundle object
        Bundle b = new Bundle();
        b.putStringArray("selectedItems", outputStrArr);
 
        // Add the bundle to the intent.
        intent.putExtras(b);
 
        // start the ResultActivity
        startActivity(intent);
    }
}
package it.duccius.download;

//import it.duccius.musicplayer.R;

import it.duccius.musicplayer.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
 
public class MainActivity extends Activity {
 
    ProgressDialog progressDialog;
    CustomListViewAdapter listViewAdapter;
    ListView listView;
 
    public static final String URL =
        "http://theopentutorials.com/wp-content/themes/theopentutorials/images/open_tutorials_logo_v4.png";
    public static final String URL1 =
        "http://theopentutorials.com/wp-content/themes/theopentutorials/images/logo.jpg";
    public static final String URL2 =
        "http://theopentutorials.com/wp-content/themes/theopentutorials/images/open_tutorials_logo_v4.png";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            listView = (ListView) findViewById(R.id.imageList);
 
        /*Creating and executing background task*/
        GetXMLTask task = new GetXMLTask(this);
        task.execute(new String[] { URL, URL1, URL2 });
 
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("In progress...");
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setIcon(R.drawable.arrow_stop_down);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    private class GetXMLTask extends AsyncTask<String, Integer, List<RowItem>> {
        private Activity context;
        List<RowItem> rowItems;
        int noOfURLs;
        public GetXMLTask(Activity context) {
            this.context = context;
        }
 
        @Override
        protected List<RowItem> doInBackground(String... urls) {
            noOfURLs = urls.length;
            rowItems = new ArrayList<RowItem>();
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
                rowItems.add(new RowItem(map));
            }
            return rowItems;
        }
 
        private Bitmap downloadImage(String urlString) {
 
            int count = 0;
            Bitmap bitmap = null;
 
            URL url;
            InputStream inputStream = null;
            BufferedOutputStream outputStream = null;
 
            try {
                url = new URL(urlString);
                URLConnection connection = url.openConnection();
                int lenghtOfFile = connection.getContentLength();
 
                inputStream = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
 
                outputStream = new BufferedOutputStream(dataStream);
 
                byte data[] = new byte[512];
                long total = 0;
 
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    /*publishing progress update on UI thread.
                    Invokes onProgressUpdate()*/
                    publishProgress((int)((total*100)/lenghtOfFile));
 
                    // writing data to byte array stream
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
 
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 1;
 
                byte[] bytes = dataStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,bmOptions);
 
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                FileUtils.close(inputStream);
                FileUtils.close(outputStream);
            }
            return bitmap;
        }
 
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
            if(rowItems != null) {
                progressDialog.setMessage("Loading " + (rowItems.size()+1) + "/" + noOfURLs);
            }
       }
 
       @Override
       protected void onPostExecute(List<RowItem> rowItems) {
        listViewAdapter = new CustomListViewAdapter(context, rowItems);
        listView.setAdapter(listViewAdapter);
        progressDialog.dismiss();
       }    
 
    }
}

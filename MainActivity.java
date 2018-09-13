package sachet.example.com.top10downloader;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView xmlListView;
    private String feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int limit = 10;
    private String feedUrl = "INVALIDATED";
    public static final String strlimit = "data_limit";
    public static final String currentURL = "current_URL";
    public static final String reference = "reference";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null){
            feedURL = savedInstanceState.getString(currentURL);
            limit = savedInstanceState.getInt(strlimit);
        }
        Log.d(TAG, "onCreate: ");
         xmlListView = findViewById(R.id.xmlListview);
        downloadURL(String.format(feedURL,limit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu,menu);
        if(limit == 10){
            menu.findItem(R.id.mnu10).setChecked(true);
        }else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.mnuFree:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnuRefresh:
                feedUrl = "INVALIDATED";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked()){
                    item.setChecked(true);
                    limit = 35 - limit;
                    Log.d(TAG, "onOptionsItemSelected: Limit changed to"+limit);
                }else{
                    Log.d(TAG, "onOptionsItemSelected: Limit value unchanged"+limit);
                }
                break;
                default:
                    return super.onOptionsItemSelected(item);
        }
            downloadURL(String.format(feedURL, limit));
        return true;
    }

    private void downloadURL(String feedURL){
        if(!feedUrl.equals(feedURL)) {
            DownloadData downloadData = new DownloadData();
            Log.d(TAG, "downloadURL: starting AsyncTask");
            downloadData.execute(feedURL);
            feedUrl = feedURL;
            Log.d(TAG, "downloadURL: AsyncTask Finished");
        }
    }

    private class DownloadData extends AsyncTask<String,Void,String>{
        private static final String TAG = "DownloadData";
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: onpost parameter is "+s);
            ParseApplication parse = new ParseApplication();
            parse.parse(s);
//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(MainActivity.this, R.layout.list_item, parse.getFeed());
            FeedAdapter arrayAdapter = new FeedAdapter(MainActivity.this,R.layout.list_record,parse.getFeed());
            xmlListView.setAdapter(arrayAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
//            Log.d(TAG, "doInBackground: starts with "+strings[0]);
            String rssFeed = downLoadXML(strings[0]);
            if(rssFeed == null) Log.e(TAG,"doInBackGround: error download");
            return rssFeed;
        }

        private String downLoadXML(String urlPath){
            StringBuilder sb = new StringBuilder();
            try{
//                SocketAddress skt = new InetSocketAddress("172.16.30.20",8080);
//                Proxy proxy = new Proxy(Proxy.Type.HTTP,skt);
                URL url = new URL(urlPath);
                Log.d(TAG, "downLoadXML: URL "+urlPath);
                HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                int responce = connect.getResponseCode();
//                Log.d(TAG,"the url responce is "+responce);
                BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                int charRead;
                char[]inputBuffer = new char[5000];
                while(true){
                    charRead = br.read(inputBuffer);
                    if(charRead<0) break;
                    if(charRead>0)sb.append(inputBuffer,0,charRead);
                }
                br.close();
                return sb.toString();
            }catch(MalformedURLException e){
                e.printStackTrace();
//                Log.e(TAG, "downLoadXML: wrong url");
                return null;
            }catch(IOException e){
                e.printStackTrace();
//                Log.e(TAG, "downLoadXML: something went wrong");
                return null;
            }catch(SecurityException e){
                e.printStackTrace();
//                Log.e(TAG, "downLoadXML: SecurityException Permission denied"+e.getMessage());
                return null;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(strlimit,this.limit);
        outState.putString(currentURL,this.feedURL);
        outState.putString(reference,this.feedUrl);
        super.onSaveInstanceState(outState);
    }
}

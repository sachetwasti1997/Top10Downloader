package sachet.example.com.top10downloader;

import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ParseApplication{
    private static final String TAG = "ParseApplication";

    private List<FeedEntry> feed;

    public ParseApplication(){
        this.feed = new ArrayList<>();
    }

    public List<FeedEntry> getFeed(){
        return feed;
    }
    public boolean parse(String xmlData){
        boolean status = true;
        FeedEntry currentReading = null;
        boolean inEntry = false;
        String textValue = "";
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while(eventType!=XmlPullParser.END_DOCUMENT){
                String tagName = xpp.getName();

                switch(eventType){
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for "+tagName);
                        if("entry".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentReading = new FeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;


                    case XmlPullParser.END_TAG:

                        if(inEntry){
//                            Log.d(TAG, "parse: Ending tag for "+tagName);
                            if("entry".equalsIgnoreCase(tagName)){
                                feed.add(currentReading);
                            }else if("name".equalsIgnoreCase(tagName)){
                                currentReading.setName(textValue);
                            }else if("artist".equalsIgnoreCase(tagName)){
                                currentReading.setArtist(textValue);
                            }else if("releaseDate".equalsIgnoreCase(tagName)){
                                currentReading.setReleaseDate(textValue);
                            }else if("summary".equalsIgnoreCase(tagName)){
                                currentReading.setSummary(textValue);
                            }else if("image".equalsIgnoreCase(tagName)){
                                currentReading.setImageURL(textValue);
                            }
                        }
                        break;
                        default:
                }
                eventType = xpp.next();
            }
//            for(FeedEntry app:feed){
//                Log.d(TAG,"*****************************");
//                Log.d(TAG,  app.toString());
//            }
        }catch(Exception e){
//            System.out.println("There has been some exception "+ e.getMessage());
            e.printStackTrace();
            status = false;
        }
        return status;
    }
}

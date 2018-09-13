package sachet.example.com.top10downloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    public static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> application;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> application) {
        super(context, resource);
        this.layoutInflater = LayoutInflater.from(context);
        this.layoutResource = resource;
        this.application = application;
    }

    class ViewHolder{
        TextView tvName;
        TextView tvArtist;
        TextView tvSummary;
        public ViewHolder(View v){
            tvName = v.findViewById(R.id.tvName);
            tvArtist = v.findViewById(R.id.tvArtist);
            tvSummary = v.findViewById(R.id.tvSummary);
        }
    }

    @Override
    public int getCount() {
        return application.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newView = convertView;
        ViewHolder holder = null;
        if(newView == null){
            newView = layoutInflater.inflate(layoutResource,parent,false);
            holder = new ViewHolder(newView);
            newView.setTag(holder);
        }else{
            holder = (ViewHolder)newView.getTag();
        }
        holder.tvName.setText(application.get(position).getName());
        holder.tvArtist.setText(application.get(position).getArtist());
        holder.tvSummary.setText(application.get(position).getSummary());
        return newView;
    }
}

package com.bianco.raincoat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Grayson on 1/23/17.
 */

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    public WeatherArrayAdapter(Context context, List<Weather> forecast) {
        super(context, -1, forecast);
    }

    // creates the custom views for the ListView's items so that we can still have reference to
    // them even if they scroll off screen so that we have less lag when re-referencing
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the Weather object for this position
        Weather day = getItem(position);
        ViewHolder viewHolder;

        // check for reusable ViewHolder from a ListView item that scrolled offscreen
        // otherwise, create a new ViewHolder
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // inflate the ListItem's layout:
            LayoutInflater inflater = LayoutInflater.from(getContext());
            // 1st arg: what to inflate; 2nd: parent to attach to; 3rd attach auto or not
            convertView = inflater.inflate(R.layout.list_view, parent, false);
            viewHolder.conditionImageView = (ImageView) convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = (TextView) convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = (TextView) convertView.findViewById(R.id.lowTextView);
            viewHolder.highTextView = (TextView) convertView.findViewById(R.id.highTextView);
            viewHolder.humidityTextView = (TextView) convertView.findViewById(R.id.humidityTextView);
            // sets new ViewHolder object as the ListView item's tag to store the ViewHolder with
            // the ListView item for future use
            convertView.setTag(viewHolder);
        } else {
            // reuse the existing ViewHolder associated with the ListView
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // if the weather condition icon is already downloaded, use that
        // otherwise, download it in a separate thread to not clog the GUI thread and slow it down
        if (bitmaps.containsKey(day.iconURL)) {
            viewHolder.conditionImageView.setImageBitmap(bitmaps.get(day.iconURL));
        } else {
            // download it
            new LoadImageTask(viewHolder.conditionImageView).execute(day.iconURL);
        }

        // get other data form Weather object and place into views
        Context context = getContext(); // for loading String resources
        // sets strings for ListView's TextViews
        viewHolder.dayTextView.setText(context.getString(R.string.day_description, day
                .dayOfWeek, day.description));
        viewHolder.lowTextView.setText(context.getString(R.string.low_temp, day.minTemp));
        viewHolder.highTextView.setText(context.getString(R.string.high_temp, day.maxTemp));
        viewHolder.humidityTextView.setText(context.getString(R.string.humidity, day.humidity));

        return convertView;
    }

    // used to associate a ListView's data with an Object
    private static class ViewHolder {
        ImageView conditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView highTextView;
        TextView humidityTextView;
    }

    // AsyncTask to load weather condition icons in a separate thread
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        // load image; param[0] is url representing image
        @Override
        protected Bitmap doInBackground(String...params) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(params[0]);

                // open URL
                connection = (HttpURLConnection) url.openConnection();

                try (InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(params[0], bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return bitmap;
        }

        // set image in the ListView
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }


}

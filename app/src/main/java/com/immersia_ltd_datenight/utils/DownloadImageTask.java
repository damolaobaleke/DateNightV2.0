package com.immersia_ltd_datenight.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DownloadImageTask extends AsyncTask<String, Void, ArrayList<Bitmap>> {
    URL url;
    HttpURLConnection urlConnection;

    @Override
    protected ArrayList<Bitmap> doInBackground(String... images) {
        try {
            for (String image : images) {
                //convert all strings to valid uniform resource locator , take image strings in array
                url = new URL(image);
                Log.i("Images", image);
            }
            ArrayList<Bitmap> imageBitmaps = new ArrayList<>();

            //create http connection and Attempting loading of url
            urlConnection = (HttpURLConnection) url.openConnection();
            //connect to the http connection
            urlConnection.connect();
            //downloads whole input stream, holds data
            InputStream in = urlConnection.getInputStream();

            //decode what is streamed into the input as a bitmap type
            Bitmap bitmap = BitmapFactory.decodeStream(in);

            //add bitmaps to the array list
            imageBitmaps.add((bitmap));

            return imageBitmaps;

        } catch (MalformedURLException e) {
            Log.e(TAG, "URl is malformed" + e);
        } catch (IOException e) {
            Log.e(TAG, "Couldnt get image from server\n" + e);
            //remember to catch UnknownhostException
        } finally {
            //disconnect the http connection
            urlConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
        super.onPostExecute(bitmaps);
        //If URL strings explicitly declared in url set bitmap here
        bitmaps.get(0);
    }


    private void getImageBitmap() {
        ArrayList<Bitmap> dateNightLogobm, dateNightTextLogobm;

        try {
            DownloadImageTask task = new DownloadImageTask();
            //execute() method calls the do in background method
            dateNightLogobm = task.execute("https://res.cloudinary.com/dayvbcxai/image/upload/v1597180001/DateNight/DateNight-Logo-Icon-Transparetn_ms1kfn.png").get();

            DownloadImageTask task2 = new DownloadImageTask();
            dateNightTextLogobm = task2.execute("https://res.cloudinary.com/dayvbcxai/image/upload/v1597179875/DateNight/datenight_text_logo_white_wokska.png").get();


        } catch (Exception e) {
            Log.i("Image 2", "Error \n" + e);
        }
    }

}

/**
 * ==========================================
 * ; Title:  Datenight
 * ; Description: Social Media
 * ; Author: Oyindamola Obaleke
 * ; Date:   4 Jul 2020
 * ;=======================================
 */
package com.datenight_immersia_ltd;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.datenight_immersia_ltd.views.landing_screen.BoardingScreen;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Getting Image";
    private static final long SPLASH_TIME_OUT = 4000;
    private ImageView dateNightLogo;
    private ImageView dateNightTextLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateNightLogo = findViewById(R.id.dateNightLogo);
        dateNightTextLogo = findViewById(R.id.datenightTextLogo);


        //getImageBitmap();
        dateNightLogo.animate().scaleX(1.1f).scaleY(1.1f).setStartDelay(1000);
        dateNightLogo.animate().scaleX(1.4f).scaleY(1.4f);
        dateNightLogo.animate().start();

        dateNightTextLogo.animate().alpha(1f).setStartDelay(2000).start();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.datenightlogo);
        dateNightLogo.setAnimation(animation);


        Runnable runnable = () -> {
            Intent intent = new Intent(this, BoardingScreen.class);
            startActivity(intent);

        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, SPLASH_TIME_OUT);

    }

    /*Dont Use async task (background thread for splash screen images. Wrote this code here,but can be used in other classes )*/
//    public static class DownloadImageTask extends AsyncTask<String, Void, ArrayList<Bitmap>> {
//        URL url;
//
//        @Override
//        protected ArrayList<Bitmap> doInBackground(String... images) {
//            try {
//                for (String image : images) {
//                    //convert all strings to valid uniform resource locator , take image strings in array
//                    url = new URL(image);
//                    Log.i("Images", image);
//                }
//                ArrayList<Bitmap> imageBitmaps = new ArrayList<>();
//
//                //create http connection and Attempting loading of url
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                //connect to the http connection
//                urlConnection.connect();
//                //downloads whole input stream, holds data
//                InputStream in = urlConnection.getInputStream();
//
//                Bitmap bitmap = BitmapFactory.decodeStream(in);
//
//                imageBitmaps.add((bitmap));
//
//                return imageBitmaps;
//
//            } catch (MalformedURLException e) {
//                Log.e(TAG, "URl is malformed" + e);
//            } catch (IOException e) {
//                Log.e(TAG, "Couldnt get image from server\n" + e);
//            } finally {
//                //disconnect the http connection
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
//            super.onPostExecute(bitmaps);
//            //If URL strings explicitly declared in url set bitmap here
//        }
//    }

//    private void getImageBitmap() {
//        ArrayList<Bitmap> dateNightLogobm,dateNightTextLogobm;
//
//        try {
//            DownloadImageTask task = new DownloadImageTask();
//            dateNightLogobm = task.execute("https://res.cloudinary.com/dayvbcxai/image/upload/v1597180001/DateNight/DateNight-Logo-Icon-Transparetn_ms1kfn.png").get();
//
//            DownloadImageTask task2 = new DownloadImageTask();
//            dateNightTextLogobm = task2.execute("https://res.cloudinary.com/dayvbcxai/image/upload/v1597179875/DateNight/datenight_text_logo_white_wokska.png").get();
//
//            dateNightLogo.setVisibility(View.VISIBLE);
//            dateNightLogo.setImageBitmap(dateNightLogobm.get(0));
//
//            dateNightTextLogo.setVisibility(View.VISIBLE);
//            dateNightTextLogo.setImageBitmap(dateNightTextLogobm.get(0));
//
//
//        } catch (Exception e) {
//            Log.i("Image 2", "Error \n" + e);
//        }
//    }

}
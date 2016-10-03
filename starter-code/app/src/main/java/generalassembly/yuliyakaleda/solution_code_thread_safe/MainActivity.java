package generalassembly.yuliyakaleda.solution_code_thread_safe;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
  private static final String TAG = MainActivity.class.getName();
  private static final int PICK_IMAGE_REQUEST = 1;
  int mImageWidth = 800;
  int mImageHeight = 800;
  private ImageView mImageView;
  private Button mChooseButton;
  private ProgressBar mProgressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mChooseButton = (Button) findViewById(R.id.choose_button);
    mImageView = (ImageView) findViewById(R.id.image);
    mProgressBar = (ProgressBar)findViewById(R.id.progress);
    mProgressBar.setMax(100);

    mImageView.setImageResource(R.drawable.placeholder);
    mChooseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectImage();
      }
    });

//    Uri myUri = Uri.parse("https://pbs.twimg.com/profile_images/712842743656103936/KBrMz0DO.jpg");
//    new ImageProcessingAsyncTask().execute(myUri);
//    Log.d("SEVTEST ", "image is loading");

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == MainActivity.RESULT_OK && null != data) {
      Uri selectedImage = data.getData();
      new ImageProcessingAsyncTask().execute(selectedImage);
      //TODO: Create the async task and execute it
    }
  }

  // brings up the photo gallery/other resources to choose a picture
  private void selectImage() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
  }

  //TODO: Fill in the parameter types
  private class ImageProcessingAsyncTask extends AsyncTask<Uri, Integer, Bitmap> {

    //TODO: Fill in the parameter type
    @Override
    protected Bitmap doInBackground(Uri... uris) {
      System.out.println("");
      Bitmap bitmap = null;
      try {
        bitmap = decodeBitmap(mImageWidth, mImageHeight, uris[0]);
        if (bitmap.getHeight() > mImageHeight || bitmap.getWidth() > mImageWidth) {
//          int height = 0;
//          int width = 0;
          int bitHeight = bitmap.getHeight();
          int bitWidth = bitmap.getWidth();
          if (bitHeight > mImageHeight) {
//            int bitWidth = bitmap.getWidth();
//            int bitHeight = bitmap.getHeight();
//            double ratio = ((double) mImageHeight) / bitHeight;
//            width = (int) (bitWidth * ratio);
            bitWidth = (int) (bitmap.getWidth() * (((double) mImageHeight) / bitmap.getHeight()));
            bitHeight = mImageHeight;
          }
          if (bitWidth > mImageWidth) {
            bitHeight = (int) (bitWidth * ((double) mImageWidth) / bitmap.getWidth());
            bitWidth = mImageWidth;
          }

          bitmap = Bitmap.createScaledBitmap(bitmap, bitWidth, bitHeight, true);
        }

//        return invertImageColors(bitmap);
//        return blurImage(bitmap);
//        return stretchImageVertically(bitmap);
        return swapHighLowValues(bitmap);
      } catch (FileNotFoundException e) {
        Log.d(TAG, "Image uri is not received or recognized");
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("");
      }
      return null;
    }

    public Bitmap decodeBitmap(int reqWidth, int reqHeight, Uri uri) throws FileNotFoundException {

      // First decode with inJustDecodeBounds=true to check dimensions
      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
//      options.inDensity = 100;
//      BitmapFactory.decodeResource(res, resId, options);
      BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
//      InputStream in = getContentResolver().openInputStream(
//        uri);
//      BitmapFactory.decodeStream(in, null, options);

      // Calculate inSampleSize
      options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

      // Decode bitmap with inSampleSize set
      options.inJustDecodeBounds = false;
//      return BitmapFactory.decodeResource(res, resId, options);
      return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
    }

    public int calculateInSampleSize(
      BitmapFactory.Options options, int reqWidth, int reqHeight) {
      // Raw height and width of image
      final int height = options.outHeight;
      final int width = options.outWidth;
      int inSampleSize = 1;

      if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) >= reqHeight
          && (halfWidth / inSampleSize) >= reqWidth) {
          inSampleSize *= 2;
        }
      }

      Log.d("SEVTEST: ", "" + inSampleSize);
      return inSampleSize;
    }

    //TODO: Fill in the parameter type
    @Override
    protected void onProgressUpdate(Integer... values) {
      super.onProgressUpdate(values);
      mProgressBar.setProgress(values[0]);
    }

    //TODO: Fill in the parameter type
    @Override
    protected void onPostExecute(Bitmap bitmap) {
      mImageView.setImageBitmap(bitmap);
      mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mProgressBar.setVisibility(View.VISIBLE);
      mProgressBar.setProgress(0);
    }

    private Bitmap invertImageColors(Bitmap bitmap){
      //You must use this mutable Bitmap in order to modify the pixels
      Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(),true);

      //Loop through each pixel, and invert the colors
      for (int i = 0; i < mutableBitmap.getWidth(); i++) {
        for(int j = 0; j < mutableBitmap.getHeight(); j++){
          //TODO: Get the Red, Green, and Blue values for the current pixel, and reverse them
          //TODO: Set the current pixel's color to the new, reversed value
          int pixel = mutableBitmap.getPixel(i, j);
          int red = 255 - Color.red(pixel);
          int blue = 255 - Color.blue(pixel);
          int green = 255 - Color.green(pixel);
          int newColor = Color.argb(255, blue, green, red);
          mutableBitmap.setPixel(i, j, newColor);

        }
        int progressVal = Math.round((long) (100*(i/(1.0*mutableBitmap.getWidth()))));
        publishProgress(progressVal);
        //TODO: Update the progress bar. progressVal is the current progress value out of 100
      }
      return mutableBitmap;
    }

    private Bitmap invertImageColorsWithRandom(Bitmap bitmap){
      //You must use this mutable Bitmap in order to modify the pixels
      Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(),true);

      //Loop through each pixel, and invert the colors
      for (int i = 0; i < mutableBitmap.getWidth(); i++) {
        for(int j = 0; j < mutableBitmap.getHeight(); j++){
          //TODO: Get the Red, Green, and Blue values for the current pixel, and reverse them
          //TODO: Set the current pixel's color to the new, reversed value
          int pixel = mutableBitmap.getPixel(i, j);
          int red = 255 - Color.red(pixel);
          int blue = 255 - Color.blue(pixel);
          int green = 255 - Color.green(pixel);
          int newColor = 0;
          if (Math.random() > .25) {
            newColor = Color.argb(255, blue, green, red);
          } else {
            newColor = Color.argb(255, (int) (Math.random()*255), (int) (Math.random()*255),
                    (int) (Math.random()*255));
          }
          mutableBitmap.setPixel(i, j, newColor);

        }
        int progressVal = Math.round((long) (100*(i/(1.0*mutableBitmap.getWidth()))));
        publishProgress(progressVal);
        //TODO: Update the progress bar. progressVal is the current progress value out of 100
      }
      return mutableBitmap;
    }

    private Bitmap blurImage(Bitmap bitmap){
      //You must use this mutable Bitmap in order to modify the pixels
      Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(),true);

      int firstPixel;
      int firstRed;
      int firstBlue;
      int firstGreen;
      int secondPixel = 0;
      int secondRed = 0;
      int secondBlue = 0;
      int secondGreen = 0;
      int thirdPixel = 0;
      int thirdRed = 0;
      int thirdBlue = 0;
      int thirdGreen = 0;
      int newRed = 0;
      int newBlue = 0;
      int newGreen = 0;
      int newColor = 0;

      //Loop through each pixel, and invert the colors
      for (int i = 0; i < mutableBitmap.getWidth(); i++) {
        for(int j = 0; j < mutableBitmap.getHeight(); j++){
          //TODO: Get the Red, Green, and Blue values for the current pixel, and reverse them
          //TODO: Set the current pixel's color to the new, reversed value
          firstPixel = mutableBitmap.getPixel(i, j);
          firstRed = Color.red(firstPixel);
          firstBlue = Color.blue(firstPixel);
          firstGreen = Color.green(firstPixel);

          if (i < (mutableBitmap.getWidth() - 31)) {
            secondPixel = mutableBitmap.getPixel(i + 30, j);
            secondRed = Color.red(secondPixel);
            secondBlue = Color.blue(secondPixel);
            secondGreen = Color.green(secondPixel);
          }

          if (j < (mutableBitmap.getHeight() - 31)) {
            thirdPixel = mutableBitmap.getPixel(i, j + 30);
            thirdRed = Color.red(thirdPixel);
            thirdBlue = Color.blue(thirdPixel);
            thirdGreen = Color.green(thirdPixel);
          }

          newRed = (firstRed + secondRed + thirdRed) / 3;
          newBlue = (firstBlue + secondBlue + thirdBlue) / 3;
          newGreen = (firstGreen + secondGreen + thirdGreen) / 3;
          newColor = Color.argb(255, newRed, newGreen,newBlue);

          mutableBitmap.setPixel(i, j, newColor);

        }
        int progressVal = Math.round((long) (100*(i/(1.0*mutableBitmap.getWidth()))));
        publishProgress(progressVal);
        //TODO: Update the progress bar. progressVal is the current progress value out of 100
      }
      return mutableBitmap;
    }

    private Bitmap stretchImageVertically(Bitmap bitmap){
      //You must use this mutable Bitmap in order to modify the pixels
      Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(),true);
//      mutableBitmap.setHeight(mutableBitmap.getHeight()*2);
      Bitmap newBitmap = Bitmap.createBitmap(mutableBitmap.getWidth(), mutableBitmap.getHeight()*2,
              Bitmap.Config.ARGB_8888);

      //Loop through each pixel, and invert the colors
      for (int i = 0; i < mutableBitmap.getWidth(); i++) {
        for(int j = 0; j < mutableBitmap.getHeight(); j++){
          //TODO: Get the Red, Green, and Blue values for the current pixel, and reverse them
          //TODO: Set the current pixel's color to the new, reversed value
          int pixel = mutableBitmap.getPixel(i, j);
          int red = Color.red(pixel);
          int blue = Color.blue(pixel);
          int green = Color.green(pixel);
          int newColor = Color.argb(255, red, green, blue);
          newBitmap.setPixel(i, j*2, newColor);
          if (j*2+1 < newBitmap.getHeight()-1) {
            newBitmap.setPixel(i, j*2+1, newColor);
          }

        }
        int progressVal = Math.round((long) (100*(i/(1.0*mutableBitmap.getWidth()))));
        publishProgress(progressVal);
        //TODO: Update the progress bar. progressVal is the current progress value out of 100
      }
      return newBitmap;
    }

    private Bitmap swapHighLowValues(Bitmap bitmap){
      Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(),true);
      bitmap = Bitmap.createBitmap(mutableBitmap.getWidth(), mutableBitmap.getHeight(),
              Bitmap.Config.ARGB_8888);

      final int block = 90;

      for (int i = 0; i < mutableBitmap.getWidth(); i+=block) {
        for(int j = 0; j < mutableBitmap.getHeight(); j+=block){
          ArrayList<Integer> colors  = new ArrayList<>();
          List<Pair<Integer, Integer>> locations = new ArrayList<>();
          Map<Integer, List<Pair<Integer, Integer>>> colorToLocation = new HashMap<>();

          for (int a = 0; a < block; a ++) {
            for (int b = 0; b < block; b++) {
             if (i+a < mutableBitmap.getWidth() -1 && j+b < mutableBitmap.getHeight() - 1) {
               int pixel = mutableBitmap.getPixel(i + a, j + b);
               int red = Color.red(pixel);
               int green = Color.green(pixel);
               int blue = Color.blue(pixel);
               int newColor = Color.argb(255, red, green, blue);
               colors.add(newColor);
               locations.add(new Pair<>(i+a, j+b));
             }
            }
          }
          for (int c = 0; c < colors.size(); c++) {
            List<Pair<Integer, Integer>> tempLocations = new ArrayList<>();
            if (colorToLocation.get(colors.get(c)) != null) {
              tempLocations = colorToLocation.get(colors.get(c));
            }
            tempLocations.add(locations.get(c));
            colorToLocation.put(colors.get(c), tempLocations);
          }

          Collections.sort(colors);

          for (int d = 0; d < colors.size(); d++) {
            Pair<Integer, Integer> paintLocation = colorToLocation.get(colors.get(d)).get(0);
            bitmap.setPixel(paintLocation.first, paintLocation.second, colors.get(colors.size()-1-d));
            colorToLocation.get(colors.get(d)).remove(0);
          }

        }
        int progressVal = Math.round((long) (100*(i/(1.0*mutableBitmap.getWidth()))));
        publishProgress(progressVal);
      }
      return bitmap;
    }


  }
}


package com.airsoft.choickchecker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class AdjustBitmap extends Activity implements OnSeekBarChangeListener {
	Bitmap bm;
	ImageView imgv;
	int proBri = 50;
	int proCon = 50;
	int comProBri = 0;
	int comProCon = 0;
	SeekBar briSeek;
	SeekBar conSeek;
	int value_bri;
	int value_con;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adjust_bitmap);
		Bitmap temp_bit;
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = 16;
		bm = BitmapFactory.decodeFile(MainActivity.oriFile.getPath(), options);
		imgv = (ImageView) findViewById(R.id.imageView1);
		briSeek = (SeekBar) findViewById(R.id.seekBar2);
		briSeek.setOnSeekBarChangeListener(this);
		conSeek = (SeekBar) findViewById(R.id.seekBar1);
		conSeek.setOnSeekBarChangeListener(this);
		bm = bm.copy(Bitmap.Config.ARGB_8888 ,true);
		temp_bit = bm;
		value_bri = MainActivity.briSet;
		value_con = MainActivity.conSet;
		briSeek.setProgress((MainActivity.briSet/4) + 50);
		conSeek.setProgress((MainActivity.conSet/2) + 50);
		temp_bit = SetBrightness(temp_bit, MainActivity.briSet);
		temp_bit = createContrast(temp_bit, MainActivity.conSet);
		show(temp_bit);
		//Toast.makeText(this, "conSeek : " + MainActivity.conSet, 1).show();
	}
	
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
		value_bri = (briSeek.getProgress() - 50) * 4;
		value_con = (conSeek.getProgress() - 50) * 2;
		if(seekBar.getId() == R.id.seekBar1) {
			TextView tv = (TextView) findViewById(R.id.textView1);
			tv.setText("Contrast : " + value_con);
		}
		else {
			TextView tv = (TextView) findViewById(R.id.textView2);
			tv.setText("Brightness : " + value_bri);
		}

	}

    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    	Bitmap temp = bm;
		value_bri = (briSeek.getProgress() - 50) * 4;
		value_con = (conSeek.getProgress() - 50) * 2;
		temp = SetBrightness(temp, value_bri);
		temp = createContrast(temp, value_con);
		
		show(temp);
		//Toast.makeText(this, "progress : " + progress + " value_bri : " + value_bri + " value_con :" + value_con, Toast.LENGTH_SHORT).show();
    }

	private void show(Bitmap bit) {
		imgv.setImageBitmap(bit);
	}
	
	 public  Bitmap SetBrightness(Bitmap src, int value) {
	     // original image size
	     int width = src.getWidth();
	     int height = src.getHeight();
	     // create output bitmap
	     Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
	     // color information
	     int A, R, G, B;
	     int pixel;
	  
	     // scan through all pixels
	     for(int x = 0; x < width; ++x) {
	         for(int y = 0; y < height; ++y) {
	             // get pixel color
	             pixel = src.getPixel(x, y);
	             A = Color.alpha(pixel);
	             R = Color.red(pixel);
	             G = Color.green(pixel);
	             B = Color.blue(pixel);
	  
	             // increase/decrease each channel
	             R += value;
	             if(R > 255) { R = 255; }
	             else if(R < 0) { R = 0; }
	  
	             G += value;
	             if(G > 255) { G = 255; }
	             else if(G < 0) { G = 0; }
	  
	             B += value;
	             if(B > 255) { B = 255; }
	             else if(B < 0) { B = 0; }
	  
	             // apply new pixel color to output bitmap
	             bmOut.setPixel(x, y, Color.argb(A, R, G, B));
	         }
	     }
	  
	     // return final image
	     return bmOut;
	 }
	 
	 public static Bitmap createContrast(Bitmap src, double value) {
			int width = src.getWidth();
			int height = src.getHeight();
			Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
			int A, R, G, B;
			int pixel;
			double contrast = Math.pow((100 + value) / 100, 2);
			for(int x = 0; x < width; ++x) {
		        for(int y = 0; y < height; ++y) {
		            // get pixel color
		            pixel = src.getPixel(x, y);
		            A = Color.alpha(pixel);
		            // apply filter contrast for every channel R, G, B
		            R = Color.red(pixel);
		            R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		            if(R < 0) { R = 0; }
		            else if(R > 255) { R = 255; }
		 
		            G = Color.red(pixel);
		            G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		            if(G < 0) { G = 0; }
		            else if(G > 255) { G = 255; }
		 
		            B = Color.red(pixel);
		            B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		            if(B < 0) { B = 0; }
		            else if(B > 255) { B = 255; }
		 
		            // set new pixel color to output bitmap
		            bmOut.setPixel(x, y, Color.argb(A, R, G, B));
		        }
		    }
			return bmOut;
			
		}
	 
	 public void fin(View v) {
		 MainActivity.briSet = value_bri;
		 MainActivity.conSet = value_con;
		 //Toast.makeText(this, "sdsa : " + MainActivity.conSet, 1).show();
		 finish();
	 }
	 
}

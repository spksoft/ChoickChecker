package com.airsoft.choickchecker;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	//Intent code for call camera
	private static final int CALL_CAMERA_ORI = 100;
	public static int conSet = 0;
	public static int briSet = 0;
	private static final int CALL_CAMERA_STU = 200;
	//Uri file location for Original exam
	public static  Uri oriFile = null;
	//Uri file location for Student exam
	private Uri stuFile = null;
	private int total_score  = -1;
	public static Bitmap oriBitmap;
	public static Bitmap stuBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//Button addOri clicked
	public void addOriClicked(View v)
	{
		if(total_score != -1)
		{
			Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
			File file =  new File(Environment.getExternalStorageDirectory(), "OriExam.jpg");
			oriFile = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, oriFile);
			startActivityForResult(intent, CALL_CAMERA_ORI);
		}
		else
		{
			Toast.makeText(this, "Please add Exam number before", Toast.LENGTH_SHORT).show();
		}
	}
	
	//Button addStu clicked
	public void addStuClicked(View v)
	{
		if(total_score != -1 && oriFile != null)
		{
			Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
			File file =  new File(Environment.getExternalStorageDirectory(), "StuExam.jpg");
			stuFile = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, stuFile);
			startActivityForResult(intent, CALL_CAMERA_STU);
		}
		else
		{
			Toast.makeText(this, "Please add Exam number or Exam Answers before", Toast.LENGTH_SHORT).show();
		}
	}
	
	//For get result from camera intent
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == CALL_CAMERA_ORI && resultCode == RESULT_OK)
		{
			conSet = 0;
			briSet = 0;
			oriResult();
		}
		else if(requestCode == CALL_CAMERA_STU && resultCode == RESULT_OK)
		{
			stuResult();
		}
		else
		{
			Toast.makeText(this, "No input picture", Toast.LENGTH_SHORT).show();
		}
	}
	
	//When add number clicked
	public void addNumberClicked(View v)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Input Exam number :");

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        total_score = Integer.parseInt(input.getText().toString());
		        Toast.makeText(getApplicationContext(), "Exam Numbers Added", Toast.LENGTH_SHORT).show();
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
		
	}
	
	//When ori exam input is OK!
	private void oriResult()
	{
		Toast.makeText(this, "Exam Answers Added", Toast.LENGTH_SHORT).show();
	}
	
	private void stuResult()
	{
		Toast.makeText(this, "Exam Student Answers Added", Toast.LENGTH_SHORT).show();
	}
	
	//Check button clicked
	public void CheckClicked(View v)
	{
		if(total_score != -1 && oriFile != null && stuFile != null)
		{
			
			Check();
		}
		else
		{
			Toast.makeText(this, "Please add Exam number Exam Answers or Student Exam before", Toast.LENGTH_SHORT).show();
		}
	}
	
	//When Student exam input is OK!
	private void Check()
	{
		
		// declare bitmap of original exam and student exam
		ImageView imgView = (ImageView) findViewById(R.id.imageView1);
		//imgView.setVisibility(View.GONE);
		TextView textView = (TextView) findViewById(R.id.textView1);
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = 4;
		oriBitmap = BitmapFactory.decodeFile(oriFile.getPath(), options);
		stuBitmap = BitmapFactory.decodeFile(stuFile.getPath(), options);
		//List contain black pixel of original exam
		ArrayList<Integer[]> blackpoint_list = new ArrayList<Integer[]>();
		int pixelPerBlackpoint;
		int user_pixel_cout = 0;
		
		oriBitmap = oriBitmap.copy(Bitmap.Config.ARGB_8888 ,true);
		stuBitmap = stuBitmap.copy(Bitmap.Config.ARGB_8888 ,true);
		oriBitmap = SetBrightness(oriBitmap, briSet);
		stuBitmap = SetBrightness(stuBitmap, briSet);
		oriBitmap = createContrast(oriBitmap, conSet);
		stuBitmap = createContrast(stuBitmap, conSet);
		for(int i = 0;i<=oriBitmap.getWidth() - 1;i++)
		{
			for(int j = 0;j<=oriBitmap.getHeight() - 1;j++)
			{
				if(shouldBeBlack(oriBitmap.getPixel(i, j)) == true)
				{
					Integer[] blackpoint_ori = new Integer[2];
					blackpoint_ori[0] = i;
					blackpoint_ori[1] = j;
					blackpoint_list.add(blackpoint_ori);
				}
			}
		}

		pixelPerBlackpoint = (int)(blackpoint_list.size() / total_score);
		for(int i = 0;i<blackpoint_list.size();i++)
		{
			int x_ori = blackpoint_list.get(i)[0];
			int y_ori = blackpoint_list.get(i)[1];
			if(shouldBeBlack(stuBitmap.getPixel(x_ori, y_ori)) == true)
			{
				//Log.v("sPk", x_ori + " = " + y_ori);
				user_pixel_cout++;
				stuBitmap.setPixel(x_ori, y_ori, Color.GREEN);
				
			}
			else
			{
				stuBitmap.setPixel(x_ori, y_ori, Color.RED);
			}
		}
		
		int score = 0;
		double dec = 0.0;
		double temp = 0.0;
		score = (int)(user_pixel_cout / pixelPerBlackpoint);
		temp = user_pixel_cout * 1.0 / pixelPerBlackpoint * 1.0;
		
		dec = temp - score;
		
		if(dec >= 0.3)
		{
			score++;
		}
		imgView.setImageBitmap(stuBitmap);
		Toast.makeText(this, "Score : " + score + "\nuser_pixel_cout : " + user_pixel_cout + "\n" + "pixelPerBlackpoint : " + pixelPerBlackpoint + "\nblackpoint_list.size" + blackpoint_list.size(), Toast.LENGTH_SHORT).show();
		textView.setText(Integer.toString(score));
		
		//Toast.makeText(this, "Score : " + score, Toast.LENGTH_LONG).show();
	}
	
	public void adjustImage(View v)
	{
		
		if(total_score != -1 && oriFile != null)
		{
			//Toast.makeText(this, "con" + conSet, 1).show();
			Intent i = new Intent(this, AdjustBitmap.class);
			startActivity(i);
		}
		else
		{
			Toast.makeText(this, "Please add Exam number or Exam Answers before", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	//About Menu Clicked
	public void AboutClicked(MenuItem item)
	{
		Intent i = new Intent(this, About.class);
		startActivity(i);
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
	 
	
	//Tools for binarize bitmap
	private static boolean shouldBeBlack(int pixel) {
        int alpha = Color.alpha(pixel);
        int redValue = Color.red(pixel);
        int blueValue = Color.blue(pixel);
        int greenValue = Color.green(pixel);
        if(alpha == 0x00) //if this pixel is transparent let me use TRASNPARENT_IS_BLACK
            return false;
        // distance from the white extreme
        double distanceFromWhite = Math.sqrt(Math.pow(0xff - redValue, 2) + Math.pow(0xff - blueValue, 2) + Math.pow(0xff - greenValue, 2));
        // distance from the black extreme //this should not be computed and might be as well a function of distanceFromWhite and the whole distance
        double distanceFromBlack = Math.sqrt(Math.pow(0x00 - redValue, 2) + Math.pow(0x00 - blueValue, 2) + Math.pow(0x00 - greenValue, 2));
        // distance between the extremes //this is a constant that should not be computed :p
        double distance = distanceFromBlack + distanceFromWhite;
        // distance between the extremes
        double SPACE_BREAKING_POINT = 13.0/30.0;
        return ((distanceFromWhite/distance)>SPACE_BREAKING_POINT);
    }
	//New code from P'Jet
	private static void floodfill(Bitmap bm, int tragetColor)
	{
		
	}
	
	private static String bolbAlgorithm(Bitmap bm)
	{
		return "TEST";
	
	}
	
}

package com.ban.badminton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;


/**
 * This is the Splash activity which is loaded when the application is invoked
 */
public class Splash extends Activity
{
	// Set the display time, in milliseconds (or extract it out as a configurable parameter)
	private final int SPLASH_DISPLAY_LENGTH = 2500;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_splash);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					//Finish the splash activity so it can't be returned to.
					Splash.this.finish();
					// Create an Intent that will start the main activity.
					Intent mainIntent = new Intent(Splash.this, Main.class);
					
					Splash.this.startActivity(mainIntent);
				}
			}, SPLASH_DISPLAY_LENGTH);
		
	}
}
package com.ban.badminton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.ban.badminton.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class Main extends SlidingActivity {

	WebView mWebView;
	private MenuItem progress;
	private Boolean ClearHistory = false;
	static Boolean active;
	private String currentVersion = "3.0.0";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Keep the screen on while running the app */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		/* Google analytics */
		EasyTracker.getInstance().activityStart(this);

		/* Actionbarsherlock: Settings */
		ActionBar ab = getSupportActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		/* Slidingmenu: settings */
		final SlidingMenu sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT);
		setBehindContentView(R.layout.activity_slidingmenu);
		setSlidingActionBarEnabled(false);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);

		/* Slidingmenu: Menu buttons create */

		TextView site = (TextView) this.findViewById(R.id.BanSite);
		site.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mWebView.loadUrl("http://banbadminton.nl/index.php?device=iphone");
				ClearHistory = true;
				setTitle("Home");
				EasyTracker.getTracker().trackView("BANsite");
				sm.toggle();
			}
		});

		/* Slidingmenu: Facebook button */
		TextView facebook = (TextView) this.findViewById(R.id.BanFacebook);
		facebook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mWebView.loadUrl("http://m.facebook.com/BANAmersfoortNoord");
				ClearHistory = true;
				setTitle("Facebook");
				EasyTracker.getTracker().trackView("BANFacebook");
				sm.toggle();

			}
		});

		/* Slidingmenu: Offline picasa button */
		TextView foto = (TextView) this.findViewById(R.id.BanFoto);
		foto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mWebView.loadUrl("file:///android_asset/foto.html");
				ClearHistory = true;
				setTitle("Foto's");
				EasyTracker.getTracker().trackView("BANFoto");
				sm.toggle();

			}
		});

		/* Slidingmenu: Offline agenda button */
		TextView agenda = (TextView) this.findViewById(R.id.BanAgenda);
		agenda.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mWebView.loadUrl("file:///android_asset/agenda.html");
				ClearHistory = true;
				setTitle("Agenda");
				EasyTracker.getTracker().trackView("BANAgenda");
				sm.toggle();

			}
		});

		/* Slidingmenu: Offline agenda button */
		TextView wedstrijd = (TextView) this.findViewById(R.id.BanWedstrijd);
		wedstrijd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mWebView.loadUrl("http://banbadminton.nl/uitslagen/Competitieweek.php?wmode=transparent");
				ClearHistory = true;
				setTitle("Wedstrijden");
				EasyTracker.getTracker().trackView("BANWedstrijd");
				sm.toggle();

			}
		});

		/* Slidingmenu: Offline about button */
		TextView about = (TextView) this.findViewById(R.id.About);
		about.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mWebView.loadUrl("file:///android_asset/about.html");
				ClearHistory = true;
				setTitle("Over");
				EasyTracker.getTracker().trackView("over");
				sm.toggle();

			}
		});

		/* Dialog: Pop-up a dialog if there is no internet available */
		if (!isNetworkAvailable()) {
			new AlertDialog.Builder(this)
					.setTitle("Geen internet")
					.setMessage(
							"Voor deze app heeft u internet nodig. Dit is op dit moment niet aanwezig. De app zal nu sluiten.")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		} else {
			/* Update checker: if internet is available then check for update */
			checkUpdate check = new checkUpdate();
			check.execute(new String[] { "http://banbadminton.nl/app/android/version.txt" });
		}

		/* Webview: Settings */
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);

		/* Webview: configuration */
		mWebView.setWebViewClient(new WebViewClient() {

			/* Webview: When web button pressed, stay in webview */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("calendar:")) {

					// Remove the string date:
					String parsed = url.substring(9);
					// Split the sting by ,
					String[] components = parsed.split(",");

					// Create begin
					Calendar begin = Calendar.getInstance();

					// begin.set(startYear, startMonth-1 (java calendar start at
					// 0), startDay, startHour, startMin);
					begin.set(Integer.parseInt(components[0]),
							Integer.parseInt(components[1]) - 1,
							Integer.parseInt(components[2]),
							Integer.parseInt(components[3]),
							Integer.parseInt(components[4]));
					long startMillis = begin.getTimeInMillis();

					// Create end
					Calendar end = Calendar.getInstance();

					// end.set(endYear, endMonth-1 (java calendar start at 0),
					// endDay, endHour, endMin);
					end.set(Integer.parseInt(components[5]),
							Integer.parseInt(components[6]) - 1,
							Integer.parseInt(components[7]),
							Integer.parseInt(components[8]),
							Integer.parseInt(components[9]));
					long endMillis = end.getTimeInMillis();

					// Create event name
					String eventName = url.substring(43);;

					// Create calendar item
					calendarevent(startMillis, endMillis, eventName);
				} else {
					view.loadUrl(url);
				}
				return true;
			}

			/*
			 * Webview: When we stop loading a page, it will hide a shuttel
			 * which is rotating
			 */
			@Override
			public void onPageFinished(WebView view, String url) {
				progress.setActionView(null);

				if (ClearHistory) {
					ClearHistory = false;
					mWebView.clearHistory();
				}
				super.onPageFinished(view, url);
			}

			/*
			 * Webview: When we start loading a page, it will display a shuttel
			 * which is rotating
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				progress.setActionView(R.layout.refresh_button);
				super.onPageStarted(view, url, favicon);
			}

			/* Webview: Display custom error when we get a page error */
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				view.loadData(
						"Sorry, maar kan op dit moment deze pagina niet laden.",
						"text/html", "UTF-8");
			}
		});

	}

	/* END ONCREATE!!!!! */

	/* Actionbarsherlock: Create top bar buttons */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_itemlist, menu);

		// Refresh button
		progress = menu.findItem(R.id.progressButton);

		/* Webview: Load url when menu is complete loading */
		mWebView.loadUrl("http://banbadminton.nl/index.php?device=iphone");

		return super.onCreateOptionsMenu(menu);
	}

	/* Actionbarsherlock: Bind action to top bar buttons */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		case R.id.progressButton:
			mWebView.reload();
			return true;
		case R.id.quit:
			finish();
		}

		return true;

	}

	/* Check if there is internet */
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/*
	 * Update checker: This is the dialog that will pop-up when there is an
	 * update
	 */
	public void onUpdate() {
		new AlertDialog.Builder(this)
				.setTitle("Nieuwe versie")
				.setMessage(
						"Er is een nieuwe update beschikbaar. Download hem nu!")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								final String appPackageName = getPackageName(); // getPackageName()
								// from
								// Context
								// or
								// Activity
								// object
								try {
									startActivity(new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("market://details?id="
													+ appPackageName)));
								} catch (android.content.ActivityNotFoundException anfe) {
									startActivity(new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("http://play.google.com/store/apps/details?id="
													+ appPackageName)));
								}
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	/*
	 * Update checker: This connect with the banbadminton site and download the
	 * content
	 */
	private class checkUpdate extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response = s;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("","*version* " + result);
			if(!result.contains("</html>")){
				if (!result.equalsIgnoreCase(currentVersion)) {
					onUpdate();
				}
			}
		}
	}

	/* Create a calendar event */
	public void calendarevent(long startMillis, long endMillis, String eventName) {
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", startMillis);
		intent.putExtra("allDay", false);
		intent.putExtra("endTime", endMillis);
		intent.putExtra("title", eventName);
		startActivity(intent);
	}

	/* Google analystics: Start tracking first page */
	@Override
	public void onStart() {
		super.onStart();
		active = true;
		EasyTracker.getTracker().trackView("Homescreen");
	}

	/* Google analystics: Stop tracking app on quit */
	@Override
	public void onStop() {
		super.onStop();
		active = false;
		EasyTracker.getInstance().activityStop(this);
	}

	/* Google analystics: Stop tracking app on destroy */
	@Override
	public void onDestroy() {
		super.onDestroy();
		active = false;
		EasyTracker.getInstance().activityStop(this) ;
	}

	/* Key action: Go back to prev page or quit app */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (mWebView.canGoBack() == true) {
					mWebView.goBack();
				}
			}

		}
		return super.onKeyDown(keyCode, event);
	}

}

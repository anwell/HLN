/*
 * Copyright (C) 2011 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.HLN;

import java.util.List;

import org.HLN.R;
import org.HLN.F2Activity.MyTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;

/**
 * This is the activity for feature 3 in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 *
 */

public class F3Activity extends DashboardActivity 
{
	ProgressDialog progress = null;
/**
 * onCreate
 *
 * Called when the activity is first created. 
 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
 * 
 * Always followed by onStart().
 *
 * @param savedInstanceState Bundle
 */

	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView (R.layout.activity_f3);
	    setTitleFromActivityLabel (R.id.title_text);
	    this.progress = ProgressDialog.show(this, "Loading", "Please Wait...", true, false); 

	    new MyTask().execute(); 
	}

	public class MyTask extends AsyncTask<Void, Void, Void> {

		public Void doInBackground(Void... unused) {
			loadFeed(ParserType.ANDROID_SAX);
			return null;
			
			}
		public void onPostExecute(Void unused) {
			progress.dismiss();   
		} 
		}
	private void loadFeed(ParserType type){
		try{
			Log.i("AndroidNews", "ParserType="+type.name());
	    	FeedParser parser = FeedParserFactory.getParser(type, "http://feed43.com/hlnresources.xml");
	    	List<Message> list = parser.parse();
	    	Message html = list.get(0);
	    	String string= html.getDescription();
	    	WebView webview = (WebView) findViewById(R.id.webView1);
	    	webview.getSettings().setPluginState(PluginState.ON);
	    	webview.getSettings().setJavaScriptEnabled(true); 
	    	//String summary = "<html><body>You scored <b>192</b> points.</body></html>"; 
	    	webview.loadDataWithBaseURL("", string, "text/html", "utf-8", "");
		} catch (Throwable t){
			Log.e("AndroidNews",t.getMessage(),t);
		}
	}
	    
	} // end class


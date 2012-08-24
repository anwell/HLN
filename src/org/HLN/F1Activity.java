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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.HLN.R.string;
import org.xmlpull.v1.XmlSerializer;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



public class F1Activity extends ListActivity {
	
	private List<Message> messages;
	private ArrayList<Order> items = null;
	private OrderAdapter m_order;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_f1);
        loadFeed(ParserType.ANDROID_SAX);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, ParserType.ANDROID_SAX.ordinal(), 
				ParserType.ANDROID_SAX.ordinal(), R.string.android_sax);
		menu.add(Menu.NONE, ParserType.SAX.ordinal(), ParserType.SAX.ordinal(),
				R.string.sax);
		menu.add(Menu.NONE, ParserType.DOM.ordinal(), ParserType.DOM.ordinal(), 
				R.string.dom);
		menu.add(Menu.NONE, ParserType.XML_PULL.ordinal(), 
				ParserType.XML_PULL.ordinal(), R.string.pull);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		ParserType type = ParserType.values()[item.getItemId()];
		ArrayAdapter<String> adapter =
			(ArrayAdapter<String>) this.getListAdapter();
		if (adapter.getCount() > 0){
			adapter.clear();
		}
		this.loadFeed(type);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent viewMessage = new Intent(Intent.ACTION_VIEW, 
				Uri.parse(messages.get(position).getLink().toExternalForm()));
		this.startActivity(viewMessage);
	}
	
	public void onClickHome (View v)
	{
		final Intent intent = new Intent(this, HomeActivity.class);
	    intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    this.startActivity (intent);
	}

	private void loadFeed(ParserType type){
    	try{
    		//Log.i("AndroidNews", "ParserType="+type.name());
	    	FeedParser parser = FeedParserFactory.getParser(type, "http://gdata.youtube.com/feeds/base/users/HigherLearningTVShow/uploads?alt=rss&v=2&orderby=published&client=ytapi-youtube-profile");
	    	long start = System.currentTimeMillis();
	    	messages = parser.parse();
	    	//long duration = System.currentTimeMillis() - start;
	    	//Log.i("AndroidNews", "Parser duration=" + duration);
	    	//String xml = writeXml();
	    	//Log.i("AndroidNews", xml);
	    	List<String> titles = new ArrayList<String>(messages.size());
	    	List<String> images = new ArrayList<String>(messages.size());
	    	items = new ArrayList<Order>();
	    	for (Message msg : messages){
	    		Order order =  new Order();
	    		order.setOrderName(msg.getTitle());
	    		
	    		//titles.add(msg.getTitle());
	    		String html = msg.getDescription();
	    		Pattern p = Pattern.compile("src=\"(.*)\"");
	    			
	    		 Matcher m = p.matcher(html);
	    		if (m.find()) {
	    			order.setOrderStatus(m.group(1));
	    			}
	    		items.add(order);

	    	}
	    	
	    	this.m_order = 
	    		new OrderAdapter(this, R.layout.row, items);
	    	setListAdapter(m_order);
    	} catch (Throwable t){
    		//Log.e("AndroidNews",t.getMessage(),t);
    	}
    }
    
	private class OrderAdapter extends ArrayAdapter<Order> {

        private ArrayList<Order> items;

        public OrderAdapter(Context context, int textViewResourceId, ArrayList<Order> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row, null);
                }
                Order o = items.get(position);
                if (o != null) {
                        TextView tt = (TextView) v.findViewById(R.id.TextView01);
                        ImageView bt = (ImageView) v.findViewById(R.id.img);
                        if (tt != null) {
                              tt.setText("Name: "+o.getOrderName());                            }
                        if(bt != null){
                        	try {
                        		
                        		Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(o.getOrderStatus()).getContent());
                        		bt.setImageBitmap(bitmap);  
                        	}
                         catch (MalformedURLException e) {   e.printStackTrace(); } catch (IOException e) {   e.printStackTrace(); } 
                        
                }
                
        }
				return v;
}
	
	
		
	private String writeXml(){
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "messages");
			serializer.attribute("", "number", String.valueOf(messages.size()));
			for (Message msg: messages){
				serializer.startTag("", "message");
				serializer.attribute("", "date", msg.getDate());
				serializer.startTag("", "title");
				serializer.text(msg.getTitle());
				serializer.endTag("", "title");
				serializer.startTag("", "url");
				serializer.text(msg.getLink().toExternalForm());
				serializer.endTag("", "url");
				serializer.startTag("", "body");
				serializer.text(msg.getDescription());
				serializer.endTag("", "body");
				serializer.endTag("", "message");
			}
			serializer.endTag("", "messages");
			serializer.endDocument();
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
}}
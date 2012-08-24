package org.HLN;
public abstract class FeedParserFactory {
	static String feedUrlsample = "http://gdata.youtube.com/feeds/base/users/HigherLearningTVShow/uploads?alt=rss&v=2&orderby=published&client=ytapi-youtube-profile";
	
	public static FeedParser getParser(){
		return getParser(ParserType.ANDROID_SAX, feedUrlsample);
	}
	
	public static FeedParser getParser(ParserType type, String feedUrl){
		switch (type){
			case SAX:
				return new SaxFeedParser(feedUrl);
			case DOM:
				return new DomFeedParser(feedUrl);
			case ANDROID_SAX:
				return new AndroidSaxFeedParser(feedUrl);
			case XML_PULL:
				return new XmlPullFeedParser(feedUrl);
			default: return null;
		}
	}
}

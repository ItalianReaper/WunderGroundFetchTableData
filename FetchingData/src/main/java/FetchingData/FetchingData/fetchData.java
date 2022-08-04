package FetchingData.FetchingData;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/*
 * This class is used to retrieve the table of weather data on Wunderground
 * @author: nicola_frugieri
 */
public class fetchData {

	public Elements fetchDataByUrl(String url) throws IOException, MalformedURLException {
		Document doc = Jsoup.connect(url).timeout(300 * 1000)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
	            .referrer("http://www.google.com")
	            .get();
		
    	Elements newsHeadlines = doc.select("table");
    	Elements tabella = null;
    	
    	for (Element headline : newsHeadlines) {
    		if(headline.hasClass("history-table desktop-table")) {
    			tabella = headline.getAllElements();
    		}
    	}
    		return tabella;
	}

}

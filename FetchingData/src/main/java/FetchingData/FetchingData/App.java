package FetchingData.FetchingData;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jsoup.select.Elements;

public class App {
    public static void main( String[] args ) throws IOException, MalformedURLException, SQLException{
    	try {
    		DataDAO.updateDatabase();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
		System.exit(0);
    }
}

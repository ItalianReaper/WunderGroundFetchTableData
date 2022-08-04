package FetchingData.FetchingData;

import java.io.IOException;
import java.net.MalformedURLException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.sql.Types;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/*
* @author: nicola_frugieri
*/
public class DataDAO {
	private static final String TABLE_NAME = "dati_meteo";	

	 /*
	 * This method controls last day record in the database. If it's before yesterday, it updates the db
	 */
	public static synchronized int updateDatabase() throws MalformedURLException, IOException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		String sql = new String();
		
		boolean dbExists = false;
		try {
			connection = DriverManagerConnectionPool.getConnection();
		}catch (SQLSyntaxErrorException e) {
			DriverManagerConnectionPool.createDatabase();
		}
		
		connection = DriverManagerConnectionPool.getConnection();
		ResultSet resultSet = connection.getMetaData().getCatalogs();
		
		while (resultSet.next()) {
		  String databaseName = resultSet.getString(1);
		  if(databaseName.equals("dati_meteorologici")) {
			  dbExists = true;
			  break;
		  }
		}
		
		resultSet.close();
		
		if(dbExists == false) {
			DriverManagerConnectionPool.createDatabase();
		}
		
		String infoMessage = "Aggiornamento database in corso...";
		final JFrame frame = new JFrame(infoMessage);
		frame.setLayout(new BorderLayout());
		JLabel label1 = new JLabel("Test", JLabel.CENTER);
		label1.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	    frame.add(label1, BorderLayout.CENTER);
		label1.setText(infoMessage);
		JLabel day = new JLabel("Day", JLabel.CENTER);
		day.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	    frame.add(day, BorderLayout.SOUTH);
	    day.setText("");
		frame.setUndecorated(true);
		frame.setSize(300, 100);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		try {
			
			System.out.println("Updating database...");
			
			sql = "insert into " + DataDAO.TABLE_NAME + " (day, time, temperature, dew_point,"
					+ "humidity, wind, speed, gust, pressure, precip_rate, precip_accum, uv, solar)"
					+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			
			String url = new String();
	    	
	    	final LocalDate firstDay = DataDAO.retrieveLastDay();
	    	LocalDate today = LocalDate.now();
	    	
	    	if(today.isAfter(firstDay)) {
	    		long dayDifference = firstDay.until(today, ChronoUnit.DAYS);
	    		LocalDate tempDate = firstDay.plusDays(1);
	    		
	    		while(dayDifference > 1) { //With this we retrieve data until the previous day of the execution, to not retrieve incomplete data
	    			dayDifference = tempDate.until(today, ChronoUnit.DAYS);
		    		for(int i = 0; i <= dayDifference - 1; i++) {
		    			url = "https://www.wunderground.com/dashboard/pws/ICIAMP2/table/"+ tempDate + "/"+ tempDate + "/daily";
		    			day.setText(tempDate.toString());
		    	    	
		    			fetchData data = new fetchData();
		    	    	Elements table = null;
		    	    	
		    	    	table = data.fetchDataByUrl(url);
		    	    	
		    	    	Elements trSet = table.select("tr");
	
		    	    	List<Element> arrayListTr = new ArrayList<Element>(trSet);
		    	    	arrayListTr.remove(0); //I remove the first two elements because Wunderground has two tr that I don't need,
		    	    	arrayListTr.remove(0); //i.e. the header of the table and a blank tr
		    	    	
		    			 for(Element tr : arrayListTr) {
		    				 
		    				 Elements tdSet = tr.children().select("td");
		    				 List<Element> arrayListTd = new ArrayList<Element>();
		    				 Iterator<Element> iterator = tdSet.iterator();
		    				 
		    				 while (iterator.hasNext()) {
		    				     Element el = iterator.next();   
		    				     arrayListTd.add(el);
		    				 }
		    				 
							 preparedStatement = connection.prepareStatement(sql);
							 
		    				 System.out.println(Date.valueOf(tempDate));
		    				 System.out.println(arrayListTd.get(0).text());
		    				 System.out.println(arrayListTd.get(1).text());
		    				 System.out.println(arrayListTd.get(2).text());
		    				 System.out.println(arrayListTd.get(3).text());
		    				 System.out.println(arrayListTd.get(4).text());
		    				 System.out.println(arrayListTd.get(5).text());
		    				 System.out.println(arrayListTd.get(6).text());
		    				 System.out.println(arrayListTd.get(7).text());
		    				 System.out.println(arrayListTd.get(8).text());
		    				 System.out.println(arrayListTd.get(9).text());
		    				 System.out.println(arrayListTd.get(10).text());
		    				 System.out.println(arrayListTd.get(11).text());
		    				 System.out.println("\n\n");
							
		    				 
							 preparedStatement.setDate(1, Date.valueOf(tempDate));
							 preparedStatement.setString(2, arrayListTd.get(0).text());
							 preparedStatement.setString(3, arrayListTd.get(1).text());
							 preparedStatement.setString(4, arrayListTd.get(2).text());
							 preparedStatement.setString(5, arrayListTd.get(3).text());
							 preparedStatement.setString(6, arrayListTd.get(4).text());
							 preparedStatement.setString(7, arrayListTd.get(5).text());
							 preparedStatement.setString(8, arrayListTd.get(6).text());
							 preparedStatement.setString(9, arrayListTd.get(7).text());
							 preparedStatement.setString(10, arrayListTd.get(8).text());
							 preparedStatement.setString(11, arrayListTd.get(9).text());
							 
							 if(arrayListTd.get(10).text().equals("")) {
								 preparedStatement.setNull(12, Types.INTEGER);
							 }else {
								 preparedStatement.setInt(12, Integer.parseInt(arrayListTd.get(10).text()));
							 }
							 
							 preparedStatement.setString(13, arrayListTd.get(11).text());
							 preparedStatement.executeUpdate();
		    			 }	
	
		    	    	tempDate = tempDate.plusDays(1);
		    		}
	    		}
	    	}
	    	
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (!connection.isClosed())
					connection.close();
			} finally {
				connection.close();
			}
		}
    	
    	System.out.println("Database is up to date!\n");
		frame.dispose();
		String dialogMessage = "Database updated succesfully!";
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);    
		JOptionPane.showMessageDialog(dialog, dialogMessage);
		return 0;
	}
	
	 /*
	 * This method controls the last day record on the database 
	 */
	public static synchronized LocalDate retrieveLastDay() throws SQLException {
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String sql = new String();
		LocalDate result = null;
		
		try {
			connection = DriverManagerConnectionPool.getConnection();
			sql = "select max(day) day from  " + DataDAO.TABLE_NAME + ";";
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			
			if (rs.next()) {
				if(rs.getDate("day") != null) {
					result = rs.getDate("day").toLocalDate();
				}else
					result = LocalDate.of(2022, 06, 25);
			}else {
				System.out.println("Error");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (!connection.isClosed())
					connection.close();
			} finally {
				connection.close();
			}
		}
		
		System.out.println("Retrieved " + result);
		return result;
	}
}

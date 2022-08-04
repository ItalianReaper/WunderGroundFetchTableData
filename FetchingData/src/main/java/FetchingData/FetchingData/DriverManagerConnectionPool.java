package FetchingData.FetchingData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
/*
 * @author: nicola_frugieri
 */
	public class DriverManagerConnectionPool  {

		private static List<Connection> freeDbConnections;

		static {
			freeDbConnections = new LinkedList<Connection>();
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println("DB driver not found:"+ e.getMessage());
			} 
		}
		
		private static synchronized Connection createDBConnection() throws SQLException {
			Connection newConnection = null;
			String ip = "localhost";
			String port = "3306";
			String db = "dati_meteorologici";
			String username = "root";
			String password = "root";
			
			newConnection = DriverManager.getConnection("jdbc:mysql://"+ ip+":"+ port+"/"+db+"?useSSL=true", username, password);

			newConnection.setAutoCommit(true);
			return newConnection;
		}


		public static synchronized Connection getConnection() throws SQLException {
			Connection connection;
			if (!freeDbConnections.isEmpty()) {
				connection = (Connection) freeDbConnections.get(0);
				freeDbConnections.remove(0);

				try {
					if (connection.isClosed())
						connection = getConnection();
				} catch (SQLException e) {
					connection.close();
					connection = getConnection();
				}
			} else {
				connection = createDBConnection();		
			}

			return connection;
		}

		public static synchronized void releaseConnection(Connection connection) throws SQLException {
			if(connection != null) freeDbConnections.add(connection);
		}
		
		public static synchronized boolean createDatabase() throws SQLException {
			boolean result = false;
			Connection connection = null;
			Statement stmt = null;
			String ip = "localhost";
			String port = "3306";
			String username = "root";
			String password = "root";
			
			connection = DriverManager.getConnection("jdbc:mysql://"+ ip+":"+ port+"/"+"?useSSL=true&allowMultiQueries=true", username, password);
			stmt = connection.createStatement();
			
			String infoMessage = "Il database non esiste, creazione database in corso...";
			final JFrame frame = new JFrame(infoMessage);
			JLabel label1 = new JLabel("Test", SwingConstants.CENTER);
			label1.setText(infoMessage);
			frame.add(label1);
			frame.setUndecorated(true);
			frame.setSize(350, 100);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			
	        String dbName = "dati_meteorologici"; // or get it from command line
	        String sql = "CREATE DATABASE `"+ dbName + "`; USE `" + dbName + "`; CREATE TABLE `dati_meteo` ( `pos` INT NOT NULL AUTO_INCREMENT, `day` date NOT NULL, `time` VARCHAR(20) NOT NULL, `temperature` VARCHAR(20) DEFAULT NULL, `dew_point` VARCHAR(20) DEFAULT NULL, `humidity` VARCHAR(20) DEFAULT NULL, `wind` VARCHAR(20) DEFAULT NULL, `speed` VARCHAR(20) DEFAULT NULL, `gust` VARCHAR(20) DEFAULT NULL, `pressure` VARCHAR(20) DEFAULT NULL, `precip_rate` VARCHAR(20) DEFAULT NULL, `precip_accum` VARCHAR(20) DEFAULT NULL, `uv` int DEFAULT NULL, `solar` VARCHAR(20) DEFAULT NULL, PRIMARY KEY (`pos`, `day`,`time`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";
	        stmt.executeUpdate(sql);
	        System.out.println("Schema created successfully...");
	       
	        frame.dispose();
			return result;
		}

}

package FetchingData.FetchingData;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

/*
 * This is unusued class to write data on excel instead of db
 * @author: nicola_frugieri
 */
public class WriteDataToExcel {

	/**
	 * This method writes data in a Excel sheet
	 * 
	 * @param date
	 * @param data
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void WriteDataToExcelFromData(LocalDate date, Elements data) throws IOException, FileNotFoundException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet spreadsheet = workbook.createSheet(String.valueOf(date));
		XSSFRow row;
		
		 LinkedHashMap<String, LinkedList<String>> dataTable = new LinkedHashMap<String, LinkedList<String>>();
		 LinkedList<String> thArray = new LinkedList<String>();
		 
		 Elements thSet = data.select("th");
		 for (Element th : thSet) {
			 thArray.add(th.text());
	    	}
 	
		 dataTable.put("thead", thArray);
		 
		 Elements trSet = data.select("tr");
		 
		 int i = 1;
		 for(Element tr : trSet) {
			 //System.out.printf("\n\t%s\t", tr.text());
			 LinkedList<String> tdArray = new LinkedList<String>();
			 Elements tdSet = tr.select("td");
			 
			 for (Element td : tdSet) {
		 			tdArray.add(td.text()); 		
		 		}
			 dataTable.put("tr"+ i, tdArray);
			 i++;
		 }	
		 
		 Set<String> keyid = dataTable.keySet();
		 //System.out.println(keyid);
	     int rowid = 0;

	        for (String key : keyid) {
	            row = spreadsheet.createRow(rowid++);
	            LinkedList<String> objList = dataTable.get(key);
	            int cellid = 0;
	  
	            for (Object obj : objList) {
	                Cell cell = row.createCell(cellid++);
	                cell.setCellValue((String)obj);
	            } 
	        }
	        
	     FileOutputStream out = new FileOutputStream(new File("N:/Desktop/Meteo.xlsx"));
	     workbook.write(out);
	     out.close();	            
	}
}

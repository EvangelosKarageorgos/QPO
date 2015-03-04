package qpo.data.info;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import qpo.data.info.helpers.CatalogInitializationHelper;
import qpo.data.model.Table;

public class Catalog {
	
	private static Catalog 				dbCatalog;
	
	private static Map<String, Table> 	catalogMap;
	
	private static SystemProperties 	systemProperties;
	
	
	private Catalog(){
		init();
	}
	
	
	public static Catalog getInstance() {
		
		if(dbCatalog==null) {
			dbCatalog = new Catalog();
		}
		
		return dbCatalog;
	}
	
	
	
	private void init() {
		
		//Init values
		catalogMap =  new HashMap<String, Table>();
		
		try {
			parseXML("src/resources/schema.xml");
			parseSystemXML("src/resources/system.xml");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
	private void parseXML(String filename) throws ParserConfigurationException, SAXException, IOException {
		
		CatalogInitializationHelper.getSchema(CatalogInitializationHelper.getXMLDocument(filename), getCatalogMap() );
	}
	
	
	private void parseSystemXML(String filename) throws ParserConfigurationException, SAXException, IOException {
		
		CatalogInitializationHelper.getSystemProperties(CatalogInitializationHelper.getXMLDocument(filename), getSystemProperties() );
	}
	
	
	
	
	public static Table getTable(String tableName) {
		return getCatalogMap().get(tableName);
	}
	
	
	
	public static Map<String, Table> getCatalogMap() {
		
		if(catalogMap==null) {
			getInstance();
		}
		
		return catalogMap;
	}


	public static SystemProperties getSystemProperties() {
		
		if(systemProperties==null) {
			systemProperties = new SystemProperties();
		}
		
		return systemProperties;
	}



}

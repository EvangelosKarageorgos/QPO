package qpo.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import qpo.data.info.Catalog;
import qpo.data.model.Attribute;
import qpo.data.model.Index;

public class QPOtester {
	
	
	@Test
	public void runQPO() {
		
//		Catalog dbCatalog = Catalog.getInstance();
		
		

		printDebug();
		
		
		
//		try {
//			parseXML("src/resources/schema.xml");
//		} 
//		catch (Exception e) {
//			e.printStackTrace();
//		}
		
	 
	}
	
	
	
	
	private void parseXML(String filename) throws ParserConfigurationException, SAXException, IOException {
		
		
//		File fXmlFile2 = new File("resources/schema.xml");
//		File fXmlFile1 = new File("src/resources/schema.xml");
//		
//		System.out.println("filename = " + fXmlFile1.getAbsolutePath() );
//		System.out.println("filename = " + fXmlFile2.getAbsolutePath() );
		
		
//		Document doc = CatalogInitializationHelper.getXMLDocument(filename);
//		
//		Map<String, Table> catalogMap =  new HashMap<String, Table>();
//		
//		CatalogInitializationHelper.getSchema(doc, catalogMap);
//		
//		
//		
//		
//		
//		
//				
//		printDebug( catalogMap);
		
	}

	
	



	private void printDebug() {
		
		
		System.out.println("<<<<<<  Customer FileInfo:   >>>>>>>>>> ");
		System.out.println("--'"+Catalog.getRelation("Customer").getFileInfo().getFilename()+"'--");
		System.out.println("#"+Catalog.getRelation("Customer").getFileInfo().getBlocksNo()+"");
		
		
		System.out.println("Customer Attributes:");
		
		for(Attribute attr : Catalog.getRelation("Customer").getAttributes() )
			System.out.println("#"+attr.getName());
		
		
		System.out.println("Category Attributes:");
		
		for(Attribute attr : Catalog.getRelation("Category").getAttributes() )
			System.out.println("#"+attr.getName());
		
		
		
		
		System.out.println("<<<<<<  Product FileInfo:   >>>>>>>>>> ");
		System.out.println("--'"+Catalog.getRelation("Product").getFileInfo().getFilename()+"'--");
		System.out.println("#"+Catalog.getRelation("Product").getFileInfo().getBlocksNo()+"");
		
		
		System.out.println("Product Attributes:");
		
		for(Attribute attr : Catalog.getRelation("Product").getAttributes() )
			System.out.println("#"+attr.getName());
		
		
		System.out.println("#ProductID - MinValue "+Catalog.getRelation("Product").getAttribute("ID").getStatistics().getMinValue()+"");
		System.out.println("#ProductID - MaxValue "+Catalog.getRelation("Product").getAttribute("ID").getStatistics().getMaxValue()+"");
		System.out.println("#ProductID - Unique Values: "+Catalog.getRelation("Product").getAttribute("ID").getStatistics().getUniqueValues()+"");
		
		System.out.println("#NationalityCode - MinValue "+Catalog.getRelation("Nationality").getAttribute("Code").getStatistics().getMinValue()+"");
		System.out.println("#NationalityCode - MaxValue "+Catalog.getRelation("Nationality").getAttribute("Code").getStatistics().getMaxValue()+"");
		System.out.println("#NationalityCode - Unique Values: "+Catalog.getRelation("Nationality").getAttribute("Code").getStatistics().getUniqueValues()+"");
		
		//Indexes
		System.out.println("Product Indexes:");
		
		for(Index idx : Catalog.getRelation("Product").getIndexes() )
			System.out.println("#"+idx.getName());
		
		
		System.out.println("Customer Indexes:");
		
		for(Index idx : Catalog.getRelation("Customer").getIndexes() )
			System.out.println("#"+idx.getName());
		
		
		System.out.println("#CustomerID_index - Type "+Catalog.getRelation("Customer").getIndex("ID").getType()+"");
		System.out.println("#CustomerID_index - Distinct Values "+Catalog.getRelation("Customer").getIndex("ID").getStatistics().getDistinctValues()+"");
		System.out.println("#CustomerSurname_index - Distinct Values "+Catalog.getRelation("Customer").getIndex("csurname").getStatistics().getDistinctValues()+"");
		System.out.println("#CustomerSurname_index - Btree Index "+Catalog.getRelation("Customer").getIndex("csurname").getStatistics().getHeightBtree()+"");
		
		
		
		printSystemProperties();
	}



	
	private void printSystemProperties() {
		
		System.out.println("<<<<<<  System Properties:   >>>>>>>>>> ");
		
		System.out.println("#Buffers: "+Catalog.getSystemProperties().getNoOfBuffers()+"");
		System.out.println("#Buffer Size: "+Catalog.getSystemProperties().getBufferSize()+"");
		System.out.println("#PageSize: "+Catalog.getSystemProperties().getPageSize()+"");
		System.out.println("#Pages/Buffer: "+Catalog.getSystemProperties().getPagesPerBuffer()+"");
		System.out.println("#Latency: "+Catalog.getSystemProperties().getAverageLatency()+"");
		System.out.println("#WriteTime: "+Catalog.getSystemProperties().getWriteTime()+"");
		System.out.println("#TransferTime: "+Catalog.getSystemProperties().getTransferTime()+"");
		
		System.out.println("<<<<<< -------------------------------------  >>>>>>>>>> ");
		
	}




	
	
	
	
	
	


	
	
	

}

package qpo.data.info.helpers;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import qpo.data.info.SystemProperties;
import qpo.data.model.Attribute;
import qpo.data.model.AttributeStatistics;
import qpo.data.model.AttributeTypeEnum;
import qpo.data.model.FileInfo;
import qpo.data.model.Index;
import qpo.data.model.IndexStatistics;
import qpo.data.model.IndexTypeEnum;
import qpo.data.model.KeyStatusEnum;
import qpo.data.model.Table;
import qpo.data.model.TableStatistics;

public class CatalogInitializationHelper {
	
	
	// XML Tags
	public static final String TABLE_XML_TAG 				= "table";
	public static final String ATTRIBUTE_XML_TAG 			= "attribute";
	public static final String INDEX_XML_TAG 				= "index";
	
	public static final String FILE_XML_TAG 				= "file";
	public static final String TABLE_STATISTICS_XML_TAG 	= "table-statistics";
	
	public static final String NAME_XML_ELEMENT				= "name";
	
	public static final String TYPE_XML_TAG 				= "type";
	public static final String KEY_STATUS_XML_TAG 			= "key-status";
	public static final String SIZE_XML_TAG 				= "size";
	
	public static final String REFERENCE_XML_TAG 			= "reference";
	
	public static final String BLOCKS_XML_TAG 				= "blocks";
	
	public static final String CARDINALITY_XML_TAG 			= "cardinality";
	public static final String TUPLE_SIZE_XML_TAG 			= "tuple-size";
	public static final String TUPLES_PER_BLOCK_XML_TAG 	= "tuples-per-block";
	
	
	public static final String ATTRIBUTE_STATISTICS_XML_TAG = "attribute-statistics";
	
	public static final String MIN_VALUE_XML_TAG 			= "min-value";
	public static final String MAX_VALUE_XML_TAG 			= "max-value";
	public static final String UNIQUE_VALUES_XML_TAG 		= "unique-values";
	
	public static final String INDEX_STATISTICS_XML_TAG 	= "index-statistics";
	
	public static final String DISTINCT_VALUES_XML_TAG 		= "distinct-values";
	public static final String B_TREE_HEIGHT_XML_TAG 		= "Btree-height";
	
	// Attribute Types
	public static final String INTEGER_TYPE 	= "Integer";
	public static final String BIGINT_TYPE 		= "BigInt";
	public static final String CHARACTER_TYPE 	= "Character";
	public static final String BOOLEAN_TYPE 	= "Boolean";
	public static final String DATE_TYPE 		= "Date";
	public static final String TIMESTAMP_TYPE 	= "Timestamp";
	
	// Attribute Key Status
	public static final String PRIMARY_KEY_STATUS 	= "Primary";
	public static final String FOREIGN_KEY_STATUS 	= "Foreign";
	public static final String NONE_KEY_STATUS 		= "None";
	
	// Index Types
	public static final String BTREE_INDEX_TYPE 			= "B+tree";
	public static final String STATIC_HASHING_INDEX_TYPE 	= "staticHashing";
	public static final String EXTENSIBLE_HASHING_INDEX_TYPE= "extensibleHashing";
	
	
	
	
	//System Properties
	public static final String SYSTEM_PROPERTIES_XML_TAG 	= "system-properties";

	public static final String LATENCY_XML_TAG 				= "latency";
	public static final String TRANSFER_XML_TAG 			= "transfer";
	public static final String WRITE_XML_TAG 				= "write";

	public static final String BUFFERS_NO_XML_TAG 			= "no-of-buffers";
	public static final String BUFFER_SIZE_XML_TAG 			= "buffer-size";
	public static final String PAGE_SIZE_XML_TAG 			= "page-size";
	public static final String PAGES_PER_BUFFER_XML_TAG 	= "pages-per-buffer";

	
	
	
	
	
	public static Document getXMLDocument(String filename) throws ParserConfigurationException, SAXException, IOException {
		
		File fXmlFile = new File(filename);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		doc.getDocumentElement().normalize();
		
		return doc;
	}
	
	
	
	public static void getSchema(Document doc, Map<String, Table> schemaMap) {
		
		NodeList nList = doc.getElementsByTagName(TABLE_XML_TAG);
		
		
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			 
			Node nNode = nList.item(temp);
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE && StringUtils.notEmptyString(((Element) nNode).getAttribute(NAME_XML_ELEMENT)) ) {
				
				Element eElement = (Element) nNode;
				
				schemaMap.put( eElement.getAttribute(NAME_XML_ELEMENT), getTable(eElement) );
			}
			
			
		}
		
		
	}
	
	
	public static void getSystemProperties(Document doc, SystemProperties systemProperties) {

		Element eElement = (Element)doc.getElementsByTagName(SYSTEM_PROPERTIES_XML_TAG).item(0);
		
		systemProperties.setAverageLatency( Integer.parseInt(eElement.getElementsByTagName(LATENCY_XML_TAG).item(0).getTextContent()) );
		systemProperties.setTransferTime( Integer.parseInt(eElement.getElementsByTagName(TRANSFER_XML_TAG).item(0).getTextContent()) );
		systemProperties.setWriteTime( Integer.parseInt(eElement.getElementsByTagName(WRITE_XML_TAG).item(0).getTextContent()) );
		
		systemProperties.setNoOfBuffers( Integer.parseInt(eElement.getElementsByTagName(BUFFERS_NO_XML_TAG).item(0).getTextContent()) );
		systemProperties.setBufferSize( Integer.parseInt(eElement.getElementsByTagName(BUFFER_SIZE_XML_TAG).item(0).getTextContent()) );
		systemProperties.setPageSize( Integer.parseInt(eElement.getElementsByTagName(PAGE_SIZE_XML_TAG).item(0).getTextContent()) );
		systemProperties.setPagesPerBuffer( Integer.parseInt(eElement.getElementsByTagName(PAGES_PER_BUFFER_XML_TAG).item(0).getTextContent()) );
	}
	
	
	
	
	
	
	public static Table getTable(Element eElement) {
		Table tbl = new Table();
		
		tbl.setName( eElement.getAttribute(NAME_XML_ELEMENT) );
		
		tbl.setFileInfo( getFileInfo( (Element) eElement.getElementsByTagName(FILE_XML_TAG).item(0) ) );
		tbl.setStatistics( getTableStatistics( (Element) eElement.getElementsByTagName(TABLE_STATISTICS_XML_TAG).item(0) ) );
		
		addAttributes(tbl, eElement);
		addIndexes(tbl, eElement);
		
		return tbl;
		
	}




	private static void addAttributes(Table tbl, Element eElement) {

		NodeList nList = eElement.getElementsByTagName(ATTRIBUTE_XML_TAG);

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if ( nNode!=null && nNode.getNodeType() == Node.ELEMENT_NODE && StringUtils.notEmptyString(((Element) nNode).getAttribute(NAME_XML_ELEMENT)) ) {
				tbl.addAttribute( getAttribute( (Element) nNode ) );
			}
		}
	}
	
	
	private static void addIndexes(Table tbl, Element eElement) {

		NodeList nList = eElement.getElementsByTagName(INDEX_XML_TAG);

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if ( nNode!=null && nNode.getNodeType() == Node.ELEMENT_NODE && StringUtils.notEmptyString(((Element) nNode).getAttribute(NAME_XML_ELEMENT)) ) {
				tbl.addIndex( getIndex( (Element) nNode ) );
			}
		}
		
	}
	
	
	
	
	
	private static FileInfo getFileInfo(Element eElement) {
		
		FileInfo fInfo = new FileInfo();
		
		fInfo.setFilename( eElement.getAttribute(NAME_XML_ELEMENT) );
		
		fInfo.setSize( Integer.parseInt(eElement.getElementsByTagName(SIZE_XML_TAG).item(0).getTextContent()) );
		fInfo.setBlocksNo( Integer.parseInt( eElement.getElementsByTagName(BLOCKS_XML_TAG).item(0).getTextContent() ) );
		
		return fInfo;
	}
	
	
	private static TableStatistics getTableStatistics(Element eElement) {
		
		TableStatistics stats = new TableStatistics();
		
		stats.setCardinality( Integer.parseInt(eElement.getElementsByTagName(CARDINALITY_XML_TAG).item(0).getTextContent()) );
		stats.setTupleSize( Integer.parseInt( eElement.getElementsByTagName(TUPLE_SIZE_XML_TAG).item(0).getTextContent() ) );
		stats.setTuplesPerBlock( Integer.parseInt( eElement.getElementsByTagName(TUPLES_PER_BLOCK_XML_TAG).item(0).getTextContent() ) );
		
		return stats;
	}
	
	
	
	
	public static Attribute getAttribute(Element eElement) {
		
		Attribute attr = new Attribute();
		
		attr.setName( eElement.getAttribute(NAME_XML_ELEMENT) );
		
		attr.setSize( Integer.parseInt(eElement.getElementsByTagName(SIZE_XML_TAG).item(0).getTextContent()) );
		attr.setType( getAttributeType( eElement.getElementsByTagName(TYPE_XML_TAG).item(0).getTextContent() ) );
		attr.setKeyStatus( getAttributeKeyStatus( eElement.getElementsByTagName(KEY_STATUS_XML_TAG).item(0).getTextContent() ) );
		
		if( attr.getKeyStatus().equals(KeyStatusEnum.Foreign) )
			addReferences(attr, (Element) eElement.getElementsByTagName(REFERENCE_XML_TAG).item(0) );
		
		
		attr.setStatistics( getAttributeStatistics( (Element) eElement.getElementsByTagName(ATTRIBUTE_STATISTICS_XML_TAG).item(0) ) );
		
		
		return attr;
	}
	

	private static void addReferences(Attribute attr, Element eElement) {
		attr.setReferencedTableName( eElement.getElementsByTagName(TABLE_XML_TAG).item(0).getTextContent() );
		attr.setReferencedAttributeName( eElement.getElementsByTagName(ATTRIBUTE_XML_TAG).item(0).getTextContent() );
	}
	
	
	private static AttributeStatistics getAttributeStatistics(Element eElement) {
		
		AttributeStatistics stats = new AttributeStatistics();
		
		if( validateXMLInput(eElement, MIN_VALUE_XML_TAG) )
			stats.setMinValue( eElement.getElementsByTagName(MIN_VALUE_XML_TAG).item(0).getTextContent() );
		if( validateXMLInput(eElement, MAX_VALUE_XML_TAG) )
			stats.setMaxValue( eElement.getElementsByTagName(MAX_VALUE_XML_TAG).item(0).getTextContent() );
		if( validateXMLInput(eElement, UNIQUE_VALUES_XML_TAG) )
			stats.setUniqueValues( Integer.parseInt( eElement.getElementsByTagName(UNIQUE_VALUES_XML_TAG).item(0).getTextContent() ) );
		
		return stats;
	}
	
	
	public static Index getIndex(Element eElement) {
		
		Index idx = new Index();
		
		idx.setName( eElement.getAttribute(NAME_XML_ELEMENT) );
		
		idx.setAttributeName( eElement.getElementsByTagName(ATTRIBUTE_XML_TAG).item(0).getTextContent() );
		idx.setType( getIndexType( eElement.getElementsByTagName(TYPE_XML_TAG).item(0).getTextContent() ) );
		
		idx.setStatistics( getIndexStatistics( (Element) eElement.getElementsByTagName(INDEX_STATISTICS_XML_TAG).item(0) ) );
		
		return idx;
	}

	
	private static IndexStatistics getIndexStatistics(Element eElement) {
		
		IndexStatistics stats = new IndexStatistics();
		
		if( validateXMLInput(eElement, DISTINCT_VALUES_XML_TAG) )
			stats.setDistinctValues( Integer.parseInt( eElement.getElementsByTagName(DISTINCT_VALUES_XML_TAG).item(0).getTextContent() ) );
		if( validateXMLInput(eElement, B_TREE_HEIGHT_XML_TAG) )
			stats.setHeightBtree( Integer.parseInt( eElement.getElementsByTagName(B_TREE_HEIGHT_XML_TAG).item(0).getTextContent() ) );
		
		return stats;
	}
	

	
	private static boolean validateXMLInput(Element eElement, String xmlTagValue) {
		return ( null!=eElement && null!=eElement.getElementsByTagName(xmlTagValue) && null!=eElement.getElementsByTagName(xmlTagValue).item(0)) ? true : false;
	}
	

	private static AttributeTypeEnum getAttributeType(String attributeTypeStr) {
		
		if( INTEGER_TYPE.equalsIgnoreCase(attributeTypeStr) )
			return AttributeTypeEnum.Integer;
		
		else if( BIGINT_TYPE.equalsIgnoreCase(attributeTypeStr) )
			return AttributeTypeEnum.BigInt;
		
		else if( CHARACTER_TYPE.equalsIgnoreCase(attributeTypeStr) )
			return AttributeTypeEnum.Character;
		
		else if( BOOLEAN_TYPE.equalsIgnoreCase(attributeTypeStr) )
			return AttributeTypeEnum.Boolean;
		
		else if( DATE_TYPE.equalsIgnoreCase(attributeTypeStr) )
			return AttributeTypeEnum.Date;
		
		else if( TIMESTAMP_TYPE.equalsIgnoreCase(attributeTypeStr) )
			return AttributeTypeEnum.Timestamp;
		
		
		return AttributeTypeEnum.Character;
	}
	
	
	private static KeyStatusEnum getAttributeKeyStatus(String attributeKeyStatusStr) {
		
		if( PRIMARY_KEY_STATUS.equalsIgnoreCase(attributeKeyStatusStr) )
			return KeyStatusEnum.Primary;
		
		else if( FOREIGN_KEY_STATUS.equalsIgnoreCase(attributeKeyStatusStr) )
			return KeyStatusEnum.Foreign;
		
		
		return KeyStatusEnum.None;
	}
	
	
	private static IndexTypeEnum getIndexType(String indexTypeStr) {
		
		if( BTREE_INDEX_TYPE.equalsIgnoreCase(indexTypeStr) )
			return IndexTypeEnum.Btree;
		
		else if( STATIC_HASHING_INDEX_TYPE.equalsIgnoreCase(indexTypeStr) )
			return IndexTypeEnum.StaticHashing;
		
		else if( EXTENSIBLE_HASHING_INDEX_TYPE.equalsIgnoreCase(indexTypeStr) )
			return IndexTypeEnum.ExtensibleHashing;
		
		
		return IndexTypeEnum.Btree;
	}




}

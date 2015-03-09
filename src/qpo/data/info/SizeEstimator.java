package qpo.data.info;

import qpo.data.model.AttributeTypeEnum;

public class SizeEstimator {

	
	
	public static Integer getEstimatedRecords(String tableName, String columnName, String value) {
		return Catalog.getRelation(tableName).getStatistics().getCardinality() /	Catalog.getRelation(tableName).getAttribute(columnName).getStatistics().getUniqueValues();
	}
	
	
	public static Integer getEstimatedRecordsRangeOverValue(String tableName, String columnName, String value) {
		Integer Nr = Catalog.getRelation(tableName).getStatistics().getCardinality();
		
		Integer estimRes = Nr/2;
		
		if(Catalog.getRelation(tableName).getAttribute(columnName).getType().equals(AttributeTypeEnum.Integer) || Catalog.getRelation(tableName).getAttribute(columnName).getType().equals(AttributeTypeEnum.BigInt)) {
			
			Double maxValue = Double.parseDouble( Catalog.getRelation(tableName).getAttribute(columnName).getStatistics().getMaxValue().toString() );
			Double minValue = Double.parseDouble( Catalog.getRelation(tableName).getAttribute(columnName).getStatistics().getMinValue().toString() );
			Double vvalue = Double.parseDouble( value );
			
			estimRes = (int) (Nr * (maxValue - vvalue)	/ (maxValue - minValue ));
		}
			
	    return estimRes;
	}
	
	
	public static Integer getEstimatedRecordsRangeUnderValue(String tableName, String columnName, String value) {
		Integer Nr = Catalog.getRelation(tableName).getStatistics().getCardinality();
		
		Integer estimRes = Nr/2;
		
		if(Catalog.getRelation(tableName).getAttribute(columnName).getType().equals(AttributeTypeEnum.Integer) || Catalog.getRelation(tableName).getAttribute(columnName).getType().equals(AttributeTypeEnum.BigInt)) {
			
			Double maxValue = Double.parseDouble( Catalog.getRelation(tableName).getAttribute(columnName).getStatistics().getMaxValue().toString() );
			Double minValue = Double.parseDouble( Catalog.getRelation(tableName).getAttribute(columnName).getStatistics().getMinValue().toString() );
			Double vvalue = Double.parseDouble( value );
			
			estimRes = (int) (Nr * (vvalue - minValue)	/ (maxValue - minValue ));
		}
			
	    return estimRes;
	}
	
	

	
	
}

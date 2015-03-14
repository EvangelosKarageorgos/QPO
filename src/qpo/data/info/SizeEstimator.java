package qpo.data.info;

import java.util.List;

import qpo.data.model.*;
import qpo.processor.*;


public class SizeEstimator {

	public static Integer getEstimatedRecords(Table table, PlanPredicateNode predicate){
		return 1;
	}

	public static Integer getJoinEstimatedRecords(Table left, Table right, PlanPredicateNode predicate){
		return 1;
	}
	
	public static Integer getTupleSize(List<Attribute> attributes){
		Integer sz = 0;
		for(Attribute a : attributes){
			sz += a.getSize();
		}
		return sz;
	}

	public static Integer getEstimatedRecords(Attribute attribute, String value) {
		return attribute.getTable().getStatistics().getCardinality() /	attribute.getStatistics().getUniqueValues();
	}
	
	
	public static Integer getEstimatedRecordsRangeOverValue(Attribute attribute, String value) {
		Integer Nr = attribute.getTable().getStatistics().getCardinality();
		
		Integer estimRes = Nr/2;
		
		if(attribute.getType().equals(AttributeTypeEnum.Integer) || attribute.getType().equals(AttributeTypeEnum.BigInt)) {
			
			Double maxValue = Double.parseDouble( attribute.getStatistics().getMaxValue().toString() );
			Double minValue = Double.parseDouble( attribute.getStatistics().getMinValue().toString() );
			Double vvalue = Double.parseDouble( value );
			
			estimRes = (int) (Nr * (maxValue - vvalue)	/ (maxValue - minValue ));
		}
			
	    return estimRes;
	}
	
	
	public static Integer getEstimatedRecordsRangeUnderValue(Attribute attribute, String value) {
		Integer Nr = attribute.getTable().getStatistics().getCardinality();
		
		Integer estimRes = Nr/2;
		
		if(attribute.getType().equals(AttributeTypeEnum.Integer) || attribute.getType().equals(AttributeTypeEnum.BigInt)) {
			
			Double maxValue = Double.parseDouble( attribute.getStatistics().getMaxValue().toString() );
			Double minValue = Double.parseDouble( attribute.getStatistics().getMinValue().toString() );
			Double vvalue = Double.parseDouble( value );
			
			estimRes = (int) (Nr * (vvalue - minValue)	/ (maxValue - minValue ));
		}
			
	    return estimRes;
	}
	
	

	
	
}

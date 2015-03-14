package qpo.data.info;

import java.util.List;

import qpo.data.model.*;
import qpo.processor.*;


public class SizeEstimator {

	
	public static Integer getEstimatedRecords(Table table, PlanPredicateNode predicate){
		
		if(predicate instanceof PlanComparisonNode)
			return getSelectionRecords(table, (PlanComparisonNode)predicate);
			
		else if(predicate instanceof PlanNegationNode)
			return getNegationRecords(table, (PlanNegationNode)predicate);
		
		else if(predicate instanceof PlanConjunctionNode)
			return getConjuctionRecords(table, (PlanConjunctionNode)predicate);
			
		else if(predicate instanceof PlanDisjunctionNode)
			getDisjuctionRecords(table, (PlanDisjunctionNode)predicate);
		
		
		return table.getStatistics().getCardinality()/2;
	}
	
	
	private static Integer getConjuctionRecords(Table table, PlanConjunctionNode conPredicate){
		
		Integer cardinalityNr = table.getStatistics().getCardinality();
		Integer conjustionEstimation = 1;
		
		for(PlanPredicateNode predNode: conPredicate.predicates){
			conjustionEstimation = conjustionEstimation * ( getEstimatedRecords(table, predNode) /cardinalityNr);
		}
		
		return cardinalityNr * conjustionEstimation;
	}
	
	private static Integer getDisjuctionRecords(Table table, PlanDisjunctionNode disPredicate){
		
		Integer cardinalityNr = table.getStatistics().getCardinality();
		Integer disjustionEstimation = 1;
	
		for(PlanPredicateNode predNode: disPredicate.predicates){
			disjustionEstimation = disjustionEstimation * ( 1-(getEstimatedRecords(table, predNode) /cardinalityNr));
		}
		
		return cardinalityNr * (1-disjustionEstimation);
	}
	
	private static Integer getNegationRecords(Table table, PlanNegationNode negPredicate){
		return table.getStatistics().getCardinality() - getEstimatedRecords(table, negPredicate.predicate);
	}
	

	private static Integer getSelectionRecords(Table table, PlanComparisonNode compPredicate){
		
		if(compPredicate.left instanceof PlanLiteralValueNode && compPredicate.right instanceof PlanLiteralValueNode)
			return fixedPredicateResult(compPredicate.left, compPredicate.right, table.getStatistics().getCardinality());
		
		Integer avgCaseCardinality = table.getStatistics().getCardinality()/2; 
		Integer cardinality = table.getStatistics().getCardinality();
		
		//TODO - extra analysis by value ranges of attributes
		PlanAttributeValueNode leftAttr = (compPredicate.left instanceof PlanAttributeValueNode)?(PlanAttributeValueNode)(compPredicate.left):null;
		PlanAttributeValueNode rightAttr = (compPredicate.right instanceof PlanAttributeValueNode)?(PlanAttributeValueNode)(compPredicate.right):null;
		
		//Join case
		if(leftAttr!=null && leftAttr.attribute.getTable()!=table)
			return cardinality;
		if(rightAttr!=null && rightAttr.attribute.getTable()!=table)
			return cardinality;
		
		if(leftAttr!=null && rightAttr!=null)
			return cardinality; 
			
		
		Attribute attr = getExpressionAttribute(leftAttr, rightAttr);
		Object val = getExpressionValue(compPredicate);
		
		if(attr==null || val==null){
			System.out.println(" <------  Not existing attribute  --------> ");
			return cardinality;
		}
		
		
		switch(compPredicate.operator){
			case equals:
				return getEstimatedRecords(attr, val);
			case notEqual:
				return table.getStatistics().getCardinality() - getEstimatedRecords(attr, val);
			case greaterThan:
				return getEstimatedRecordsRangeOverValue(attr, val);
			case lessThan:
				return getEstimatedRecordsRangeOverValue(attr, val);
			case like:
				return table.getStatistics().getCardinality() / 10;
			default:
				break;
		}
		
		
		//Average case. Should not reach here anyway
		return avgCaseCardinality;
	}


	
	private static Object getExpressionValue(PlanComparisonNode compPredicate) {
		
		if(compPredicate.left instanceof PlanLiteralValueNode)
			return ((PlanLiteralValueNode) compPredicate.left).getValue();
		
		else if(compPredicate.right instanceof PlanLiteralValueNode)
			return ((PlanLiteralValueNode) compPredicate.right).getValue();
		
		return null;
	}

	private static Attribute getExpressionAttribute(PlanAttributeValueNode leftAttr, PlanAttributeValueNode rightAttr) {
		return leftAttr!=null ? leftAttr.attribute : (rightAttr!=null ? rightAttr.attribute : null);
	}
	
	private static Integer fixedPredicateResult(PlanValueNode left, PlanValueNode right, Integer cardinality) {
		return (left.toString().equalsIgnoreCase(right.toString())) ? cardinality: 0;
	}
	
	
	
	
	
	
	
	

	public static Integer getJoinEstimatedRecords(Table left, Table right, PlanPredicateNode predicate){
		
		
		
		Integer old_N_right = right.getStatistics().getCardinality();
		
		Integer N_left 	= getEstimatedRecords(left, predicate);
		Integer N_right = getEstimatedRecords(right, predicate);
		
		return (N_left * N_right) / old_N_right;
	}
	
	
	
	
	public static Integer getEstimatedRecords(Attribute attribute, Object value) {
		return attribute.getTable().getStatistics().getCardinality() /	attribute.getStatistics().getUniqueValues();
	}
	
	
	public static Integer getEstimatedRecordsRangeOverValue(Attribute attribute, Object value) {
		Integer Nr = attribute.getTable().getStatistics().getCardinality();
		
		Integer estimRes = Nr/2;
		
		if(attribute.getType().equals(AttributeTypeEnum.Integer) || attribute.getType().equals(AttributeTypeEnum.BigInt)
				|| value instanceof Number ) {
			
			Integer maxValue 	= Integer.parseInt( attribute.getStatistics().getMaxValue().toString() );
			Integer minValue 	= Integer.parseInt( attribute.getStatistics().getMinValue().toString() );
			Integer vvalue 		= Integer.parseInt( value.toString() );
			
			estimRes = (int) (Nr * (maxValue - vvalue)	/ (maxValue - minValue ));
		}
		
		//TODO For Strings OR Date, calculations accordingly and distance functions
			
	    return estimRes;
	}
	
	
	public static Integer getEstimatedRecordsRangeUnderValue(Attribute attribute, Object value) {
		Integer Nr = attribute.getTable().getStatistics().getCardinality();
		
		Integer estimRes = Nr/2;
		
		if(attribute.getType().equals(AttributeTypeEnum.Integer) || attribute.getType().equals(AttributeTypeEnum.BigInt) 
				|| value instanceof Number ) {
			
			Integer maxValue 	= Integer.parseInt( attribute.getStatistics().getMaxValue().toString() );
			Integer minValue 	= Integer.parseInt( attribute.getStatistics().getMinValue().toString() );
			Integer vvalue 		= Integer.parseInt( value.toString() );
			
			estimRes = (int) (Nr * (vvalue - minValue)	/ (maxValue - minValue ));
		}
		
		//TODO For Strings OR Date, calculations accordingly and distance functions
		
	    return estimRes;
	}
	
	

	
	public static Integer getTupleSize(List<Attribute> attributes){
		Integer sz = 0;
		for(Attribute a : attributes){
			sz += a.getSize();
		}
		return sz;
	}
	
	
}

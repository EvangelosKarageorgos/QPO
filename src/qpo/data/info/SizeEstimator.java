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
			return getDisjuctionRecords(table, (PlanDisjunctionNode)predicate);
		
		
		return table.getStatistics().getCardinality()/2;
	}
	
	
	private static Integer getConjuctionRecords(Table table, PlanConjunctionNode conPredicate){
		
		Double cardinalityNr = (double)table.getStatistics().getCardinality();
		Double conjustionEstimation = 1.0;
		Integer intermediateRes = 1;
		
		for(PlanPredicateNode predNode: conPredicate.predicates){
//			conjustionEstimation = conjustionEstimation * ( (double)getEstimatedRecords(table, predNode) /cardinalityNr);
			conjustionEstimation = conjustionEstimation * (double)getEstimatedRecords(table, predNode) ;
		}
		
//		return (int)(cardinalityNr * conjustionEstimation);
		conjustionEstimation = (double)(conjustionEstimation/Math.pow(cardinalityNr, conPredicate.predicates.size()));
		intermediateRes = (int) (cardinalityNr * conjustionEstimation);
		
		return (intermediateRes>1) ? intermediateRes : 1;
	}
	
	private static Integer getDisjuctionRecords(Table table, PlanDisjunctionNode disPredicate){
		
		Double cardinalityNr = (double)table.getStatistics().getCardinality();
		Double disjustionEstimation = 1.0;
	
		for(PlanPredicateNode predNode: disPredicate.predicates){
			disjustionEstimation = disjustionEstimation * ( 1-((double)getEstimatedRecords(table, predNode) /cardinalityNr));
		}
		
		return (int) (cardinalityNr * (1-disjustionEstimation));
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
			return getAttributeOnAttributeSizeEstimation(cardinality, leftAttr.attribute, rightAttr.attribute, compPredicate.operator); 
			
		
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
				return getEstimatedRecordsRangeUnderValue(attr, val);
			case like:
				return table.getStatistics().getCardinality() / 10;
			default:
				break;
		}
		
		
		//Average case. Should not reach here anyway
		return avgCaseCardinality;
	}

	public static Integer getJoinEstimatedRecords(Table left, Table right, PlanPredicateNode predicate){
		
		if(predicate instanceof PlanComparisonNode)
			return getJoinSelectionRecords(left, right, (PlanComparisonNode)predicate);
			
		else if(predicate instanceof PlanNegationNode)
			return getJoinNegationRecords(left, right, (PlanNegationNode)predicate);
		
		else if(predicate instanceof PlanConjunctionNode)
			return getJoinConjuctionRecords(left, right, (PlanConjunctionNode)predicate);
			
		else if(predicate instanceof PlanDisjunctionNode)
			getJoinDisjuctionRecords(left, right, (PlanDisjunctionNode)predicate);
		
		
		return left.getStatistics().getCardinality()*right.getStatistics().getCardinality()/2;
	}
	
	
	private static Integer getJoinConjuctionRecords(Table left, Table right, PlanConjunctionNode conPredicate){
		
//		Integer cardinalityNr = left.getStatistics().getCardinality()*right.getStatistics().getCardinality();
//		Integer conjustionEstimation = 1;
//		
//		for(PlanPredicateNode predNode: conPredicate.predicates){
//			conjustionEstimation = conjustionEstimation * ( getJoinEstimatedRecords(left, right, predNode) /cardinalityNr);
//		}
//		
//		return cardinalityNr * conjustionEstimation;
		
		Double cardinalityNr = (double)left.getStatistics().getCardinality()*right.getStatistics().getCardinality();
		Double conjustionEstimation = 1.0;
		Integer intermediateRes = 1;
		
		for(PlanPredicateNode predNode: conPredicate.predicates){
//			conjustionEstimation = conjustionEstimation * ( (double)getEstimatedRecords(table, predNode) /cardinalityNr);
			conjustionEstimation = conjustionEstimation * (double)getJoinEstimatedRecords(left, right, predNode) ;
		}
		
//		return (int)(cardinalityNr * conjustionEstimation);
		conjustionEstimation = (double)(conjustionEstimation/Math.pow(cardinalityNr, conPredicate.predicates.size()));
		intermediateRes = (int) (cardinalityNr * conjustionEstimation);
		
		return (intermediateRes>1) ? intermediateRes : 1;
	}
	
	private static Integer getJoinDisjuctionRecords(Table left, Table right, PlanDisjunctionNode disPredicate){
		
		Double cardinalityNr = (double)left.getStatistics().getCardinality()*right.getStatistics().getCardinality();
		Double disjustionEstimation = 1.0;
	
		for(PlanPredicateNode predNode: disPredicate.predicates){
			disjustionEstimation = disjustionEstimation * ( 1-(getJoinEstimatedRecords(left, right, predNode) /cardinalityNr));
		}
		
		return (int)(cardinalityNr * (1-disjustionEstimation));
	}
	
	private static Integer getJoinNegationRecords(Table left, Table right, PlanNegationNode negPredicate){
		return left.getStatistics().getCardinality()*right.getStatistics().getCardinality() - getJoinEstimatedRecords(left, right, negPredicate.predicate);
	}
	

	private static Integer getJoinSelectionRecords(Table left, Table right, PlanComparisonNode compPredicate){
		
		if(compPredicate.left instanceof PlanLiteralValueNode && compPredicate.right instanceof PlanLiteralValueNode)
			return fixedPredicateResult(compPredicate.left, compPredicate.right, left.getStatistics().getCardinality()*right.getStatistics().getCardinality());
		
		Integer avgCaseCardinality = left.getStatistics().getCardinality()*right.getStatistics().getCardinality()/2; 
		Integer cardinality = left.getStatistics().getCardinality()*right.getStatistics().getCardinality();
		
		//TODO - extra analysis by value ranges of attributes
		PlanAttributeValueNode leftAttr = (compPredicate.left instanceof PlanAttributeValueNode)?(PlanAttributeValueNode)(compPredicate.left):null;
		PlanAttributeValueNode rightAttr = (compPredicate.right instanceof PlanAttributeValueNode)?(PlanAttributeValueNode)(compPredicate.right):null;
		
		//Join case
		
		if(leftAttr!=null && rightAttr!=null){
			if(leftAttr.attribute.getTable()==rightAttr.attribute.getTable()){
				return getAttributeOnAttributeSizeEstimation(cardinality, leftAttr.attribute, rightAttr.attribute, compPredicate.operator); 
			} else{
				if(leftAttr.attribute.getKeyStatus()==KeyStatusEnum.Foreign
					&& leftAttr.attribute.getReferencedAttributeName().equalsIgnoreCase(rightAttr.attribute.getName())
					&& leftAttr.attribute.getReferencedTableName().equalsIgnoreCase(rightAttr.attribute.getRelationName())){
						return left.getStatistics().getCardinality();
				}
				if(rightAttr.attribute.getKeyStatus()==KeyStatusEnum.Foreign
					&& rightAttr.attribute.getReferencedAttributeName().equalsIgnoreCase(leftAttr.attribute.getName())
					&& rightAttr.attribute.getReferencedTableName().equalsIgnoreCase(leftAttr.attribute.getRelationName())){
						return right.getStatistics().getCardinality();
					
				}
				return getAttributeOnAttributeSizeEstimation(cardinality, leftAttr.attribute, rightAttr.attribute, compPredicate.operator);
			}
		}
		
			
		
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
				return cardinality - getEstimatedRecords(attr, val);
			case greaterThan:
				return getEstimatedRecordsRangeOverValue(attr, val);
			case lessThan:
				return getEstimatedRecordsRangeOverValue(attr, val);
			case like:
				return cardinality / 10;
			default:
				break;
		}
		
		
		//Average case. Should not reach here anyway
		return avgCaseCardinality;
	}

	private static Integer getAttributeOnAttributeSizeEstimation(Integer fullCardinality, Attribute attribute1, Attribute attribute2, ComparisonOperatorType operator){
		return fullCardinality / 10;
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
	
	
	
	/*public static Integer getJoinEstimatedRecords(Table left, Table right, PlanPredicateNode predicate){
		Integer old_N_right = right.getStatistics().getCardinality();
		
		Integer N_left 	= getEstimatedRecords(left, predicate);
		Integer N_right = getEstimatedRecords(right, predicate);
		
		return (N_left * N_right) / old_N_right;
	}*/
	
	
	
	
	public static Integer getEstimatedRecords(Attribute attribute, Object value) {
		return attribute.getTable().getStatistics().getCardinality() /	attribute.getStatistics().getUniqueValues();
	}
	
	
	public static Integer getEstimatedRecordsRangeOverValue(Attribute attribute, Object value) {
		Integer Nr = attribute.getTable().getStatistics().getCardinality();
		
		Integer estimRes = Nr/2;
		
		if(attribute.getType().equals(AttributeTypeEnum.Integer) || attribute.getType().equals(AttributeTypeEnum.BigInt)
				|| value instanceof Number ) {
			
			Double maxValue 	= Double.parseDouble( attribute.getStatistics().getMaxValue().toString() );
			Double minValue 	= Double.parseDouble( attribute.getStatistics().getMinValue().toString() );
			Double vvalue 		= Double.parseDouble( value.toString() );
			
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
			
			Double maxValue 	= Double.parseDouble( attribute.getStatistics().getMaxValue().toString() );
			Double minValue 	= Double.parseDouble( attribute.getStatistics().getMinValue().toString() );
			Double vvalue 		= Double.parseDouble( value.toString() );
			
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

package qpo.data.info;

import qpo.data.model.*;
import qpo.processor.*;


public class CostEstimator {

	
	// Retrieval-Search
	public static Integer getCostOfLinear(Integer blocksR) {
		return Catalog.getSystemProperties().getAverageLatency() + Catalog.getSystemProperties().getTransferTime() * blocksR; 
	}
	
	
	public static Integer getCostOfBtreePrimaryEqualityOnKey(Integer treeHeight) {
		return ( Catalog.getSystemProperties().getAverageLatency() + Catalog.getSystemProperties().getTransferTime() ) * (treeHeight+1);
	}
	
	public static Integer getCostOfBtreePrimaryEqualityOnNonKey(Integer treeHeight, Integer blocksContainingValue) {
		return ( Catalog.getSystemProperties().getAverageLatency() + Catalog.getSystemProperties().getTransferTime() ) * treeHeight 
				 + Catalog.getSystemProperties().getTransferTime() * blocksContainingValue;
	}
	
	
	public static Integer getCostOfBtreeSecondaryEqualityOnKey(Integer treeHeight) {
		return ( Catalog.getSystemProperties().getAverageLatency() + Catalog.getSystemProperties().getTransferTime() ) * (treeHeight+1);
	}
	
	public static Integer getCostOfBtreeSecondaryEqualityOnNonKey(Integer treeHeight, Integer recordsFetched) {
		return ( Catalog.getSystemProperties().getAverageLatency() + Catalog.getSystemProperties().getTransferTime() ) * (treeHeight+recordsFetched);
	}
	
	
	public static Integer getCostOfBtreePrimaryComparison(Integer treeHeight, Integer blocksContainingValue) {
		return ( Catalog.getSystemProperties().getAverageLatency() + Catalog.getSystemProperties().getTransferTime() ) * treeHeight 
				 + Catalog.getSystemProperties().getTransferTime() * blocksContainingValue;
	}
	
	public static Integer getCostOfBtreeSecondaryComparison(Integer treeHeight, Integer recordsFetched) {
		return ( Catalog.getSystemProperties().getAverageLatency() + Catalog.getSystemProperties().getTransferTime() ) * (treeHeight+recordsFetched);
	}
	
	
	// Hashing -- estimation 
	public static Integer getFixedCostOfHashing(){
		return Catalog.getSystemProperties().getAverageLatency() + 2*Catalog.getSystemProperties().getTransferTime();
	}
	
	
	// -------------------------------------- JOINS ----------------- //
	
	private static Integer log(Integer x, Integer base)	{
	    return (int) (Math.log(x) / Math.log(base));
	}
	
	// BlockNested
	public static Integer getJoinCostOfBlockNested(Integer blocksR, Integer blocksS, Integer blocksMem){
		return getSeeksCostOfBlockNested(blocksR, blocksS, blocksMem) +  // Seeks
				getTransfersCostOfBlockNested(blocksR, blocksS, blocksMem); // Transfers
	}
	
	private static Integer getSeeksCostOfBlockNested(Integer blocksR, Integer blocksS, Integer blocksMem){
		
		Integer blocksRInMem = ((blocksR/(blocksMem-2))<2) ? 2 : ((blocksR%(blocksMem-2))==0) ? (blocksR/(blocksMem-2)) : (blocksR/(blocksMem-2))+1 ;
		
		return blocksMem > (blocksR+blocksS+1) 
				? ( 1 + 1 ) * Catalog.getSystemProperties().getAverageLatency() 
		        : (2*blocksRInMem) * Catalog.getSystemProperties().getAverageLatency(); 
	}
	
	private static Integer getTransfersCostOfBlockNested(Integer blocksR, Integer blocksS, Integer blocksMem){
		
		Integer blocksRInMem = ((blocksR/(blocksMem-2))<2) ? 2 : ((blocksR%(blocksMem-2))==0) ? (blocksR/(blocksMem-2)) : (blocksR/(blocksMem-2))+1 ;
		
		return blocksMem > (blocksR+blocksS+1) 
				? ( blocksR + blocksS ) * Catalog.getSystemProperties().getTransferTime() 
		        : ( blocksRInMem*blocksS + blocksR ) * Catalog.getSystemProperties().getTransferTime();
	}
	
	
	//External Sort
	public static Integer getJoinCostOfExternalSort(Integer blocksR, Integer blocksS, Integer blocksMem){
		return blocksMem > (blocksR+blocksS+1) 
				? ( blocksR + blocksS ) * Catalog.getSystemProperties().getTransferTime() + (1+1) * Catalog.getSystemProperties().getAverageLatency() 
				: getCostOfExternalSort(blocksR, blocksMem) + getCostOfExternalSort(blocksS, blocksMem);
				
	}
	
	public static Integer getCostOfExternalSort(Integer blocksR, Integer blocksMem, Integer blocksBuf){
		return getSeeksCostOfExternalSort(blocksR, blocksMem, blocksBuf) +  // Seeks
				getTransfersCostOfExternalSort(blocksR, blocksMem, blocksBuf); // Transfers
	}
	
	public static Integer getCostOfExternalSort(Integer blocksR, Integer blocksMem){
		return getCostOfExternalSort(blocksR, blocksMem, 1);
	}
	
	
	private static Integer getSeeksCostOfExternalSort(Integer blocksR, Integer blocksMem, Integer blocksBuf){
		
		Integer logBrInMem = (log((blocksR/blocksMem),blocksMem-1)<1) ? 1 : log((blocksR/blocksMem),blocksMem-1);
		
		return ( (2*(blocksR/blocksMem)) + (blocksR/blocksBuf)*( (2*logBrInMem)-1) )   * Catalog.getSystemProperties().getAverageLatency(); 
	}
	
	private static Integer getTransfersCostOfExternalSort(Integer blocksR, Integer blocksMem, Integer blocksBuf){
		
		Integer logBrInMem = (log((blocksR/blocksMem),blocksMem-1)<1) ? 1 : log((blocksR/blocksMem),blocksMem-1);

		return (blocksR * ((2*logBrInMem)+1))   * Catalog.getSystemProperties().getTransferTime();
	}
	
	
	
	//Hash
	public static Integer getJoinCostOfHash(Integer blocksR, Integer blocksS, Integer blocksMem){
		return getJoinCostOfHash(blocksR, blocksS, blocksMem, 1);
				
	}
	
	public static Integer getJoinCostOfHash(Integer blocksR, Integer blocksS, Integer blocksMem, Integer blocksBuf){
		return getSeeksCostOfExternalHash(blocksR, blocksS, blocksMem, blocksBuf) +  // Seeks
				getTransfersCostOfExternalHash(blocksR, blocksS, blocksMem, blocksBuf); // Transfers
	}
	
	
	private static Integer getSeeksCostOfExternalHash(Integer blocksR, Integer blocksS, Integer blocksMem, Integer blocksBuf){
		
		Integer logBsMem = (log(blocksS,blocksMem-1)<2) ? 2 : log(blocksS,blocksMem-1);
		
		return blocksMem > (blocksR+blocksS+1) 
			? 2*( (blocksR/blocksBuf) + (blocksS/blocksBuf) )      				* Catalog.getSystemProperties().getAverageLatency()
			: 2*( (blocksR/blocksBuf) + (blocksS/blocksBuf) )*(logBsMem-1)      * Catalog.getSystemProperties().getAverageLatency(); 
	}
	
	private static Integer getTransfersCostOfExternalHash(Integer blocksR, Integer blocksS, Integer blocksMem, Integer blocksBuf){
		
		Integer logBsMem = (log(blocksS,blocksMem-1)<2) ? 2 : log(blocksS,blocksMem-1);
		
		return blocksMem > (blocksR+blocksS+1) 
			? (3*(blocksR + blocksS))											* Catalog.getSystemProperties().getTransferTime()
			: ( (2*(blocksR + blocksS)*(logBsMem-1)) + blocksR + blocksS)		* Catalog.getSystemProperties().getTransferTime();
	}
	
	
	
	
	
	// Indexed BlockNested
	public static Integer getJoinCostOfIndexedBlockNested(Integer blocksR, Integer estimatedRecsS){
		return Catalog.getSystemProperties().getAverageLatency() + blocksR * Catalog.getSystemProperties().getTransferTime()  +  // R
				Catalog.getSystemProperties().getAverageLatency() + estimatedRecsS * estimatedRecsS; // Indexed records of S
	}
	
	
	
	
	
	
	
	
	
	
	// Join Cost Estimator
	public static JoinInfo getCheapestJoin(PlanJoinNode joinNode) throws Exception {
		
		Integer blocksR = joinNode.left.getTable().getStatistics().getBlocksOnDisk();
		Integer blocksS = joinNode.right.getTable().getStatistics().getBlocksOnDisk();
		
//		JoinInfo joinInfo = getCheapestTableComposition(blocksR, blocksS);
//		Integer estimatedRecsS = joinNode.right.getTable().getStatistics().getCardinality() /	joinNode.rightAttr.attribute.getStatistics().getUniqueValues();
//		if( joinInfo.getCostTime()> getJoinCostOfIndexedBlockNested(blocksR, estimatedRecsS) )
		 
		return getCheapestTableComposition(blocksR, blocksS);
	}
	
	
	public static JoinInfo getCheapestDiff(PlanDiffNode planDiffNode) throws Exception  {
		
		Integer blocksR = planDiffNode.left.getTable().getStatistics().getBlocksOnDisk();
		Integer blocksS = planDiffNode.right.getTable().getStatistics().getBlocksOnDisk();
		
		return getCheapestTableComposition(blocksR, blocksS);
	}


	public static JoinInfo getCheapestIntersection(PlanIntersectNode planIntersectNode) throws Exception {
		
		Integer blocksR = planIntersectNode.left.getTable().getStatistics().getBlocksOnDisk();
		Integer blocksS = planIntersectNode.right.getTable().getStatistics().getBlocksOnDisk();
		
		return getCheapestTableComposition(blocksR, blocksS);
	}
	
	
	
	private static JoinInfo getCheapestTableComposition(Integer blocksR, Integer blocksS) throws Exception {
		
		JoinInfo joinInfo = null;
		Integer minCostIO = null;
		
		for(JoinTypeEnum joinType : JoinTypeEnum.values()) {
			Integer currentCost = getCostPerJoin(blocksR, blocksS, joinType);

//			System.out.println(joinType+":" + currentCost);
			
			if( minCostIO==null || minCostIO>currentCost ) {
				minCostIO = currentCost;
				joinInfo = new JoinInfo(joinType, minCostIO);
			}
		}
		
		return joinInfo;
	}
	
	
	
	private static Integer getCostPerJoin(Integer blocksR, Integer blocksS, JoinTypeEnum joinType) throws Exception {

		Integer blocksMem = Catalog.getSystemProperties().getPagesPerBuffer();
		
		switch(joinType){
		case HashJoin:
			return getJoinCostOfHash(blocksR, blocksS, blocksMem);
		case MergeSort:
			return getJoinCostOfExternalSort(blocksR, blocksS, blocksMem);
		case BlockLoopNested:
			return getJoinCostOfBlockNested(blocksR, blocksS, blocksMem);
		case IndexedBtreeJoin:
			return getJoinCostOfBlockNested(blocksR, blocksS, blocksMem)+100000; //TODO Btree estimation
		case IndexedHashJoin:
			return getJoinCostOfBlockNested(blocksR, blocksS, blocksMem)+100000; //TODO Hash Estimation
//		case IndexedBlockNested:
//			return getJoinCostOfIndexedBlockNested(blocksR, estimatedRecsOfS, blocksMem);
		default:
			return getJoinCostOfHash(blocksR, blocksS, blocksMem);
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Integer getSelectionEstimatedCost(Table table, PlanPredicateNode predicate){

		if(predicate instanceof PlanComparisonNode)
			return getSelectionCost(table, (PlanComparisonNode)predicate);

		else if(predicate instanceof PlanNegationNode)
			return getCostOfLinear( table.getStatistics().getBlocksOnDisk() );

		else if(predicate instanceof PlanConjunctionNode)
			return getConjuctionCost(table, (PlanConjunctionNode)predicate);

		else if(predicate instanceof PlanDisjunctionNode)
			return getDisjuctionCost(table, (PlanDisjunctionNode)predicate);


		return getCostOfLinear( table.getStatistics().getBlocksOnDisk() ); 
	}
	
	
	private static Integer getConjuctionCost(Table table, PlanConjunctionNode conPredicate){
		
		Integer minEstimatedCost = null;
		
		for(PlanPredicateNode predNode: conPredicate.predicates){
			
			Integer expressionEstimatedCost = getSelectionEstimatedCost(table, predNode);
			
			if(minEstimatedCost==null || minEstimatedCost>expressionEstimatedCost)
				minEstimatedCost = expressionEstimatedCost;
		}
		
		return minEstimatedCost;
	}
	
	private static Integer getDisjuctionCost(Table table, PlanDisjunctionNode disPredicate){
		
		Integer maxEstimatedCost = getCostOfLinear( table.getStatistics().getBlocksOnDisk() );;
		Integer disEstimatedCost = 0;
	
		for(PlanPredicateNode predNode: disPredicate.predicates){
			disEstimatedCost += getSelectionEstimatedCost(table, predNode);;
		}
		
		return Math.min(maxEstimatedCost, disEstimatedCost);
	}
	
	private static Integer getSelectionCost(Table table, PlanComparisonNode compPredicate){
		
		Integer maxEstimatedCost = getCostOfLinear( table.getStatistics().getBlocksOnDisk() );
		Integer selectionEstimatedCost = null;
		
		if(compPredicate.left instanceof PlanLiteralValueNode && compPredicate.right instanceof PlanLiteralValueNode)
			return fixedPredicateResult(compPredicate.left, compPredicate.right, maxEstimatedCost);
		
		//TODO - extra analysis by value ranges of attributes
		PlanAttributeValueNode leftAttr = (compPredicate.left instanceof PlanAttributeValueNode)?(PlanAttributeValueNode)(compPredicate.left):null;
		PlanAttributeValueNode rightAttr = (compPredicate.right instanceof PlanAttributeValueNode)?(PlanAttributeValueNode)(compPredicate.right):null;
		
		//Join case
		if(leftAttr!=null && rightAttr!=null)
			return maxEstimatedCost; 
			
		
		Attribute attr = SizeEstimator.getExpressionAttribute(leftAttr, rightAttr);
		Object val = SizeEstimator.getExpressionValue(compPredicate);
		
		if(attr==null || val==null){
			return maxEstimatedCost;
		}
		
		
		switch(compPredicate.operator){
			case equals:
				selectionEstimatedCost = getEqualityExpressionCost(attr, val, maxEstimatedCost);
				break;
			case notEqual:
				selectionEstimatedCost = maxEstimatedCost;
				break;
			case greaterThan:
				selectionEstimatedCost = getComparisonOverValueCost(attr, val, maxEstimatedCost);
				break;
			case lessThan:
				selectionEstimatedCost = getComparisonUnderValueCost(attr, val, maxEstimatedCost);
				break;
			case like:
				return maxEstimatedCost;
			default:
				break;
		}
		
		
		return Math.min(maxEstimatedCost, selectionEstimatedCost);
	}
	
	
	private static Integer getEqualityExpressionCost(Attribute attr, Object val, Integer maxEstimatedCost) {
		
		Index idx = attr.getTable().getIndex(attr.getName());
		
		if(idx==null)
			return maxEstimatedCost;
		
		switch(idx.getType()){
			case Btree:
				return (KeyStatusEnum.Primary.equals(attr.getKeyStatus())) 
						? getCostOfBtreePrimaryEqualityOnKey(idx.getStatistics().getHeightBtree())
								: getCostOfBtreeSecondaryEqualityOnNonKey(idx.getStatistics().getHeightBtree(), SizeEstimator.getEstimatedRecords(attr, val));
			case StaticHashing:
				return SizeEstimator.getEstimatedRecords(attr, val) * getFixedCostOfHashing();
			case ExtensibleHashing:
				return SizeEstimator.getEstimatedRecords(attr, val) * getFixedCostOfHashing();
			default:
				break;
		}
		
		
		return maxEstimatedCost;
	}
	
	
	private static Integer getComparisonUnderValueCost(Attribute attr, 	Object val, Integer maxEstimatedCost) {
		Index idx = attr.getTable().getIndex(attr.getName());
		
		if(idx==null)
			return maxEstimatedCost;
		
		switch(idx.getType()){
			case Btree:
				return (KeyStatusEnum.Primary.equals(attr.getKeyStatus())) 
					? Math.min( maxEstimatedCost, getCostOfBtreePrimaryComparison(idx.getStatistics().getHeightBtree(), getEstimatedBlocks(attr, SizeEstimator.getEstimatedRecordsRangeUnderValue(attr, val))) )
					: Math.min( maxEstimatedCost, getCostOfBtreeSecondaryComparison(idx.getStatistics().getHeightBtree(), SizeEstimator.getEstimatedRecordsRangeUnderValue(attr, val)) );
			case StaticHashing:
				return Math.min( maxEstimatedCost,	SizeEstimator.getEstimatedRecordsRangeUnderValue(attr, val)* getFixedCostOfHashing() );
			case ExtensibleHashing:		
				return Math.min( maxEstimatedCost,	SizeEstimator.getEstimatedRecordsRangeUnderValue(attr, val)* getFixedCostOfHashing() );
			default:
				break;
		}
		
		return maxEstimatedCost;
		
	}
	

	private static Integer getComparisonOverValueCost(Attribute attr, Object val, Integer maxEstimatedCost) {

		Index idx = attr.getTable().getIndex(attr.getName());
		
		if(idx==null)
			return maxEstimatedCost;
		
		switch(idx.getType()){
			case Btree:
				return (KeyStatusEnum.Primary.equals(attr.getKeyStatus())) 
					? Math.min( maxEstimatedCost, getCostOfBtreePrimaryComparison(idx.getStatistics().getHeightBtree(), getEstimatedBlocks(attr, SizeEstimator.getEstimatedRecordsRangeUnderValue(attr, val))) )
					: Math.min( maxEstimatedCost, getCostOfBtreeSecondaryComparison(idx.getStatistics().getHeightBtree(), SizeEstimator.getEstimatedRecordsRangeUnderValue(attr, val)) );
			case StaticHashing:
				return Math.min( maxEstimatedCost,	SizeEstimator.getEstimatedRecordsRangeOverValue(attr, val)* getFixedCostOfHashing() );
			case ExtensibleHashing:		
				return Math.min( maxEstimatedCost,	SizeEstimator.getEstimatedRecordsRangeOverValue(attr, val)* getFixedCostOfHashing() );	
			default:
				break;
		}
		
		return maxEstimatedCost;
		
	}
	

	private static Integer getEstimatedBlocks(Attribute attr, Integer estimatedRecords) {
		return Math.min(estimatedRecords, attr.getTable().getStatistics().getBlocksOnDisk());
	}


	private static Integer fixedPredicateResult(PlanValueNode left, PlanValueNode right, Integer linearCost) {
		return (left.toString().equalsIgnoreCase(right.toString())) ? linearCost : 0;
	}
	
	
	
	
	


	
	
	
	
	
}

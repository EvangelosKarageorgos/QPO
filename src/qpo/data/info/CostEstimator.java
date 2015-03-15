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
	
	
	
	
	
	
	
	// Join Cost Estimator
	public static JoinInfo getCheapestJoin(PlanJoinNode joinNode) throws Exception {
		
		JoinInfo joinInfo = null;
		Integer minCostIO = null;
		
		for(JoinTypeEnum joinType : JoinTypeEnum.values()) {
			Integer currentCost = getCostPerJoin(joinNode, joinType);

			System.out.println(joinType+":" + currentCost);
			
			if( minCostIO==null || minCostIO>currentCost ) {
				minCostIO = currentCost;
				joinInfo = new JoinInfo(joinType, minCostIO);
			}
		}
		
		return joinInfo;
	}
	
	
	private static Integer getCostPerJoin(PlanJoinNode joinNode, JoinTypeEnum joinType) throws Exception {

		Integer blocksR = joinNode.left.getTable().getStatistics().getBlocksOnDisk();
		Integer blocksS = joinNode.right.getTable().getStatistics().getBlocksOnDisk();
		
//		System.out.println("BlcR=" + blocksR);
//		System.out.println("BlcS=" + blocksS);
//		System.out.println("BlckFileR=" + ((Relation)joinNode.left.getTable()).getFileInfo().getBlocksNo() );
//		System.out.println("BlckFileS=" + ((Relation)joinNode.right.getTable()).getFileInfo().getBlocksNo() );
		
		Integer blocksMem = Catalog.getSystemProperties().getPagesPerBuffer();
		
		switch(joinType){
		case HashJoin:
			return getJoinCostOfHash(blocksR, blocksS, blocksMem);
		case MergeSort:
			return getJoinCostOfExternalSort(blocksR, blocksS, blocksMem);
		case BlockLoopNested:
			return getJoinCostOfBlockNested(blocksR, blocksS, blocksMem);
		case IndexedBtreeJoin:
			return getJoinCostOfBlockNested(blocksR, blocksS, blocksMem)+100; //TODO Btree estimation
		case IndexedHashJoin:
			return getJoinCostOfBlockNested(blocksR, blocksS, blocksMem)+100; //TODO Hash Estimation
		default:
			return getJoinCostOfHash(blocksR, blocksS, blocksMem);
		}

	}
	
	
	
	
	
}

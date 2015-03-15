package qpo.data.info;

import qpo.data.model.JoinInfo;


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
		return ( (blocksR/(blocksMem-2))*blocksS + blocksR ) * Catalog.getSystemProperties().getAverageLatency(); 
	}
	
	private static Integer getTransfersCostOfBlockNested(Integer blocksR, Integer blocksS, Integer blocksMem){
		return (2*(blocksR/(blocksMem-2))) * Catalog.getSystemProperties().getTransferTime();
	}
	
	
	//External Sort
	public static Integer getJoinCostOfExternalSort(Integer blocksR, Integer blocksS, Integer blocksMem){
		return getCostOfExternalSort(blocksR, blocksMem) + getCostOfExternalSort(blocksS, blocksMem);
				
	}
	
	public static Integer getCostOfExternalSort(Integer blocksR, Integer blocksMem, Integer blocksBuf){
		return getSeeksCostOfExternalSort(blocksR, blocksMem, blocksBuf) +  // Seeks
				getTransfersCostOfExternalSort(blocksR, blocksMem, blocksBuf); // Transfers
	}
	
	public static Integer getCostOfExternalSort(Integer blocksR, Integer blocksMem){
		return getCostOfExternalSort(blocksR, blocksMem, 1);
	}
	
	
	private static Integer getSeeksCostOfExternalSort(Integer blocksR, Integer blocksMem, Integer blocksBuf){
		return ( (2*(blocksR/blocksMem)) + (blocksR/blocksBuf)*((2*log((blocksR/blocksMem),blocksMem-1))-1) )   * Catalog.getSystemProperties().getAverageLatency(); 
	}
	
	private static Integer getTransfersCostOfExternalSort(Integer blocksR, Integer blocksMem, Integer blocksBuf){
		return (blocksR * ((2*log((blocksR/blocksMem),blocksMem-1))+1))   * Catalog.getSystemProperties().getTransferTime();
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
		return blocksMem > (blocksR+blocksS+1) 
			? 2*( (blocksR/blocksBuf) + (blocksS/blocksBuf) )      								* Catalog.getSystemProperties().getAverageLatency()
			: 2*( (blocksR/blocksBuf) + (blocksS/blocksBuf) )*(log(blocksS,blocksMem-1)-1)      * Catalog.getSystemProperties().getAverageLatency(); 
	}
	
	private static Integer getTransfersCostOfExternalHash(Integer blocksR, Integer blocksS, Integer blocksMem, Integer blocksBuf){
		return blocksMem > (blocksR+blocksS+1) 
			? (3*(blocksR + blocksS))															* Catalog.getSystemProperties().getTransferTime()
			: ( (2*(blocksR + blocksS)*(log(blocksS,blocksMem-1)-1)) + blocksR + blocksS)		* Catalog.getSystemProperties().getTransferTime();
	}
	
	
	
	
	
	
	
	// Join Cost Estimator
	public static JoinInfo getCheapestJoin(){
		return null;
	}
	
	
	
	
}

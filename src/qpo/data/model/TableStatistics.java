package qpo.data.model;

public class TableStatistics {
	private Integer cardinality;
	private Integer tupleSize;
	private Integer tuplesPerBlock;
	
	public Integer getCardinality() {
		return cardinality;
	}
	public void setCardinality(Integer cardinality) {
		this.cardinality = cardinality;
	}
	
	
	public Integer getTupleSize() {
		return tupleSize;
	}
	public void setTupleSize(Integer tupleSize) {
		this.tupleSize = tupleSize;
	}
	
	public Integer getTuplesPerBlock() {
		return tuplesPerBlock;
	}
	public void setTuplesPerBlock(Integer tuplesPerBlock) {
		this.tuplesPerBlock = tuplesPerBlock;
	}

}

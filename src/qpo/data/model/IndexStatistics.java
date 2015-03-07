package qpo.data.model;

public class IndexStatistics {
	private Integer distinctValues;
	private Integer heightBtree;
	
	public Integer getDistinctValues() {
		return distinctValues;
	}
	public void setDistinctValues(Integer distinctValues) {
		this.distinctValues = distinctValues;
	}
	
	public Integer getHeightBtree() {
		return heightBtree;
	}
	public void setHeightBtree(Integer heightBtree) {
		this.heightBtree = heightBtree;
	}
}

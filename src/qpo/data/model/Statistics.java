package qpo.data.model;

import java.util.HashMap;
import java.util.Map;

public class Statistics {

	//Table
	private Integer cardinality;
	private Integer tupleSize;
	private Integer tuplesPerBlock;
	
	//Attributes
	private Integer uniqueValues;
	private String 	minValue;
	private String 	maxValue;
	
	private Map<String, Integer> histogramValues;
	
	//Indexes
	private Integer distinctValues;
	private Integer heightBtree;

	
	//Table
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
	
	
	//Attribute
	public Integer getUniqueValues() {
		return uniqueValues;
	}
	public void setUniqueValues(Integer uniqueValues) {
		this.uniqueValues = uniqueValues;
	}
	
	public String getMinValue() {
		return minValue;
	}
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	
	public String getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	
	public Map<String, Integer> getHistogramValues() {
		if(histogramValues==null)
			histogramValues = new HashMap<String, Integer>(); 
			
		return histogramValues;
	}
	public void setHistogramValues(Map<String, Integer> histogramValues) {
		this.histogramValues = histogramValues;
	}
	
	
	//Index
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

package qpo.data.model;

import java.util.HashMap;
import java.util.Map;

public class AttributeStatistics {
	private Integer uniqueValues;
	private Object 	minValue;
	private Object 	maxValue;
	private Map<String, Integer> histogramValues;
	
	public Integer getUniqueValues() {
		return uniqueValues;
	}
	public void setUniqueValues(Integer uniqueValues) {
		this.uniqueValues = uniqueValues;
	}
	
	public Object getMinValue() {
		return minValue;
	}
	public void setMinValue(Object minValue) {
		this.minValue = minValue;
	}
	
	public Object getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Object maxValue) {
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
}

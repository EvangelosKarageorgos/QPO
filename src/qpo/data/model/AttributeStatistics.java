package qpo.data.model;

import java.util.HashMap;
import java.util.Map;

public class AttributeStatistics {
	private Integer uniqueValues;
	private Object 	minValue;
	private Object 	maxValue;
	private Map<String, Integer> histogramValues;

	public AttributeStatistics clone(){
		AttributeStatistics result = new AttributeStatistics();
		result.uniqueValues = uniqueValues;
		result.minValue = minValue;
		result.maxValue = maxValue;
		if(histogramValues!=null){
			result.histogramValues = new HashMap<String, Integer>();
			for(String k : histogramValues.keySet()){
				result.histogramValues.put(k, histogramValues.get(k));
			}
		}
		return result;
	}

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

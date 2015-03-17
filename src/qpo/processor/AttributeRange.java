package qpo.processor;

import java.util.List;

public class AttributeRange {
	public Comparable minValue;
	public Comparable maxValue;
	public void mergeInto(List<AttributeRange> ranges){
		for(AttributeRange ar : ranges){
			if(ar.mergeWith(this))
				return;
		}
		
		for(int i=0; i<ranges.size(); i++){
			AttributeRange prev = i==0?null:ranges.get(i-1);
			AttributeRange curr = ranges.get(i);
			if(prev==null && curr.minValue.compareTo(maxValue)==1){
				ranges.add(0, this);
				return;
			}
			if(prev!=null && prev.maxValue.compareTo(minValue)==1 && curr.minValue.compareTo(maxValue)==1){
				ranges.add(i, this);
				return;
			}
		}
		ranges.add(ranges.size(), this);
	}

	public boolean mergeWith(AttributeRange ar){
		int css = ar.minValue.compareTo(minValue);
		int csl = ar.minValue.compareTo(maxValue);
		int cls = ar.maxValue.compareTo(minValue);
		int cll = ar.maxValue.compareTo(maxValue);
		if(cls==-1 || csl==1)
			return false;
		if(css==-1)
			minValue = ar.minValue;
		if(cll==1)
			maxValue = ar.maxValue;
		return true;
	}
	
	
}

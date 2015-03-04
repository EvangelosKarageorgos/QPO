package qpo.data.info;

public class SystemProperties {

	
	private Integer 		noOfBuffers;
	private Integer 		bufferSize;
	private Integer 		pageSize;
	private Integer 		pagesPerBuffer;
	
	private Integer 		averageLatency;
	private Integer 		transferTime;
	private Integer 		writeTime;
	
	
	
	
	public Integer getNoOfBuffers() {
		return noOfBuffers;
	}
	public void setNoOfBuffers(Integer noOfBuffers) {
		this.noOfBuffers = noOfBuffers;
	}
	
	public Integer getBufferSize() {
		return bufferSize;
	}
	public void setBufferSize(Integer bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public Integer getPagesPerBuffer() {
		return pagesPerBuffer;
	}
	public void setPagesPerBuffer(Integer pagesPerBuffer) {
		this.pagesPerBuffer = pagesPerBuffer;
	}
	
	
	
	public Integer getAverageLatency() {
		return averageLatency;
	}
	public void setAverageLatency(Integer averageLatency) {
		this.averageLatency = averageLatency;
	}
	
	public Integer getTransferTime() {
		return transferTime;
	}
	public void setTransferTime(Integer transferTime) {
		this.transferTime = transferTime;
	}
	
	public Integer getWriteTime() {
		return writeTime;
	}
	public void setWriteTime(Integer writeTime) {
		this.writeTime = writeTime;
	}
	
	
	
}

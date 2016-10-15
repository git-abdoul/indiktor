package com.fsi.monitoring.kpi.monitor.calypso.ds.resourceData;


public class DSEventBuffer {
	private int currentBufferSize;
	private int maxBufferSize;
	private int bufferLimitSize;
	
	public DSEventBuffer(int currentBufferSize, int maxBufferSize,
			int bufferLimitSize) {
		this.currentBufferSize = currentBufferSize;
		this.maxBufferSize = maxBufferSize;
		this.bufferLimitSize = bufferLimitSize;
	}

	public int getCurrentBufferSize() {
		return currentBufferSize;
	}

	public int getMaxBufferSize() {
		return maxBufferSize;
	}

	public int getBufferLimitSize() {
		return bufferLimitSize;
	}
}

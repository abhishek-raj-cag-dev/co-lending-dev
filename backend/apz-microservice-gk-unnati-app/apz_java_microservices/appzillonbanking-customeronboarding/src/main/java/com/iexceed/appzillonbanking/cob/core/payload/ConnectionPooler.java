package com.iexceed.appzillonbanking.cob.core.payload;

public class ConnectionPooler {

	private int maxTotalConnections;
	private int maxRouteConnections;
	private int maxLocalHostConnections;
	
	private int defaultKeepAliveTime;
	private int connectionTimeout;
	private int requestTimeout;
	
	private int socketTimeout;
	private int idleConnectionWaitTime;
	private int taskSchedulerPoolSize;
	
	private String hostIp;
	private int hostPortNumber;
	
	public ConnectionPooler() {
		super();
	}

	public ConnectionPooler(int maxTotalConnections, int maxRouteConnections, int maxLocalHostConnections,
			int defaultKeepAliveTime, int connectionTimeout, int requestTimeout, int socketTimeout,
			int idleConnectionWaitTime, int taskSchedulerPoolSize, String hostIp, int hostPortNumber) {
		super();
		this.maxTotalConnections = maxTotalConnections;
		this.maxRouteConnections = maxRouteConnections;
		this.maxLocalHostConnections = maxLocalHostConnections;
		this.defaultKeepAliveTime = defaultKeepAliveTime;
		this.connectionTimeout = connectionTimeout;
		this.requestTimeout = requestTimeout;
		this.socketTimeout = socketTimeout;
		this.idleConnectionWaitTime = idleConnectionWaitTime;
		this.taskSchedulerPoolSize = taskSchedulerPoolSize;
		this.hostIp = hostIp;
		this.hostPortNumber = hostPortNumber;
	}

	public int getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}

	public int getMaxRouteConnections() {
		return maxRouteConnections;
	}

	public void setMaxRouteConnections(int maxRouteConnections) {
		this.maxRouteConnections = maxRouteConnections;
	}

	public int getMaxLocalHostConnections() {
		return maxLocalHostConnections;
	}

	public void setMaxLocalHostConnections(int maxLocalHostConnections) {
		this.maxLocalHostConnections = maxLocalHostConnections;
	}

	public int getDefaultKeepAliveTime() {
		return defaultKeepAliveTime;
	}

	public void setDefaultKeepAliveTime(int defaultKeepAliveTime) {
		this.defaultKeepAliveTime = defaultKeepAliveTime;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getIdleConnectionWaitTime() {
		return idleConnectionWaitTime;
	}

	public void setIdleConnectionWaitTime(int idleConnectionWaitTime) {
		this.idleConnectionWaitTime = idleConnectionWaitTime;
	}

	public int getTaskSchedulerPoolSize() {
		return taskSchedulerPoolSize;
	}

	public void setTaskSchedulerPoolSize(int taskSchedulerPoolSize) {
		this.taskSchedulerPoolSize = taskSchedulerPoolSize;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public int getHostPortNumber() {
		return hostPortNumber;
	}

	public void setHostPortNumber(int hostPortNumber) {
		this.hostPortNumber = hostPortNumber;
	}

	@Override
	public String toString() {
		return "ConnectionPooler [maxTotalConnections=" + maxTotalConnections + ", maxRouteConnections="
				+ maxRouteConnections + ", maxLocalHostConnections=" + maxLocalHostConnections
				+ ", defaultKeepAliveTime=" + defaultKeepAliveTime + ", connectionTimeout=" + connectionTimeout
				+ ", requestTimeout=" + requestTimeout + ", socketTimeout=" + socketTimeout
				+ ", idleConnectionWaitTime=" + idleConnectionWaitTime + ", taskSchedulerPoolSize="
				+ taskSchedulerPoolSize + ", hostIp=" + hostIp + ", hostPortNumber=" + hostPortNumber + "]";
	}
}

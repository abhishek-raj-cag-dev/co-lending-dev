package com.iexceed.appzillonbanking.cob.exception;

/**
 * @author Ankit.G
 */
public class ReplicationFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String tableName;

	public ReplicationFailedException(String message) {
		super(message);
		this.tableName = null;
	}

	public ReplicationFailedException(String tableName, String message) {
		super(message);
		this.tableName = tableName;
	}

	/**
	 * Preserves original exception/Root exception
	 */
	public ReplicationFailedException(String tableName, String message, Throwable cause) {
		super(message, cause);
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

}

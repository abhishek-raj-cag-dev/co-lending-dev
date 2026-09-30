package com.iexceed.appzillonbanking.cob.core.domain.ab;
 
import javax.persistence.*;
 
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
 
import com.fasterxml.jackson.annotation.JsonProperty;
 
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "TB_ABOB_CA_DEVIATION_RA_TRACKER")
@IdClass(DeviationRATrackerId.class)
@Getter
@Setter
@ToString
public class DeviationRATracker {
 
	@Id
	private String applicationId;
 
	@Id
	private String recordId;
	
	@Column(name = "app_id")
	@JsonProperty("app_id")
	private String appId;
 
	@Column(name = "record_msg")
	@JsonProperty("record_msg")
	private String recordMsg;
 
	@Column(name = "record_type")
	@JsonProperty("record_type")
	private String recordType;
 
	@Column(name = "ca_by")
	@JsonProperty("ca_by")
	private String caBy;
 
	@Column(name = "authority")
	@JsonProperty("authority")
	private String authority;
 
	@Column(name = "approved_by")
	@JsonProperty("approved_by")
	private String approvedBy;
 
	@Column(name = "approved_status")
	@JsonProperty("approved_status")
	private String approvedStatus;
 
	@CreationTimestamp
	@Column(name = "create_ts")
	@JsonProperty("create_ts")
	private LocalDateTime createTs;
 
	@UpdateTimestamp
	@Column(name = "approved_ts")
	@JsonProperty("approved_ts")
	private LocalDateTime approvedTs;
 
	@Column(name = "remarks")
	@JsonProperty("remarks")
	private String remarks;
 
	@Column(name = "product")
	@JsonProperty("product")
	private String product;

	@Transient
	@JsonProperty("createdTimeStamp")
	private String createdTimeStamp;

	@Transient
	@JsonProperty("approvedTimeStamp")
	private String approvedTimeStamp;

 
}
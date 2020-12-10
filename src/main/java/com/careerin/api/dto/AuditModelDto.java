package com.careerin.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
@Getter
@Setter
public class AuditModelDto implements Serializable {

	private static final long serialVersionUID = -1579886131202791581L;

	private Long id;

	private LocalDateTime createdAt;

	private Long createdBy;

	private LocalDateTime changedAt;

	private Long changedBy;
}

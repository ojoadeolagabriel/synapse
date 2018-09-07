package com.synapse.task.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class Error {
	private String code;
	private String responseMessage;
	private String body;
	private String additionalInformation;
}

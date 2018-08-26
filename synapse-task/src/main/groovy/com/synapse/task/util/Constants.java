package com.synapse.task.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants {
    public static final int ONE = 1;
    public static final String DEFAULT_MESSAGE_PIPELINE = "::synapse.message.pipeline::";
    public static final ObjectMapper mapper;

    static {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
        mapper = new ObjectMapper();
        mapper.setDateFormat(df);
    }
}

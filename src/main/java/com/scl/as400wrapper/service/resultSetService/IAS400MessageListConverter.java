package com.scl.as400wrapper.service.resultSetService;

import com.ibm.as400.access.AS400Message;

import java.util.Map;

public interface IAS400MessageListConverter {
    Map<String, Object> ConvertToMap(AS400Message[] aS400Messages);
}

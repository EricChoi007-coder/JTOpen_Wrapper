package com.scl.as400wrapper.service.spResultParamService;

import com.ibm.as400.access.AS400JDBCCallableStatement;
import com.scl.as400wrapper.domain.spParams.SpParamAs400;

import java.util.Dictionary;
import java.util.List;

public interface IResultParamService {
    Dictionary<String,Object> getParamResultAndFormat(AS400JDBCCallableStatement stm, List<SpParamAs400> spParamAs400List);
}

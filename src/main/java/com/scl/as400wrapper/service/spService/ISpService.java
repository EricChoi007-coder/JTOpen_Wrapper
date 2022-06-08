package com.scl.as400wrapper.service.spService;

import com.ibm.as400.access.AS400JDBCCallableStatement;
import com.scl.as400wrapper.domain.spParams.SpParamAs400;

import java.util.List;

public interface ISpService {

    AS400JDBCCallableStatement setStatement(AS400JDBCCallableStatement stm, List<SpParamAs400> spParamAs400List);
}

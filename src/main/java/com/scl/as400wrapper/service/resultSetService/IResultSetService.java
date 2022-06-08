package com.scl.as400wrapper.service.resultSetService;

import com.ibm.as400.access.AS400JDBCCallableStatement;
import com.scl.as400wrapper.domain.spParams.SpParamAs400;

import java.sql.SQLException;
import java.util.Dictionary;
import java.util.List;

public interface IResultSetService {
    Dictionary<String,Object> getSingleResultMapReturn(AS400JDBCCallableStatement stm, List<SpParamAs400> spParamAs400List);

    List<Dictionary<String,Object>> getMultiResultMapReturn(AS400JDBCCallableStatement stm, List<SpParamAs400> spParamAs400List) throws SQLException;
}

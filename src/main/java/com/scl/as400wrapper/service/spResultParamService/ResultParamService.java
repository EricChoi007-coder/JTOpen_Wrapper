package com.scl.as400wrapper.service.spResultParamService;

import com.ibm.as400.access.AS400JDBCCallableStatement;
import com.scl.as400wrapper.domain.spParams.SpParamAs400;
import com.scl.as400wrapper.domain.spParams.SpParamInOutType;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

@Service
public class ResultParamService implements IResultParamService{
    public Dictionary<String,Object> getParamResultAndFormat(AS400JDBCCallableStatement stm, List<SpParamAs400> spParamAs400List)
    {
        //Create empty result Dic
        Dictionary<String,Object> resultMapDictionaries = new Hashtable<>();

        for (SpParamAs400 spParamAs400 : spParamAs400List){
            if(spParamAs400.getParamInOutType() == SpParamInOutType.OUT_TYPE || spParamAs400.getParamInOutType() == SpParamInOutType.IN_OUT_TYPE){
                switch (spParamAs400.getParamInSpecType()) {
                    case JDBC_VARCHAR:
                        try {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),stm.getString((int) spParamAs400.getId()));
                        } catch (SQLException e) {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),"JDBC_VARCHAR_ERROR:"+e.getMessage());
                        }
                        break;
                    case JDBC_DECIMAL:
                        try {
                            resultMapDictionaries.put( spParamAs400.getId().toString(),stm.getBigDecimal((int) spParamAs400.getId()));
                        } catch (SQLException e) {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),"JDBC_DECIMAL_ERROR:"+e.getMessage());
                        }
                        break;
                    case JDBC_DOUBLE:
                        try {
                            resultMapDictionaries.put( spParamAs400.getId().toString(),stm.getDouble((int) spParamAs400.getId()));
                        } catch (SQLException e) {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),"JDBC_DOUBLE_ERROR:"+e.getMessage());
                        }
                        break;
                    case JDBC_INTEGER:
                        try {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),stm.getInt((int) spParamAs400.getId()));
                        } catch (SQLException e) {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),"JDBC_INTEGER_ERROR:"+e.getMessage());
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + spParamAs400.getParamInSpecType());
                }

            }
        }
        return resultMapDictionaries;

    }
}

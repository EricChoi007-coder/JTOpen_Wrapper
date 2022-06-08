package com.scl.as400wrapper.service.resultSetService;

import com.ibm.as400.access.AS400JDBCCallableStatement;
import com.scl.as400wrapper.domain.spParams.SpParamAs400;
import com.scl.as400wrapper.domain.spParams.SpParamInOutType;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

@Service
public class ResultSetService implements IResultSetService{
    public Dictionary<String,Object> getSingleResultMapReturn(AS400JDBCCallableStatement stm, List<SpParamAs400> spParamAs400List){

        Dictionary<String,Object> resultMapDictionaries = new Hashtable<>();

        for (SpParamAs400 spParamAs400 : spParamAs400List){
            if(spParamAs400.getParamInOutType() == SpParamInOutType.OUT_TYPE || spParamAs400.getParamInOutType() == SpParamInOutType.IN_OUT_TYPE){
                switch (spParamAs400.getParamInSpecType()) {
                    case JDBC_VARCHAR:
                        try {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),stm.getString((int) spParamAs400.getId()));
                        } catch (Exception e) {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),"JDBC_VARCHAR_ERROR:"+e.getMessage());
                        }
                        break;
                    case JDBC_DECIMAL:
                        try {
                            resultMapDictionaries.put( spParamAs400.getId().toString(),stm.getBigDecimal((int) spParamAs400.getId()));
                        } catch (Exception e) {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),"JDBC_DECIMAL_ERROR:"+e.getMessage());
                        }
                        break;
                    case JDBC_DOUBLE:
                        try {
                            resultMapDictionaries.put( spParamAs400.getId().toString(),stm.getDouble((int) spParamAs400.getId()));
                        } catch (Exception e) {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),"JDBC_DOUBLE_ERROR:"+e.getMessage());
                        }
                        break;
                    case JDBC_INTEGER:
                        try {
                            resultMapDictionaries.put(spParamAs400.getId().toString(),stm.getInt((int) spParamAs400.getId()));
                        } catch (Exception e) {
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

    public List<Dictionary<String,Object>> getMultiResultMapReturn(AS400JDBCCallableStatement stm, List<SpParamAs400> spParamAs400List) throws SQLException {
        List<Dictionary<String,Object>> resultMapList = new ArrayList<>();

            while(stm.getMoreResults()){
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
                resultMapList.add(resultMapDictionaries);
            }



                    return resultMapList;

    }
}

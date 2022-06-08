package com.scl.as400wrapper.service.spService;

import com.ibm.as400.access.AS400JDBCCallableStatement;
import com.scl.as400wrapper.domain.spParams.SpParamAs400;
import com.scl.as400wrapper.domain.spParams.SpParamInOutType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;

@Service
public class SpService implements ISpService{

    public AS400JDBCCallableStatement setStatement(AS400JDBCCallableStatement stm, List<SpParamAs400> spParamAs400List){

        //validation of List<ParamAs400> paramAs400List
        int listSize = spParamAs400List.size();
        try {
            for (SpParamAs400 spParamAs400 : spParamAs400List) {

                //Param_IN_TYPE
                if (spParamAs400.getParamInOutType() == SpParamInOutType.IN_TYPE) {

                    switch (spParamAs400.getParamInSpecType()) {
                        case JDBC_VARCHAR:
                            stm.setString((int) spParamAs400.getId(), (String) spParamAs400.getParamContent());
                            break;
                        case JDBC_DECIMAL:
                            stm.setBigDecimal((int) spParamAs400.getId(),(BigDecimal) spParamAs400.getParamContent());
                            break;
                        case JDBC_DOUBLE:
                            stm.setDouble((int) spParamAs400.getId(),(Double) spParamAs400.getParamContent());
                            break;
                        case JDBC_INTEGER:
                            stm.setInt((int) spParamAs400.getId(),(Integer) spParamAs400.getParamContent());
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + spParamAs400.getParamInSpecType());
                    }

                    //Param_OUT_TYPE
                } else if (spParamAs400.getParamInOutType() == SpParamInOutType.OUT_TYPE) {
                    switch (spParamAs400.getParamInSpecType()){
                        case JDBC_VARCHAR:
                            stm.registerOutParameter((int) spParamAs400.getId(), Types.VARCHAR);
                            break;
                        case JDBC_INTEGER:
                            stm.registerOutParameter((int) spParamAs400.getId(),Types.INTEGER);
                            break;
                        case JDBC_DECIMAL:
                            stm.registerOutParameter((int) spParamAs400.getId(),Types.DECIMAL);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + spParamAs400.getParamInSpecType());
                    }
                    //Param_IN_OUT_TYPE
                } else if (spParamAs400.getParamInOutType() == SpParamInOutType.IN_OUT_TYPE) {
                    switch (spParamAs400.getParamInSpecType()) {
                        case JDBC_VARCHAR:
                            stm.setString((int) spParamAs400.getId(), (String) spParamAs400.getParamContent());
                            stm.registerOutParameter((int) spParamAs400.getId(), Types.VARCHAR);
                            break;
                        case JDBC_DECIMAL:
                            stm.setBigDecimal((int) spParamAs400.getId(), (BigDecimal) spParamAs400.getParamContent());
                            stm.registerOutParameter((int) spParamAs400.getId(),Types.DECIMAL);
                            break;
                        case JDBC_DOUBLE:
                            stm.setDouble((int) spParamAs400.getId(), (Double) spParamAs400.getParamContent());
                            stm.registerOutParameter((int) spParamAs400.getId(),Types.DOUBLE);
                            break;
                        case JDBC_INTEGER:
                            stm.setInt((int) spParamAs400.getId(), (Integer) spParamAs400.getParamContent());
                            stm.registerOutParameter((int) spParamAs400.getId(),Types.INTEGER);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + spParamAs400.getParamInSpecType());
                    }
                   //Param ERROR (Not Belong to IN/OUT/IN_OUT)
                } else {
                    throw new IllegalStateException("Unexpected value: " + spParamAs400.toString());
                }
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
        return stm;
    }

}

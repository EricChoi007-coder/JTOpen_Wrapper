package com.scl.as400wrapper.controller;

import com.ibm.as400.access.*;
import com.scl.as400wrapper.domain.StoreProcedureRequest;
import com.scl.as400wrapper.service.connectionService.as400ConnectionService.IAS400ConnectionBuilder;
import com.scl.as400wrapper.service.spResultParamService.IResultParamService;
import com.scl.as400wrapper.service.resultSetService.IResultSetService;
import com.scl.as400wrapper.service.spService.ISpService;
import com.scl.as400wrapper.utilis.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("as400/store-procedure")
public class AS400StoreProcedureController {

    @Autowired
    private ISpService spService;

    @Autowired
    private IResultSetService resultService;

    @Autowired
    private IResultParamService resultParamService;

    @Autowired
    private IAS400ConnectionBuilder aS400ConnectionBuilder;

    @PostMapping("no-param-no-return")
    //@RequestBody is a must for JSON Format
    public JsonData ProcedureCallWithoutParam(@RequestBody StoreProcedureRequest storeProcedureRequest) {
        //get initial params from input params
        String server = storeProcedureRequest.getSpUrl();
        String user = storeProcedureRequest.getUserName();
        String pass = storeProcedureRequest.getUserPassword();
        String sp = storeProcedureRequest.getSpContent();

        AS400 as400 = null;
        AS400JDBCDriver driver = null;
        AS400JDBCConnection con = null;
        AS400JDBCCallableStatement stm = null;
        AS400JDBCResultSet rs = null;


        try {
            driver = new AS400JDBCDriver();
            as400 = aS400ConnectionBuilder.getFromConnectionPool(server, user, pass);

            // Connect to the Database
            con = AS400JDBCConnection.class.cast(driver.connect(as400));

            // Prepare the call
            stm = AS400JDBCCallableStatement.class.cast(con.prepareCall(sp));

            // Execute the Stored Procedure
            rs = AS400JDBCResultSet.class.cast(stm.executeQuery());

            //format JDBC result to Map<String,Object>
            List<Map<String, Object>> resultList = new ArrayList<>();
            while (rs.next()) {
                int rowSize = rs.getMetaData().getColumnCount();
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= rowSize; i++) {
                    String labelName = rs.getMetaData().getColumnName(i);
                    Object obj = rs.getObject(labelName);
                    map.put(labelName, obj);
                }
                resultList.add(map);
            }
            return JsonData.buildSuccess(resultList);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonData.buildError(e.getMessage());
        } finally {
//            try {
//                // Make sure to disconnect
//                as400.disconnectAllServices();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    @PostMapping("param-one-result-set")
    //@RequestBody is a must for JSON Format
    public JsonData ProcedureCallWithParamAndOneResultSet(@RequestBody StoreProcedureRequest storeProcedureRequest) {
        //get initial params from input params
        String server = storeProcedureRequest.getSpUrl();
        String user = storeProcedureRequest.getUserName();
        String pass = storeProcedureRequest.getUserPassword();
        String sp = storeProcedureRequest.getSpContent();

        AS400 as400 = null;
        AS400JDBCDriver driver = null;
        AS400JDBCConnection con = null;
        AS400JDBCCallableStatement stm = null;
        AS400JDBCResultSet rs = null;


        try {
            driver = new AS400JDBCDriver();
            as400 = aS400ConnectionBuilder.getFromConnectionPool(server, user, pass);

            // Connect to the Database
            con = AS400JDBCConnection.class.cast(driver.connect(as400));

            // Prepare the call
            stm = AS400JDBCCallableStatement.class.cast(con.prepareCall(sp));
            //stm Setting Sting/Decimal
            spService.setStatement(stm,storeProcedureRequest.getParamAs400List());


            // Execute the Stored Procedure
            // just 1 result-set return
            AS400JDBCResultSet.class.cast(stm.executeQuery());

            //format stm excute Result & Return
            return JsonData.buildSuccess( resultService.getSingleResultMapReturn(stm,storeProcedureRequest.getParamAs400List()) );

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//                // Make sure to disconnect
//                as400.disconnectAllServices();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        return JsonData.buildSuccess("Procedure Call Failure");
    }


    @PostMapping("param-multi-result-set")
    //@RequestBody is a must for JSON Format
    public JsonData ProcedureCallWithParamAndMultiResultSet(@RequestBody StoreProcedureRequest storeProcedureRequest) {
        //get initial params from input params
        String server = storeProcedureRequest.getSpUrl();
        String user = storeProcedureRequest.getUserName();
        String pass = storeProcedureRequest.getUserPassword();
        String sp = storeProcedureRequest.getSpContent();

        AS400 as400 = null;
        AS400JDBCDriver driver = null;
        AS400JDBCConnection con = null;
        AS400JDBCCallableStatement stm = null;
        AS400JDBCResultSet rs = null;

        try {
            driver = new AS400JDBCDriver();
            as400 = aS400ConnectionBuilder.getFromConnectionPool(server, user, pass);

            // Connect to the Database
            con = AS400JDBCConnection.class.cast(driver.connect(as400));

            // Prepare the call
            stm = AS400JDBCCallableStatement.class.cast(con.prepareCall(sp));
            //stm Setting Sting/Decimal
            spService.setStatement(stm,storeProcedureRequest.getParamAs400List());


            // Execute the Stored Procedure
            // just 1 result-set return
            AS400JDBCResultSet.class.cast(stm.execute());

            //format stm excute Result & MULTI-Return
            return JsonData.buildSuccess( resultService.getMultiResultMapReturn(stm,storeProcedureRequest.getParamAs400List()) );


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//                // Make sure to disconnect
//                as400.disconnectAllServices();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        return JsonData.buildSuccess("Procedure Call Failure");
    }

    @PostMapping("param-in-param-out")
    //@RequestBody is a must for JSON Format
    public JsonData ProcedureCallWithParamAndParamBack(@RequestBody StoreProcedureRequest storeProcedureRequest) {
        //get initial params from input params
        String server = storeProcedureRequest.getSpUrl();
        String user = storeProcedureRequest.getUserName();
        String pass = storeProcedureRequest.getUserPassword();
        String sp = storeProcedureRequest.getSpContent();

        AS400 as400 = null;
        AS400JDBCDriver driver = null;
        AS400JDBCConnection con = null;
        AS400JDBCCallableStatement stm = null;
        AS400JDBCResultSet rs = null;


        try {
            driver = new AS400JDBCDriver();
            as400 = aS400ConnectionBuilder.getFromConnectionPool(server, user, pass);

            // Connect to the Database
            con = AS400JDBCConnection.class.cast(driver.connect(as400));

            // Prepare the call
            stm = AS400JDBCCallableStatement.class.cast(con.prepareCall(sp));
            //stm Setting Sting/Decimal
            spService.setStatement(stm,storeProcedureRequest.getParamAs400List());


            // Execute the Stored Procedure
            // just 1 result-set return in
            AS400JDBCResultSet.class.cast(stm.execute());

            //format stm excute Result & Return
            return JsonData.buildSuccess( resultParamService.getParamResultAndFormat(stm,storeProcedureRequest.getParamAs400List()));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//                // Make sure to disconnect
//                as400.disconnectAllServices();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        return JsonData.buildSuccess("Procedure Call Failure");
    }
}

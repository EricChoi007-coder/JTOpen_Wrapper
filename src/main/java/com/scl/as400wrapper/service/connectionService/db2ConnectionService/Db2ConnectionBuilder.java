package com.scl.as400wrapper.service.connectionService.db2ConnectionService;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class Db2ConnectionBuilder implements IDb2ConnectionBuilder{

    public  Connection getDb2Connection(String url, String usr, String pwd) throws SQLException, ClassNotFoundException {
        Connection con = null;

            // Register AS400 Client
            java.sql.DriverManager.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
            con= DriverManager.getConnection(url,usr,pwd);
            //Test Connection
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            System.out.println("Connected.");

        return con;
    }
}

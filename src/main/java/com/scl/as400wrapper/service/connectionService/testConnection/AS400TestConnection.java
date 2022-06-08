package com.scl.as400wrapper.service.connectionService.testConnection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.sql.Connection;
import java.sql.DriverManager;

@PropertySource({"classpath:application.properties"})
public class AS400TestConnection {

    @Value("${test.url}")
    private static String serverUrl ;   //Format: jdbc:as400://172.16.3.168
    @Value("${test.usr}")
    private static String userName ;
    @Value("${test.pwd}")
    private static String userPwd ;

    public static Connection getAS400Connection() {
        Connection con = null;
        try {
            // Register AS400 Client
            java.sql.DriverManager.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
            con= DriverManager.getConnection(serverUrl,userName,userPwd);
            //Test Connection
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            System.out.println("Connected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}

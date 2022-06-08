package com.scl.as400wrapper.service.connectionService.db2ConnectionService;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDb2ConnectionBuilder {
    Connection getDb2Connection(String url, String usr, String pwd) throws SQLException, ClassNotFoundException;
}

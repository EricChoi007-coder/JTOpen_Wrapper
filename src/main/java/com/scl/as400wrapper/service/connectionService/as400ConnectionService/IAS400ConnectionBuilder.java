package com.scl.as400wrapper.service.connectionService.as400ConnectionService;

import com.ibm.as400.access.AS400;

import java.util.concurrent.ExecutionException;

public interface IAS400ConnectionBuilder {
    AS400 getFromConnectionPool(String as400Url, String usr, String pwd) throws ExecutionException;
}

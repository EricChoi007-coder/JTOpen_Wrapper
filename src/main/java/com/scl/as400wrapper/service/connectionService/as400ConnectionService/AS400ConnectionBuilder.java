package com.scl.as400wrapper.service.connectionService.as400ConnectionService;

import com.ibm.as400.access.AS400;
import com.scl.as400wrapper.utilis.cacheUtils.BaseCache;
import com.scl.as400wrapper.utilis.cacheUtils.CacheKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class AS400ConnectionBuilder implements IAS400ConnectionBuilder {

    //AS400 Client Instance Cache
    @Autowired
    private BaseCache baseCache;
    @Autowired
    private CacheKeyGenerator cacheKeyGenerator;

    public AS400 buildConnection(String as400Url, String usr, String pwd){
        AS400 system = new AS400(as400Url ,usr, pwd);
        return system;
    }

    public AS400 getFromConnectionPool(String as400Url, String usr, String pwd) throws ExecutionException {
        //Ver_2 Cache
        //Get Unique key by CommandRequest
        String cacheKey = cacheKeyGenerator.generateCacheKey(as400Url ,usr, pwd);
        AS400 system = baseCache.getAS400Cache().get(cacheKey,()->{
            //if no key in Guava Cache, Create new AS400 instance and put in Guava Cache and return new created instance
            AS400 newCreatedSystem = new AS400(as400Url ,usr, pwd);
            // Guava will put cacheKey:Value into Cache Automatically
            // baseCache.getAS400Cache().put(cacheKey,newSystem);
            return newCreatedSystem;
        });

        return system;
    }
}

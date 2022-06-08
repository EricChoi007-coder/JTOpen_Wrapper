package com.scl.as400wrapper.utilis.cacheUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ibm.as400.access.AS400;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class BaseCache {

    private Cache<String, AS400> tenMinuteCache = CacheBuilder.newBuilder()
            .initialCapacity(10)   //set Cache Initial size
            .maximumSize(200)    //set Cache Max size
            .concurrencyLevel(5)   //併發數
            .expireAfterAccess(600, TimeUnit.SECONDS) //Expire Time
            .recordStats()
            .build();

    public Cache<String, AS400> getAS400Cache(){
        return tenMinuteCache;
    }

    public void setAS400Cache(Cache<String,AS400> tenMinuteCache){
        this.tenMinuteCache = tenMinuteCache;
    }



}


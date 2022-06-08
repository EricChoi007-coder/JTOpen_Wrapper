package com.scl.as400wrapper.utilis.cacheUtils;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class CacheKeyGenerator {

    public static String generateCacheKey(String url, String usr, String pwd){
        return "AS400_INSTANCE:"+url+":"+usr+":"+"pwd";
    }

}

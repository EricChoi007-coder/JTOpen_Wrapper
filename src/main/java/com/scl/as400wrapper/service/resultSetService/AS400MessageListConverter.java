package com.scl.as400wrapper.service.resultSetService;

import com.ibm.as400.access.AS400Message;

import java.util.HashMap;
import java.util.Map;

public class AS400MessageListConverter implements IAS400MessageListConverter{

    public Map<String, Object> ConvertToMap(AS400Message[] aS400Messages) throws IllegalArgumentException{

        String ErrorInfo = "";

        Map<String, Object> resultMap = new HashMap<>();  //Result_Map
        for (int i = 0; i < aS400Messages.length; i++) {
          //  System.out.println(aS400Messages[i].getText());
            try{
                // Map<String, Object>: Key: (String)index, value: Return value
                resultMap.put(""+i,aS400Messages[i].getText());

            }catch (Exception e) {
                ErrorInfo.concat(aS400Messages[i].getText()+"_");
            }
        }
        //do Exception downgrade if exist
        if(ErrorInfo == ""){
            return resultMap;
        }else {
            throw new IllegalArgumentException(ErrorInfo);
        }
    }

}

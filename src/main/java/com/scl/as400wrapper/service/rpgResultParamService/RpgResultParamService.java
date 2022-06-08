package com.scl.as400wrapper.service.rpgResultParamService;

import com.ibm.as400.access.AS400DecFloat;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.ProgramParameter;
import com.scl.as400wrapper.domain.rpgParams.RpgParamAs400;
import com.scl.as400wrapper.domain.rpgParams.RpgParamInOutType;
import com.scl.as400wrapper.domain.spParams.SpParamInOutType;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RpgResultParamService implements  IRpgResultParamService{

    public Map<String, Object>  getResultParamReturnList(ProgramParameter[] parmlist, List<RpgParamAs400> rpgParamAs400List){

        Map<String, Object> resultMapDictionaries = new HashMap<>();

        for(RpgParamAs400 rpgParamAs400 : rpgParamAs400List){
            if(rpgParamAs400.getRpgParamInOutType() == RpgParamInOutType.OUT_TYPE || rpgParamAs400.getRpgParamInOutType() == RpgParamInOutType.IN_OUT_TYPE){
                switch (rpgParamAs400.getRpgParamInSpecType()) {
                    case RPG_TEXT:
                        AS400Text asText = new AS400Text(rpgParamAs400.getParamContentLength());
                        resultMapDictionaries.put(rpgParamAs400.getId().toString()+"_"+rpgParamAs400.getDescription(), (String)asText.toObject(parmlist[(int) rpgParamAs400.getId()].getOutputData()));
                        break;
                    case RPG_DECIMAL:
                        AS400DecFloat aS400DecFloat = new AS400DecFloat(rpgParamAs400.getParamContentLength());
                        resultMapDictionaries.put( rpgParamAs400.getId().toString()+"_"+rpgParamAs400.getDescription(),aS400DecFloat.toDouble(parmlist[(int) rpgParamAs400.getId()].getOutputData()));
                        break;
                    case RPG_RETURN:
                        resultMapDictionaries.put(rpgParamAs400.getId().toString()+"_"+rpgParamAs400.getDescription(), parmlist[(int) rpgParamAs400.getId()].getOutputData());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + rpgParamAs400.getRpgParamInSpecType());
                }

            }

        }
        return resultMapDictionaries;
    }
}

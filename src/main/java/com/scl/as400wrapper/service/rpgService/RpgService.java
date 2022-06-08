package com.scl.as400wrapper.service.rpgService;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.AS400ZonedDecimal;
import com.ibm.as400.access.ProgramParameter;
import com.scl.as400wrapper.domain.rpgParams.RpgParamAs400;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RpgService implements IRpgService{

    public ProgramParameter[] setProgramParameters(ProgramParameter[] parameters, List<RpgParamAs400> rpgParamAs400List, AS400 as400){

        for (RpgParamAs400 rpgParamAs400 : rpgParamAs400List){
            switch (rpgParamAs400.getRpgParamInSpecType()) {
                case RPG_DECIMAL:
                    AS400ZonedDecimal as400ZonedDecimal = new AS400ZonedDecimal(Integer.parseInt(rpgParamAs400.getDecimalRange().split("\\|")[0]),Integer.parseInt(rpgParamAs400.getDecimalRange().split("\\|")[1]) );
                    double myAge = (Double) rpgParamAs400.getParamContent();
                    parameters[rpgParamAs400.getId()] = new ProgramParameter(as400ZonedDecimal.toBytes(myAge),rpgParamAs400.getParamContentLength());
                    break;
                case RPG_TEXT:
                    AS400Text text = new AS400Text(rpgParamAs400.getParamContentLength(), as400);
                    byte[] statusFormat = text.toBytes(rpgParamAs400.getParamContent());
                    parameters[rpgParamAs400.getId()] = new ProgramParameter(statusFormat,rpgParamAs400.getParamContentLength());
                    break;
                case RPG_RETURN:
                    parameters[rpgParamAs400.getId()] = new ProgramParameter(rpgParamAs400.getParamContentLength());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + rpgParamAs400.getRpgParamInSpecType());
            }
        }
       return parameters;
    }
}

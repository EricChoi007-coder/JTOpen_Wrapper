package com.scl.as400wrapper.service.rpgResultParamService;

import com.ibm.as400.access.ProgramParameter;
import com.scl.as400wrapper.domain.rpgParams.RpgParamAs400;

import java.util.List;
import java.util.Map;

public interface IRpgResultParamService {
    Map<String, Object> getResultParamReturnList(ProgramParameter[] parmlist, List<RpgParamAs400> rpgParamAs400List);
}

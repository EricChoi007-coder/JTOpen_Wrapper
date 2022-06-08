package com.scl.as400wrapper.service.rpgService;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;
import com.scl.as400wrapper.domain.rpgParams.RpgParamAs400;

import java.util.List;

public interface IRpgService {
    ProgramParameter[] setProgramParameters(ProgramParameter[] parameters, List<RpgParamAs400> rpgParamAs400List, AS400 as400);
}

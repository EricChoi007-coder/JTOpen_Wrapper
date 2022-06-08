package com.scl.as400wrapper.controller;

import com.ibm.as400.access.*;
import com.scl.as400wrapper.domain.RPGRequest;
import com.scl.as400wrapper.service.connectionService.as400ConnectionService.IAS400ConnectionBuilder;
import com.scl.as400wrapper.service.rpgResultParamService.IRpgResultParamService;
import com.scl.as400wrapper.service.rpgService.IRpgService;
import com.scl.as400wrapper.service.resultSetService.AS400MessageListConverter;
import com.scl.as400wrapper.utilis.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("as400/rpg")
public class AS400RPGCallController {

@Autowired
    private IRpgService rpgService;

@Autowired
    private IRpgResultParamService rpgResultParamService;

@Autowired
    private IAS400ConnectionBuilder aS400ConnectionBuilder;

    @PostMapping("without-pre-cmd-resultset-out")
    //@RequestBody is a must for JSON Format
    public JsonData getAS400ProcedureWithoutPreCmd(@RequestBody RPGRequest rpgRequest) throws ExecutionException {
        //RPG Request Validation

        // Ver_1 Create an AS400 object for the server that contains the program.
       // AS400 as400 = new AS400(rpgRequest.getRpgUrl(), rpgRequest.getUserName(), rpgRequest.getUserPassword());

        //Ver_2 Get From Cache
        AS400 as400 = aS400ConnectionBuilder.getFromConnectionPool(rpgRequest.getRpgUrl(), rpgRequest.getUserName(), rpgRequest.getUserPassword());

        // Get the path to the program From Request Body
        String rpgDestination = rpgRequest.getRpgDestination();

        //split the rpgDestination "Lib|Func_name|PGM" => Lib + Func_name + PGM format
        String[] rpgDestinationList = rpgDestination.split("\\|");
        QSYSObjectPathName programName = new QSYSObjectPathName(rpgDestinationList[0], rpgDestinationList[1], rpgDestinationList[2]);

        // Create the program call object.  Associate the object with the AS400 object that represents the server we get status from.
        ProgramCall getSystemStatus = new ProgramCall(as400);
        try {
            // Create the program parameter list.
            // parameters that will be added to this list.
            //get paramList length in order to create the paramList
            int length = rpgRequest.getRpgParamAs400List().size();
            ProgramParameter[] parmlist = new ProgramParameter[length];

            //set paramList
            parmlist = rpgService.setProgramParameters(parmlist,rpgRequest.getRpgParamAs400List(),as400);

//            //PARAM STANDARD & SAMPLE
//            // The server program returns data in parameter 1.  It is an output
//            // parameter.  Allocate 64 bytes for this parameter.
//
//            parmlist[0] = new ProgramParameter(64);
//            // Parameter 2 is the buffer size of parm 1.  It is a numeric input
//            // parameter.  Sets its value to 64, convert it to the server format,
//            // then add the parm to the parm list.
//
//            AS400Bin4 bin4 = new AS400Bin4();
//            Integer iStatusLength = new Integer(64);
//            byte[] statusLength = bin4.toBytes(iStatusLength);
//            parmlist[1] = new ProgramParameter(statusLength);
//
//
//            // Parameter 3 is the status-format parameter.  It is a string input
//            // parameter.  Set the string value, convert it to the server format,
//            // then add the parameter to the parm list.
//
//            AS400Text text1 = new AS400Text(8, as400);
//            byte[] statusFormat = text1.toBytes("SSTS0200");
//            parmlist[2] = new ProgramParameter(statusFormat);
//
//
//            // Parameter 4 is the reset-statistics parameter.  It is a string input
//            // parameter.  Set the string value, convert it to the server format,
//            // then add the parameter to the parm list.
//
//            AS400Text text3 = new AS400Text(10, as400);
//            byte[] resetStats = text3.toBytes("*NO       ");
//            parmlist[3] = new ProgramParameter(resetStats);
//
//
//            // Parameter 5 is the error info parameter.  It is an input/output
//            // parameter.  Add it to the parm list.
//
//            byte[] errorInfo = new byte[32];
//            parmlist[4] = new ProgramParameter(errorInfo, 0);

            // Set the program to call and the parameter list to the program
            // call object.

            getSystemStatus.setProgram(programName.getPath(), parmlist);

            getSystemStatus.run();

            // Run the program 2 times to make the more accurate result (recommended)

            if (getSystemStatus.run() == true) {

                // If the program did not run get the list of error messages
                // from the program object and display the messages.  The error
                // would be something like program-not-found or not-authorized
                // to the program.

                AS400Message[] msgList = getSystemStatus.getMessageList();
                //Convert Output to ResultMap<String,Object>
                AS400MessageListConverter aS400MessageListConverter = new AS400MessageListConverter();
                //Convert AS400Message[] => Map<String,Object>
                Map<String, Object> resultMap = aS400MessageListConverter.ConvertToMap(msgList);
                return JsonData.buildSuccess(resultMap);
//                System.out.println("The program did not run.  Server messages:");

            } else {
                                //Error Info if equals "" at the end , return error info, else return response data from DB2/AS400
                String ErrorInfo = "";
                AS400Message[] msgList = getSystemStatus.getMessageList();
                Map<String, Object> resultMap = new HashMap<>();  //Result_Map
                for (int i = 0; i < msgList.length; i++) {
                    System.out.println(msgList[i].getText());
                    resultMap.put(i+"",msgList[i].getText());
                    ErrorInfo.concat(msgList[i].getText()+"_");
                }

//                 This program is done running program so disconnect from
//                 the command server on the server.  Program call and command
//                 call use the same server on the server.

              return JsonData.buildError("Error_Info:"+ErrorInfo);
            }
        } catch (Exception e) {
            // If any of the above operations failed say the program failed
            // and output the exception.
            System.out.println("Program call failed");
            System.out.println(e);
            return JsonData.buildError(e.getMessage());
        }finally {
          //  as400.disconnectService(AS400.COMMAND);
        }
    }

    @PostMapping("pre-cmd-in-result-resultset-out")
    //@RequestBody is a must for JSON Format
    public JsonData getAS400ProcedureWithPreCmd(@RequestBody RPGRequest rpgRequest) {
        //RPG Request Validation
        // Create an AS400 object for the server that contains the program.
        AS400 as400 = new AS400(rpgRequest.getRpgUrl(), rpgRequest.getUserName(), rpgRequest.getUserPassword());
        try {
            // Get the path to the program From Request Body
            String rpgDestination = rpgRequest.getRpgDestination();

            //split the rpgDestination "Lib|Func_name|PGM" => Lib + Func_name + PGM format
            String[] rpgDestinationList = rpgDestination.split("\\|");
            QSYSObjectPathName programName = new QSYSObjectPathName(rpgDestinationList[0], rpgDestinationList[1], rpgDestinationList[2]);

            //process pre-CMD call---------------------------------
            CommandCall command = new CommandCall(as400);
            try {
                // Run the command
                if (command.run(rpgRequest.getCmdContent()) != true) {
                    // Note that there was an error.
                    System.out.println("Command failed!");
                    return JsonData.buildError("CommandCall_Run_Failure_Cmd"+rpgRequest.getCmdContent());
                }
            }catch (Exception e){
                return JsonData.buildError("CommandCall_Failure_ErrMsg:"+e.getMessage());
            }

            //Process Program Call----------------------------------
            // Create the program call object.  Associate the object with the AS400 object that represents the server we get status from.
            ProgramCall getSystemStatus = new ProgramCall(as400);

            // Create the program parameter list.
            // parameters that will be added to this list.
            //get paramList length in order to create the paramList
            int length = rpgRequest.getRpgParamAs400List().size();
            ProgramParameter[] parmlist = new ProgramParameter[length];

            //set paramList
            parmlist = rpgService.setProgramParameters(parmlist,rpgRequest.getRpgParamAs400List(),as400);
//            //PARAM STANDARD & SAMPLE
//            // The server program returns data in parameter 1.  It is an output
//            // parameter.  Allocate 64 bytes for this parameter.
//
//            parmlist[0] = new ProgramParameter(64);
//            // Parameter 2 is the buffer size of parm 1.  It is a numeric input
//            // parameter.  Sets its value to 64, convert it to the server format,
//            // then add the parm to the parm list.
//
//            AS400Bin4 bin4 = new AS400Bin4();
//            Integer iStatusLength = new Integer(64);
//            byte[] statusLength = bin4.toBytes(iStatusLength);
//            parmlist[1] = new ProgramParameter(statusLength);
//
//
//            // Parameter 3 is the status-format parameter.  It is a string input
//            // parameter.  Set the string value, convert it to the server format,
//            // then add the parameter to the parm list.
//
//            AS400Text text1 = new AS400Text(8, as400);
//            byte[] statusFormat = text1.toBytes("SSTS0200");
//            parmlist[2] = new ProgramParameter(statusFormat);
//
//
//            // Parameter 4 is the reset-statistics parameter.  It is a string input
//            // parameter.  Set the string value, convert it to the server format,
//            // then add the parameter to the parm list.
//
//            AS400Text text3 = new AS400Text(10, as400);
//            byte[] resetStats = text3.toBytes("*NO       ");
//            parmlist[3] = new ProgramParameter(resetStats);
//
//
//            // Parameter 5 is the error info parameter.  It is an input/output
//            // parameter.  Add it to the parm list.
//
//            byte[] errorInfo = new byte[32];
//            parmlist[4] = new ProgramParameter(errorInfo, 0);

            // Set the program to call and the parameter list to the program
            // call object.
            getSystemStatus.setProgram(programName.getPath(), parmlist);

            getSystemStatus.run();

            // Run the program 2 times to make the more accurate result (recommended)

            if (getSystemStatus.run() == true) {

                // If the program did not run get the list of error messages
                // from the program object and display the messages.  The error
                // would be something like program-not-found or not-authorized
                // to the program.

                AS400Message[] msgList = getSystemStatus.getMessageList();
                //Convert Output to ResultMap<String,Object>
                AS400MessageListConverter aS400MessageListConverter = new AS400MessageListConverter();
                //Convert AS400Message[] => Map<String,Object>
                Map<String, Object> resultMap = aS400MessageListConverter.ConvertToMap(msgList);
                return JsonData.buildSuccess(resultMap);
//                System.out.println("The program did not run.  Server messages:");

            } else {
                //Error Info if equals "" at the end , return error info, else return response data from DB2/AS400
                String ErrorInfo = "";
                AS400Message[] msgList = getSystemStatus.getMessageList();
                Map<String, Object> resultMap = new HashMap<>();  //Result_Map
                for (int i = 0; i < msgList.length; i++) {
                    System.out.println(msgList[i].getText());
                    resultMap.put(i+"",msgList[i].getText());
                    ErrorInfo.concat(msgList[i].getText()+"_");
                }

//                 This program is done running program so disconnect from
//                 the command server on the server.  Program call and command
//                 call use the same server on the server.

                return JsonData.buildError("Error_Info:"+ErrorInfo);
            }
        } catch (Exception e) {
            // If any of the above operations failed say the program failed
            // and output the exception.
            System.out.println("Program call failed");
            System.out.println(e);
            return JsonData.buildError(e.getMessage());
        }finally {
          //  as400.disconnectService(AS400.COMMAND);
        }
    }

    @PostMapping("without-pre-cmd-paramset-out")
    //@RequestBody is a must for JSON Format
    public JsonData getAS400ProcedureWithoutPreCmdAndParamOut(@RequestBody RPGRequest rpgRequest) {
        //RPG Request Validation
        // Create an AS400 object for the server that contains the program.
        AS400 as400 = new AS400(rpgRequest.getRpgUrl(), rpgRequest.getUserName(), rpgRequest.getUserPassword());

        // Get the path to the program From Request Body
        String rpgDestination = rpgRequest.getRpgDestination();

        //split the rpgDestination "Lib|Func_name|PGM" => Lib + Func_name + PGM format
        String[] rpgDestinationList = rpgDestination.split("\\|");
        QSYSObjectPathName programName = new QSYSObjectPathName(rpgDestinationList[0], rpgDestinationList[1], rpgDestinationList[2]);

        // Create the program call object.  Associate the object with the AS400 object that represents the server we get status from.
        ProgramCall getSystemStatus = new ProgramCall(as400);
        try {
            // Create the program parameter list.
            // parameters that will be added to this list.
            //get paramList length in order to create the paramList
            int length = rpgRequest.getRpgParamAs400List().size();
            ProgramParameter[] parmlist = new ProgramParameter[length];

            //set paramList
            parmlist = rpgService.setProgramParameters(parmlist,rpgRequest.getRpgParamAs400List(),as400);

//            //PARAM STANDARD & SAMPLE
//            // The server program returns data in parameter 1.  It is an output
//            // parameter.  Allocate 64 bytes for this parameter.
//
//            parmlist[0] = new ProgramParameter(64);
//            // Parameter 2 is the buffer size of parm 1.  It is a numeric input
//            // parameter.  Sets its value to 64, convert it to the server format,
//            // then add the parm to the parm list.
//
//            AS400Bin4 bin4 = new AS400Bin4();
//            Integer iStatusLength = new Integer(64);
//            byte[] statusLength = bin4.toBytes(iStatusLength);
//            parmlist[1] = new ProgramParameter(statusLength);
//
//
//            // Parameter 3 is the status-format parameter.  It is a string input
//            // parameter.  Set the string value, convert it to the server format,
//            // then add the parameter to the parm list.
//
//            AS400Text text1 = new AS400Text(8, as400);
//            byte[] statusFormat = text1.toBytes("SSTS0200");
//            parmlist[2] = new ProgramParameter(statusFormat);
//
//
//            // Parameter 4 is the reset-statistics parameter.  It is a string input
//            // parameter.  Set the string value, convert it to the server format,
//            // then add the parameter to the parm list.
//
//            AS400Text text3 = new AS400Text(10, as400);
//            byte[] resetStats = text3.toBytes("*NO       ");
//            parmlist[3] = new ProgramParameter(resetStats);
//
//
//            // Parameter 5 is the error info parameter.  It is an input/output
//            // parameter.  Add it to the parm list.
//
//            byte[] errorInfo = new byte[32];
//            parmlist[4] = new ProgramParameter(errorInfo, 0);

            // Set the program to call and the parameter list to the program
            // call object.

            getSystemStatus.setProgram(programName.getPath(), parmlist);

            getSystemStatus.run();

            // Run the program 2 times to make the more accurate result (recommended)

            if (getSystemStatus.run() == true) {
                // If the program did not run get the list of error messages
                // from the program object and display the messages.  The error
                // would be something like program-not-found or not-authorized
                // to the program.

                Map<String, Object> resultMap =rpgResultParamService.getResultParamReturnList(parmlist,rpgRequest.getRpgParamAs400List());
                return JsonData.buildSuccess(resultMap);
//                System.out.println("The program did not run.  Server messages:");

            } else {
                //Error Info if equals "" at the end , return error info, else return response data from DB2/AS400
                String ErrorInfo = "";
                AS400Message[] msgList = getSystemStatus.getMessageList();
                Map<String, Object> resultMap = new HashMap<>();  //Result_Map
                for (int i = 0; i < msgList.length; i++) {
                    System.out.println(msgList[i].getText());
                    resultMap.put(i+"",msgList[i].getText());
                    ErrorInfo.concat(msgList[i].getText()+"_");
                }

//                 This program is done running program so disconnect from
//                 the command server on the server.  Program call and command
//                 call use the same server on the server.

                return JsonData.buildError("Error_Info:"+ErrorInfo);
            }
        } catch (Exception e) {
            // If any of the above operations failed say the program failed
            // and output the exception.
            System.out.println("Program call failed");
            System.out.println(e);
            return JsonData.buildError(e.getMessage());
        }finally {
           // as400.disconnectService(AS400.COMMAND);
        }
    }

    @PostMapping("pre-cmd-paramset-out")
    //@RequestBody is a must for JSON Format
    public JsonData getAS400ProcedureWithPreCmdAndParamOut(@RequestBody RPGRequest rpgRequest) {
        //RPG Request Validation
        // Create an AS400 object for the server that contains the program.
        AS400 as400 = new AS400(rpgRequest.getRpgUrl(), rpgRequest.getUserName(), rpgRequest.getUserPassword());

        // Get the path to the program From Request Body
        String rpgDestination = rpgRequest.getRpgDestination();

        //split the rpgDestination "Lib|Func_name|PGM" => Lib + Func_name + PGM format
        String[] rpgDestinationList = rpgDestination.split("\\|");
        QSYSObjectPathName programName = new QSYSObjectPathName(rpgDestinationList[0], rpgDestinationList[1], rpgDestinationList[2]);

        //CommandCall Part
        CommandCall command = new CommandCall(as400);
        try {
            // Run the command
            if (command.run(rpgRequest.getCmdContent()) != true) {
                // Note that there was an error.
                System.out.println("Command failed!");
                return JsonData.buildError("CommandCall_Run_Failure_Cmd"+rpgRequest.getCmdContent());
            }
        }catch (Exception e){
            return JsonData.buildError("CommandCall_Failure_ErrMsg:"+e.getMessage());
        }

        // Create the program call object.  Associate the object with the AS400 object that represents the server we get status from.
        ProgramCall getSystemStatus = new ProgramCall(as400);
        try {
            // Create the program parameter list.
            // parameters that will be added to this list.
            //get paramList length in order to create the paramList
            int length = rpgRequest.getRpgParamAs400List().size();
            ProgramParameter[] parmlist = new ProgramParameter[length];

            //set paramList
            parmlist = rpgService.setProgramParameters(parmlist,rpgRequest.getRpgParamAs400List(),as400);

//            //PARAM STANDARD & SAMPLE
//            // The server program returns data in parameter 1.  It is an output
//            // parameter.  Allocate 64 bytes for this parameter.
//
//            parmlist[0] = new ProgramParameter(64);
//            // Parameter 2 is the buffer size of parm 1.  It is a numeric input
//            // parameter.  Sets its value to 64, convert it to the server format,
//            // then add the parm to the parm list.
//
//            AS400Bin4 bin4 = new AS400Bin4();
//            Integer iStatusLength = new Integer(64);
//            byte[] statusLength = bin4.toBytes(iStatusLength);
//            parmlist[1] = new ProgramParameter(statusLength);
//
//
//            // Parameter 3 is the status-format parameter.  It is a string input
//            // parameter.  Set the string value, convert it to the server format,
//            // then add the parameter to the parm list.
//
//            AS400Text text1 = new AS400Text(8, as400);
//            byte[] statusFormat = text1.toBytes("SSTS0200");
//            parmlist[2] = new ProgramParameter(statusFormat);
//
//
//            // Parameter 4 is the reset-statistics parameter.  It is a string input
//            // parameter.  Set the string value, convert it to the server format,
//            // then add the parameter to the parm list.
//
//            AS400Text text3 = new AS400Text(10, as400);
//            byte[] resetStats = text3.toBytes("*NO       ");
//            parmlist[3] = new ProgramParameter(resetStats);
//
//
//            // Parameter 5 is the error info parameter.  It is an input/output
//            // parameter.  Add it to the parm list.
//
//            byte[] errorInfo = new byte[32];
//            parmlist[4] = new ProgramParameter(errorInfo, 0);

            // Set the program to call and the parameter list to the program
            // call object.

            getSystemStatus.setProgram(programName.getPath(), parmlist);

            getSystemStatus.run();

            // Run the program 2 times to make the more accurate result (recommended)

            if (getSystemStatus.run() == true) {
                // If the program did not run get the list of error messages
                // from the program object and display the messages.  The error
                // would be something like program-not-found or not-authorized
                // to the program.

                Map<String, Object> resultMap =rpgResultParamService.getResultParamReturnList(parmlist,rpgRequest.getRpgParamAs400List());
                return JsonData.buildSuccess(resultMap);
//                System.out.println("The program did not run.  Server messages:");

            } else {
                //Error Info if equals "" at the end , return error info, else return response data from DB2/AS400
                String ErrorInfo = "";
                AS400Message[] msgList = getSystemStatus.getMessageList();
                Map<String, Object> resultMap = new HashMap<>();  //Result_Map
                for (int i = 0; i < msgList.length; i++) {
                    System.out.println(msgList[i].getText());
                    resultMap.put(i+"",msgList[i].getText());
                    ErrorInfo.concat(msgList[i].getText()+"_");
                }

//                 This program is done running program so disconnect from
//                 the command server on the server.  Program call and command
//                 call use the same server on the server.

                return JsonData.buildError("Error_Info:"+ErrorInfo);
            }
        } catch (Exception e) {
            // If any of the above operations failed say the program failed
            // and output the exception.
            System.out.println("Program call failed");
            System.out.println(e);
            return JsonData.buildError(e.getMessage());
        }finally {
           // as400.disconnectService(AS400.COMMAND);
        }
    }
}

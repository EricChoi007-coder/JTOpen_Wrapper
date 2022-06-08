package com.scl.as400wrapper.controller;

import com.ibm.as400.access.*;
import com.scl.as400wrapper.service.connectionService.testConnection.AS400TestConnection;
import com.scl.as400wrapper.utilis.JsonData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("test")
@PropertySource({"classpath:application.properties"})
public class AS400TestController {

    //Initial AS400 Client Session Instance Params
    @Value("${test.url}")
    private String ServerUrl ;
    @Value("${test.usr}")
    private String UserName;
    @Value("${test.pwd}")
    private String UserPwd ;

    @PostMapping("sql")
    //@RequestBody is a must for JSON Format
    public JsonData SqlCall() {
        String sqlRequestContent = "SELECT * FROM CASDD0PDV2.CFPGT";
        try {
            Connection as400Connection = AS400TestConnection.getAS400Connection();
            Statement statement = as400Connection.createStatement();
            ResultSet rs = statement.executeQuery(sqlRequestContent);
            //pub ResultMap(JDBC) => Format List<Map<String, Object>>
            List<Map<String, Object>> resultList = new ArrayList<>();
            while (rs.next()) {
                int rowSize = rs.getMetaData().getColumnCount();
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= rowSize; i++) {
                    String labelName = rs.getMetaData().getColumnName(i);
                    Object obj = rs.getObject(labelName);
                    map.put(labelName, obj);
                }
                resultList.add(map);
            }
            return JsonData.buildSuccess(resultList);
        } catch (Exception e) {
            return JsonData.buildError(e.getMessage());
        }
    }

    @PostMapping("store-procedure")
    //@RequestBody is a must for JSON Format
    public JsonData StoreProcedureCall()  {

        try {
        Connection con;
        CallableStatement cstmt;
        ResultSet rs;

    AS400JDBCDriver driver = new AS400JDBCDriver();
    AS400 as400 = new AS400(ServerUrl, UserName, UserPwd);
    // Connect to the Database
    con = AS400JDBCConnection.class.cast(driver.connect(as400));
    cstmt = con.prepareCall("CALL DSN8.DSN8ED2");

    cstmt.executeUpdate();            // Call the stored procedure
    return JsonData.buildSuccess(cstmt.getMetaData());

}catch (Throwable e){
    return JsonData.buildError(e.getMessage());
}
        //clse stream link
    }

    @PostMapping("rpg-call")
    public JsonData RpgCall(){
        //Can Set Global Return Message (Success Msg & Error Msg & Exception Msg) here to return
        String message = "";

        //Connect to the iSeries using hostname, userid and password

        AS400 as400System = new AS400(ServerUrl, UserName, UserPwd);

        //The ProgramCall class allows a user to call an iSeries server program,
        //pass parameters to it (input and output), and access data returned in the
        //output parameters after the program runs. Use ProgramCall to call programs.
        ProgramCall program = new ProgramCall(as400System);

        try
        {
            // Initialize the name of the program to run.
           // String programName = "/QSYS.LIB/{your_library}.LIB/{RPGLE or CL program name}.PGM";
            String programName = "/VEN13MOD.LIB/VPMKC275.PGM";

            ProgramParameter[] parameterList = new ProgramParameter[1];
            AS400Text text = new AS400Text(40, as400System);
            byte[] statusFormat = text.toBytes("EricTest777777777");
            parameterList[0] = new ProgramParameter(statusFormat,40);

//            // Decimal Paramter
//            AS400ZonedDecimal as400ZonedDecimal = new AS400ZonedDecimal(9,0) ;
//            parameterList[1] = new ProgramParameter(as400ZonedDecimal.toBytes(5763102));  //hard code value

            // Set the program name and parameter list.
            program.setProgram(programName, parameterList);
            // Run the program.
            if (program.run() != true)
            {
                // Report failure.
                System.out.println("Program failed!");
                // Show the messages.
                AS400Message[] messageList = program.getMessageList();
                for (int i = 0; i < messageList.length; ++i)
                {
                    // Show each message.
                    System.out.println(messageList[i].getText());
                    // Load additional message information.
                    messageList[i].load();
                    //Show help text.
                    System.out.println(messageList[i].getHelp());
                }
              return  JsonData.buildError("Program failed:"+messageList); //Return Error Message
            }
            else
            {
                //Success Run
                message = (String) new AS400Text(40, as400System).toObject(parameterList[1].getOutputData());
                JsonData.buildSuccess(message);
            }
        }
        catch (Exception e)
        {
            System.out.println("Program " + program.getProgram() + " issued an exception!");
            e.printStackTrace();
           return JsonData.buildError(e.getMessage());
        }finally {
            // Done with the server.
            as400System.disconnectAllServices();
        }
        return null;
    }

    @PostMapping("command-call")
    public JsonData CommandCall()
    {
        AS400 system = new AS400(ServerUrl, UserName, UserPwd);
        CommandCall command = new CommandCall(system);
        try
        {
            // Run the command "CRTLIB FRED."
            if (command.run("CRTLIB FRED") != true)
            {
                // Note that there was an error.
                System.out.println("Command failed!");
                return JsonData.buildError("CommandCall Failure");
            }

            // Show the messages (returned whether or not there was an error.)
            AS400Message[] messagelist = command.getMessageList();
            for (int i = 0; i < messagelist.length; ++i)
            {
                // Show each message.
                System.out.println(messagelist[i].getText());
            }
           return  JsonData.buildSuccess(messagelist);
        }
        catch (Exception e)
        {
            System.out.println("Command " + command.getCommand() + " issued an exception!");
            e.printStackTrace();
            return JsonData.buildError(e.getMessage());
        }finally {
            // Done with the system.
            system.disconnectService(AS400.COMMAND);
        }
    }

    @PostMapping("service-program-call")
    //@RequestBody is a must for JSON Format
    public JsonData ServiceProgramCall()  {
        // Create a single parameter parameter list.
        ProgramParameter[] parameterList = new ProgramParameter[1];

        // Create the input parameter.  We are sending the number 9 to the service program.
        AS400Bin4 bin4 = new AS400Bin4();
        byte[] parameter = bin4.toBytes(9);
        parameterList[0] = new ProgramParameter(parameter);

        // Construct the system object.  The service program is on this system.
        AS400 system = new AS400(ServerUrl,UserName,UserPwd);

        // Construct the ServiceProgramCall object.
        ServiceProgramCall sPGMCall = new ServiceProgramCall(system);
try {
    // Set the fully qualified service program and the parameter list.
    sPGMCall.setProgram("/QSYS.LIB/MYPGM.LIB/ENTRYPTS.SRVPGM", parameterList);

    // Set the procedure to call in the service program.
    sPGMCall.setProcedureName("int_int");

    // Set the format of returned value.  The program we call returns an integer.
    sPGMCall.setReturnValueFormat(ServiceProgramCall.RETURN_INTEGER);

    // Call the service program.
    if (sPGMCall.run() != true) {
        // Get the error messages when the call fails.
        AS400Message[] messageList = sPGMCall.getMessageList();
        for (int i = 0; i < messageList.length; ++i) {
            System.out.println(messageList[i].getText());
        }
        return JsonData.buildError("ErrorMsg:"+messageList.toString());
    } else {
        // Get the returned value when the call is successful.
        int i = bin4.toInt(sPGMCall.getReturnValue());
        System.out.println("Result is: " + i);
        return JsonData.buildSuccess(i);
    }
}catch(Throwable e){
    e.printStackTrace();
    return JsonData.buildError(e.getMessage());
}finally {
    system.disconnectService(AS400.COMMAND);
}

    }
}

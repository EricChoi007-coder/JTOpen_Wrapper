package com.scl.as400wrapper.controller;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.scl.as400wrapper.domain.CommandRequest;
import com.scl.as400wrapper.service.connectionService.as400ConnectionService.IAS400ConnectionBuilder;
import com.scl.as400wrapper.utilis.JsonData;
import com.scl.as400wrapper.utilis.cacheUtils.BaseCache;
import com.scl.as400wrapper.utilis.cacheUtils.CacheKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("as400/command-call")
public class AS400CommandCallController {

    //AS400 Client Instance Cache
    @Autowired
    private BaseCache baseCache;
    @Autowired
    private CacheKeyGenerator cacheKeyGenerator;

    @Autowired
    private IAS400ConnectionBuilder aS400ConnectionBuilder;

    @PostMapping("no-return")
    public JsonData CommandCallControlWithoutReturn(@RequestBody CommandRequest commandRequest) throws ExecutionException {


        //Ver_1 system created whenver Request call in
        //AS400 system = new AS400(commandRequest.getServerUrl() ,commandRequest.getUserName(), commandRequest.getUserPassword());


        //Ver_2 Cache
        //Get Unique key by CommandRequest
//        String cacheKey = cacheKeyGenerator.generateCacheKey(commandRequest.getServerUrl() ,commandRequest.getUserName(), commandRequest.getUserPassword());
//        AS400 system = baseCache.getAS400Cache().get(cacheKey,()->{
//            //if no key in Guava Cache, Create new AS400 instance and put in Guava Cache and return new created instance
//            AS400 newCreatedSystem = new AS400(commandRequest.getServerUrl() ,commandRequest.getUserName(), commandRequest.getUserPassword());
//            // Guava will put cacheKey:Value into Cache Automatically
//            // baseCache.getAS400Cache().put(cacheKey,newSystem);
//            return newCreatedSystem;
//        });

        //Ver_3 Cache Abstract Spring Contain
        AS400 system = aS400ConnectionBuilder.getFromConnectionPool(commandRequest.getServerUrl() ,commandRequest.getUserName(), commandRequest.getUserPassword());

        //Initial Command Call
        try
        {
           CommandCall command = new CommandCall(system);

            // Run the command
            if (command.run(commandRequest.getCmdContent()) != true)
            {
                // Note that there was an error.
                System.out.println("Command failed!");
                //Failure return with command detail
                return JsonData.buildError("CommandCall Failure:"+commandRequest.getCmdContent());
            }
            //Success return without value
            return JsonData.buildSuccess("Execute_CommandCall_Success:"+commandRequest.getCmdContent());
        }
        catch (Exception e)
        {
            System.out.println("Command:" + commandRequest.getCmdContent() + " issued an exception!");
            e.printStackTrace();
            return JsonData.buildError("CommandCall Failure:"+commandRequest.getCmdContent()+"ERR_MSG:"+e.getMessage());
        }finally {
            // Done with the system.
          //  system.disconnectService(AS400.COMMAND);
        }
}

    @PostMapping("return")
    public JsonData CommandCallControlWithReturn(@RequestBody CommandRequest commandRequest) throws ExecutionException {

        AS400 system = aS400ConnectionBuilder.getFromConnectionPool(commandRequest.getServerUrl() ,commandRequest.getUserName(), commandRequest.getUserPassword());
        //AS400 system = new AS400(commandRequest.getServerUrl() ,commandRequest.getUserName(), commandRequest.getUserPassword());
        CommandCall command = new CommandCall(system);
        try
        {
            // Run the command
            if (command.run(commandRequest.getCmdContent()) != true)
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
            return JsonData.buildSuccess(messagelist);
        }
        catch (Exception e)
        {
            System.out.println("Command " + command.getCommand() + " issued an exception!");
            e.printStackTrace();
           return  JsonData.buildError(e.getMessage());
        }finally {
            // Done with the system.
            //system.disconnectService(AS400.COMMAND);
        }
    }
}

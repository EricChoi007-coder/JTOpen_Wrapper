package com.scl.as400wrapper.controller;

import com.scl.as400wrapper.domain.SqlRequest;
import com.scl.as400wrapper.service.connectionService.db2ConnectionService.IDb2ConnectionBuilder;
import com.scl.as400wrapper.utilis.JsonData;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@RestController
@RequestMapping("as400/db2")
public class AS400SQLController {

    @Autowired
    private IDb2ConnectionBuilder db2ConnectionBuilder;

    @PostMapping("sql")
    //@RequestBody is a must for JSON Format
    public JsonData getAS400Data(@RequestBody SqlRequest sqlRequest) {

        //System.out.println("SQL-Request:" + sqlRequest.toString());

        String sqlRequestContent = sqlRequest.getSqlContent();

        //build open-telemetry tracer
        Tracer tracer = GlobalTracer.get();
        //Tag Info : NewGuid + SqlContentId + Description + Current_TimeStamp
        String TagName = "GUID:" + UUID.randomUUID() + "_ID:" + sqlRequest.getId() + "_Description:" + sqlRequest.getDescription() + "_SqlContentHash:" + sqlRequest.getSqlContent().hashCode();
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan("SPAN" + sqlRequest.getDescription())
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER);
        //Open Telemetry Tag Span Start
        Span span = spanBuilder.start();
        span.setTag(TagName, "start");
        Tags.COMPONENT.set(span, "AS400WrapperController:" + TagName);

        //Business Logic Process
        try {
            if (sqlRequestContent.isEmpty()) {
                throw new Exception("Query for" + sqlRequest.getId() + "-" + sqlRequest.getDescription() + "is empty");
            }

            //Ver_1 Build Connection to DB2
            //Connection as400Connection = AS400TestConnection.getAS400Connection();

            //Ver_2 Build Connection to DB2
            Connection as400Connection = db2ConnectionBuilder.getDb2Connection(sqlRequest.getDb2Url(),sqlRequest.getDb2Name(),sqlRequest.getDb2Password());
            Statement statement = as400Connection.createStatement();

            Tags.COMPONENT.set(span, "AS400 Connection Success");

            //get resultMap from JDBC Connection
            ResultSet rs = statement.executeQuery(sqlRequestContent);

            Tags.COMPONENT.set(span, "AS400 Raw Data Success");

            //pub ResultMap(JDBC) => Format List<Map<String, Object>>
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
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

            Tags.COMPONENT.set(span, "AS400 Formatted Data Success");

            //set success return response
            return JsonData.buildSuccess(resultList);
        } catch (Exception e) {
            //Set Error Info & Error Tag
            String ErrorInfo = "Request Error:" + TagName + "_Query:" + sqlRequest.getSqlContent() + "_ErrorInfo:" + e.getMessage();
            Tags.COMPONENT.set(span, ErrorInfo);

            return JsonData.buildError(ErrorInfo);
        } finally {
            //Open-telemetry span finish
            Tags.COMPONENT.set(span, "Finish_Tracing:" + TagName);
            span.finish();
        }
    }


}
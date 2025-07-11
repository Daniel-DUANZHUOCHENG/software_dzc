package edu.neu.oaas.utils;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockResponseHelper {
    
    public static Response createMockResponse(boolean isSuccessful, String body) {
        Response.Builder builder = new Response.Builder()
            .protocol(Protocol.HTTP_1_1)
            .request(new Request.Builder().url("http://localhost").build())
            .code(isSuccessful ? 200 : 500)
            .message(isSuccessful ? "OK" : "Internal Server Error");
            
        if (body != null) {
            builder.body(ResponseBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                body
            ));
        }
        
        return builder.build();
    }
} 
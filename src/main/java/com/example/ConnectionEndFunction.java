package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import com.example.utils.WebSocketResponseUtils;

public class ConnectionEndFunction implements RequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse> {

    @Override
    public APIGatewayV2WebSocketResponse handleRequest(APIGatewayV2WebSocketEvent apiGatewayV2WebSocketEvent, Context context) {
        //Make disconnect for webSocket function
        return WebSocketResponseUtils.successfulResponse("{\"message\": \"Disconnected.\"}");
    }
}

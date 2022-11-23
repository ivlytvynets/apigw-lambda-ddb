package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.model.User;
import com.example.utils.DDBUtils;
import com.example.utils.RequestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RankedUpdateFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final DDBUtils ddbUtils = new DDBUtils(System.getenv("TABLE"), System.getenv("REGION"));

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String userId;
        LambdaLogger logger = context.getLogger();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            RequestHelper helper = new RequestHelper(event.getBody(),
                                                     event.getPathParameters());
            String id = helper.getParam("id");
            User rankRecord = ddbUtils.getRankRecord(id);
            rankRecord.setRank(rankRecord.getRank() + getRankDelta(helper.getParam("status")));
            userId = ddbUtils.addRankRecord(rankRecord);
        } catch (JsonProcessingException e) {
            logger.log(e.getMessage());
            return response.withStatusCode(400).withBody("{\"message\": \"Body incorrect.\"}");
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(200)
                .withBody(String.format("{\"message\": \"Successfully updated rank for user %s.\"}", userId));
    }

    private Integer getRankDelta(String status) {
        if ("win".equals(status)) {
            return 2;
        } else if ("draw".equals(status)) {
            return 0;
        }
        return -1;
    }
}

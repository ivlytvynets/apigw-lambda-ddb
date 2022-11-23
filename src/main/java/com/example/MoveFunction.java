package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import com.example.utils.CacheUtils;
import com.example.utils.RequestHelper;
import com.example.utils.WebSocketResponseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import redis.clients.jedis.Jedis;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiAsyncClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MoveFunction implements RequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse> {
    private static final CacheUtils cache = new CacheUtils(System.getenv("HOST"),
            Integer.valueOf(System.getenv("PORT")));

    private final ApiGatewayManagementApiAsyncClient client = ApiGatewayManagementApiAsyncClient.builder()
            .endpointOverride(new URI(System.getenv("ENDPOINT")))
            .region(Region.of(System.getenv("REGION")))
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .httpClient(AwsCrtAsyncHttpClient.create())
            .build();

    public MoveFunction() throws URISyntaxException {
    }

    @Override
    public APIGatewayV2WebSocketResponse handleRequest(APIGatewayV2WebSocketEvent apiGatewayV2WebSocketEvent, Context context) {
        PostToConnectionRequest postToConnectionRequest;
        LambdaLogger logger = context.getLogger();
        Jedis jedis = cache.getClient();

        try {
            RequestHelper helper = new RequestHelper(apiGatewayV2WebSocketEvent.getBody(),
                    apiGatewayV2WebSocketEvent.getQueryStringParameters());
            String sendTo = helper.getPathParam("player");
            String moveToConnection = jedis.get(CacheUtils.PREFIX_CONNECTION_PLAYER + sendTo);
            if ("nil".equals(moveToConnection)) {
                return WebSocketResponseUtils.badResponse("User unavailable");
            }
            Map<String, String> moves = RequestHelper.parseMoves(helper.getParam("moves"));
            jedis.hset(CacheUtils.PREFIX_MOVES + moveToConnection, moves);
            postToConnectionRequest = PostToConnectionRequest.builder()
                    .connectionId(moveToConnection)
                    .data(SdkBytes.fromByteArray(RequestHelper
                            .toJson("New state" + moves)
                            .getBytes()))
                    .build();
        } catch (JsonProcessingException e) {
            logger.log(e.getMessage());
            return WebSocketResponseUtils.badResponseServer();
        }
        CompletableFuture<PostToConnectionResponse> response = client.postToConnection(postToConnectionRequest);
        try {
            response.join();
        } catch (CancellationException | CompletionException e) {
            logger.log(e.getMessage());
            try {
                return WebSocketResponseUtils.badResponse("Move failed!");
            } catch (JsonProcessingException e1) {
                logger.log(e1.getMessage());
                return WebSocketResponseUtils.badResponseServer();
            }
        }
        return WebSocketResponseUtils.successfulResponse("{\"message\": \"Move sent and save.\"}");
    }
}

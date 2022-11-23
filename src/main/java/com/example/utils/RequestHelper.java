package com.example.utils;

import com.example.model.Move;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHelper {

    private static final ObjectMapper mapper;
    private final String eventBody;
    private final Map<String, String> parsedBody;
    private final Map<String, String> pathParam;

    static {
        mapper = new ObjectMapper();
    }

    public RequestHelper(String body, Map<String, String> pathParam) throws JsonProcessingException {
        this.eventBody = body;
        this.pathParam = pathParam;
        this.parsedBody = mapper.readValue(eventBody, new TypeReference<>() {});
    }

    public String getParam(String key) throws JsonProcessingException {
        return parsedBody.get(key);
    }

    public String getPathParam(String key){
        return pathParam.get(key);
    }

    public static String writeToBody(List<?> value) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, value);
        return out.toString();
    }

    public static String toJson (Object str) throws JsonProcessingException {
        return mapper.writeValueAsString(str);
    }

    public static Map<String, String> parseMoves(String json) throws JsonProcessingException {
        return Arrays.stream(json.split("(?<=}, )"))
                .map(s -> {
                    try {
                        return mapper.readValue(s, Move.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return new Move();
                    }
                })
                .collect(Collectors.toMap(Move::getId, Move::getSign));
    }
}

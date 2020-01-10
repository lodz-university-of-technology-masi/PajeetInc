package com.serverless.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamSelector {
    private InputStream inputStream;

    public InputStreamSelector(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    protected String getAttributeAsText(String attribute) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readValue(inputStream, JsonNode.class);
        return node.get(attribute).asText();
    }
}
package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map;

public class Getouid implements RequestHandler<Map<String, Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        try {
            // nickname 가져오기
            String nickname = (String) event.get("nickname");

            // 요청 URL 설정
            String baseUrl = "https://open.api.nexon.com/fconline/v1/id";
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("nickname", nickname);
            String url = baseUrl + "?" + buildQueryParams(queryParams);

            // HttpClient 객체 생성
            HttpClient httpClient = HttpClient.newBuilder().build();

            // HttpRequest 객체 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("accept", "application/json")
                    .header("x-nxopen-api-key", "test_f15f9ee815ab4be133e13c2028780bfc1375fec5c24fbe5b2436a1070879273300ad5d0d0126614ece0a1c1c2d180b62")
                    .GET()
                    .build();

            // 요청 보내기
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();

            // "ouid" 필드의 값을 추출하여 출력
            String ouid = jsonResponse.get("ouid").getAsString();
            System.out.println("ouid: " + ouid);



            return ouid;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

    // Query Parameters를 문자열로 변환하는 메서드
    public static String buildQueryParams(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return result.toString();
    }
}
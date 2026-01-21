package com.heim.api.payment.application.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.heim.api.payment.application.dto.PaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

@Service
public class PaymentService {

    @Value("${wava.api.url}")
    private String wavaApiUrl;

    @Value("${wava.merchant.key}")
    private String merchantKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String createPaymentLink(PaymentRequest paymentRequest) throws Exception {
        String json = objectMapper.writeValueAsString(paymentRequest);
        PaymentRequest requestP = new PaymentRequest();


        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(wavaApiUrl)
                .post(body)
                .addHeader("merchant-key", merchantKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody =  response.body().string();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String paymentLink = rootNode.get("result").get("link").asText();

            if (!response.isSuccessful()) {
                throw new RuntimeException("Error en Wava API: " + response.code() + " " + response.message());
            }

            return paymentLink;
        }
    }


}

package com.heim.api.payment.application.service;


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

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(wavaApiUrl)
                .post(body)
                .addHeader("merchant-key", merchantKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error en Wava API: " + response.code() + " " + response.message());
            }
            assert response.body() != null;
            return response.body().string();
        }
    }


}

package com.heim.api.payment.application.service;


import com.heim.api.payment.application.dto.PayRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
public class PaymentService {

    @Value("${epayco.public_key}")
    private String publicKey;

    @Value("${epayco.confirmation_url}")
    private String confirmationUrl;

    @Value("${epayco.response_url}")
    private String responseUrl;

    public String generatePayment(PayRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://checkout.epayco.co/checkout.php")
                .queryParam("publickey", publicKey)
                .queryParam("name", "Pago de servicio")
                .queryParam("description", "Mudanza para " + request.getName())
                .queryParam("currency", "COP")
                .queryParam("amount", request.getAmount())
                .queryParam("tax_base", "0")
                .queryParam("tax", "0")
                .queryParam("country", "CO")
                .queryParam("lang", "es")
                .queryParam("external", "false")
                .queryParam("email_billing", request.getEmail())
                .queryParam("name_billing", request.getName())
                .queryParam("mobilephone_billing", request.getPhoneNumber())
                .queryParam("response", responseUrl)
                .queryParam("confirmation", confirmationUrl);
        // .queryParam("method", request.getPaymentMethod());  <== Comentado o eliminado

        URI uri = builder.build().encode().toUri();
        return uri.toString();
    }


}

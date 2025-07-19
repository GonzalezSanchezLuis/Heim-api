package com.heim.api.payment.application.service;


import com.heim.api.payment.application.dto.PaymentRequest;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PaymentService {

  /*  @Value("${epayco.public_key}")
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
    } */

    public String generateCheckoutUrl(PaymentRequest request) {
        String URL_BASE = "https://checkout.epayco.co/checkout.php";
        StringBuilder url = new StringBuilder(URL_BASE + "?");

        String PUBLIC_KEY = "43a7d7ae21e91208f9d86bc760941c30";
        url.append("public_key=").append(PUBLIC_KEY);
        url.append("&amount=").append(request.getAmount());
        url.append("&name=").append(encode(request.getName()));
        url.append("&description=").append(encode(request.getDescription()));
        url.append("&currency=").append(request.getCurrency());
        url.append("&country=CO");
        url.append("&invoice=").append(request.getInvoice());
        url.append("&email_billing=").append(request.getEmail());
        url.append("&lang=es");
        url.append("&tax=0");
        url.append("&tax_base=0");
        url.append("&method=").append(request.getMethod()); // Nequi, etc.
        url.append("&url_response=https://tusitio.com/respuesta");
        url.append("&url_confirmation=https://tusitio.com/confirmacion");

        return url.toString();
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }


}

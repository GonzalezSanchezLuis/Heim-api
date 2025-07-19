package com.heim.api.price.application.dto;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PriceResponse {
    private final BigDecimal price;
    private final String formattedPrice;
    private final double distanceKm;
    private final double timeMin;
    private final String formattedDistance;
    private final String formattedDuration;
    private final List<Map<String, Double>> route;

    public PriceResponse(BigDecimal price,double distanceKm, double timeMin, List<Map<String, Double>> route) {
        this.price = price;
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.ENGLISH);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        this.formattedPrice = decimalFormat.format(price);
        this.distanceKm = distanceKm;
        this.timeMin = timeMin;
        this.formattedDistance = String.format("%.1f km", distanceKm);
        this.formattedDuration = String.format("%.0f min", timeMin);
        this.route = route;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getTimeMin() {
        return timeMin;
    }

    public String getFormattedDistance() {
        return formattedDistance;
    }

    public String getFormattedDuration() {
        return formattedDuration;
    }

    public List<Map<String, Double>> getRoute() {
        return route;
    }
}


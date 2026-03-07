package com.ciblorgasport.geolocationservice.dto;

import jakarta.validation.constraints.NotNull;

public class PositionRequest {

    @NotNull(message = "La latitude est obligatoire")
    private Double latitude;

    @NotNull(message = "La longitude est obligatoire")
    private Double longitude;

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}

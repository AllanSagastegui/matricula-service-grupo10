package com.edusmart.matriculaservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfiguracionDTO(
        String sede,
        @JsonProperty("costo-credito") Double costoCredito,
        @JsonProperty("max-creditos-ciclo") Integer maxCreditosCiclo,
        @JsonProperty("mensaje-bienvenida") String mensajeBienvenida,
        @JsonProperty("limite-cuotas-pago") Integer limiteCuotasPago,
        @JsonProperty("datasource.url") String datasourceUrl
) {
}

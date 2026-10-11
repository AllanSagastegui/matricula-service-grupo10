package com.edusmart.matriculaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetalleCursoMatriculadoDTO {
    private Long idCurso;
    private String codigoCurso;
    private String nombreCurso;
    private String seccion;
    private Integer creditos;
    private Double costoPorCredito;
    private Double subtotalCurso;

}

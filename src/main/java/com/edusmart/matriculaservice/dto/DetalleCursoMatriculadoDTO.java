package com.edusmart.matriculaservice.dto;

public record DetalleCursoMatriculadoDTO(
        Long idCurso,
        String codigoCurso,
        String nombreCurso,
        String seccion,
        Integer creditos,
        Double costoPorCredito,
        Double subtotalCurso
) {
}

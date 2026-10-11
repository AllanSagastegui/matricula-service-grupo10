package com.edusmart.matriculaservice.dto;

import java.util.List;

public record ResumenFichaEstudianteDTO(
        Long estudianteId,
        String nombreEstudiante,
        String carrera,
        String sede,
        Integer totalMatriculasRealizadas,
        Double promedioPonderadoAcumulado,
        List<MatriculaResponseDTO> historialMatriculas,
        List<CursoDisponibilidadDTO> catalogoCursosElegibles,
        String situacionAcademica
) {
}

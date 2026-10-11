package com.edusmart.matriculaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumenFichaEstudianteDTO {
    private Long estudianteId;
    private String nombreEstudiante;
    private String carrera;
    private String sede;
    private Integer totalMatriculasRealizadas;
    private Double promedioPonderadoAcumulado;
    private List<MatriculaResponseDTO> historialMatriculas;
    private List<CursoDisponibilidadDTO> catalogoCursosElegibles;
    private String situacionAcademica;
}
package com.edusmart.matriculaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class MatriculaResponseDTO {
    private Long idMatricula;
    private String codigoMatricula;
    private LocalDateTime fechaMatricula;
    private Long estudianteId;
    private String periodoAcademico;
    private String estadoMatricula;
    private List<DetalleCursoMatriculadoDTO> listaCursosMatriculados;
    private Integer totalCreditosInscritos;
    private Double montoTotalPension;
}

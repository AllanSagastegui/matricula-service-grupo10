package com.edusmart.matriculaservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MatriculaResponseDTO(
        Long idMatricula,
        String codigoMatricula,
        LocalDateTime fechaMatricula,
        Long estudianteId,
        String periodoAcademico,
        String estadoMatricula,
        List<DetalleCursoMatriculadoDTO> listaCursosMatriculados,
        Integer totalCreditosInscritos,
        Double montoTotalPension
) {

    public MatriculaResponseDTO conEstado(String nuevoEstado) {
        return new MatriculaResponseDTO(
                idMatricula,
                codigoMatricula,
                fechaMatricula,
                estudianteId,
                periodoAcademico,
                nuevoEstado,
                listaCursosMatriculados,
                totalCreditosInscritos,
                montoTotalPension
        );
    }
}

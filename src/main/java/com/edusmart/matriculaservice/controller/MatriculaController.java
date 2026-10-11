package com.edusmart.matriculaservice.controller;

import com.edusmart.matriculaservice.client.CursoClient;
import com.edusmart.matriculaservice.dto.CursoDisponibilidadDTO;
import com.edusmart.matriculaservice.dto.DetalleCursoMatriculadoDTO;
import com.edusmart.matriculaservice.dto.MatriculaResponseDTO;
import com.edusmart.matriculaservice.dto.ResumenFichaEstudianteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matriculas")
@RefreshScope
public class MatriculaController {

    @Value("${edusmart.sede:Campus Lima Central}")
    private String sede;

    @Value("${edusmart.costo-credito:120.00}")
    private Double costoCredito;

    @Value("${edusmart.max-creditos-ciclo:22}")
    private Integer maxCreditosCiclo;

    @Value("${edusmart.mensaje-bienvenida:Bienvenido}")
    private String mensajeBienvenida;

    @Value("${edusmart.limite-cuotas-pago:5}")
    private Integer limiteCuotasPago;

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/edusmart_lima_db}")
    private String datasourceUrl;

    @Autowired
    private CursoClient cursoClient;

    @GetMapping("/configuracion")
    public java.util.Map<String, Object> obtenerConfiguracion() {
        java.util.Map<String, Object> config = new java.util.HashMap<>();
        config.put("sede", sede);
        config.put("costoCredito", costoCredito);
        config.put("maxCreditosCiclo", maxCreditosCiclo);
        config.put("mensajeBienvenida", mensajeBienvenida);
        config.put("limiteCuotasPago", limiteCuotasPago);
        config.put("datasourceUrl", datasourceUrl);
        return config;
    }

    @GetMapping("/{idMatricula}")
    public MatriculaResponseDTO obtenerMatricula(@PathVariable("idMatricula") Long idMatricula) {
        List<DetalleCursoMatriculadoDTO> detalles = new ArrayList<>();
        detalles.add(new DetalleCursoMatriculadoDTO(1L, "MAT101", "Matemática I", "A", 4, costoCredito, 4 * costoCredito));
        detalles.add(new DetalleCursoMatriculadoDTO(2L, "PRO101", "Programación I", "B", 5, costoCredito, 5 * costoCredito));

        int totalCreditos = 0;
        double totalPension = 0.0;
        for (DetalleCursoMatriculadoDTO d : detalles) {
            totalCreditos += d.getCreditos();
            totalPension += d.getSubtotalCurso();
        }

        return MatriculaResponseDTO.builder()
                .idMatricula(idMatricula)
                .codigoMatricula("MAT-2026-" + idMatricula)
                .fechaMatricula(LocalDateTime.now())
                .estudianteId(100L)
                .periodoAcademico("2026-I")
                .estadoMatricula("EN_PROCESO")
                .listaCursosMatriculados(detalles)
                .totalCreditosInscritos(totalCreditos)
                .montoTotalPension(totalPension)
                .build();
    }

    @GetMapping("/validar-y-confirmar/{idMatricula}")
    public MatriculaResponseDTO validarYConfirmarMatricula(@PathVariable("idMatricula") Long idMatricula) {
        MatriculaResponseDTO matricula = obtenerMatricula(idMatricula);
        boolean aprobado = true;

        for (DetalleCursoMatriculadoDTO detalle : matricula.getListaCursosMatriculados()) {
            try {
                CursoDisponibilidadDTO curso = cursoClient.obtenerCursoPorId(detalle.getIdCurso());
                if (curso == null || curso.getVacantesDisponibles() <= 0 || !"ABIERTO".equalsIgnoreCase(curso.getEstadoCurso())) {
                    aprobado = false;
                    break;
                }
            } catch (Exception e) {
                aprobado = false;
                break;
            }
        }

        matricula.setEstadoMatricula(aprobado ? "CONFIRMADA" : "RECHAZADA_SIN_VACANTES");
        return matricula;
    }

    @GetMapping("/ficha-estudiante/{estudianteId}")
    public ResumenFichaEstudianteDTO obtenerFichaEstudiante(@PathVariable("estudianteId") Long estudianteId) {
        List<CursoDisponibilidadDTO> cursosElegibles = new ArrayList<>();
        try {
            cursosElegibles = cursoClient.listarCursosPorEstudiantes(estudianteId);
        } catch (Exception e) {
            cursosElegibles = new ArrayList<>();
        }

        List<MatriculaResponseDTO> historial = new ArrayList<>();
        historial.add(obtenerMatricula(1L));

        double promedio = 16.5;

        return ResumenFichaEstudianteDTO.builder()
                .estudianteId(estudianteId)
                .nombreEstudiante("Cristhofer Alumno")
                .carrera("Ingeniería de Software")
                .sede(sede)
                .totalMatriculasRealizadas(historial.size())
                .promedioPonderadoAcumulado(promedio)
                .historialMatriculas(historial)
                .catalogoCursosElegibles(cursosElegibles)
                .situacionAcademica(promedio >= 12.0 ? "REGULAR" : "OBSERVADO")
                .build();
    }
}
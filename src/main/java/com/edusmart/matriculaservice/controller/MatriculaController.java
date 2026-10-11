package com.edusmart.matriculaservice.controller;

import com.edusmart.matriculaservice.client.CursoClient;
import com.edusmart.matriculaservice.dto.ConfiguracionDTO;
import com.edusmart.matriculaservice.dto.CursoDisponibilidadDTO;
import com.edusmart.matriculaservice.dto.DetalleCursoMatriculadoDTO;
import com.edusmart.matriculaservice.dto.MatriculaResponseDTO;
import com.edusmart.matriculaservice.dto.ResumenFichaEstudianteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RefreshScope
@RestController
@RequestMapping("/api/v1/matriculas")
public class MatriculaController {

    private final CursoClient cursoClient;
    private final Map<Long, String> estadosPorMatricula = new ConcurrentHashMap<>();

    @Value("${edusmart.sede:Campus Lima Central}")
    private String sede;

    @Value("${edusmart.costo-credito:120.00}")
    private Double costoCredito;

    @Value("${edusmart.max-creditos-ciclo:22}")
    private Integer maxCreditosCiclo;

    @Value("${edusmart.mensaje-bienvenida:Bienvenido al Sistema de Matricula - Sede Lima}")
    private String mensajeBienvenida;

    @Value("${edusmart.limite-cuotas-pago:5}")
    private Integer limiteCuotasPago;

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/edusmart_lima_db}")
    private String datasourceUrl;

    public MatriculaController(CursoClient cursoClient) {
        this.cursoClient = cursoClient;
    }

    @GetMapping("/configuracion")
    public ConfiguracionDTO obtenerConfiguracion() {
        return new ConfiguracionDTO(
                sede,
                costoCredito,
                maxCreditosCiclo,
                mensajeBienvenida,
                limiteCuotasPago,
                datasourceUrl
        );
    }

    @GetMapping("/{idMatricula}")
    public MatriculaResponseDTO obtenerMatricula(
            @PathVariable("idMatricula") Long idMatricula) {

        List<DetalleCursoMatriculadoDTO> detalles = List.of(
                crearDetalle(1L, "MAT101", "Matemática I", "A", 4),
                crearDetalle(2L, "PRO101", "Programación I", "B", 5)
        );

        int totalCreditos = detalles.stream()
                .mapToInt(DetalleCursoMatriculadoDTO::creditos)
                .sum();
        double totalPension = detalles.stream()
                .mapToDouble(DetalleCursoMatriculadoDTO::subtotalCurso)
                .sum();

        String estado = estadosPorMatricula.getOrDefault(idMatricula, "EN_PROCESO");

        return new MatriculaResponseDTO(
                idMatricula,
                "MAT-2026-" + idMatricula,
                LocalDateTime.now(),
                100L,
                "2026-I",
                estado,
                detalles,
                totalCreditos,
                totalPension
        );
    }

    @GetMapping("/validar-y-confirmar/{idMatricula}")
    public MatriculaResponseDTO validarYConfirmarMatricula(
            @PathVariable("idMatricula") Long idMatricula) {

        MatriculaResponseDTO matricula = obtenerMatricula(idMatricula);
        boolean todosDisponibles = matricula.listaCursosMatriculados().stream()
                .allMatch(this::cursoDisponible);

        String estado = todosDisponibles
                ? "CONFIRMADA"
                : "RECHAZADA_SIN_VACANTES";
        estadosPorMatricula.put(idMatricula, estado);

        return matricula.conEstado(estado);
    }

    @GetMapping("/ficha-estudiante/{estudianteId}")
    public ResumenFichaEstudianteDTO obtenerFichaEstudiante(
            @PathVariable("estudianteId") Long estudianteId) {

        List<CursoDisponibilidadDTO> cursosElegibles;
        try {
            cursosElegibles = cursoClient.listarCursosPorEstudiante(estudianteId);
        } catch (RuntimeException ex) {
            cursosElegibles = List.of();
        }

        List<MatriculaResponseDTO> historial = List.of(obtenerMatricula(1L));
        double promedioPonderado = 16.5;
        String situacionAcademica = promedioPonderado >= 12.0
                ? "REGULAR"
                : "OBSERVADO";

        return new ResumenFichaEstudianteDTO(
                estudianteId,
                "Estudiante " + estudianteId,
                "Ingeniería de Software",
                sede,
                historial.size(),
                promedioPonderado,
                historial,
                cursosElegibles,
                situacionAcademica
        );
    }

    private DetalleCursoMatriculadoDTO crearDetalle(
            Long idCurso,
            String codigoCurso,
            String nombreCurso,
            String seccion,
            Integer creditos) {

        double subtotal = creditos * costoCredito;
        return new DetalleCursoMatriculadoDTO(
                idCurso,
                codigoCurso,
                nombreCurso,
                seccion,
                creditos,
                costoCredito,
                subtotal
        );
    }

    private boolean cursoDisponible(DetalleCursoMatriculadoDTO detalle) {
        try {
            CursoDisponibilidadDTO curso = cursoClient.obtenerCursoPorId(detalle.idCurso());
            return curso != null
                    && curso.vacantesDisponibles() != null
                    && curso.vacantesDisponibles() > 0
                    && "ABIERTO".equalsIgnoreCase(curso.estadoCurso());
        } catch (RuntimeException ex) {
            return false;
        }
    }
}

package com.edusmart.matriculaservice.client;

import com.edusmart.matriculaservice.dto.CursoDisponibilidadDTO;
import jakarta.ws.rs.Path;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "curso-service-grupo10")
public interface CursoClient {
    @GetMapping("/api/v1/cursos/{idCurso}")
    CursoDisponibilidadDTO obtenerCursoPorId(@PathVariable("idCurso") Long idCurso);
    @GetMapping("/api/v1/cursos/estudiante/{estudianteId}")
    List<CursoDisponibilidadDTO> listarCursosPorEstudiantes(@PathVariable("estudianteId")Long estudianteId);
}

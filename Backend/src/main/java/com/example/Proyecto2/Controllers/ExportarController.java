package com.example.Proyecto2.Controllers;

import com.example.Proyecto2.Service.ServiceExportarINT;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exportar")
public class ExportarController {
	private final ServiceExportarINT serviceExportar;

    public ExportarController(ServiceExportarINT serviceExportar) {
        this.serviceExportar = serviceExportar;
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> exportar() {
        String xml = serviceExportar.exportarXml();
        return ResponseEntity.ok(xml);
    }

}
//Guardar no es automatico 
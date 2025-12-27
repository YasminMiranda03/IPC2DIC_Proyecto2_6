package com.example.Proyecto2.Controllers;
import com.example.Proyecto2.Service.ServiceImportarINT;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/importar")
public class ImportarController {
	
	 private final ServiceImportarINT serviceImportar;

	    public ImportarController(ServiceImportarINT serviceImportar) {
	        this.serviceImportar = serviceImportar;
	    }

	    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	    public ResponseEntity<String> importar(@RequestParam("file") MultipartFile file) {

	        if (file == null || file.isEmpty()) {
	            return ResponseEntity.badRequest().body("ERROR: Archivo vacío o no enviado.");
	        }

	        String resultado = serviceImportar.importarXml(file);

	        if (resultado != null && resultado.startsWith("ERROR")) {
	            return ResponseEntity.badRequest().body(resultado);
	        }

	        return ResponseEntity.ok(resultado);
	    }
	}
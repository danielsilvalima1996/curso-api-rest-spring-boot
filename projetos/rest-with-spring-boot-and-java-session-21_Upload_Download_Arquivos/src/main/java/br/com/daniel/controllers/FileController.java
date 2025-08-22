package br.com.daniel.controllers;

import br.com.daniel.controllers.docs.FileControllerDocs;
import br.com.daniel.data.dto.UploadFileResponseDTO;
import br.com.daniel.services.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/file/v1")
public class FileController implements FileControllerDocs {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService service;

    @PostMapping("/uploadFile")
    @Override
    public UploadFileResponseDTO uploadFile(@RequestParam("file") MultipartFile file) {
        var fileName = service.storeFile(file);
        var fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/file/v1/downloadFile/")
                .path(fileName)
                .toUriString();
        return new UploadFileResponseDTO(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    @Override
    public List<UploadFileResponseDTO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files).map(this::uploadFile).toList();
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    @Override
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName, HttpServletRequest request) {
        Resource resource = service.loadFileAsResource(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception e) {
            logger.error("Could not determine file type!", e);
        }
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

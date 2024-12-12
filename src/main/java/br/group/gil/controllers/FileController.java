package br.group.gil.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.group.gil.data.vo.v1.UploadFileResponseVO;
import br.group.gil.services.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "File Endpoint")
@RestController
@RequestMapping("api/file/v1")
public class FileController {
	
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    @Autowired
    private FileStorageService service;
    
    @PostMapping(value="/uploadFile")
    public UploadFileResponseVO uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
    	logger.info("Storing file from disk");
    	
    	var filename = service.storeFile(file);
    	String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
    			.path("/api/file/v1/downloadFile/")
    			.path(filename)
    			.toUriString();		
    	    return 
    			new UploadFileResponseVO(filename, fileDownloadUri, file.getContentType(), file.getSize());   	
  }	

    @PostMapping(value = "/uploadMultipleFiles")
    public List<UploadFileResponseVO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        logger.info("Storing files from disk");

        try {
            return Arrays.stream(files)
                    .map(file -> {
                        try {
                            return uploadFile(file);
                        } catch (IOException e) {
                            logger.error("Error uploading file: {}", file.getOriginalFilename(), e);
                            throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error uploading multiple files", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload files", e);
        }
    }
    
    
    @GetMapping(value="/downloadFile/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) 
      throws IOException {
    	logger.info("Reading a file on disk");
    	
    	Resource resource = service.loadFileAsResouce(filename);
    	
    	String contentType = "";
    	
    	try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (Exception e) {
			logger.info("Could not determine file type!");	
		}
    	
    	if (contentType.isEmpty()) contentType = "application/octet-stream";
    	
    	return ResponseEntity.ok()
    			.contentType(MediaType.parseMediaType(contentType))
     			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
    			.body(resource);
    	}	
}

package br.group.gil.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.group.gil.config.FileStorageConfig;
import br.group.gil.exceptions.FileStorageException;
import br.group.gil.exceptions.MyFileNotFoundException;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;
	
	@Autowired
	public FileStorageService(FileStorageConfig fileStorageConfig) {
		Path path = Paths.get(fileStorageConfig.getUpload_dir())
				.toAbsolutePath().normalize();
		
		this.fileStorageLocation = path;
		
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception e) {
			throw new FileStorageException(
				"Could not create the directory where the uploaded files will be storage!", e);
		}	
	}
	
	public String storeFile(MultipartFile file) throws IOException {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		
		try {
		  //Filename..txt
		  if(filename.contains("..")) {
			throw new FileStorageException(
			  "Filename contains invalid path sequence " + filename );
			}
			
			Path targetLocation = this.fileStorageLocation.resolve(filename);
			
			Files.copy(file.getInputStream(), targetLocation,  StandardCopyOption.REPLACE_EXISTING);	
		
			return filename;
		} catch (Exception e) {
			throw new FileStorageException(
					"Could not store file " + filename + ". please try again!" , e);
		}
	}
	
	public Resource loadFileAsResouce(String filename) throws MalformedURLException {
		try {
			Path filePath = this.fileStorageLocation.resolve(filename).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			
			if(resource.exists()) return resource;
			else throw new MyFileNotFoundException("File not found");
			
		} catch (Exception e) {
			throw new MyFileNotFoundException("File not found" + filename, e);
		}
	}
}

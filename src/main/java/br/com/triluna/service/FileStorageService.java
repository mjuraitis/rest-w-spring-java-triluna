package br.com.triluna.service;

import br.com.triluna.config.FileStorageConfig;
import br.com.triluna.exception.FileStorageException;
import br.com.triluna.exception.MyFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {

        Path path = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath()
                .normalize();

        this.fileStorageLocation = path;

        try {

            Files.createDirectories(this.fileStorageLocation);
        }
        catch (Exception ex) {

            throw new FileStorageException("Could not create upload directory.", ex);
        }
    }

    public String storeFile(MultipartFile file) {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {

            if (fileName.contains("..")) {

                throw new FileStorageException("File name contains invalid path sequence: " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }
        catch (Exception ex) {

            throw new FileStorageException("Could not store file: " + fileName + ". Please try again.", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {

        try {

            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {

                return resource;
            }
            else {

                throw new MyFileNotFoundException("File (resource) not found: " + fileName);
            }
        }
        catch (Exception ex) {

            throw new MyFileNotFoundException("File not found: " + fileName, ex);
        }
    }
}

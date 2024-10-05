package kr.co.dearmydiary.audit.audit.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.dearmydiary.audit.audit.dto.FileUploadDto;
import kr.co.dearmydiary.audit.audit.entity.PrfData;
import kr.co.dearmydiary.audit.audit.ocr.NaverClovaOCR;
import kr.co.dearmydiary.audit.audit.service.PrfDataService;
import kr.co.dearmydiary.audit.audit.service.PrfFileService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequiredArgsConstructor
public class PrfFileController {

    private final PrfDataService service;
    private final PrfFileService fileService;
    private final NaverClovaOCR naverClovaOCR;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("file/{folderName}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String folderName, @PathVariable String fileName) {

        Path file = Paths.get(uploadDir).resolve(folderName).resolve(fileName);
        
        
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = new UrlResource(file.toUri());

            String contentType = Files.probeContentType(file);
            // 파일 이름을 URL 인코딩합니다.
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20"); // 공백을 %20으로 변환합니다.
            return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                //.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"") // 파일을 다운로드하도록 설정
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
        } catch (MalformedURLException e) {
            // Log error and return internal server error response
            e.printStackTrace(); // Logging the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(
                
            );
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/files/getData")//Not use
    public PrfData getData(
        @RequestBody PrfData prfData
    ){
        return service.findByIdCatCd(prfData);
    }

    @PostMapping("/files/get")
    public List<PrfData> getDataList(//Not use
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "") String searchStd,
        @RequestParam(required = false, defaultValue = "") String searchEd,
        @RequestBody PrfData prfData
        ){
        return service.getDataList(page, size, searchStd, searchEd, prfData);
    }
    
    @PutMapping("/files/put")//Not use
    public PrfData putData(
        @RequestBody PrfData prfData
        ){
        return service.updateData(prfData);
    }
    
    @PostMapping("/files/post")
    public FileUploadDto PostData(
        @RequestParam("files") MultipartFile[] files
    ){
        FileUploadDto fileUploadDto = new FileUploadDto();
        fileUploadDto.setFileList(files);
        //FileUploadDto fileUploadDto = FileUploadDto.builder().fileList(files).build();
        //fileService.saveFiles(fileUploadDto);
        //return naverClovaOCR.getNaverOcr(fileUploadDto);
        return fileService.saveFiles(fileUploadDto);
    }
    
    @PutMapping("/files/delete")//Not use
    public PrfData DeleteData(@RequestBody PrfData prfData){
        return service.deleteData(prfData);
    }
}

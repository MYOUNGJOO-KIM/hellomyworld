package kr.co.jrindustry.audit.audit.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.jrindustry.audit.audit.dto.FileUploadDto;
import kr.co.jrindustry.audit.audit.entity.PrfData;
import kr.co.jrindustry.audit.audit.entity.PrfDataMgt;
import kr.co.jrindustry.audit.audit.service.PrfDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
public class PrfDataController {
    
    private final PrfDataService service;

    @PostMapping("/data/getFileUploadDto")
    public FileUploadDto getFileUploadDto(
        @RequestParam("files") MultipartFile[] files
    ){
        FileUploadDto fileUploadDto = new FileUploadDto();
        fileUploadDto.setFileList(files);
        return service.getFileUploadDto(fileUploadDto);
    }

    
    @PostMapping("/data/getData")
    public PrfData getData(
        @RequestBody PrfData prfData
    ){
        return service.findByCatCd(prfData);
    }
        
    @PostMapping("/data/get")
    public List<PrfData> getDataList(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "") String searchStd,
        @RequestParam(required = false, defaultValue = "") String searchEd,
        @RequestBody PrfData prfData
        ){
        return service.getDataList(page, size, searchStd, searchEd, prfData);
    }

    @PutMapping("/data/put")
    public PrfData putData(
        @RequestBody PrfData prfData
        ){
        return service.updateData(prfData);
    }
    
    @PostMapping("/data/post")
    public PrfData PostData(
        @RequestParam("files") MultipartFile[] files,
        @RequestParam("filePath") String filePath,
        @RequestParam("requestBody") String requestBody,
        @RequestParam("dataMgtList") List<String> dataMgtContentList
    )throws IOException{

        FileUploadDto fileUploadDto = new FileUploadDto();
        fileUploadDto.setFilePath(filePath);
        fileUploadDto.setFileList(files);

        ObjectMapper objectMapper = new ObjectMapper();
        PrfData prfData = objectMapper.readValue(requestBody, PrfData.class);
            
        List<PrfDataMgt> prfDataMgtList = dataMgtContentList.stream().map(content -> new PrfDataMgt(content)).collect(Collectors.toList());

        return service.insertData(prfData, prfDataMgtList, fileUploadDto);
    }
    
    @PutMapping("/data/delete")
    public PrfData DeleteData(@RequestBody PrfData prfData){
        return service.deleteData(prfData);
    }
}

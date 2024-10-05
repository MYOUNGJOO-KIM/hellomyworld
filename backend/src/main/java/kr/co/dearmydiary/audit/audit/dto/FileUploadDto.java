package kr.co.dearmydiary.audit.audit.dto;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadDto {
    
    @JsonIgnore
    private MultipartFile[] fileList;
    private String filePath = "";
    private String ocrTextOutput = "";
    private ArrayList<Path> fullPathList = new ArrayList<Path>();
    private ArrayList<String> fileDownloadUriList = new ArrayList<String>();
    private String statusMsg = "";

    public void setFileList(List<MultipartFile> files) {
        // 내부적으로 배열로 변환하여 처리
        this.fileList = files.toArray(new MultipartFile[0]);
    }

    public void setFileList(MultipartFile[] files) {
        // 내부적으로 배열로 변환하여 처리
        this.fileList = files;
    }
}
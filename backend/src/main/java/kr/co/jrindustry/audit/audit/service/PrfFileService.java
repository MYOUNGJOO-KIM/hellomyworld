package kr.co.jrindustry.audit.audit.service;

import java.util.List;
import java.util.ArrayList;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import kr.co.jrindustry.audit.audit.dto.FileUploadDto;
import kr.co.jrindustry.audit.audit.ocr.Tesseract;
import java.awt.image.BufferedImage;



@Service
public class PrfFileService {

    @Value("${tesseract-api}")
    private String tesseractApi;

    @Value("${file.upload-dir}")
    private String uploadDir;

    //public Tesseract tesseract;

    @Transactional
    public FileUploadDto saveFiles (FileUploadDto fileUploadDto){
        try{
            
            if(fileUploadDto.getFilePath() == null){
                fileUploadDto.setFilePath("");
            } 
            
            String uploadDirRoot = uploadDir;
            
            uploadDirRoot += fileUploadDto.getFilePath();
            Path filePath = Paths.get(uploadDirRoot);
            
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath); 
            }
            
            String now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
            
            int i = 0;
            for(MultipartFile file : fileUploadDto.getFileList()){
                i++;
                
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                String newFileName = now + "_file_" + i + fileName.substring(fileName.lastIndexOf("."));
                Path fullPath = filePath.resolve(newFileName);
                
                //pdf 들어왔을때 행동 작성하기
                // 파일 확장자 체크
                if (file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {

                    List<String> imagePaths = new ArrayList<>();
                    
                    // PDF 파일 로드
                    PDDocument document = PDDocument.load(file.getInputStream());
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    
                    // PDF 페이지를 이미지로 변환 및 저장
                    for (int page = 0; page < document.getNumberOfPages(); ++page) {
                        String imageFileName = now + "_file_" + i + "_page_" + (page + 1) + ".jpg";
                        Path imagePath = filePath.resolve(imageFileName); // 이미지 경로

                        BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300);
                        ImageIO.write(bim, "JPG", imagePath.toFile()); // 이미지 파일 저장
                        imagePaths.add(imagePath.toString()); // 저장된 이미지 경로 리스트에 추가

                        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/file")
                            .path(fileUploadDto.getFilePath())
                            .path(imageFileName)
                            .toUriString();
    
                        fileUploadDto.getFileDownloadUriList().add(fileDownloadUri);
                    }
                    document.close();
                } else {

                    Files.copy(file.getInputStream(), fullPath);
                    
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/file")
                            .path(fileUploadDto.getFilePath())
                            .path(newFileName)
                            .toUriString();
    
                    fileUploadDto.getFileDownloadUriList().add(fileDownloadUri);
                }


                
            }
            return fileUploadDto;

        } catch (FileAlreadyExistsException e) {
            fileUploadDto.setStatusMsg("File upload failed: duplicate files");
            return fileUploadDto;
        } catch (Exception e){
            fileUploadDto.setStatusMsg("Files upload failed");
            return fileUploadDto;
        }
    }

    public FileUploadDto getFileUploadDto(FileUploadDto fileUploadDto){

        //List<byte[]> imageBytesList = new ArrayList<>();
        MultipartFile[] fileList = fileUploadDto.getFileList();
        List<MultipartFile> newFileList = new ArrayList<>();

        for(int i = 0; i < fileList.length; i++){
            
            if (fileList[i].getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                
                try (PDDocument document = PDDocument.load(fileList[i].getInputStream())) {
                    PDFRenderer pdfRenderer = new PDFRenderer(document);

                    for (int page = 0; page < document.getNumberOfPages(); ++page) {
                        BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300); // DPI 조정 가능

                        // ByteArrayOutputStream을 사용하여 이미지 데이터를 바이트 배열로 변환
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(bim, "JPG", baos); // JPG 형식으로 저장
                        byte[] imageBytes = baos.toByteArray();
                        
                        MultipartFile imageFile = new MockMultipartFile("image_" + (page + 1), imageBytes);
                        newFileList.add(imageFile);
                    }
                } catch (Exception e){
                    fileUploadDto.setStatusMsg("get Files failed");
                }

            } else {

                newFileList.add(fileList[i]);
            }
        }

        fileUploadDto.setFileList(newFileList);


        Tesseract tesseract = new Tesseract();
        String str = tesseract.getReadedText(tesseractApi, fileUploadDto);
        fileUploadDto.setOcrTextOutput(str);

        return fileUploadDto;
    }

    
}

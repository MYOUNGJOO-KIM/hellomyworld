package kr.co.jrindustry.audit.audit.ocr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.jrindustry.audit.audit.dto.FileUploadDto;

@Service
public class Tesseract {

    public String getReadedText(String tesseractApi, FileUploadDto fileUploadDto){
        

        String readedText = "";

        try {

            MultipartFile[] fileList = fileUploadDto.getFileList();

            for(MultipartFile file : fileList){
                String fileName = UUID.randomUUID().toString() + ".tmp"; // 임시 파일 이름
                File tempFile = new File(fileName);

                try (InputStream inputStream = file.getInputStream();
                    OutputStream outputStream = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                // curl 명령어 실행
                String[] command = {"curl", "-X", "POST", tesseractApi, "-F", "image=@"+tempFile.getAbsolutePath()};
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                
                // 프로세스 실행
                Process process = processBuilder.start();
                
                // 결과를 읽어오기 위한 BufferedReader
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder result = new StringBuilder();
                
                // 프로세스의 결과 출력
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                
                // 프로세스가 종료될 때까지 기다림
                int exitCode = process.waitFor();
                System.out.println("Exited with code: " + exitCode);
                System.out.println("Response: " + result.toString());

                // 임시 파일 삭제
                tempFile.delete();

                ObjectMapper objMapper = new ObjectMapper();
                JsonNode jsonObj = objMapper.readTree(result.toString());
                JsonNode textNode = jsonObj.get("text");
                if (textNode != null) {
                    readedText += textNode.asText();
                    readedText += "\n\n------\n\n";
                }
        
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return readedText;
    }
}

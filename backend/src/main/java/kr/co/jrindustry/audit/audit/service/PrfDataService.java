package kr.co.jrindustry.audit.audit.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jrindustry.audit.audit.dto.BoardPrfCategoryMgt;
import kr.co.jrindustry.audit.audit.dto.FileUploadDto;
import kr.co.jrindustry.audit.audit.entity.PrfAttachment;
import kr.co.jrindustry.audit.audit.entity.PrfCategoryMgt;
import kr.co.jrindustry.audit.audit.entity.PrfData;
import kr.co.jrindustry.audit.audit.entity.PrfDataMgt;
import kr.co.jrindustry.audit.audit.repository.PrfDataRepo;
import kr.co.jrindustry.audit.audit.specification.PrfDataSpecification;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrfDataService {

    private final PrfDataRepo repo;
    private final PrfFileService fileService;
    private final PrfAttachmentService attachService;
    private final PrfDataMgtService dataMgtService;
    private final PrfCategoryMgtService mgtService;
    

    Logger logger = LoggerFactory.getLogger(PrfDataService.class);

    public FileUploadDto getFileUploadDto(FileUploadDto fileUploadDto){ 
        
        return fileService.getFileUploadDto(fileUploadDto);
        
    }


    public List<PrfData> getDataList(int page, int size, String searchStd, String searchEd, PrfData prfData){

        PrfDataSpecification spec = new PrfDataSpecification(prfData);
        return repo.findAll(spec, Sort.by("prfSeq"));
    }

    @Transactional
    public PrfData insertData(PrfData prfData, List<PrfDataMgt> PrfDataMgtList, FileUploadDto fileUploadDto){

        if( prfData.getCatCd() == null || prfData.getCatCd().isEmpty() ){
            throw new RuntimeException("Requires catCd");//500
        } else {
            PrfData data = repo.save(prfData);
            logger.info(String.valueOf(data.getPrfSeq()));
            if(!fileUploadDto.getFileList()[0].getOriginalFilename().equals("empty_file.jpg")){

                FileUploadDto dto = fileService.saveFiles(fileUploadDto);
                
                //data = repo.save(prfData.set);//update한 URL로 변경
                for(String fileDownloadUri : dto.getFileDownloadUriList()){
                    attachService.insertAttachment(PrfAttachment.builder().prfSeq(data.getPrfSeq()).catNm(prfData.getCatNm()).catCd(prfData.getCatCd()).attachFilePath(fileDownloadUri).delYn('n').build());
                }
            }

            for(PrfDataMgt prfDataMgt : PrfDataMgtList){
                prfDataMgt.setPrfSeq(data.getPrfSeq());
                prfDataMgt.setCatCd(data.getCatCd());
                prfDataMgt.setCatNm(data.getCatNm());
                dataMgtService.updateMgtAttach(prfDataMgt);
            }

            return data;
        }
    }

    @Transactional
    public PrfData updateData(PrfData prfData){//사용 X

        PrfData prfDataEntity = findByIdCatCd(prfData);

        if (prfDataEntity == null) {
            throw new RuntimeException("Requires an existing id(prfSeq or catCd)");
        }

        prfDataEntity.setCatCd(prfData.getCatCd());
        prfDataEntity.setCatNm(prfData.getCatNm());
        prfDataEntity.setPrfNm(prfData.getPrfNm());
        prfDataEntity.setPrfDesc(prfData.getPrfDesc());
        
        return repo.save(prfDataEntity);
    }

    @Transactional
    public PrfData deleteData(PrfData prfData){//사용 X
        //todoLIst
        //이거 삭제하면 다른 증빙자료 전부 삭제
        //mgt 내역 저장한 테이블에 연계된 것도 삭제
        PrfData prfDataEntity = findByIdCatCd(prfData);

        if (prfDataEntity == null) {
            throw new RuntimeException("Requires an existing id(prfSeq or catCd)");
        }

        prfDataEntity.setDelYn('y');
        return repo.save(prfDataEntity);
    }

    public PrfData findByIdCatCd(PrfData prfData) {

        Optional<PrfData> optionalCategory;
        PrfDataSpecification spec = new PrfDataSpecification(prfData);
        optionalCategory = repo.findOne(spec);

        if (optionalCategory.isPresent()) {
            //optionalCategory.get().getPrfSeq();
            //PrfAttachment.builder().prfSeq(optionalCategory.get().getPrfSeq()).catCd(optionalCategory.get().getCatCd()).build();
            List<PrfAttachment> attachList = attachService.getAttachmentList(0,10,null,null,PrfAttachment.builder().prfSeq(optionalCategory.get().getPrfSeq()).catCd(optionalCategory.get().getCatCd()).build());
            List<BoardPrfCategoryMgt> mgtList = mgtService.getCategoryMgtList(0, 10, null, null, null, null, PrfCategoryMgt.builder().catCd(optionalCategory.get().getCatCd()).build());
            optionalCategory.get().setAttachList(attachList);
            optionalCategory.get().setMgtList(mgtList);
            return optionalCategory.get();
        } else {
            return null;
        }
        // if (optionalCategory.isPresent()) {
        //     return optionalCategory.get();
        // } else {
        //     throw new RuntimeException("Requires an existing id(prfSeq or catCd)");
        // }
    }

    public PrfData findByCatCd(PrfData prfData) {

        prfData.setMgtList(mgtService.getCategoryMgtList(0, 100, null, null, null, null, PrfCategoryMgt.builder().catCd(prfData.getCatCd()).build()));
        //List<BoardPrfCategoryMgt> mgtList = mgtService.getCategoryMgtList(0, , null, null, null, null, PrfCategoryMgt.builder().catCd(optionalCategory.get(0).getCatCd()).build());

        //아직 get해서 보여주는 로직은 없기 때문에, mgtList 만 가져오기.
        //보여주는 로직 필요해지면 밑에 로직 활성화 하고 수정하기.
        return prfData;
        // List<PrfData> optionalCategory;
        // PrfDataSpecification spec = new PrfDataSpecification(prfData);
        // optionalCategory = repo.findAll(spec);
        
        // if (optionalCategory != null && optionalCategory.size() > 0) {
        //     List<PrfAttachment> attachList = attachService.getAttachmentList(0,10,null,null,PrfAttachment.builder().prfSeq(optionalCategory.get(0).getPrfSeq()).catCd(optionalCategory.get(0).getCatCd()).build());
        //     List<BoardPrfCategoryMgt> mgtList = mgtService.getCategoryMgtList(0, 10, null, null, null, null, PrfCategoryMgt.builder().catCd(optionalCategory.get(0).getCatCd()).build());
        //     //optionalCategory.get().getPrfSeq();
        //     //PrfAttachment.builder().prfSeq(optionalCategory.get().getPrfSeq()).catCd(optionalCategory.get().getCatCd()).build();
        //     optionalCategory.get(0).setAttachList(attachList);//아직은 리스트처리가 없으므로 잠시 대기
        //     optionalCategory.get(0).setMgtList(mgtList);//아직은 리스트처리가 없으므로 잠시 대기
        //     return optionalCategory.get(0);
        // } else {
        //     return null;
        // }

    }
    
}

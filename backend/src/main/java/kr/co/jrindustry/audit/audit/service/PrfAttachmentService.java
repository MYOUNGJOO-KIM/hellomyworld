package kr.co.jrindustry.audit.audit.service;

import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.jrindustry.audit.audit.dto.BoardPrfAttachment;
import kr.co.jrindustry.audit.audit.entity.PrfAttachment;
import kr.co.jrindustry.audit.audit.entity.PrfData;
import kr.co.jrindustry.audit.audit.repository.PrfAttachmentRepo;
import kr.co.jrindustry.audit.audit.repository.PrfDataRepo;
import kr.co.jrindustry.audit.audit.specification.PrfAttachmentSpacification;
import kr.co.jrindustry.audit.audit.specification.PrfDataSpecification;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrfAttachmentService { 

    private final PrfAttachmentRepo repo;
    private final PrfDataRepo dtrepo;

    public List<PrfAttachment> getAttachmentList(int page, int size, String searchStd, String searchEd, PrfAttachment prfAttachment){
        
        PrfAttachmentSpacification spec = new PrfAttachmentSpacification(prfAttachment);

        Pageable pageable = PageRequest.of(page, size, Sort.by("attachSeq"));
        Page<PrfAttachment> resultPage = repo.findAll(spec, pageable);
        
        return resultPage.getContent();
    }



    public List<BoardPrfAttachment> getChildCategoryAttachList(int page, int size, String searchKey, String searchStr, String searchStd, String searchEd, Character printYn, PrfAttachment prfAttachment) {
        try{
            if(page > 0){
                page -= 1;
            }
            
            List<PrfAttachment> prfAttachmentList = repo.findByIdWithChildren(prfAttachment.getCatCd(), searchKey,  searchStr, (page * size),  size, searchStd, searchEd, printYn);
            Long totalCnt = repo.findByIdWithChildrenCnt( prfAttachment.getCatCd(), searchKey, searchStr, searchStd, searchEd, printYn);
            
            List<BoardPrfAttachment> prfBoardAttachmentList = new ArrayList<BoardPrfAttachment>();

            for(PrfAttachment attach : prfAttachmentList){

                PrfData prfData = new PrfData();
                prfData.setPrfSeq(attach.getPrfSeq());
                PrfDataSpecification spec = new PrfDataSpecification(prfData);
                List<PrfData> prfDataList = dtrepo.findAll(spec);

                for(PrfData data : prfDataList){
                    attach.setPrfNm(data.getPrfNm());
                    attach.setPrfDesc(data.getPrfDesc());
                }

                URL url = new URL(attach.getAttachFilePath());
                String fileName = Paths.get(url.getPath()).getFileName().toString();
                attach.setFileNm(fileName);
                
                BoardPrfAttachment bpa = new BoardPrfAttachment(attach);
                bpa.setId(attach.getAttachSeq());
                bpa.setTotalCnt(totalCnt);
                bpa.setPrfNm(attach.getPrfNm());
                bpa.setPrfDesc(attach.getPrfDesc());
                bpa.setFileNm(attach.getFileNm());
                prfBoardAttachmentList.add(bpa);
            }
            return prfBoardAttachmentList;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PrfAttachment insertAttachment(PrfAttachment prfAttachment){
        if( prfAttachment.getPrfSeq() == null ){
                throw new RuntimeException("Requires getPrfSeq");//500
            
            } else {
                return repo.save(prfAttachment);
         }
    }

    @Transactional
    public PrfAttachment updateAttachment(PrfAttachment prfAttachment){

        PrfAttachment prfAttachEntity = findById(prfAttachment.getAttachSeq());

        prfAttachEntity.setCatCd(prfAttachment.getCatCd());
        prfAttachEntity.setCatNm(prfAttachment.getCatNm());
        prfAttachEntity.setAttachFilePath(prfAttachment.getAttachFilePath());
        prfAttachEntity.setAttachFileStream(prfAttachment.getAttachFileStream());
        
        return repo.save(prfAttachEntity);
        
    }

    @Transactional
    public PrfAttachment deleteAttachment(PrfAttachment prfAttachment){

        PrfAttachment prfAttachEntity = findById(prfAttachment.getAttachSeq());

        prfAttachEntity.setDelYn(prfAttachment.getDelYn());
        return repo.save(prfAttachEntity);
    }

    public PrfAttachment findById(BigInteger attachSeq) {
        
        Optional<PrfAttachment> optionalCategory = repo.findById(attachSeq);
        
        if (optionalCategory.isPresent()) {
            return optionalCategory.get();
        } else {
            throw new RuntimeException("Requires an existing id(attachSeq)");
        }
    }

    
    
}

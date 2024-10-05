package kr.co.dearmydiary.audit.audit.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import kr.co.dearmydiary.audit.audit.dto.BoardPrfCategory;
import kr.co.dearmydiary.audit.audit.dto.RsTreeDataDto;
import kr.co.dearmydiary.audit.audit.entity.PrfAttachment;
import kr.co.dearmydiary.audit.audit.entity.PrfCategory;
import kr.co.dearmydiary.audit.audit.entity.PrfCategoryMgt;
import kr.co.dearmydiary.audit.audit.entity.PrfData;
import kr.co.dearmydiary.audit.audit.entity.PrfDataMgt;
import kr.co.dearmydiary.audit.audit.repository.PrfAttachmentRepo;
import kr.co.dearmydiary.audit.audit.repository.PrfCategoryMgtRepo;
import kr.co.dearmydiary.audit.audit.repository.PrfCategoryRepo;
import kr.co.dearmydiary.audit.audit.repository.PrfDataMgtRepo;
import kr.co.dearmydiary.audit.audit.repository.PrfDataRepo;
import kr.co.dearmydiary.audit.audit.specification.PrfAttachmentSpacification;
import kr.co.dearmydiary.audit.audit.specification.PrfCategoryMgtSpecification;
import kr.co.dearmydiary.audit.audit.specification.PrfCategorySpecification;
import kr.co.dearmydiary.audit.audit.specification.PrfDataMgtSpacification;
import kr.co.dearmydiary.audit.audit.specification.PrfDataSpecification;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrfCategoryService { 

    private final EntityManager entityManager;
    private final PrfCategoryRepo repo;
    private final PrfCategoryMgtService pcms;
    private final PrfCategoryMgtRepo pcmr;
    private final PrfDataRepo pdr;
    private final PrfAttachmentRepo par;
    private final PrfDataMgtRepo pdmr;
    Logger logger = LoggerFactory.getLogger(PrfCategoryService.class);

    public Page<PrfCategory> getAllCategory( int page, int size, String searchStd, String searchEd, PrfCategory prfCategory ){

        if(page > 0){//자동 1-- pageable 초깃값이 0이므로
            page -= 1;
        }

        PrfCategorySpecification spec = new PrfCategorySpecification(prfCategory);
        Pageable pageable = PageRequest.of(page, size, Sort.by("catSeq"));
        Page<PrfCategory> resultPage = repo.findAll(spec, pageable);

        return resultPage;
    }

    public List<PrfCategory> getAllCategoryList(PrfCategory prfCategory ){
        PrfCategorySpecification spec = new PrfCategorySpecification(prfCategory);
        List<PrfCategory> resultPage = repo.findAll(spec);
        
        return resultPage;
    }

    public List<PrfCategory> getCategoryList( int page, int size, String searchStd, String searchEd, PrfCategory prfCategory ){

        if(page > 0){
            page -= 1;
        }

        PrfCategorySpecification spec = new PrfCategorySpecification(prfCategory);
        Pageable pageable = PageRequest.of(page, size, Sort.by("catSeq"));
        Page<PrfCategory> resultPage = repo.findAll(spec, pageable);

        return resultPage.getContent();
    }

    //@Transactional
    public List<RsTreeDataDto> getCategoryListTree(PrfCategory prfCategory) {
        
        Integer totalCnt = repo.findByIdWithChildrenCnt(null, prfCategory.getMgtYn(), prfCategory.getPrintYn(), null,null);
        List<PrfCategory> list = repo.findByIdWithChildren(null, prfCategory.getMgtYn(), prfCategory.getPrintYn(), null, null, 0, totalCnt);
        
        Map<String, RsTreeDataDto> categoryMap = list.stream()
            .collect(Collectors.toMap(PrfCategory::getCatCd, this::convertToDto));

        List<RsTreeDataDto> rootNodes = new ArrayList<>();

        for (PrfCategory category : list) {
            RsTreeDataDto dto = categoryMap.get(category.getCatCd());
            if (category.getUpCatCd() != null && !category.getUpCatCd().isEmpty()) {
                if (categoryMap.containsKey(category.getUpCatCd())) {
                    RsTreeDataDto parentDto = categoryMap.get(category.getUpCatCd());
                    if (parentDto != null) {
                        parentDto.getChildren().add(dto);
                    }
                }
            } else {
                rootNodes.add(dto);
            }
        }
        
        return rootNodes;
    }

    public List<BoardPrfCategory> getChildCategoryList(int page, int size, String searchKey, String searchStr, PrfCategory prfCategory ) {

        if(page > 0){
            page -= 1;
        }
        Integer totalCnt = repo.findByIdWithChildrenCnt( prfCategory.getCatCd(), null, null, searchKey,  searchStr);
        List<PrfCategory> prfCategoryList = repo.findByIdWithChildren(prfCategory.getCatCd(), null, null, searchKey, searchStr, (page * size), size);
        List<BoardPrfCategory> boardPrfCategoryList = new ArrayList<BoardPrfCategory>();


        for(PrfCategory entity : prfCategoryList){
            BoardPrfCategory bdto = new BoardPrfCategory(entity);
            bdto.setId(bdto.getCatSeq());
            bdto.setTotalCnt(totalCnt);
            boardPrfCategoryList.add(bdto);
        }

        return boardPrfCategoryList;
    }

    public List<Map<String, String>> getParentCategories(PrfCategory prfCategory) {

        //List<PrfCategory> prfCategoryList = repo.findByIdWithParents(prfCategory.getCatCd());

        Long totalCnt = repo.count();
        List<PrfCategory> list = repo.findByIdWithChildren(null, null, null, null, null, 0, Long.valueOf(totalCnt).intValue());

        List<Map<String, String>> mapList = list.stream()
        .map(category -> Map.of(
            "key", category.getCatCd(),
            "value", category.getCatNm()
        ))
        .collect(Collectors.toList());

        return mapList;
    }

    public PrfCategory insertCategory(PrfCategory prfCategory){

        if( prfCategory.getCatCd() == null || prfCategory.getCatCd().isEmpty() ){
            throw new RuntimeException("Requires catCd");//500 front 예외 필

        } else if (findByIdCatCd(PrfCategory.builder().catCd(prfCategory.getCatCd()).build()) != null){
            throw new RuntimeException("Duplicate CatCd");//500 front 예외 필

        } else {
            // int newSeq = repo.findLastCatSeq();
            
            // prfCategory.setCatSeq(new BigInteger(String.valueOf(newSeq)));
            return repo.save(prfCategory);
        }
    }

    @Transactional
    public PrfCategory updateCategory(PrfCategory prfCategory, boolean deleteYn){

        // delete부분 시작
        // PrfCategory prfCtEntity = repo.findById(prfCategory.getCatSeq()).get();
        
        // int totalCnt = repo.findByIdWithChildrenCnt( prfCtEntity.getCatCd(), null,  null);
        // delete부분 종료

        //PrfCategory prfCtEntity = findById(id);//front 수정 필
        Optional<PrfCategory> optPrfCtEntity = repo.findById(prfCategory.getCatSeq());
        
        if(optPrfCtEntity.isPresent()){

            PrfCategory prfCtEntity = optPrfCtEntity.get();

            //PrfCategory findParent = new PrfCategory().builder().catCd(prfCtEntity.getUpCatCd()).build();
            PrfCategory catCdCkCategory = findByIdCatCd(PrfCategory.builder().catCd(prfCategory.getCatCd()).build());
            PrfCategory catSeqCkCategory = findByIdCatCd(PrfCategory.builder().catSeq(prfCategory.getCatSeq()).build());
            if(catCdCkCategory != null && catSeqCkCategory != null && catCdCkCategory.getCatCd() != catSeqCkCategory.getCatCd()){
                //같은 항목 수정하는 거면 넘어가야해서 findByIdCatCd(PrfCategory.builder().catSeq(prfCategory.getCatSeq()).catCd(prfCategory.getCatCd()).build()) == null
                throw new RuntimeException("Duplicate CatCd");//500 front 예외 필
            }

            if( prfCategory.getCatCd() == null || prfCategory.getCatCd().isEmpty() ){
                throw new RuntimeException("Requires catCd");//500 front 예외 필
            }  else if( prfCtEntity.getUpCatCd() != null && findByIdCatCd(PrfCategory.builder().catCd(prfCtEntity.getUpCatCd()).build()) == null ){//child 의 부모 매핑 확인
                throw new RuntimeException("upCatCd did not exist");//500 front 예외 필
            } else if ( prfCtEntity.getUpCatCd() != null && findByIdCatCd(PrfCategory.builder().catCd(prfCategory.getUpCatCd()).build()) == null ){//child 의 부모 매핑 확인
                throw new RuntimeException("upCatCd does not exist");//500 front 예외 필
            } else {
    
                int totalCnt = repo.findByIdWithChildrenCnt( prfCtEntity.getCatCd(), null, null, null,  null);
                
                List<PrfCategory> childCategoryList = repo.findByIdWithChildren(prfCtEntity.getCatCd(), null, null, null, null, 0, totalCnt);
                //category 첫 번째 자식 select
                Map<String, List<PrfCategory>> categoryMap = childCategoryList.stream()
                .collect(Collectors.groupingBy(PrfCategory::getUpCatCd));
                List<PrfCategory> firstChildCategoryList = categoryMap.get(prfCtEntity.getCatCd());

                PrfCategoryMgt prfCategoryMgt = PrfCategoryMgt.builder().catCd(prfCtEntity.getCatCd()).build();
                PrfCategoryMgtSpecification pcmSpec = new PrfCategoryMgtSpecification(prfCategoryMgt);
                //int cnt = Long.valueOf(pcmr.count()).intValue();
                //List<PrfCategoryMgt> childCategoryMgtList = pcmr.findByIdWithChildren(prfCtEntity.getCatCd(), null, null, 0, Long.valueOf(pcmr.count()).intValue(), null, null);
                //mgt 첫 번째 자식 select
                List<PrfCategoryMgt> firstChildCategoryMgtList = pcmr.findAll(pcmSpec);

                PrfData prfData = PrfData.builder().catCd(prfCtEntity.getCatCd()).build();
                PrfDataSpecification pdSpec = new PrfDataSpecification(prfData);
                //List<PrfData> childDataList = pdr.findByIdWithChildren(prfCtEntity.getCatCd(), null, null, 0, Long.valueOf(pdr.count()).intValue(), null, null);
                //data 첫 번째 자식 select
                List<PrfData> firstChildDataList = pdr.findAll(pdSpec);

                PrfAttachment prfAttachment = PrfAttachment.builder().catCd(prfCtEntity.getCatCd()).build();
                PrfAttachmentSpacification paSpec = new PrfAttachmentSpacification(prfAttachment);
                //List<PrfAttachment> childPrfAttachmentList = par.findByIdWithChildren(prfCtEntity.getCatCd(), null, null, 0, Long.valueOf(par.count()).intValue(), null, null);
                //attachment 첫 번째 자식 select
                List<PrfAttachment> firstChildPrfAttachmentList = par.findAll(paSpec);

                PrfDataMgt prfDataMgt = PrfDataMgt.builder().catCd(prfCtEntity.getCatCd()).build();
                PrfDataMgtSpacification pdmSpec = new PrfDataMgtSpacification(prfDataMgt);
                List<PrfDataMgt> firstChildPrfDataMgtList = pdmr.findAll(pdmSpec);

                if(firstChildCategoryList != null){
                    for(PrfCategory prf : firstChildCategoryList){
                        if(prf.getUpCatCd() != prfCategory.getCatCd()){
                            prf.setUpCatCd(prfCategory.getCatCd());
                        }
                        if(prf.getUpCatNm() != prfCategory.getCatNm()){
                            prf.setUpCatNm(prfCategory.getCatNm());
                        }
                        repo.save(prf);
                    }
                }

                if(firstChildCategoryMgtList != null){
                    for(PrfCategoryMgt prf : firstChildCategoryMgtList){
                        if(prf.getCatCd() != prfCategory.getCatCd()){
                            prf.setCatCd(prfCategory.getCatCd());
                        }
                        if(prf.getCatNm() != prfCategory.getCatNm()){
                            prf.setCatNm(prfCategory.getCatNm());
                        }
                        pcmr.save(prf);
                    }
                }

                if(firstChildDataList != null){
                    for(PrfData prf : firstChildDataList){
                        if(prf.getCatCd() != prfCategory.getCatCd()){
                            prf.setCatCd(prfCategory.getCatCd());
                        }
                        if(prf.getCatNm() != prfCategory.getCatNm()){
                            prf.setCatNm(prfCategory.getCatNm());
                        }
                        pdr.save(prf);
                    }
                }

                if(firstChildPrfAttachmentList != null){
                    for(PrfAttachment prf : firstChildPrfAttachmentList){
                        if(prf.getCatCd() != prfCategory.getCatCd()){
                            prf.setCatCd(prfCategory.getCatCd());
                        }
                        if(prf.getCatNm() != prfCategory.getCatNm()){
                            prf.setCatNm(prfCategory.getCatNm());
                        }
                        par.save(prf);
                    }
                }

                if(firstChildPrfDataMgtList != null){
                    for(PrfDataMgt pdm : firstChildPrfDataMgtList){
                        if(pdm.getCatCd() != prfCategory.getCatCd()){
                            pdm.setCatCd(prfCategory.getCatCd());
                        }
                        if(pdm.getCatNm() != prfCategory.getCatNm()){
                            pdm.setCatNm(prfCategory.getCatNm());
                        }
                        pdmr.save(pdm);
                    }
                }


                if(childCategoryList != null){
                    for(PrfCategory prf : childCategoryList){
                        if(prfCategory.getMgtYn() != null && prfCategory.getMgtYn() != prf.getMgtYn()){
                            prf.setMgtYn(prfCategory.getMgtYn());
                        }
                        if(prfCategory.getPrintYn() != null && prfCategory.getPrintYn() != prf.getPrintYn()){
                            prf.setPrintYn(prfCategory.getPrintYn());
                        }
                        // if(prfCategory.getDelYn() != null && prfCategory.getDelYn() != prf.getDelYn()){
                        //     prf.setDelYn(prfCategory.getDelYn());
                        // }
                        repo.save(prf);
                    }
                }

                // if(childCategoryMgtList != null){
                //     for(PrfCategoryMgt prf : childCategoryMgtList){
                //         if(prfCategory.getDelYn() != null && prfCategory.getDelYn() != prf.getDelYn()){
                //             prf.setDelYn(prfCategory.getDelYn());
                //             pcmr.save(prf);
                //         }
                //     }
                // }

                // if(childDataList != null){
                //     for(PrfData prf : childDataList){
                //         if(prfCategory.getDelYn() != null && prfCategory.getDelYn() != prf.getDelYn()){
                //             prf.setDelYn(prfCategory.getDelYn());
                //             pdr.save(prf);
                //         }
                //     }
                // }

                // if(childPrfAttachmentList != null){
                //     for(PrfAttachment prf : childPrfAttachmentList){
                //         if(prfCategory.getDelYn() != null && prfCategory.getDelYn() != prf.getDelYn()){
                //             prf.setDelYn(prfCategory.getDelYn());
                //             par.save(prf);
                //         }
                //     }
                // }
    
                repo.save(prfCategory);//부모 저장
    
                return prfCtEntity;
            }
        } else {
            throw new RuntimeException("Not a valid catSeq");//500 front 예외 필
        }
    }

    @Transactional
    public PrfCategory deleteCategory(PrfCategory prfCategory){

        Optional<PrfCategory> optPrfCtEntity = repo.findById(prfCategory.getCatSeq());

        if(optPrfCtEntity.isPresent()){

            PrfCategory prfCtEntity = optPrfCtEntity.get();
            prfCtEntity.setDelYn('y');
            //PrfCategory findParent = new PrfCategory().builder().catCd(prfCtEntity.getUpCatCd()).build();
            
            if( prfCategory.getCatCd() == null || prfCategory.getCatCd().isEmpty() ){
                throw new RuntimeException("Requires catCd");//500 front 예외 필
            }  else if( prfCtEntity.getUpCatCd() != null && findByIdCatCd(PrfCategory.builder().catCd(prfCtEntity.getUpCatCd()).build()) == null ){//child 의 부모 매핑 확인
                throw new RuntimeException("upCatCd did not exist");//500 front 예외 필
            } else if ( prfCtEntity.getUpCatCd() != null && findByIdCatCd(PrfCategory.builder().catCd(prfCategory.getUpCatCd()).build()) == null ){//child 의 부모 매핑 확인
                throw new RuntimeException("upCatCd does not exist");//500 front 예외 필
            } else {

                List<PrfCategory> childCategoryList = repo.findByIdWithChildren(prfCtEntity.getCatCd(), null, null, null, null, 0, Long.valueOf(repo.count()).intValue());
                
                List<PrfCategoryMgt> childCategoryMgtList = pcmr.findByIdWithChildren(prfCtEntity.getCatCd(), null, null, 0, Long.valueOf(pcmr.count()).intValue(), null, null);
                
                List<PrfData> childDataList = pdr.findByIdWithChildren(prfCtEntity.getCatCd(), null, null, 0, Long.valueOf(pdr.count()).intValue(), null, null);
                
                List<PrfAttachment> childPrfAttachmentList = par.findByIdWithChildren(prfCtEntity.getCatCd(), null, null, 0, Long.valueOf(par.count()).intValue(), null, null, null);
                //2024-09-06 이상함. 쿼리 조회값과 다르게 가져옴.

                List<PrfDataMgt> childPrfDataMgtList = pdmr.findByIdWithChildren(prfCtEntity.getCatCd(), null, null, 0, Long.valueOf(pdmr.count()).intValue(), null, null);


                if(childCategoryList != null){
                    for(PrfCategory prf : childCategoryList){
                        if(prfCategory.getDelYn() != null && prfCtEntity.getDelYn() != prf.getDelYn()){
                            prf.setDelYn('y');
                        }
                        repo.save(prf);
                    }
                }

                if(childCategoryMgtList != null){
                    for(PrfCategoryMgt prf : childCategoryMgtList){
                        if(prfCategory.getDelYn() != null && prfCtEntity.getDelYn() != prf.getDelYn()){
                            prf.setDelYn('y');
                            pcmr.save(prf);
                        }
                    }
                }

                if(childDataList != null){
                    for(PrfData prf : childDataList){
                        if(prfCategory.getDelYn() != null && prfCtEntity.getDelYn() != prf.getDelYn()){
                            prf.setDelYn('y');
                            pdr.save(prf);
                        }
                    }
                }

                if(childPrfAttachmentList != null){
                    for(PrfAttachment prf : childPrfAttachmentList){
                        if(prfCategory.getDelYn() != null && prfCtEntity.getDelYn() != prf.getDelYn()){
                            prf.setDelYn('y');
                            par.save(prf);
                        }
                    }
                }

                if(childPrfDataMgtList != null){
                    for(PrfDataMgt prf : childPrfDataMgtList){
                        if(prfCategory.getDelYn() != null && prfCtEntity.getDelYn() != prf.getDelYn()){
                            prf.setDelYn('y');
                            pdmr.save(prf);
                        }
                    }
                }

            }

            return repo.save(prfCtEntity);
        } 
        else {
            throw new RuntimeException("Not a valid catSeq");//500 front 예외 필
        }
    }

    public PrfCategory findByIdCatCd(PrfCategory prfCategory) {

        Optional<PrfCategory> optionalCategory;
        PrfCategorySpecification spec = new PrfCategorySpecification(prfCategory);
        optionalCategory = repo.findOne(spec);

        if (optionalCategory.isPresent()) {
            
            return optionalCategory.get();
        } else {
            return null;
        }
    }

    private RsTreeDataDto convertToDto(PrfCategory category) {
        RsTreeDataDto rtDto = RsTreeDataDto.builder().key(category.getCatCd()).title(category.getCatNm()).children(new ArrayList<>()).mgtYn(category.getMgtYn()).printYn(category.getPrintYn()).upCatCd(category.getUpCatCd()).upCatNm(category.getUpCatNm()).catSeq(category.getCatSeq()).build();

        return rtDto;
    }

    
}

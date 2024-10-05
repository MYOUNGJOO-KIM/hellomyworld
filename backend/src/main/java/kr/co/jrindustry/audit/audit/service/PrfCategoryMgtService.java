package kr.co.jrindustry.audit.audit.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jrindustry.audit.audit.dto.BoardPrfCategoryMgt;
import kr.co.jrindustry.audit.audit.entity.PrfCategoryMgt;
import kr.co.jrindustry.audit.audit.repository.PrfCategoryMgtRepo;
import kr.co.jrindustry.audit.audit.specification.PrfCategoryMgtSpecification;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrfCategoryMgtService {

    private final PrfCategoryMgtRepo repo;

    public List<BoardPrfCategoryMgt> getCategoryMgtList(int page, int size, String searchKey, String searchStr, String searchStd, String searchEd, PrfCategoryMgt prfCategoryMgt){

        if(page > 0){
            page -= 1;
        }

        PrfCategoryMgtSpecification spec = new PrfCategoryMgtSpecification(prfCategoryMgt);

        Pageable pageable = PageRequest.of(page, size, Sort.by("mgtOrderSeq"));
        //List<PrfCategoryMgt> prfCategoryMgtList = repo.findByIdWithChildren(prfCategoryMgt.getCatCd(), searchKey, searchStr, page, size, searchStd, searchEd);
        Page<PrfCategoryMgt> prfCategoryMgtList = repo.findAll(spec, pageable);
        Long totalCnt = repo.count(spec);

        List<BoardPrfCategoryMgt> boardPrfCategoryMgtList = new ArrayList<BoardPrfCategoryMgt>();
        for(PrfCategoryMgt entity : prfCategoryMgtList){
            BoardPrfCategoryMgt bdto = new BoardPrfCategoryMgt(entity);
            bdto.setId(bdto.getMgtSeq());
            bdto.setTotalCnt(totalCnt);
            boardPrfCategoryMgtList.add(bdto);
        }

        return boardPrfCategoryMgtList;
    }

    public PrfCategoryMgt insertCategoryMgt(PrfCategoryMgt prfCategoryMgt){

        if( prfCategoryMgt.getCatCd() == null || prfCategoryMgt.getCatCd().isEmpty() ){
            throw new RuntimeException("Requires catCd");//500

        } else {
            return repo.save(prfCategoryMgt);
        }
    }

    public PrfCategoryMgt updateCategoryMgt(PrfCategoryMgt prfCategoryMgt){

        PrfCategoryMgt prfCtMgtEntity = findById(prfCategoryMgt.getMgtSeq());

        if( prfCategoryMgt.getCatCd() == null || prfCategoryMgt.getCatCd().isEmpty() ) {
            throw new RuntimeException("Requires an existing id(catCd)");

        } 

        prfCtMgtEntity.setCatCd(prfCategoryMgt.getCatCd());
        prfCtMgtEntity.setCatNm(prfCategoryMgt.getCatNm());
        prfCtMgtEntity.setMgtNm(prfCategoryMgt.getMgtNm());
        prfCtMgtEntity.setDataType(prfCategoryMgt.getDataType());
        prfCtMgtEntity.setMgtOrderSeq(prfCategoryMgt.getMgtOrderSeq());
        
        return repo.save(prfCategoryMgt);
        
        
    }

    @Transactional
    public PrfCategoryMgt deleteCategoryMgt(PrfCategoryMgt prfCategoryMgt){
        //ToDoList
        //이거 삭제하면 딸려있는 증빙자료들도 전부 삭제
        PrfCategoryMgt prfCtMgtEntity = findById(prfCategoryMgt.getMgtSeq());
        prfCtMgtEntity.setDelYn('y');

        return repo.save(prfCtMgtEntity);
    }


    public boolean existsById(BigInteger mgtSeq) {
        return repo.findById(mgtSeq).isPresent();
    }

    public PrfCategoryMgt findById(BigInteger mgtSeq) {
        
        Optional<PrfCategoryMgt> optionalCategory = repo.findById(mgtSeq);
        
        if (optionalCategory.isPresent()) {
            return optionalCategory.get();
        } else {
            throw new RuntimeException("Requires an existing id(mgtSeq)");
        }
    }
}

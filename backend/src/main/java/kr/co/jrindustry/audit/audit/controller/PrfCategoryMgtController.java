package kr.co.jrindustry.audit.audit.controller;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import kr.co.jrindustry.audit.audit.dto.BoardPrfCategoryMgt;
import kr.co.jrindustry.audit.audit.entity.PrfCategoryMgt;
import kr.co.jrindustry.audit.audit.service.PrfCategoryMgtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
public class PrfCategoryMgtController {

    private final PrfCategoryMgtService service;

    @PostMapping("/category_mgt/get")
    public List<BoardPrfCategoryMgt> getCategoryMgtList(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false) String searchKey,
        @RequestParam(required = false) String searchStr,
        @RequestParam(required = false) String searchStd,
        @RequestParam(required = false) String searchEd,
        @RequestBody PrfCategoryMgt prfCategoryMgt
        ){
        
        return service.getCategoryMgtList(page, size, searchKey, searchStr, searchStd, searchEd, prfCategoryMgt);
    }
    
    @PutMapping("/category_mgt/put")
    public PrfCategoryMgt putCategoryMgt(@RequestBody PrfCategoryMgt prfCategoryMgt){
        return service.updateCategoryMgt(prfCategoryMgt);
    }
    
    @PostMapping("/category_mgt/post")
    public PrfCategoryMgt postCategoryMgt(@RequestBody PrfCategoryMgt prfCategoryMgt){
        return service.insertCategoryMgt(prfCategoryMgt);
    }
    
    @PutMapping("/category_mgt/delete")
    public void deleteCategoryMgt(@RequestBody PrfCategoryMgt prfCategoryMgt){
        service.deleteCategoryMgt(prfCategoryMgt);
    }
}

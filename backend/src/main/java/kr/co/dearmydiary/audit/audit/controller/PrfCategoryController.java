package kr.co.dearmydiary.audit.audit.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

import kr.co.dearmydiary.audit.audit.dto.BoardPrfCategory;
import kr.co.dearmydiary.audit.audit.dto.RsTreeDataDto;
import kr.co.dearmydiary.audit.audit.entity.PrfCategory;
import kr.co.dearmydiary.audit.audit.service.PrfCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
public class PrfCategoryController {

    private final PrfCategoryService service;

    @PostMapping("/category/get")
    public List<PrfCategory> getCategoryList(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "") String searchKey,
        @RequestParam(required = false, defaultValue = "") String searchStr,
        @RequestParam(required = false, defaultValue = "") String searchStd,
        @RequestParam(required = false, defaultValue = "") String searchEd,
        @RequestBody PrfCategory prfCategory
        ){
        
        return service.getCategoryList(page, size, searchStd, searchEd, prfCategory);
    }

    @PostMapping("/category/getTree")
    public List<RsTreeDataDto> getCategoryListTree(
        @RequestBody PrfCategory prfCategory
        ){

        return service.getCategoryListTree(prfCategory);
    }

    @PostMapping("/category/getChildCategoryList")
    public List<BoardPrfCategory> getChildCategoryList(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false) String searchKey,
        @RequestParam(required = false) String searchStr,
        @RequestBody PrfCategory prfCategory
        ){
            
        return service.getChildCategoryList(page, size, searchKey, searchStr, prfCategory);
    }

    @PostMapping("/category/getParentCategoryList")
    public List<Map<String, String>> getParentCategoryList(
        @RequestBody PrfCategory prfCategory
        ){
        
        return service.getParentCategories(prfCategory);
    }
    
    @PutMapping("/category/put")
    public PrfCategory putCategory(@RequestBody PrfCategory prfCategory){//@RequestParam String id, 

        return service.updateCategory(prfCategory, false);//update after PrfCategory
    }

    @PostMapping("/category/post")
    public PrfCategory postCategory(
        @RequestBody PrfCategory prfCategory){

        return service.insertCategory(prfCategory);
    }
    
    @PutMapping("/category/delete")
    public void deleteCategory(
        @RequestBody PrfCategory prfCategory
        ){

        service.deleteCategory(prfCategory);
    }
}

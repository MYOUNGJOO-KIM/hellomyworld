package kr.co.jrindustry.audit.audit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import kr.co.jrindustry.audit.audit.dto.BoardPrfAttachment;
import kr.co.jrindustry.audit.audit.entity.PrfAttachment;
import kr.co.jrindustry.audit.audit.service.PrfAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
public class PrfAttachmentController { 

    private final PrfAttachmentService service;
    
    @PostMapping("/attachment/getChildCategoryAttachList")
    public List<BoardPrfAttachment> getChildCategoryAttachList(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false) String searchKey,
        @RequestParam(required = false) String searchStr,
        @RequestParam(required = false) String searchStd,
        @RequestParam(required = false) String searchEd,
        @RequestParam(required = false) String mgtYn,
        @RequestParam(required = false) Character printYn,
        @RequestBody PrfAttachment prfAttachment
    ){
        
        return service.getChildCategoryAttachList(page, size, searchKey, searchStr, searchStd, searchEd, printYn, prfAttachment);
    }
        
    @PostMapping("/attachment/get")
    public List<PrfAttachment> getAttachmentList(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "2024-06-15") String searchStd,
        @RequestParam(required = false, defaultValue = "2024-07-22") String searchEd,
        @RequestBody PrfAttachment prfAttachment
    ){
        return service.getAttachmentList(page, size, searchStd, searchEd, prfAttachment);
    }

    @PutMapping("/attachment/put")
    public PrfAttachment putAttachment(@RequestBody PrfAttachment prfAttachment){
        return service.updateAttachment(prfAttachment);
    }
    
    @PostMapping("/attachment/post")
    public PrfAttachment PostAttachment(@RequestBody PrfAttachment prfAttachment){
        return service.insertAttachment(prfAttachment);
    }
    
    @PutMapping("/attachment/delete")
    public PrfAttachment DeleteAttachment(@RequestBody PrfAttachment prfAttachment){
        return service.deleteAttachment(prfAttachment);
    }
}

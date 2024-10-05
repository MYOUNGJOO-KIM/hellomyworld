package kr.co.jrindustry.audit.audit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import kr.co.jrindustry.audit.audit.entity.PrfDataMgt;
import kr.co.jrindustry.audit.audit.service.PrfDataMgtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
public class PrfDataMgtController {

    private final PrfDataMgtService service;

    @PostMapping("/data_mgt/get")
    public List<PrfDataMgt> getAttachMgtList(PrfDataMgt PrfDataMgt){
        return service.getDataMgtList();
    }
    
    @PutMapping("/data_mgt/put")
    public PrfDataMgt putMgtAttach(PrfDataMgt PrfDataMgt){
        return service.updateMgtAttach(PrfDataMgt);
    }
    
    @PostMapping("/data_mgt/post")
    public PrfDataMgt PostMgtAttach(PrfDataMgt PrfDataMgt){
        return service.updateMgtAttach(PrfDataMgt);
    }
    
    @PutMapping("/data_mgt/delete")
    public void DeleteMgtAttach(PrfDataMgt PrfDataMgt){
        service.deleteMgtAttach(PrfDataMgt);
    }
}

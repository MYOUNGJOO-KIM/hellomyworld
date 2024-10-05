package kr.co.jrindustry.audit.audit.service;

import java.util.List;
import org.springframework.stereotype.Service;
import kr.co.jrindustry.audit.audit.entity.PrfDataMgt;
import kr.co.jrindustry.audit.audit.repository.PrfDataMgtRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrfDataMgtService {

    private final PrfDataMgtRepo repo;

    public List<PrfDataMgt> getDataMgtList(){
        return repo.findAll();
    }

    public PrfDataMgt updateMgtAttach(PrfDataMgt prfDataMgt){
        
        return repo.save(prfDataMgt);
    }

    public void deleteMgtAttach(PrfDataMgt prfDataMgt){
        repo.save(prfDataMgt);
    }

    
    
}
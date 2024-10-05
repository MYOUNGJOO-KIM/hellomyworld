package kr.co.dearmydiary.audit;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.dearmydiary.audit.audit.entity.PrfCategory;
import kr.co.dearmydiary.audit.audit.repository.PrfCategoryRepo;
import kr.co.dearmydiary.audit.audit.service.PrfDataService;

@SpringBootTest
public class PrfCategoryRepoTest {
    @Autowired
    PrfCategoryRepo repo;
    Logger logger = LoggerFactory.getLogger(PrfCategoryRepoTest.class);

    @Test
    public void testSelectAll(){
        List<PrfCategory> list = repo.findAll();

        for(PrfCategory record : list){
            logger.info("========================");
            logger.info(record.getCatCd());
            logger.info(record.getCatNm());
            logger.info("========================");
        }
    }
}

package kr.co.dearmydiary.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import kr.co.dearmydiary.audit.audit.repository.PrfCategoryRepo;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {
    
    private final PrfCategoryRepo repo;
    Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("/")
    public String root() {
        

        //return "index";
        return "redirect:/category/get";
    }
}

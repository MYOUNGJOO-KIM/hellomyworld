package kr.co.jrindustry.audit.audit.specification;


import org.springframework.data.jpa.domain.Specification;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import kr.co.jrindustry.audit.audit.entity.PrfDataMgt;

public class PrfDataMgtSpacification implements Specification<PrfDataMgt> {

    private PrfDataMgt prfDataMgt;

    public PrfDataMgtSpacification(PrfDataMgt prfDataMgt) {
        this.prfDataMgt = prfDataMgt;
    }
    
    @Override
    public Predicate toPredicate (Root<PrfDataMgt> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Predicate predicate = builder.conjunction();
 
        if (prfDataMgt.getCatCd() != null || prfDataMgt.getCatCd().isEmpty()) {
            predicate = builder.and(predicate, builder.equal(root.get("catCd"), prfDataMgt.getCatCd()));
        } 
        if (prfDataMgt.getPrfSeq() != null ) {
            predicate = builder.and(predicate, builder.equal(root.get("prfSeq"), prfDataMgt.getPrfSeq()));
        }
        if (prfDataMgt.getDataMgtSeq() != null ) {
            predicate = builder.and(predicate, builder.equal(root.get("dataMgtSeq"), prfDataMgt.getDataMgtSeq()));
        }


        return predicate;
    }
}

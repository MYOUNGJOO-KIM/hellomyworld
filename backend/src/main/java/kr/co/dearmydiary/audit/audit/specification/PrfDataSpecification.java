package kr.co.dearmydiary.audit.audit.specification;


import org.springframework.data.jpa.domain.Specification;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import kr.co.dearmydiary.audit.audit.entity.PrfData;
import lombok.NoArgsConstructor;

public class PrfDataSpecification implements Specification<PrfData> {

    private PrfData prfData;

    public PrfDataSpecification(PrfData prfData) {
        this.prfData = prfData;
    }
    
    @Override
    public Predicate toPredicate (Root<PrfData> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Predicate predicate = builder.conjunction();
 
        if (prfData.getCatCd() != null) {
            predicate = builder.and(predicate, builder.equal(root.get("catCd"), prfData.getCatCd()));
        } 
        if (prfData.getPrfSeq() != null) {
            predicate = builder.and(predicate, builder.equal(root.get("prfSeq"), prfData.getPrfSeq()));
        }
        predicate = builder.and(predicate, builder.equal(root.get("delYn"), 'n'));

        return predicate;
    }
}

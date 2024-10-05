package kr.co.dearmydiary.audit.audit.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.databind.util.ArrayBuilders.BooleanBuilder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import kr.co.dearmydiary.audit.audit.entity.PrfCategory;
import kr.co.dearmydiary.audit.audit.entity.PrfCategoryMgt;
import kr.co.dearmydiary.audit.audit.repository.PrfCategoryRepo;
import lombok.NoArgsConstructor;

public class PrfCategoryMgtSpecification implements Specification<PrfCategoryMgt> {

    private PrfCategoryMgt prfCategoryMgt;

    public PrfCategoryMgtSpecification(PrfCategoryMgt prfCategoryMgt) {
        this.prfCategoryMgt = prfCategoryMgt;
    }
    
    @Override
    public Predicate toPredicate (Root<PrfCategoryMgt> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Predicate predicate = builder.conjunction();
 
        if (prfCategoryMgt.getCatCd() != null && !prfCategoryMgt.getCatCd().isEmpty()) {
            predicate = builder.and(predicate, builder.equal(root.get("catCd"), prfCategoryMgt.getCatCd()));
        } 

        //builder.greaterThanOrEqualTo(), builder.lessThanOrEqualTo(), builder.like(), builder.in()

        // else if (criteria.getOperation().equalsIgnoreCase("<")) {
        //     return builder.lessThanOrEqualTo(
        //       root.<String> get(criteria.getKey()), criteria.getValue().toString());
        // } 
        // else if (criteria.getOperation().equalsIgnoreCase(":")) {
        //     if (root.get(criteria.getKey()).getJavaType() == String.class) {
        //         return builder.like(
        //           root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
        //     } else {
        //         return builder.equal(root.get(criteria.getKey()), criteria.getValue());
        //     }
        // }
        predicate = builder.and(predicate, builder.equal(root.get("delYn"), 'n'));

        return predicate;
    }

    // public static Specification<PrfCategoryMgt> hasCatCd(String catCd) {
    //     return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("catCd"), catCd);
    // }



    // public static List<PrfCategory> findByPrfCategory(PrfCategory prfCategoryEntity){
        

    //     //BooleanBuilder predicate = new BooleanBuilder();

    //     return new List<PrfCategory>;

    //     // if(prfCategoryEntity.getCatNm() != null) {
    //     //     spec = spec.and(testEntitySpecification.likeName(name));
    //     // }
    //     // if(content != null) {
    //     //     spec = spec.and(testEntitySpecification.likeContent(content));
    //     // }
    //     // return testEntityRepository.findAll(spec);
    //     //return 
    // }
}

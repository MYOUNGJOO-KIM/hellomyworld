package kr.co.dearmydiary.audit.audit.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.databind.util.ArrayBuilders.BooleanBuilder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import kr.co.dearmydiary.audit.audit.entity.PrfCategory;
import kr.co.dearmydiary.audit.audit.entity.PrfCategoryMgt;
import kr.co.dearmydiary.audit.audit.repository.PrfCategoryRepo;

public class PrfCategorySpecification implements Specification<PrfCategory> {

    private PrfCategory prfCategory;

    public PrfCategorySpecification(PrfCategory prfCategory) {
        this.prfCategory = prfCategory;
    }
    
    @Override
    public Predicate toPredicate (Root<PrfCategory> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Predicate predicate = builder.conjunction();
 
        if (prfCategory.getCatSeq() != null ) {//|| prfCategory.getCatCd().isEmpty()
            predicate = builder.and(predicate, builder.equal(root.get("catSeq"), prfCategory.getCatSeq()));
        } 
        if (prfCategory.getCatCd() != null ) {//|| prfCategory.getCatCd().isEmpty()
            predicate = builder.and(predicate, builder.equal(root.get("catCd"), prfCategory.getCatCd()));
        } 
        // if (prfCategory.getCatNm() != null ) {
        //     predicate = builder.and(predicate, builder.equal(root.get("catNm"), prfCategory.getCatNm()));
        // }
        if (prfCategory.getUpCatCd() != null ) {
            predicate = builder.and(predicate, builder.equal(root.get("upCatCd"), prfCategory.getUpCatCd()));
        }
        if (prfCategory.getMgtYn() != null ) {
            predicate = builder.and(predicate, builder.equal(root.get("mgtYn"), prfCategory.getMgtYn()));
        }

        // if(prfCategory.getSearchStr() != null){
        //     predicate = builder.like(root.get("searchStr"), "%" + prfCategory.getSearchStr() + "%");
        // }
        // if (prfCategory.getUpCatNm() != null ) {
        //     predicate = builder.and(predicate, builder.equal(root.get("upCatNm"), prfCategory.getUpCatNm()));
        // }
        
        predicate = builder.and(predicate, builder.equal(root.get("delYn"), 'n'));
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

        return predicate;
    }


    public static Specification<PrfCategory> withCatCd (String catCd){
        return (Root<PrfCategory> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            // LEFT JOIN
            Join<PrfCategory, PrfCategory> childrenJoin = root.join("children", JoinType.LEFT);
            //query.select(root).distinct(true);
            // SELECT with FETCH 오류남
            //query.distinct(true).select(root).where(cb.equal(root.get("catCd"), catCd));
            if(catCd != null && !catCd.isEmpty()){

                predicate = cb.and(predicate, cb.equal(root.get("catCd"), catCd));
                //query.where(cb.equal(root.get("catCd"), catCd));
            }
            // Add condition to exclude the initial catCd from the result
            Predicate excludeCatCdPredicate = cb.notEqual(root.get("catCd"), catCd);
            // ORDER BY
            // Apply filters
            query.where(predicate, excludeCatCdPredicate);

            // Order results by catCd in ascending order
            query.orderBy(cb.asc(root.get("catCd")));
            
            return query.getRestriction();
        };
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

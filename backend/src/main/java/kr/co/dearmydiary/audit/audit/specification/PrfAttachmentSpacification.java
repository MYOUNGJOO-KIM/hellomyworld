package kr.co.dearmydiary.audit.audit.specification;


import org.springframework.data.jpa.domain.Specification;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import kr.co.dearmydiary.audit.audit.entity.PrfAttachment;
import kr.co.dearmydiary.audit.audit.entity.PrfData;

public class PrfAttachmentSpacification implements Specification<PrfAttachment> {

    private PrfAttachment prfAttachment;

    public PrfAttachmentSpacification(PrfAttachment prfAttachment) {
        this.prfAttachment = prfAttachment;
    }
    
    @Override
    public Predicate toPredicate (Root<PrfAttachment> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Predicate predicate = builder.conjunction();
 
        if (prfAttachment.getCatCd() != null || prfAttachment.getCatCd().isEmpty()) {
            predicate = builder.and(predicate, builder.equal(root.get("catCd"), prfAttachment.getCatCd()));
        } 
        if (prfAttachment.getPrfSeq() != null ) {
            predicate = builder.and(predicate, builder.equal(root.get("prfSeq"), prfAttachment.getPrfSeq()));
        }
        if (prfAttachment.getAttachSeq() != null ) {
            predicate = builder.and(predicate, builder.equal(root.get("attachSeq"), prfAttachment.getAttachSeq()));
        }


        return predicate;
    }
}

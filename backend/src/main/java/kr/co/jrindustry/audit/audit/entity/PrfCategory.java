package kr.co.jrindustry.audit.audit.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "prf_category")
public class PrfCategory {

    public PrfCategory(PrfCategory prfCategory){
        this.catSeq = prfCategory.getCatSeq();
        this.catNm = prfCategory.getCatNm();
        this.catCd = prfCategory.getCatCd();
        this.upCatNm = prfCategory.getUpCatNm();
        this.upCatCd = prfCategory.getUpCatCd();
        this.printYn = prfCategory.getPrintYn();
        this.mgtYn = prfCategory.getMgtYn();
        this.delYn = prfCategory.getDelYn();
        this.inDt = prfCategory.getInDt();
        this.inId = prfCategory.getInId();
        this.chgDt = prfCategory.getChgDt();
        this.chgId = prfCategory.getChgId();
    }
    
    @Column(name = "cat_nm")
    String catNm;

    
    @Column(name = "cat_cd")
    String catCd;

    @Column(name = "up_cat_nm")
    String upCatNm;

    @Column(name = "up_cat_cd")
    String upCatCd;

    @Column(name = "print_yn")
    Character printYn;

    @Column(name = "mgt_yn")
    Character mgtYn;

    @Column(name = "del_yn")
    Character delYn = 'n';
    
    @Column(name = "in_dt")
    LocalDateTime inDt;

    @Column(name = "in_id")
    String inId;

    @Column(name = "chg_dt")
    LocalDateTime chgDt;

    @Column(name = "chg_id")
    String chgId;

    @Id
    @Column(name = "cat_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    BigInteger catSeq;

    @PrePersist
    protected void onCreate() {
        inDt = LocalDateTime.now();
        inId = "mjk931223in";
    }

    @PreUpdate
    protected void onUpdate() {
        chgDt = LocalDateTime.now();
        chgId = "mjk931223chg";
    }

    @PostLoad
    private void convertEmptyStringsToNull() {
        if (this.upCatNm != null && this.upCatNm.trim().isEmpty()) {
            this.upCatNm = null;
        }
        if (this.upCatCd != null && this.upCatCd.trim().isEmpty()) {
            this.upCatCd = null;
        }
        if (this.inId != null && this.inId.trim().isEmpty()) {
            this.inId = null;
        }
        if (this.chgId != null && this.chgId.trim().isEmpty()) {
            this.chgId = null;
        }
    }

    
    
}

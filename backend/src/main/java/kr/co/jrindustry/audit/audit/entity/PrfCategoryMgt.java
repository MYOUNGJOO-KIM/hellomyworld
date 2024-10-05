package kr.co.jrindustry.audit.audit.entity;


import java.math.BigInteger;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Cacheable;
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

@Cacheable(false)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "prf_category_mgt")
public class PrfCategoryMgt {

    public PrfCategoryMgt (PrfCategoryMgt prfCategoryMgt){
        this.mgtSeq = prfCategoryMgt.mgtSeq;
        this.mgtNm = prfCategoryMgt.mgtNm;
        this.catCd = prfCategoryMgt.catCd;
        this.catNm = prfCategoryMgt.catNm;
        this.dataType = prfCategoryMgt.dataType;
        this.delYn = prfCategoryMgt.delYn;
        this.inDt = prfCategoryMgt.inDt;
        this.inId = prfCategoryMgt.inId;
        this.chgDt = prfCategoryMgt.chgDt;
        this.chgId = prfCategoryMgt.chgId;
        this.mgtOrderSeq = prfCategoryMgt.mgtOrderSeq;
    }

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mgt_seq")
    BigInteger mgtSeq;

    @Column(name = "cat_nm")
    String catNm;

    
    @Column(name = "cat_cd")
    String catCd;

    @Column(name = "mgt_nm")
    String mgtNm;

    @Column(name = "data_type")
    String dataType;

    @Column(name = "del_yn")
    Character delYn = 'n';
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "in_dt")
    LocalDateTime inDt;

    @Column(name = "in_id")
    String inId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "chg_dt")
    LocalDateTime chgDt;

    @Column(name = "chg_id")
    String chgId;

    @Column(name = "mgt_order_seq")
    BigInteger mgtOrderSeq;


    @PrePersist
    protected void onCreate() {
        inDt = LocalDateTime.now();
        inId = "mjk931223in";
    }

    @PreUpdate
    protected void onUpdate() {
        chgDt = LocalDateTime.now();
        inId = "mjk931223in";
    }

    @PostLoad
    private void convertEmptyStringsToNull() {
        if (this.inId != null && this.inId.trim().isEmpty()) {
            this.inId = null;
        }
        
        if (this.chgId != null && this.chgId.trim().isEmpty()) {
            this.chgId = null;
        }
    }
}

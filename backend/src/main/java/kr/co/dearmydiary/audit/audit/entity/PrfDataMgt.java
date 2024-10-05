package kr.co.dearmydiary.audit.audit.entity;


import java.math.BigInteger;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "prf_data_mgt")
public class PrfDataMgt {

    public PrfDataMgt(String content){//PrfDataController insert에서 String list 매개변수가 호출
        this.content = content;
    }

    public PrfDataMgt(PrfDataMgt prfDataMgt){
        this.dataMgtSeq = prfDataMgt.getDataMgtSeq();
        this.prfSeq = prfDataMgt.getPrfSeq();
        this.content = prfDataMgt.getContent();
        this.delYn = prfDataMgt.getDelYn();
        this.inDt = prfDataMgt.getInDt();
        this.inId = prfDataMgt.getInId();
        this.chgDt = prfDataMgt.getChgDt();
        this.chgId = prfDataMgt.getChgId();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_mgt_seq")
    BigInteger dataMgtSeq;
    
    @Column(name = "prf_seq")
    BigInteger prfSeq;

    @Column(name = "cat_nm")
    String catNm;

    @Column(name = "cat_cd")
    String catCd; 
    
    @Column(name = "content")
    String content;

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
}






package kr.co.dearmydiary.audit.audit.entity;


import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import kr.co.dearmydiary.audit.audit.dto.BoardPrfCategoryMgt;
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
@Table(name = "prf_data")
public class PrfData {

    public PrfData(PrfData prfData){
        this.prfSeq = prfData.getPrfSeq();
        this.catNm = prfData.getCatNm();
        this.catCd = prfData.getCatCd();
        this.prfNm = prfData.getPrfNm();
        this.prfDesc = prfData.getPrfDesc();
        this.delYn = prfData.getDelYn();
        this.inDt = prfData.getInDt();
        this.inId = prfData.getInId();
        this.chgDt = prfData.getChgDt();
        this.chgId = prfData.getChgId();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prf_seq")
    BigInteger prfSeq;
    
    @Column(name = "cat_nm")
    String catNm;

    @Column(name = "cat_cd")
    String catCd; 

    @Column(name = "prf_nm")
    String prfNm;

    @Column(name = "prf_desc")
    String prfDesc;

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

    @Transient//mapping X
    List<PrfAttachment> attachList;

    @Transient//mapping X
    List<BoardPrfCategoryMgt> mgtList;

    @Transient//mapping X
    BigInteger catSeq;

    @Transient//mapping X
    String ocrTextOutput;


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

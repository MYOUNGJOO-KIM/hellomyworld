package kr.co.dearmydiary.audit.audit.entity;


import java.math.BigInteger;
import java.time.LocalDateTime;

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
@Table(name = "prf_attachment")
public class PrfAttachment {

    public PrfAttachment(PrfAttachment prfAttachment){
        this.attachSeq = prfAttachment.getAttachSeq();
        this.prfSeq = prfAttachment.getPrfSeq();
        this.catNm = prfAttachment.getCatNm();
        this.catCd = prfAttachment.getCatCd();
        this.attachFileStream = prfAttachment.getAttachFileStream();
        this.attachFilePath = prfAttachment.getAttachFilePath();
        this.delYn = prfAttachment.getDelYn();
        this.inDt = prfAttachment.getInDt();
        this.inId = prfAttachment.getInId();
        this.chgDt = prfAttachment.getChgDt();
        this.chgId = prfAttachment.getChgId();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attach_seq")
    BigInteger attachSeq;

    @Column(name = "prf_seq")
    BigInteger prfSeq;

    @Column(name = "cat_nm")
    String catNm;

    @Column(name = "cat_cd")
    String catCd;

    @Column(name = "attach_file_stream")
    String attachFileStream;

    @Column(name = "attach_file_path")
    String attachFilePath;

    @Column(name = "del_yn")
    char delYn = 'n';
    
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
    String prfNm;

    @Transient//mapping X
    String prfDesc;

    @Transient//mapping X
    String fileNm;

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

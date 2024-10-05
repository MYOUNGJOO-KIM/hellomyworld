package kr.co.dearmydiary.audit.audit.dto;

import java.math.BigInteger;

import kr.co.dearmydiary.audit.audit.entity.PrfCategory;
import lombok.Data;

@Data
public class BoardPrfCategory extends PrfCategory {
    BigInteger id;
    Integer totalCnt;

    public BoardPrfCategory (PrfCategory prfCategory){
        super(prfCategory);
    }
}

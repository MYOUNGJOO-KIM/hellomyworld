package kr.co.jrindustry.audit.audit.dto;

import java.math.BigInteger;
import kr.co.jrindustry.audit.audit.entity.PrfCategoryMgt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardPrfCategoryMgt extends PrfCategoryMgt {
    BigInteger id;
    Long totalCnt;

    public BoardPrfCategoryMgt (PrfCategoryMgt prfCategoryMgt){
        super(prfCategoryMgt);
    }
}

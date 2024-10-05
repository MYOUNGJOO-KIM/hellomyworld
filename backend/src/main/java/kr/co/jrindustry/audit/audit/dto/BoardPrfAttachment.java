package kr.co.jrindustry.audit.audit.dto;

import java.math.BigInteger;

import kr.co.jrindustry.audit.audit.entity.PrfAttachment;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BoardPrfAttachment extends PrfAttachment {
    BigInteger id;
    Long totalCnt;

    public BoardPrfAttachment (PrfAttachment prfAttachment){
        super(prfAttachment);
    }
}

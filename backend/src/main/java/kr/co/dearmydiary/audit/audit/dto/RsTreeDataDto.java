package kr.co.dearmydiary.audit.audit.dto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsTreeDataDto {
    private String key = "";
    private String title = "";
    private List<RsTreeDataDto> children = new ArrayList<>();

    private Character mgtYn;
    private Character printYn;
    private BigInteger catSeq;

    private String upCatCd = "";
    private String upCatNm = "";
}

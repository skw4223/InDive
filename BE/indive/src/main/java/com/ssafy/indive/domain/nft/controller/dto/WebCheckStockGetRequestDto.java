package com.ssafy.indive.domain.nft.controller.dto;

import com.ssafy.indive.domain.member.entity.Member;
import com.ssafy.indive.domain.nft.service.dto.ServiceCheckStockGetRequestDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebCheckStockGetRequestDto {

    private Long artistSeq;

    public WebCheckStockGetRequestDto(Long artistSeq) {
        this.artistSeq = artistSeq;
    }

    public ServiceCheckStockGetRequestDto convertToService() {
        return new ServiceCheckStockGetRequestDto(artistSeq);
    }
}

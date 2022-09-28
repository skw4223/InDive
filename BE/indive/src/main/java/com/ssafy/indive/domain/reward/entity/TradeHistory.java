package com.ssafy.indive.domain.reward.entity;

import com.ssafy.indive.domain.member.entity.Member;
import com.ssafy.indive.domain.reward.entity.enumeration.ProductionState;
import com.ssafy.indive.global.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Generated
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeHistory extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "th_seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "th_ri_seq")
    private RewardItem rewardItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "th_buyer_seq")
    private Member buyer;

    @Column(name = "th_state")
    @Enumerated(EnumType.STRING)
    private ProductionState state;

    @Column(name = "th_message")
    private String message;

    @Builder
    public TradeHistory(Long seq, RewardItem rewardItem, Member buyer, ProductionState state, String message) {
        this.seq = seq;
        this.rewardItem = rewardItem;
        this.buyer = buyer;
        this.state = state;
        this.message = message;
    }
}
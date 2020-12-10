package com.careerin.api.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "USER_TOKEN_STATUS")
@Getter
@Setter
public class UserTokenStatus extends AuditModel {

    private static final long serialVersionUID = 1694949693464047231L;

    @Column(name = "IS_REFRESH_ACTIVE")
    private Boolean isRefreshTokenActive;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User users;

    @OneToOne(optional = false, mappedBy = "userTokenStatus")
    private RefreshToken refreshToken;


}

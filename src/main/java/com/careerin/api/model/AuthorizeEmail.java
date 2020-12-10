package com.careerin.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "AUTHORIZE_EMAIL")
@Getter
@Setter
public class AuthorizeEmail extends AuditModel {

    private static final long serialVersionUID = 7260241098497849686L;

    @Column(name = "TOKEN", nullable = false, unique = true)
    private String token;

    @Column(name = "TOKEN_STATUS")
    @Enumerated(EnumType.STRING)
    private TokenStatus tokenStatus;

    @Column(name = "EXPIRY_DATE_TIME", nullable = false)
    private LocalDateTime expiryAt;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(nullable = false, name = "USER_ID", foreignKey =  @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User users;
}

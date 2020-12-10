package com.careerin.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "PASSWORD_RESET")
@Getter
@Setter
public class PasswordReset extends AuditModel {

    private static final long serialVersionUID = -985102386592592529L;

    @Column(name = "TOKEN_NAME", nullable = false, unique = true)
    private String token;

    @Column(name = "EXPIRY_DATE_TIME", nullable = false)
    private LocalDateTime expiryAt;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(nullable = false, name = "USER_ID", foreignKey =  @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User users;
}

package com.careerin.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "REFRESH_TOKEN")
@Getter
@Setter
public class RefreshToken extends AuditModel {

    private static final long serialVersionUID = 2142102751033894253L;

    @Column(name = "TOKEN", nullable = false, unique = true)
    @NaturalId(mutable = true)
    private String token;

    @Column(name = "REFRESH_COUNT")
    private Long refreshCount;

    @Column(name = "EXPIRY_DATE_TIME", nullable = false)
    private LocalDateTime expiryAt;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_TOKEN_ID", unique = true)
    private UserTokenStatus userTokenStatus;

    public void incrementRefreshCount() {
        refreshCount = refreshCount + 1;
    }
}

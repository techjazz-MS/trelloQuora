package com.upgrad.quora.service.entity;

      /*ID BIGSERIAL           PRIMARY KEY,
        uuid                   VARCHAR(200) NOT NULL,
        USER_ID                INTEGER NOT NULL,
        ACCESS_TOKEN           VARCHAR(500) NOT NULL,
        EXPIRES_AT             TIMESTAMP NOT NULL,
        LOGIN_AT               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        LOGOUT_AT              TIMESTAMP NULL*/

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_auth")
@NamedQueries({
        @NamedQuery(name = "userAuthByToken", query = "select ua from UserAuthEntity ua where ua.accessToken = :token"),
        @NamedQuery(name = "updateLogoutByToken", query = "update UserAuthEntity ua set ua.logoutAt = :logoutAt where ua.accessToken = :token")
})

public class UserAuthEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "UUID")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID")
    private UserEntity userId;

    @Column(name = "ACCESS_TOKEN")
    @NotNull
    @Size(max = 500)
    private String accessToken;

    @Column(name = "EXPIRES_AT")
    @NotNull
    private ZonedDateTime expiresAt;

    @Column(name = "LOGIN_AT")
    @NotNull
    private ZonedDateTime loginAt;

    @Column(name = "LOGOUT_AT")
    private ZonedDateTime logoutAt;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getExpiresAt(ZonedDateTime expiresAt) {
        return this.expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ZonedDateTime getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(ZonedDateTime loginAt) {
        this.loginAt = loginAt;
    }

    public ZonedDateTime getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(ZonedDateTime logoutAt) {
        this.logoutAt = logoutAt;
    }
}

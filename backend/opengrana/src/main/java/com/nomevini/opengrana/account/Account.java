package com.nomevini.opengrana.account;

import com.github.f4b6a3.uuid.UuidCreator;
import com.nomevini.opengrana.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountType type;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(
                    name = "opening_balance", nullable = false, updatable = false, precision = 19, scale = 4)),
            @AttributeOverride(name = "currency", column = @Column(
                    name = "currency", nullable = false, updatable = false, length = 3))
    })
    private Money openingBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountSource source = AccountSource.MANUAL;

    @Column(name = "external_id")
    private UUID externalId;

    @Column(name = "item_id")
    private UUID itemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "balance_as_of", nullable = false)
    private LocalDateTime balanceAsOf;

    @Version
    @Column(nullable = false)
    private Long version;

    @Generated
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Generated
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Account(User user, String name, AccountType type, Money openingBalance) {
        this.user = user;
        this.name = name;
        this.type = type;
        this.openingBalance = openingBalance;
    }

    @PrePersist
    void onPrePersist() {
        generatePublicId();
        initializeBalanceAsOf();
    }

    private void generatePublicId() {
        if (publicId == null) {
            publicId = UuidCreator.getTimeOrderedEpoch();
        }
    }

    private void initializeBalanceAsOf() {
        if (balanceAsOf == null) {
            balanceAsOf = LocalDateTime.now();
        }
    }

    public Long getUserId() {
        return user.getId();
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public void changeType(AccountType newType) {
        this.type = newType;
    }

    public void archive() {
        this.status = AccountStatus.ARCHIVED;
    }

    public void close() {
        this.status = AccountStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }
}

package com.nomevini.opengrana.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByUser_IdOrderByName(Long userId);

    Optional<Account> findByIdAndUser_Id(Long id, Long userId);

    Optional<Account> findByPublicIdAndUser_Id(UUID publicId, Long userId);

    /** Reservado para a ingestão Open Finance; ainda não usado neste bloco. */
    Optional<Account> findByExternalId(UUID externalId);
}
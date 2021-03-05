package io.kermoss.trx.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.kermoss.trx.domain.GlobalTransaction;

@Repository
public interface GlobalTransactionRepository extends JpaRepository<GlobalTransaction, String> {
    Optional<GlobalTransaction> findById(final String id);
    Optional<GlobalTransaction> findByParent(final String parentId);
    Optional<GlobalTransaction> findByNameAndParentAndStatus(final String name, final String parentId, final GlobalTransaction.GlobalTransactionStatus status);
    Optional<GlobalTransaction> findByNameAndParent(final String name, final String parentId);

    
}
 
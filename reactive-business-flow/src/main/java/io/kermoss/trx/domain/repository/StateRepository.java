package io.kermoss.trx.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.kermoss.trx.domain.State;

@Repository
public interface StateRepository extends JpaRepository<State, String> {
}

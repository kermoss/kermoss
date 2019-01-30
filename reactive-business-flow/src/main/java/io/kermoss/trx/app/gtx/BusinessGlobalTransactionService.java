package io.kermoss.trx.app.gtx;

import java.util.Optional;

import io.kermoss.trx.domain.GlobalTransaction;

public interface BusinessGlobalTransactionService {

	Optional<GlobalTransaction> findGlobalTransaction(String id);

	GlobalTransaction participateToGlobalTransaction(Optional<RequestGlobalTransaction> orgt);

	Optional<GlobalTransaction> retrieveGlobalTransaction(Optional<RequestGlobalTransaction> orgt);

}

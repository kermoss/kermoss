package io.kermoss.bfm.validator;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessFlowManagerValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(BusinessFlowManagerValidator.class);
	
	@Autowired
	List<TransactionBFMValidator> trxBFMValidator;
	
	private List<TrxBoundary> trxBoundaries = new LinkedList<>();

	

	@PostConstruct
	public void init() {
		
		trxBFMValidator.forEach(validator->validator.validate(trxBoundaries));
		trxBoundaries.stream().filter(x->!x.isValidated()).forEach(x->{
			throw new  BusinessFlowManagerValidatorException("the Business Transaction with class "+x.getClazz().getName()+" not attached "+ " to any Global or Local Business transaction " );
		});
	}




	public void setTrxBFMValidator(List<TransactionBFMValidator> trxBFMValidator) {
		this.trxBFMValidator = trxBFMValidator;
	}

	
}
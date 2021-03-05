package io.kermoss.bfm.validator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public abstract class TransactionBFMValidator {

	
	public abstract  void validate(List<TrxBoundary> trxBoundaries) ;
	
	protected void addTrxBoundary(List<TrxBoundary> trxBoundaries,TrxBoundary trxBoundary) {
		
		Optional<TrxBoundary> trxMe = trxBoundaries.stream()
		.filter(trx -> trx.getName().equals(trxBoundary.getName())).findAny();
		if(!trxMe.isPresent()) {
		  trxBoundaries.add(trxBoundary);
		}else {
			TrxBoundary trxBo = trxMe.get();
			trxBo.setChildOf(trxBoundary.getChildOf());
			trxBo.setClazz(trxBoundary.getClazz());
			trxBo.setType(trxBoundary.getType());
			trxBo.getChildren().stream().forEach(trxb->{trxb.setValidated(true);});
		}
		if("anyLocalTrx".equals(trxBoundary.getChildOf())){
			trxBoundary.setValidated(true);
		}
		
		Optional<TrxBoundary> oTrxParent = trxBoundaries.stream()
				.filter(trx -> trx.getName().equals(trxBoundary.getChildOf())).findAny();
		
		TrxBoundary trxParent= oTrxParent.isPresent()?oTrxParent.get():new TrxBoundary(null,false,trxBoundary.getName(),null,null);
		
		trxParent.getChildren().add(trxBoundary);
		if(trxParent.getType()!=null) {
			trxBoundary.setValidated(true);
		}
		
		
		
	 
		trxBoundaries.stream()
				.filter(trx -> trxBoundary.getName().equals(trx.getChildOf())).forEach(x->{
					trxBoundary.getChildren().add(x);
					x.setValidated(true);
				});
	
	}
	
	
    protected boolean isAnnotationPresent(Method m , Class annotation) {
    	boolean skipCheckingForBridgeMethods=m.isBridge();
    	return m.isAnnotationPresent(annotation) || skipCheckingForBridgeMethods ;
    }
}

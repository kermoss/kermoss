package io.kermoss.trx.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

public class BusinessKey {
 
	 public static final Long getKey(Optional<List<String>> businessKey) {
		 Long key=null;
		 if(businessKey !=null && businessKey.isPresent()) {
				String sbKey = businessKey.get().stream().collect(Collectors.joining());
				key= Hashing.murmur3_128().hashString(sbKey, Charsets.UTF_8).asLong();
			}
		 return key;
	 }
}

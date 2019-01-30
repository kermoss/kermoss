package io.kermoss.infra;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@Service
public class BubbleCache {

 private Map<String, BubbleMessage> cache = new WeakHashMap<>();

 public void addBubble(String key,BubbleMessage bubbleMessage) {
	 this.cache.put(key, bubbleMessage);
 }

 public Optional<BubbleMessage> getBubble(String eventId){
     return Optional.ofNullable(this.cache.get(eventId));
 }
}

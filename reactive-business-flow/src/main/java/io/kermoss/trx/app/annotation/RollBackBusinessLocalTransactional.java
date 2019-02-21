package io.kermoss.trx.app.annotation;

import org.springframework.context.event.EventListener;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@EventListener
public @interface RollBackBusinessLocalTransactional {

}

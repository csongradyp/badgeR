package com.noe.badger.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Achievement AOP Pointcut.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AchievementScore {

    /**
     * Name of counter to increment.
     */
    String counter();

    String score();

}

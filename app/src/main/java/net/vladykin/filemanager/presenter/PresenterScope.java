package net.vladykin.filemanager.presenter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Dagger scope for objects, which lives as long
 * as presenter lives.
 *
 * @author Vladimir Vladykin
 */
@Scope
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
public @interface PresenterScope {}

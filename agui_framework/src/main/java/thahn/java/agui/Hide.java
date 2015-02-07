package thahn.java.agui;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to hide a method from parsing/serialization. This allows a
 * method to be public for reasons other than exposing it to
 * parsing/serialization.
 */
@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hide {
}


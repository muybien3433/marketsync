package pl.muybien.transfer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportColumn {
    /** Header caption in the template / CSV. */
    String header();

    /** Optional: mark as required (in addition to Bean Validation). */
    boolean required() default false;

    /** Optional: custom converter class if default is not enough. */
    Class<? extends ImportConverter<?>> converter() default ImportConverter.Auto.class;
}


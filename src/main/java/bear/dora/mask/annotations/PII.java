package bear.dora.mask.annotations;

import bear.dora.mask.constants.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface PII {
    int keepLastDigits() default 0;
    String pattern() default Constants.MAX_PATTERN;
    String special() default "*";
}

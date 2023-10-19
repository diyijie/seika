package io.zeika.nacos.springcloud;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RegSeikaApi {
    Class<?>[] Value() default {};
}

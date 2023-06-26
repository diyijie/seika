package io.zeika.nacos.springcloud;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
 public @interface SeikaServiceApi {
 String value()  ;//application name
}

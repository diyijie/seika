package io.zbus.nacos;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
 public @interface SeikaServiceApi {
 String value()  ;//application name
}

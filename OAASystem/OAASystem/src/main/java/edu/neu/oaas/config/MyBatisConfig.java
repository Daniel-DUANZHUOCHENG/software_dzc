
package edu.neu.oaas.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("edu.neu.oaas.mapper") // 指定 Mapper 接口的包名
public class MyBatisConfig {
}


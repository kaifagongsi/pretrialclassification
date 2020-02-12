package com.kfgs.pretrialclassification.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Date: 2020-01-09-09-47
 * Module:
 * Description:
 *
 * @author:
 */
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "pretrialclassification")
public class PretrialClassificationProperties {
    private SwaggerProperties swagger = new SwaggerProperties();
}

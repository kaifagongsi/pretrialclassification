package com.kfgs.pretrialclassification.domain.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * Date: 2020-07-07-17-42
 * Module:
 * Description:
 *
 * @author:
 */
@Data
public class LicenceParams {

    @NotBlank(message = "licence Id cannot be empty.")
    private String licenceId;
}

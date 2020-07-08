package com.kfgs.pretrialclassification.test.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.kfgs.pretrialclassification.domain.Licence;
import com.kfgs.pretrialclassification.domain.request.LicenceParams;
import com.kfgs.pretrialclassification.test.service.impl.LicenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Date: 2020-07-01-09-34
 * Module:
 * Description:
 *
 * @author:
 */
@RestController
@RequestMapping(value = "/licence")
public class TestException {

    @Autowired
    private LicenceService licenceService;

    @GetMapping(value = "/{licenceId}")
    public Licence getLicence(@PathVariable("licenceId") Long licenceId) {
        return licenceService.queryDetail(licenceId);
    }

    /**
     * 测试传参异常
     */
    @PostMapping(value = "/request")
    public Licence getLicence(@Validated LicenceParams licence) {
        return licenceService.queryDetail(Long.parseLong(licence.getLicenceId()));
    }
    /**
     * 测试传参异常
     */
    @PostMapping(value = "/add")
    public Licence addLicenceTextRequestBody(@Validated @RequestBody Licence licence) {
        return licenceService.queryDetail(licence.getLicenceId());
    }

    /**
     * 测试传参异常
     */
    @PostMapping(value = "/addlincence")
    public void addLicence( Licence licence) {
        licenceService.addLicence(licence);
    }
}

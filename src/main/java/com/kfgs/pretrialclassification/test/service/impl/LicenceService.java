package com.kfgs.pretrialclassification.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kfgs.pretrialclassification.common.exception.LicenceEnum;
import com.kfgs.pretrialclassification.dao.LicenceMapper;
import com.kfgs.pretrialclassification.domain.Licence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 *
 * </pre>
 *
 * @author sprainkle
 * @date 2019/5/3
 */
@Service
public class LicenceService   {

    @Autowired
    private LicenceMapper licenceMapper;

    /**
     * 查询{@link Licence} 详情
     * @param licenceId
     * @return
     */
    public Licence queryDetail(Long licenceId) {

        QueryWrapper<Licence> wrapper = new QueryWrapper<>();
        wrapper.eq("licence_id",licenceId);
        Licence licence = licenceMapper.selectOne(wrapper);
        // 校验非空
        LicenceEnum.LICENCE_NOT_FOUND.assertNotNull(licence);
        return licence;

    }




    public void addLicence(Licence licence) {
        int insert = licenceMapper.insert(licence);
        System.out.println(insert);
    }
}

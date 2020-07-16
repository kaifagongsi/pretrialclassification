package com.kfgs.pretrialclassification.caseHandle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseHandle.service.CaseHandleService;
import com.kfgs.pretrialclassification.dao.FenleiBaohuLogMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaseHandleServiceImpl implements CaseHandleService {

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuLogMapper fenleiBaohuLogMapper;

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;


    //案件清单导入
    @Override
    public void saveInfo(FenleiBaohuMain fenleiBaohuMain){
        //判断插入的案件是否重复
        int num = fenleiBaohuMainMapper.findDoubleByID(fenleiBaohuMain.getId(), fenleiBaohuMain.getOraginization());
        if(num > 0){
            if(true){//所有重复案件全部跳过。
                return;
            }
        }
        fenleiBaohuMainMapper.insertEntity(fenleiBaohuMain);
    }

    //查询列表页未分配案件清单
    @Override
    public IPage findByState(String pageNo, String limit) {
        Page<FenleiBaohuMain> page = new Page<FenleiBaohuMain>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuMainMapper.findByState(page,"0");
    }


    //删除数据
    @Override
    public QueryResponseResult deleteDataByID(String id) {
        QueryWrapper<FenleiBaohuMain> wrapper = new QueryWrapper();
        wrapper.eq("id",id);
        int delete = fenleiBaohuMainMapper.delete(wrapper);
        if(1 == delete){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    //更新粗分号
    @Override
    public QueryResponseResult updateSimpleclasscode(String id, String simpleclasscode) {
        FenleiBaohuMain main = new FenleiBaohuMain();
        main.setId(id);
        main.setSimpleclasscode(simpleclasscode);
        int returnBack = fenleiBaohuMainMapper.updateById(main);
        if(1 == returnBack){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    //获取未完成案件清单
    @Override
    public IPage selectByCondition(String pageNo, String limit) {
        Page<FenleiBaohuMainResultExt> page = new Page<FenleiBaohuMainResultExt>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuMainMapper.selectByCondition(page,"","","","","","1","","");
    }

}

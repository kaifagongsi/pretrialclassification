package com.kfgs.pretrialclassification.common.utils;

import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class FreeMarkerUtils {

    /**
     *
     * @param fenleiBaohuResultExts
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public static String newlyAssignedCases(List<FenleiBaohuResultExt> fenleiBaohuResultExts, FreeMarkerConfig freeMarkerConfig) throws IOException, TemplateException {

        //加载模板
        Template template = freeMarkerConfig.getConfiguration().getTemplate("mailtemplate_sfds.ftl","UTF-8");
        HashMap<String,List<FenleiBaohuResultExt>> resultMap = new HashMap<>();
        resultMap.put("model",fenleiBaohuResultExts);
        //静态化
        return  FreeMarkerTemplateUtils.processTemplateIntoString(template, resultMap);
    }



}

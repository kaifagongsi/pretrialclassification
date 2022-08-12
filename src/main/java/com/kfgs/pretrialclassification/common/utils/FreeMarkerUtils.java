package com.kfgs.pretrialclassification.common.utils;

import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import com.kfgs.pretrialclassification.pretrialClassificationApplication;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

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
    public static String newlyAssignedCases(List<FenleiBaohuResultExt> fenleiBaohuResultExts) throws IOException, TemplateException {
        //创建配置类
        Configuration configuration=new Configuration(Configuration.getVersion());
        //设置模板路径
        String classpath = pretrialClassificationApplication.class.getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/generator/templates/"));

        //加载模板
        Template template = configuration.getTemplate("mailtemplate_sfds.ftl","UTF-8");
        HashMap<String,List<FenleiBaohuResultExt>> resultMap = new HashMap<>();
        resultMap.put("model",fenleiBaohuResultExts);
        //静态化
        return  FreeMarkerTemplateUtils.processTemplateIntoString(template, resultMap);
    }



}

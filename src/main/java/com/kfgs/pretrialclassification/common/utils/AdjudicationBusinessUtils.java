package com.kfgs.pretrialclassification.common.utils;

import com.kfgs.pretrialclassification.dao.FenleiBaohuAdjudicationMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuAdjudication;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import com.kfgs.pretrialclassification.domain.response.CaseFinishResponseEnum;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.domain.response.QueryResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class AdjudicationBusinessUtils {

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;
    @Autowired
    FenleiBaohuUserinfoMapper fenleiBaohuUserinfoMapper;
    @Autowired
    FenleiBaohuAdjudicationMapper fenleiBaohuAdjudicationMapper;

    private static AdjudicationBusinessUtils adjudicationBusinessUtils;

    @PostConstruct
    public void init(){
        adjudicationBusinessUtils = this;
        adjudicationBusinessUtils.fenleiBaohuAdjudicationMapper = this.fenleiBaohuAdjudicationMapper;
        adjudicationBusinessUtils.fenleiBaohuUserinfoMapper = this.fenleiBaohuUserinfoMapper;
        adjudicationBusinessUtils.fenleiBaohuResultMapper = this.fenleiBaohuResultMapper;
    }


    /**
     * 校验：无分类号、无主分、多个主分、超过两人给出组合码且总数大于99组,FM案件CPC为空
     * @param id
     * @param ipcmiList 主分list
     * @param ipcoiList 副分list
     * @param ipcaList 附加信息
     * @param csetsList csetsList
     * @param csets csets
     * @param cci cci
     * @param type 0获取数组第一个，其他表示为随机
     * @return
     */
    public static QueryResponseResult JudgeWhetherToEnterTheRuling(String id,List<String> ipcmiList,List<String> ipcoiList,List<String> ipcaList, List<String> csetsList, String csets, String cci, String type){
        //0.设置返回的实体
        FenleiBaohuAdjudication fenleiBaohuAdjudication = new FenleiBaohuAdjudication();
        //0.1设置id
        fenleiBaohuAdjudication.setId(id);
        //0.2设置时间
        fenleiBaohuAdjudication.setRukuTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        //0.3设置类型
        fenleiBaohuAdjudication.setState("7");
        //0.4
        //获取相应参数根据id获取实体 此sql有排序
        List<FenleiBaohuResultExt> fenleiBaohuResultExtList =  adjudicationBusinessUtils.fenleiBaohuResultMapper.selectSimpleClassCodeAndClassificationById(id);
        //0.5初始化返回值
        QueryResult queryResult = new QueryResult();
        HashMap hashMap = new HashMap(8);
        //0.6初始化组合吗
        String[] csetsNum = {};
        if(!"".equalsIgnoreCase(csets)){
            csetsNum = csets.split(";");
        }
        //1.无分类号
        if(ipcmiList.size() == 0 && ipcoiList.size() ==0 && ipcaList.size() == 0){
            fenleiBaohuAdjudication.setProcessingreasons("无分类号");
            //设置裁决组长
            fenleiBaohuAdjudication.setProcessingPerson(getAdjudicator(fenleiBaohuResultExtList,0,""));
            hashMap.put("item",fenleiBaohuAdjudication);
            queryResult.setMap(hashMap);
            // 不执行插入操作 仅将结果返回
            return new QueryResponseResult(CaseFinishResponseEnum.NO_CLASSIFICATION,queryResult);
        }else if (ipcmiList == null || ipcmiList.size() == 0){
            //2.判断无主分
            if(ipcoiList.size() == 1){
                //3.1无主分有一个分类员给副分：案件发给副分分类员对应的裁决组长
                fenleiBaohuAdjudication.setProcessingPerson(getAdjudicator(fenleiBaohuResultExtList,0,""));
                hashMap.put("item",fenleiBaohuAdjudication);
                queryResult.setMap(hashMap);
                return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_ONE_IPCOI,queryResult);
            }else if(ipcoiList.size() > 1){
                //3.2无主分有多个个分类元给副分：案件发给随机裁决组长
                fenleiBaohuAdjudication.setProcessingPerson(getAdjudicator(fenleiBaohuResultExtList,1,"ipcoi"));
                hashMap.put("item",fenleiBaohuAdjudication);
                queryResult.setMap(hashMap);
                return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_MORE_IPCOI,queryResult);
            }else if(ipcoiList.size() == 0 && ipcaList.size() == 1){
                //3.3无主分，无副分，仅有一个人有附加信息:给附加信息的分类员对应的裁决组长
                fenleiBaohuAdjudication.setProcessingPerson(getAdjudicator(fenleiBaohuResultExtList,0,""));
                hashMap.put("item",fenleiBaohuAdjudication);
                queryResult.setMap(hashMap);
                return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_NO_IPCOI_ONE_IPCA,queryResult);
            }else if(ipcoiList.size() == 0 && ipcaList.size() > 1){
                //3.4无主分，无副分，有多个人有附加信息:给附加信息的随机分类员的裁决组长
                fenleiBaohuAdjudication.setProcessingPerson(getAdjudicator(fenleiBaohuResultExtList,1,"ipca"));
                hashMap.put("item",fenleiBaohuAdjudication);
                queryResult.setMap(hashMap);
                return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_NO_IPCOI_MORE_IPCA,queryResult);
            }else {
                //3.5 无主分无副分无附加信息（当前情况不应该出现，逻辑出现bug）
                log.error("出现逻辑问题：案件id:" + id + ",当前案件无法判断出触发裁决的原因。");
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        } else if (ipcmiList.size() > 1) {
            //3.是否多个主分
            //设置随机裁决组长
            fenleiBaohuAdjudication.setProcessingPerson(getAdjudicator(fenleiBaohuResultExtList,1,"ipcmi"));
            hashMap.put("item",fenleiBaohuAdjudication);
            queryResult.setMap(hashMap);
            return new QueryResponseResult(CaseFinishResponseEnum.ONE_MORE_IPCMI,queryResult);
        } else if (csetsList.size() >= 2 && csetsNum.length > 99){
            //4.判断组合吗"超过两人给出组合码且组合码总数多于99组，进入裁决";
            //int xiabiao = getArrayXiaoBiao(fenleiBaohuResultExtList,"ipcmi");
            fenleiBaohuAdjudication.setProcessingPerson(getAdjudicator(fenleiBaohuResultExtList,1,"csets"));
            hashMap.put("item",fenleiBaohuAdjudication);
            queryResult.setMap(hashMap);
            return new QueryResponseResult(CaseFinishResponseEnum.MAX_NUM_CSETS,queryResult);
        }else if("FM".equals(type) && "".equals(cci)) {
            //5.若是发明案件，判断CPC是否为空，为空进裁决
            fenleiBaohuAdjudication.setProcessingreasons("发明案件的的CPC分类号为空");
            fenleiBaohuAdjudication.setProcessingPerson(getAdjudicator(fenleiBaohuResultExtList,0,""));
            hashMap.put("item",fenleiBaohuAdjudication);
            queryResult.setMap(hashMap);
            return new QueryResponseResult(CaseFinishResponseEnum.FM_NO_CCI,queryResult);
        } else {
            //6.不触发裁决
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }
    }

    /**
     * 获取符合要求的下标
     * @param fenleiBaohuResultExtList  需要获取下标的数组
     * @param type 类型 ipcmi，ipcoi，ipca
     * @return
     */
    private static int getArrayXiaoBiao(List<FenleiBaohuResultExt> fenleiBaohuResultExtList, String type) {
        int random = 0 ;
        int size = 0;
        if("ipcmi".equalsIgnoreCase(type)){
            for(FenleiBaohuResultExt fenleiBaohuResultExt : fenleiBaohuResultExtList){
                String ipcmi = fenleiBaohuResultExt.getIPCMI();
                if("" != ipcmi && null != ipcmi){
                    size ++;
                }
            }
            random = (int) (Math.random() * size);
        }else if("ipcoi".equalsIgnoreCase(type)) {
            for(FenleiBaohuResultExt fenleiBaohuResultExt : fenleiBaohuResultExtList){
                String ipcoi = fenleiBaohuResultExt.getIPCOI();
                if("" != ipcoi && null != ipcoi){
                    size ++;
                }
            }
            random = (int) (Math.random() * size);
        }
        else if("ipca".equalsIgnoreCase(type)) {

            for(FenleiBaohuResultExt fenleiBaohuResultExt : fenleiBaohuResultExtList){
                String ipca = fenleiBaohuResultExt.getIpca();
                if("" != ipca && null != ipca){
                    size ++;
                }
            }
            random = (int) (Math.random() * size);
        }else if("csets".equals(type)){
            for(FenleiBaohuResultExt fenleiBaohuResultExt : fenleiBaohuResultExtList){
                String csets = fenleiBaohuResultExt.getCsets();
                if("" != csets && null != csets){
                    size ++;
                }
            }
            random = (int) (Math.random() * size);
        }
        return random;
    }


    /**
     * 获取裁决组长
     * @param fenleiBaohuResultExts
     * @param type  0 表示获取第一个 1表示获取随机下标
     * @param ziduan ipcmi,ipcoi,ipca
     * @return
     */
    private static String getAdjudicator(List<FenleiBaohuResultExt> fenleiBaohuResultExts,int type,String ziduan){
        String worker = "";
        if(0 == type){
            worker = fenleiBaohuResultExts.get(0).getWorker();
        }else if(1 == type && StringUtils.isNotBlank(ziduan)){
            //设置随机裁决组长
            worker = fenleiBaohuResultExts.get(getArrayXiaoBiao(fenleiBaohuResultExts,ziduan)).getWorker();
        }else{
            log.error("getAdjudicator 获取裁决组长时传参异常");
        }
        //获取裁决组长
        return adjudicationBusinessUtils.fenleiBaohuUserinfoMapper.selectAdjudicatorByWorkerName(worker);
    }

    /**
     * 合并ipci 并去重
     * @param ipcmiList 主分类号
     * @param ipcoiList 副分类号
     * @param ipcaList 附加信息
     * @return
     */
    public static String mergeIPCI(List<String> ipcmiList,List<String> ipcoiList,List<String> ipcaList){
        StringBuffer ipci = new StringBuffer();
        //String ipcmi = ipcmiList.get(0);
        //ipci += ipcmi;
        ipci.append(ipcmiList.get(0));
        //拼接副分类号
        String ipcoi = "";
//        ipcoiList = ListUtils.delRepeatReturnList(ipcoiList);
//        ipcaList = ListUtils.delRepeatReturnList(ipcaList);
        if (ipcoiList != null && ipcoiList.size()!=0){
            //ipci += ";"; 修改ipcmi 和ipcoi 以逗号区分
            ipci.append(",");
            for (int i=0;i<ipcoiList.size();i++){
                /*ipcoi = ipcoiList.get(i);
                ipci += ipcoi;
                ipci += ",";*/
                ipci.append(ipcoiList.get(i)+",");
            }
            // ipci = ipci.substring(0,ipci.length()-1);
            ipci = new StringBuffer(ipci.substring(0,ipci.length() -1));
        }
        if(ipcaList != null && ipcaList.size()!=0 ){
            ipci.append("*");
            for(String ca : ipcaList){
                ipci.append(ca + ",");
            }
            ipci = new StringBuffer(ipci.substring(0,ipci.length() -1));
        }
        /*if (!"".equals(csets)){
         *//* ipci += "*";
            ipci += csets;*//*
            ipci.append("*").append(csets);
        }*/
        return ipci.toString();
    }

    /**
     * 合并csets (组合码) 不用去重
     * @param csetsList  csets的list
     * @return
     */
    public static String margeCsets(List<String> csetsList){
        StringBuffer csets = new StringBuffer();
        if (csetsList != null){
            if (csetsList.size()>=2){
                for(int i=0;i<csetsList.size()-1;i++){
                    csets.append(csetsList.get(i)).append(";");
                }
                csets.append(csets).append(csetsList.get(csetsList.size()-1));
            } else if(csetsList.size() == 1){
                //csets += csetsList.get(0);
                csets.append(csets).append(csetsList.get(0));
            }
        }
        return csets.toString();
    }

    /**
     * 合并cci 需要去重
     * @param cciList
     * @return
     */
    public static String margeCci(List<String> cciList){
        String cci = "";
        //先去重
        if (cciList.size() > 1){
            List<String> list_cci = new ArrayList<>();
            StringBuffer sb_cci = new StringBuffer();
            for (int i=0;i<cciList.size();i++){
                if(!list_cci.contains(cciList.get(i))){
                    list_cci.add(cciList.get(i));
                    sb_cci.append(cciList.get(i) + ",");
                }
            }
            cci = sb_cci.toString().substring(0,sb_cci.toString().length()-1);
        }else if (cciList.size() == 1) {
            cci = cciList.get(0);
        }
        return cci;
    }

    /**
     * 合并cca 需要去重
     * @param ccaList 合并cca
     * @return
     */
    public static String margeCca(List<String> ccaList){
        String cca = "";
        //去重
        if (ccaList.size() >1){
            List<String> list_cca = new ArrayList<>();
            StringBuffer sb_cca = new StringBuffer();
            for (int i=0;i<ccaList.size();i++){
                if (!list_cca.contains(ccaList.get(i))){
                    list_cca.add(ccaList.get(i));
                    sb_cca.append(ccaList.get(i)+",");
                }
            }
            cca = sb_cca.toString().substring(0,sb_cca.toString().length()-1);
        }else if (ccaList.size() == 1){
            cca = ccaList.get(0);
        }
        return  cca;
    }


}

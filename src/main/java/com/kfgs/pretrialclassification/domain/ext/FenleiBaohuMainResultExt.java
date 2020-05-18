package com.kfgs.pretrialclassification.domain.ext;

import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;

public class FenleiBaohuMainResultExt extends FenleiBaohuMain {
    /*
    ipci,分类号
     */
    private String ipci;

    /*
    CCI
     */
    private String cci;

    /*
    CCA
     */
    private String cca;

    /*
    CSETS
     */
    private String csets;

    public String getMainworker() {
        return mainworker;
    }

    public void setMainworker(String mainworker) {
        this.mainworker = mainworker;
    }

    public String getAssworker() {
        return assworker;
    }

    public void setAssworker(String assworker) {
        this.assworker = assworker;
    }

    /*
        主分类员
         */
    private String mainworker;

    /*
    副分类员
     */
    private String assworker;

    public String getMaintype() {
        return maintype;
    }

    public void setMaintype(String maintype) {
        this.maintype = maintype;
    }

    public String getAsstype() {
        return asstype;
    }

    public void setAsstype(String asstype) {
        this.asstype = asstype;
    }

    /*
        主分类
         */
    private String maintype;

    /*
    副分类
     */
    private String asstype;

    public String getIpci() {
        return ipci;
    }

    public void setIpci(String ipci) {
        this.ipci = ipci;
    }

    public String getCci() {
        return cci;
    }

    public void setCci(String cci) {
        this.cci = cci;
    }

    public String getCca() {
        return cca;
    }

    public void setCca(String cca) {
        this.cca = cca;
    }

    public String getCsets() {
        return csets;
    }

    public void setCsets(String csets) {
        this.csets = csets;
    }

}

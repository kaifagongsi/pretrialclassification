package com.kfgs.pretrialclassification.domain.ext;

import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;

public class FenleiBaohuMainResultExt extends FenleiBaohuMain {


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

    public String getIpca() {
        return ipca;
    }

    public void setIpca(String ipca) {
        this.ipca = ipca;
    }

    public String getIpcoi() {
        return ipcoi;
    }

    public void setIpcoi(String ipcoi) {
        this.ipcoi = ipcoi;
    }

    public String getIpcmi() {
        return ipcmi;
    }

    public void setIpcmi(String ipcmi) {
        this.ipcmi = ipcmi;
    }

    /*
     IPCA
     */
    private String ipca;
    /*
    IPCOI
     */
    private String ipcoi;
    /*
    /*
    IPCMI
     */
    private String ipcmi;
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

    public String getChuantype() {
        return chuantype;
    }

    public void setChuantype(String chuantype) {
        this.chuantype = chuantype;
    }

    /*
        出案类型
         */
    private String chuantype;

}

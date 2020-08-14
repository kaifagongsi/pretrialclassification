package com.kfgs.pretrialclassification.domain.ext;

import com.kfgs.pretrialclassification.domain.FenleiBaohuUpdateIpc;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FenleiBaohuUpdateipcExt  extends FenleiBaohuUpdateIpc {
    private  String oldIpcmi;//旧ipcmi
    private  String oldIpcoi;//旧ipcoi
    private  String oldIpca;//旧ipca
    private  String oldCci;//旧cci
    private  String oldCca;//旧cca
    private  String oldCsets;//旧csets
}

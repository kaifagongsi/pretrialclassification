package com.kfgs.pretrialclassification.domain.ext;

import com.kfgs.pretrialclassification.domain.FenleiBaohuUpdateIpc;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FenleiBaohuUpdateipcExt  extends FenleiBaohuUpdateIpc {

    /**
     * pdf路径
     */
    private String pdfPath;
}

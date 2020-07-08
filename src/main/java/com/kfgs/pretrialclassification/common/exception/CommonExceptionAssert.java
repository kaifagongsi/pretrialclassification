package com.kfgs.pretrialclassification.common.exception;

import cn.hutool.core.util.ArrayUtil;

import java.text.MessageFormat;

/**
 * Date: 2020-06-29-10-36
 * Module:
 * Description:
 *
 * @author:
 */
public interface CommonExceptionAssert  extends IResponseEnum, Assert  {
    @Override
    default BaseException newException(Object... args) {
        String msg = this.getMessage();
        if (ArrayUtil.isNotEmpty(args)) {
            msg = MessageFormat.format(this.getMessage(), args);
        }

        return new ArgumentException(this, args, msg);
    }

    @Override
    default BaseException newException(Throwable t, Object... args) {
        String msg = this.getMessage();
        if (ArrayUtil.isNotEmpty(args)) {
            msg = MessageFormat.format(this.getMessage(), args);
        }

        return new ArgumentException(this, args, msg, t);
    }
}

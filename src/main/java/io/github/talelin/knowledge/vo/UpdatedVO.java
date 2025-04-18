package io.github.talelin.knowledge.vo;

import io.github.talelin.autoconfigure.bean.Code;
import io.github.talelin.knowledge.common.util.ResponseUtil;
import org.springframework.http.HttpStatus;

/**
 * @author pedro@TaleLin
 * 更新成功视图对象
 */
public class UpdatedVO extends UnifyResponseVO<String> {

    public UpdatedVO() {
        super(Code.UPDATED.getCode());
        ResponseUtil.setCurrentResponseHttpStatus(HttpStatus.OK.value());
    }

    public UpdatedVO(int code) {
        super(code);
        ResponseUtil.setCurrentResponseHttpStatus(HttpStatus.OK.value());
    }

    public UpdatedVO(String message) {
        super(message);
        ResponseUtil.setCurrentResponseHttpStatus(HttpStatus.OK.value());
    }

    public UpdatedVO(int code, String message) {
        super(code, message);
        ResponseUtil.setCurrentResponseHttpStatus(HttpStatus.OK.value());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

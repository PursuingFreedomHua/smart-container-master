package cn.fuguang.kefu;

import cn.fuguang.exception.ContainerException;
import cn.fuguang.web.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 智能客服模块全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class KefuExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(ContainerException.class)
    public BaseResult<Void> handleContainerException(ContainerException e) {
        log.error("业务异常: code={}, msg={}", e.getDefineCode(), e.getMessage());
        return BaseResult.fail(e.getDefineCode(), e.getMessage());
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public BaseResult<Void> handleUnknownException(Exception e) {
        log.error("系统异常", e);
        return BaseResult.fail();
    }
}

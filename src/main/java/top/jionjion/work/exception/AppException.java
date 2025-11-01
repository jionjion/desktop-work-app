package top.jionjion.work.exception;

/**
 * 自定义异常
 *
 * @author Jion
 */
public class AppException extends RuntimeException {

    /**
     * 无参构造方法
     */
    public AppException() {
        super();
    }

    /**
     * 带错误信息的构造方法
     *
     * @param message 错误信息
     */
    public AppException(String message) {
        super(message);
    }

    /**
     * 带错误信息和原因的构造方法
     *
     * @param message 错误信息
     * @param cause   异常原因
     */
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造方法
     *
     * @param cause 异常原因
     */
    public AppException(Throwable cause) {
        super(cause);
    }
}
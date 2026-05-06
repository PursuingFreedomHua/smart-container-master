package cn.fuguang.constants;

public class RedisConstants {

    /**
     * 开门分布式锁key
     */
    public static final String REDIS_LOCK_CREATE_ORDER_INDEX = "REDIS_LOCK_CREATE_ORDER_";

    /**
     * 设备重量信息key
     */
    public static final String DEVICE_WEIGHT_INFO = "DEVICE_WEIGHT_INFO_";

    /**
     * 申请开门订单key
     */
    public static final String ORDER_OPEN_GATE = "ORDER_OPEN_GATE_";

    /**
     * 设备最后一次心跳缓存key
     */
    public static final String DEVICE_HEART_LAST_TIME_KEY = "DEVICE_HEART_LAST_TIME_KEY_";

    /**
     * 设备最后一次心跳缓存key
     */
    public static final String SERVICE_HEART_COUNT_KEY = "SERVICE_HEART_COUNT_KEY_";

    /**
     * 智能客服对话历史key
     */
    public static final String CONV_HISTORY_PREFIX = "CONV_HISTORY_";

    /**
     * 智能客服停止生成标记key
     */
    public static final String CONV_STOP_FLAG_PREFIX = "CONV_STOP_FLAG_";


}

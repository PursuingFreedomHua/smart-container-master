package cn.fuguang.kefu.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建会话响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreateResDTO {

    /** 会话 ID */
    private String sessionId;

    /** 创建时间戳 */
    private Long createTime;
}

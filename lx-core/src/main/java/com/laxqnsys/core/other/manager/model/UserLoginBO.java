package com.laxqnsys.core.other.manager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuzhenhong
 * @date 2024/5/22 11:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginBO {

    private String value;

    private long expireTime;

}

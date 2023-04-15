package com.joker.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/4/15 09:53
 * @description: 附件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentVO implements Serializable {
    /**
     * 附件名字
     */
    private String attachmentName;
    /**
     * 附件详情
     */
    private String attachmentDescription;
    /**
     * 附件地址
     */
    private String url;
}

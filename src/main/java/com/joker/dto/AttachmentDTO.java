package com.joker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/4/11 20:47
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO implements Serializable {
    /**
     * 受理人
     */
    private String assignee;

    /**
     * 业务ID
     */
    private String businessKey;

    /**
     * 附件名称
     */
    String attachmentName;

    /**
     * 附件详情
     */
    String attachmentDescription;

    /**
     * 附件地址
     */
    String url;
}

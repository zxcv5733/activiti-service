package com.joker.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author: Li dong
 * @date: 2023/4/15 09:46
 * @description: 节点信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeInfoVO implements Serializable {
    /**
     * 评论信息
     */
    private List<String> comments;

    /**
     * 附件信息
     */
    private List<AttachmentVO> attachments;

    /**
     * 审批人
     */
    private String assignee;

    /**
     * 发起人
     */
    private String startUser;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 审批状态
     */
    private String status;
}

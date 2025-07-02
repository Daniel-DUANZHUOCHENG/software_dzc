/*
 * Meeting Application table creation
 */
CREATE TABLE IF NOT EXISTS `meeting_application` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `meetingId` INT NOT NULL COMMENT '会议ID',
  `applicantId` INT NOT NULL COMMENT '申请人用户ID',
  `applicantName` VARCHAR(100) DEFAULT NULL COMMENT '申请人姓名',
  `applicantCompany` VARCHAR(200) DEFAULT NULL COMMENT '申请人公司名',
  `applicationTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '申请状态 pending/approved/rejected',
  `tenantId` INT DEFAULT NULL COMMENT '申请人租户ID',
  `meetingTenantId` INT DEFAULT NULL COMMENT '会议所属租户ID',
  `approverId` INT DEFAULT NULL COMMENT '审批人ID',
  `approverName` VARCHAR(100) DEFAULT NULL COMMENT '审批人姓名',
  `approvalTime` TIMESTAMP NULL COMMENT '审批时间',
  `rejectionReason` VARCHAR(255) DEFAULT NULL COMMENT '拒绝原因',
  INDEX (`meetingId`),
  INDEX (`applicantId`),
  INDEX (`tenantId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议参会申请表'; 
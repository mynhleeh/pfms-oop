package com.pfms.presentation.dto.data;

import java.util.Objects;

/**
 * DTO đóng gói chi tiết một sự cố hoặc sai lệch phát hiện trong quá trình đối
 * soát dữ liệu hệ thống (FR-DAT-03).
 */
public final class ReconciliationIssueResponseDTO {

    // region State
    private final String severity;
    private final String ruleCode;
    private final String issueType;
    private final String message;
    private final String affectedEntityId;
    private final String recommendation;
    // endregion

    // region Constructors
    public ReconciliationIssueResponseDTO(
            String severity,
            String ruleCode,
            String issueType,
            String message,
            String affectedEntityId,
            String recommendation) {

        this.severity = (severity == null || severity.trim().isEmpty())
                ? "INFO"
                : severity.trim().toUpperCase();
        this.ruleCode = (ruleCode == null) ? "" : ruleCode.trim();
        this.issueType = (issueType == null) ? "" : issueType.trim();
        this.message = (message == null) ? "" : message.trim();
        this.affectedEntityId = (affectedEntityId == null) ? "" : affectedEntityId.trim();
        this.recommendation = (recommendation == null) ? "" : recommendation.trim();
    }

    public ReconciliationIssueResponseDTO(
            String severity,
            String ruleCode,
            String issueType,
            String message,
            String recommendation) {
        this(severity, ruleCode, issueType, message, null, recommendation);
    }
    // endregion

    // region Getters
    public String getSeverity() {
        return this.severity;
    }

    public String getRuleCode() {
        return this.ruleCode;
    }

    public String getIssueType() {
        return this.issueType;
    }

    public String getMessage() {
        return this.message;
    }

    public String getAffectedEntityId() {
        return this.affectedEntityId;
    }

    public String getRecommendation() {
        return this.recommendation;
    }

    public boolean isError() {
        return "ERROR".equalsIgnoreCase(this.severity);
    }

    public boolean isWarning() {
        return "WARNING".equalsIgnoreCase(this.severity);
    }

    public boolean isInfo() {
        return "INFO".equalsIgnoreCase(this.severity);
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReconciliationIssueResponseDTO that)) {
            return false;
        }
        return Objects.equals(this.severity, that.severity)
                && Objects.equals(this.ruleCode, that.ruleCode)
                && Objects.equals(this.issueType, that.issueType)
                && Objects.equals(this.message, that.message)
                && Objects.equals(this.affectedEntityId, that.affectedEntityId)
                && Objects.equals(this.recommendation, that.recommendation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.severity,
                this.ruleCode,
                this.issueType,
                this.message,
                this.affectedEntityId,
                this.recommendation);
    }

    @Override
    public String toString() {
        return "ReconciliationIssueResponseDTO{" +
                "severity='" + this.severity + '\'' +
                ", ruleCode='" + this.ruleCode + '\'' +
                ", issueType='" + this.issueType + '\'' +
                ", message='" + this.message + '\'' +
                ", affectedEntityId='" + this.affectedEntityId + '\'' +
                ", recommendation='" + this.recommendation + '\'' +
                '}';
    }
    // endregion
}


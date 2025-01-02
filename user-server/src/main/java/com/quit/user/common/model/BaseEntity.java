package com.quit.user.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column
    private LocalDateTime updateAt;

    @Column
    private String updateBy;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private String deletedBy;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    public void markAsCreated(String createdBy) {
        this.createdBy = createdBy;
    }

    public void markAsUpdated(String updatedBy) {
        this.updateBy = updatedBy;
    }

    public void markAsDeleted(String deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
        this.isDeleted = true;
    }
}
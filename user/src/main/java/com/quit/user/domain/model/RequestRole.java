package com.quit.user.domain.model;

import com.quit.user.common.model.BaseEntity;
import com.quit.user.domain.enums.RequestStatus;
import com.quit.user.domain.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_rolerequest")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "requestRole", nullable = false)
    private UserRoleEnum requestRole;

    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "requestedAt", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approvedBy", nullable = true)
    private Long approvedBy;

    @Column(name = "approvedAt", nullable = true)
    private LocalDateTime approvedAt;

    @PrePersist
    public void prePersist() {
        if (getCreatedBy() == null) {
            markAsCreated(String.valueOf(this.id));

        }
    }

    public static RequestRole create(Long userId, UserRoleEnum requestRole){
        return RequestRole.builder()
                .userId(userId)
                .requestRole(requestRole)
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
    }

    public void update(
            RequestStatus status,
            Long approvedBy){

        this.approvedBy=approvedBy;
        this.approvedAt=LocalDateTime.now();
        this.status=status;

}

}

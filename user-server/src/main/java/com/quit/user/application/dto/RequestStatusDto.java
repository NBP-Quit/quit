package com.quit.user.application.dto;

import com.quit.user.domain.enums.RequestStatus;

// DTO 클래스
public class RequestStatusDto {
    private RequestStatus status;

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}

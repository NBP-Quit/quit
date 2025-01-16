package com.quit.user.infrastructure.repository;

import com.quit.user.domain.enums.RequestStatus;
import com.quit.user.domain.model.RequestRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequestRoleRepository extends JpaRepository<RequestRole, UUID> {
    List<RequestRole> findByStatus(RequestStatus status);
}

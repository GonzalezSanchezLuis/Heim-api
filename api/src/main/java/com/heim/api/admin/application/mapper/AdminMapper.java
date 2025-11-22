package com.heim.api.admin.application.mapper;

import com.heim.api.admin.application.dto.AdminRequest;
import com.heim.api.admin.application.dto.AdminResponse;
import com.heim.api.admin.domain.entity.Admin;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminResponse toResponse(Admin admin);
    Admin toEntity(AdminRequest adminRequest);
}

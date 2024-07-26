package com.alkl1m.auth.service;

import com.alkl1m.auth.domain.entity.Role;
import com.alkl1m.auth.web.payload.AddRolesPayload;

import java.util.Set;

public interface RoleService {

    void saveRole(AddRolesPayload payload);

    Set<Role> getRolesForUser(String login, String currentUserLogin);

}

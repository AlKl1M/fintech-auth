package com.alkl1m.auth.service;

import com.alkl1m.auth.web.payload.AddRolesPayload;
import com.alkl1m.auth.web.payload.UserRolesResponse;

public interface RoleService {

    void saveRole(AddRolesPayload payload);

    UserRolesResponse getRolesForUser(String login, String currentUserLogin);

}

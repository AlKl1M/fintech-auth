package com.alkl1m.auth.web.controller;

import com.alkl1m.auth.TestBeans;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = TestBeans.class)
class UserRolesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/user.sql")
    void testGetRoles_withAdmin_returnsAnotherUserRoles() throws Exception {
        Cookie[] cookies = loginAndGetCookies("admin", "password");

        Cookie jwt = cookies[0];
        Cookie jwtRefresh = cookies[1];

        mockMvc.perform(MockMvcRequestBuilders.get("/user-roles/{login}", "John Doe")
                .cookie(jwt)
                .cookie(jwtRefresh))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                    [
                                      {
                                        "id": 1,
                                        "name": "USER"
                                      }
                                    ]
                                """)
                );
    }

    @Test
    @Sql("/sql/user.sql")
    void testGetRoles_withNonAdminCheckingSameUserRoles_returnsUserRoles() throws Exception {
        Cookie[] cookies = loginAndGetCookies("John Doe", "password");

        Cookie jwt = cookies[0];
        Cookie jwtRefresh = cookies[1];

        mockMvc.perform(MockMvcRequestBuilders.get("/user-roles/{login}", "John Doe")
                        .cookie(jwt)
                        .cookie(jwtRefresh))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                    [
                                      {
                                        "id": 1,
                                        "name": "USER"
                                      }
                                    ]
                                """)
                );
    }

    @Test
    @Sql("/sql/user.sql")
    void testGetRoles_withNonAdminCheckingNotUserRoles_returnsValidData() throws Exception {
        Cookie[] cookies = loginAndGetCookies("John Doe", "password");

        Cookie jwt = cookies[0];
        Cookie jwtRefresh = cookies[1];

        mockMvc.perform(MockMvcRequestBuilders.get("/user-roles/{login}", "admin")
                        .cookie(jwt)
                        .cookie(jwtRefresh))
                .andExpectAll(
                        status().isForbidden(),
                        content().json("""
                            {
                              "message": "Access denied.",
                              "errors": null
                            }
                        """)
                );
    }

    private Cookie[] loginAndGetCookies(String login, String password) throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                            {
                                "login": "%s",
                                "password": "%s"
                            }
                            """, login, password)))
                .andReturn();

        return new Cookie[]{
                mvcResult.getResponse().getCookie("jwt"),
                mvcResult.getResponse().getCookie("jwt-refresh")
        };
    }

}
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = TestBeans.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void testRegisterUser_withValidRequest_returnsValidMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "login": "Jane Doe",
                      "email": "jane@example.com",
                      "password": "password"
                    }
                """))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/user.sql")
    void testRegisterUser_withExistingUser_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "login": "John Doe",
                      "email": "john.doe@example.com",
                      "password": "password"
                    }
                """))
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                        {"message":"User already exists.","errors":null}
                        """)
                );
    }

    @Test
    void testRegisterUser_withInvalidPayload_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "login":  null,
                      "email": null,
                      "password": null
                    }
                """))
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    @Sql("/sql/user.sql")
    void testLoginUser_withExistingUser_returnsValidResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "login": "John Doe",
                                      "password": "password"
                                    }
                                """))
                .andExpectAll(
                        status().isOk(),
                        cookie().exists("jwt"),
                        cookie().exists("jwt-refresh")
                );
    }

    @Test
    void testLoginUser_withNotExistingUser_returnsValidResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "login": "John Doe",
                                      "password": "password"
                                    }
                                """))
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                                    {
                                      "message": "Authentication failed.",
                                      "errors": null
                                    }
                                """)
                );
    }

    @Test
    @Sql("/sql/user.sql")
    void testRefreshToken_withValidRequest_returnsValidData() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "login": "John Doe",
                                      "password": "password"
                                    }
                                """))
                .andReturn();
        String jwt = mvcResult.getResponse().getCookie("jwt").getValue();
        String jwtRefresh = mvcResult.getResponse().getCookie("jwt-refresh").getValue();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refreshToken")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("jwt-refresh", jwtRefresh), new Cookie("jwt", jwt)))
                .andExpectAll(
                        status().isOk(),
                        cookie().exists("jwt")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    void testLogoutUser_withValidRequest_returnsValidMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout"))
                .andExpectAll(
                        status().isOk(),
                        cookie().value("jwt", ""),
                        cookie().value("jwt-refresh", "")
                );
    }

}
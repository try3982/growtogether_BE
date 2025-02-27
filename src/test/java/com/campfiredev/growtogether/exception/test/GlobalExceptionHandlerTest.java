package com.campfiredev.growtogether.exception.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TestController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @SpyBean
  private TestService testService;

  @Test
  @DisplayName("CustomException 발생 시 globalExceptionHandler 호출")
  void testCustomExceptionHandling() throws Exception {

    mockMvc.perform(get("/custom-exception")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("ALREADY_JOINED_STUDY"))
        .andExpect(jsonPath("$.description").value("이미 참석 중인 스터디입니다."));
  }

  @Test
  @DisplayName("Exception 발생 시 globalExceptionHandler 호출")
  void testIllegalAccessExceptionHandling() throws Exception {

    mockMvc.perform(get("/exception")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
        .andExpect(jsonPath("$.description").value("서버 오류입니다."));
  }
}
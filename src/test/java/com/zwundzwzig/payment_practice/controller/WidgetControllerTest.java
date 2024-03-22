package com.zwundzwzig.payment_practice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest
@AutoConfigureMockMvc
public class WidgetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/success")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("orderId", "1W_pCfO4rzG9szJEcThKl")
                    .param("amount", "50000")
                    .param("paymentKey", "test_payment_key"))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void testBadRequest() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/success")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("amount", "50000")
                    .param("paymentKey", "test_payment_key"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

}
package com.zwundzwzig.payment_practice.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
public class WidgetController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @GetMapping("")
  public String index() {
    return "checkout";
  }

//  @GetMapping(value = "success")
//  public String paymentResult(
//          Model model,
//          @RequestParam(value = "orderId") String orderId,
//          @RequestParam(value = "amount") Integer amount,
//          @RequestParam(value = "paymentKey") String paymentKey) throws Exception {
//
//    String secretKey = "test_ck_PBal2vxj81yOlXJZogQJ85RQgOAN:";
//
//    Base64.Encoder encoder = Base64.getEncoder();
//    byte[] encodedBytes = encoder.encode(secretKey.getBytes("UTF-8"));
//    String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);
//
//    URL url = new URL("https://api.tosspayments.com/v1/payments/" + paymentKey);
//
//    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//    connection.setRequestProperty("Authorization", authorizations);
//    connection.setRequestProperty("Content-Type", "application/json");
//    connection.setRequestMethod("POST");
//    connection.setDoOutput(true);
//    JSONObject obj = new JSONObject();
//    obj.put("orderId", orderId);
//    obj.put("amount", amount);
//
//    OutputStream outputStream = connection.getOutputStream();
//    outputStream.write(obj.toString().getBytes("UTF-8"));
//
//    int code = connection.getResponseCode();
//    boolean isSuccess = code == 200 ? true : false;
//    model.addAttribute("isSuccess", isSuccess);
//
//    InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
//
//    Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
//    JSONParser parser = new JSONParser();
//    JSONObject jsonObject = (JSONObject) parser.parse(reader);
//    responseStream.close();
//    model.addAttribute("responseStr", jsonObject.toJSONString());
//    System.out.println(jsonObject.toJSONString());
//
//    model.addAttribute("method", (String) jsonObject.get("method"));
//    model.addAttribute("orderName", (String) jsonObject.get("orderName"));
//
//    if (((String) jsonObject.get("method")) != null) {
//      if (((String) jsonObject.get("method")).equals("카드")) {
//        model.addAttribute("cardNumber", (String) ((JSONObject) jsonObject.get("card")).get("number"));
//      } else if (((String) jsonObject.get("method")).equals("가상계좌")) {
//        model.addAttribute("accountNumber", (String) ((JSONObject) jsonObject.get("virtualAccount")).get("accountNumber"));
//      } else if (((String) jsonObject.get("method")).equals("계좌이체")) {
//        model.addAttribute("bank", (String) ((JSONObject) jsonObject.get("transfer")).get("bank"));
//      } else if (((String) jsonObject.get("method")).equals("휴대폰")) {
//        model.addAttribute("customerMobilePhone", (String) ((JSONObject) jsonObject.get("mobilePhone")).get("customerMobilePhone"));
//      }
//    } else {
//      model.addAttribute("code", (String) jsonObject.get("code"));
//      model.addAttribute("message", (String) jsonObject.get("message"));
//    }
//
//    return "success";
//  }

  @GetMapping(value = "fail")
  public String paymentResult(
          Model model,
          @RequestParam(value = "message") String message,
          @RequestParam(value = "code") Integer code
  ) throws Exception {

    model.addAttribute("code", code);
    model.addAttribute("message", message);

    return "fail";
  }

  @GetMapping(value = "/success")
    public ResponseEntity<JSONObject> paymentResult(
          Model model,
          @RequestParam(value = "orderId") String orderId,
          @RequestParam(value = "amount") Integer amount,
          @RequestParam(value = "paymentKey") String paymentKey) throws Exception {

    JSONObject obj = new JSONObject();
    obj.put("orderId", orderId);
    obj.put("amount", amount);
    obj.put("paymentKey", paymentKey);

    // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
    // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
    String widgetSecretKey = "test_sk_P9BRQmyarYDagBXae54LVJ07KzLN";
    Base64.Encoder encoder = Base64.getEncoder();
    byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
    String authorizations = "Basic " + new String(encodedBytes);

    // 결제를 승인하면 결제수단에서 금액이 차감돼요.
    URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("Authorization", authorizations);
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestMethod("POST");
    connection.setDoOutput(true);

    OutputStream outputStream = connection.getOutputStream();
    outputStream.write(obj.toString().getBytes("UTF-8"));

    int code = connection.getResponseCode();
    boolean isSuccess = code == 200;

    InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

    // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
    Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = (JSONObject) parser.parse(reader);
    responseStream.close();

    model.addAttribute("responseStr", jsonObject.toJSONString());
    System.out.println(jsonObject.toJSONString());

        if (jsonObject.get("method") != null) {
      if (jsonObject.get("method").equals("카드")) {
        model.addAttribute("cardNumber", ((JSONObject) jsonObject.get("card")).get("number"));
      } else if (jsonObject.get("method").equals("가상계좌")) {
        model.addAttribute("accountNumber", ((JSONObject) jsonObject.get("virtualAccount")).get("accountNumber"));
      } else if (((String) jsonObject.get("method")).equals("계좌이체")) {
        model.addAttribute("bank", ((JSONObject) jsonObject.get("transfer")).get("bank"));
      } else if (((String) jsonObject.get("method")).equals("휴대폰")) {
        model.addAttribute("customerMobilePhone", (String) ((JSONObject) jsonObject.get("mobilePhone")).get("customerMobilePhone"));
      }
    } else {
      model.addAttribute("code", (String) jsonObject.get("code"));
      model.addAttribute("message", (String) jsonObject.get("message"));
    }

    return ResponseEntity.status(code).body(jsonObject);
  }

}
package com.example.mall.pay.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Map;

@Controller
@RequestMapping("/alipay")
public class PayPageController {
    @GetMapping("/return")
    public String payReturn(HttpServletRequest request, ModelMap model){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String[]> result = request.getParameterMap();
        String id = result.get("trade_no")[0];
        String orderId = result.get("out_trade_no")[0];
        String totalAmount = result.get("total_amount")[0];
        model.addAttribute("id", id);
        model.addAttribute("orderId", orderId);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("payTime",sdf.format(System.currentTimeMillis()));
        return "success";
    }
}

package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.ConferenceReceipt;
import edu.neu.oaas.service.ConferenceReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/conferenceReceipt")
public class ConferenceReceiptController {

    @Autowired
    private ConferenceReceiptService conferenceReceiptService;

    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submitConferenceReceipt(@RequestBody ConferenceReceipt conferenceReceipt) {
        try {
            conferenceReceiptService.createConferenceReceipt(conferenceReceipt);
            Map<String, String> response = new HashMap<>();
            response.put("message", "提交成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "提交失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

package edu.neu.oaas.service;

import edu.neu.oaas.mapper.ConferenceReceiptMapper;
import edu.neu.oaas.pojo.ConferenceReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConferenceReceiptService {

    @Autowired
    private ConferenceReceiptMapper conferenceReceiptMapper;

    public void createConferenceReceipt(ConferenceReceipt conferenceReceipt) {
        conferenceReceiptMapper.insertConferenceReceipt(conferenceReceipt);
    }
}

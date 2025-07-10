package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.ConferenceReceipt;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConferenceReceiptMapper {

    @Insert("INSERT INTO conference_receipt (conferenceID, unit, name, gender, phone, email, roomType, arrivalMode, arrivalFlight, arrivalTime, departureMode, departureFlight, departureTime, remarks) " +
            "VALUES (#{conferenceID}, #{unit}, #{name}, #{gender}, #{phone}, #{email}, #{roomType}, #{arrivalMode}, #{arrivalFlight}, #{arrivalTime}, #{departureMode}, #{departureFlight}, #{departureTime}, #{remarks})")
    void insertConferenceReceipt(ConferenceReceipt conferenceReceipt);
}

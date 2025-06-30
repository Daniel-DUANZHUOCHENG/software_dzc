package edu.neu.oaas.service;

import edu.neu.oaas.pojo.Information;
import edu.neu.oaas.mapper.InformationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InformationService {

    @Autowired
    private InformationMapper informationMapper;

    public List<Information> getAllInformation() {
        return informationMapper.getAllInformation();
    }

    public void addInformation(Information information) {
        informationMapper.addInformation(information);
    }

    public void updateInformation(Information information) {
        informationMapper.updateInformation(information);
    }

    public void deleteInformation(int id) {
        informationMapper.deleteInformation(id);
    }

    public Information getInformationById(int id) {
        return informationMapper.getInformationById(id);
    }
}


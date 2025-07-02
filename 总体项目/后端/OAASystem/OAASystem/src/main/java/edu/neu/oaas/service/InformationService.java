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

    public void deleteInformation(int id) {
        informationMapper.deleteInformation(id);
    }

    public Information getInformationById(int id) {
        return informationMapper.getInformationById(id);
    }

    public List<Information> findByPathPrefix(String pathPrefix) {
        return informationMapper.findByPathPrefix(pathPrefix);
    }

    public List<Information> searchByTitleAndAuthor(String pathPrefix, String title, String author) {
        return informationMapper.searchByTitleAndAuthor(pathPrefix, title, author);
    }

    public void insertInformation(Information information) {
        informationMapper.insertInformation(information);
    }

    public int updateInformation(Information information) {
        return informationMapper.updateInformation(information);
    }

    public void deleteByPathPrefix(String pathPrefix) {
        informationMapper.deleteByPathPrefix(pathPrefix);
    }

    public List<Information> getPendingInformation() {
        return informationMapper.selectPendingInformation();
    }

    public List<Information> getInformationByApprovalStatus(String approvalStatus) {
        return informationMapper.selectInformationByApprovalStatus(approvalStatus);
    }

    public int approveInformation(int id, String approvalStatus, String rejectionReason) {
        return informationMapper.approveInformation(id, approvalStatus, rejectionReason);
    }

    public List<Information> getApprovedInformation() {
        return informationMapper.selectApprovedInformation();
    }

    public List<Information> getInformationByTenantId(int tenantId) {
        return informationMapper.getInformationByTenantId(tenantId);
    }

    public List<Information> getAllInformation() {
        return informationMapper.getAllInformation();
    }
}


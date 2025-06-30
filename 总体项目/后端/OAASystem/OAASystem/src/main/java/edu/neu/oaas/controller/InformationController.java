package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.Information;
import edu.neu.oaas.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/information")
public class InformationController {

    @Autowired
    private InformationService informationService;

    @GetMapping
    public List<Information> getAllInformation() {
        return informationService.getAllInformation();
    }

    @GetMapping("/detail/{id}")
    public Information getInformationById(@PathVariable int id) {
        return informationService.getInformationById(id);
    }

    @PostMapping
    public Information addInformation(@RequestBody Information information) {
        validateInformation(information);
        informationService.addInformation(information);
        return information;
    }

    @PutMapping("/{id}")
    public Information updateInformation(@PathVariable int id, @RequestBody Information information) {
        information.setId(id);
        validateInformation(information);
        informationService.updateInformation(information);
        return information;
    }

    @DeleteMapping("/{id}")
    public void deleteInformation(@PathVariable int id) {
        informationService.deleteInformation(id);
    }

    private void validateInformation(Information information) {
        if (information.getCompany() == null || information.getCompany().isEmpty()) {
            throw new IllegalArgumentException("Company cannot be null or empty");
        }
        if (information.getTenantId() == 0) {
            throw new IllegalArgumentException("Tenant ID cannot be null or zero");
        }
    }
}

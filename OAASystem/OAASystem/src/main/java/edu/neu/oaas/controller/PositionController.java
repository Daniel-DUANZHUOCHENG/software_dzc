package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.Position;
import edu.neu.oaas.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/pos")
public class PositionController {
    @Autowired
    private PositionService positionService;
    @RequestMapping("/insert")
    public void insert(@RequestBody Position position){
        System.out.println(position.toString());
        positionService.insert(position);
    }


    @RequestMapping("/delete")
    public void delete(@RequestBody Position position){
        System.out.println(")))))))))))))))))))))))))))))))");
        positionService.delete(position);
    }

    @RequestMapping("/get")
    public List<Position> get(Integer departmentId){
        System.out.println("*********************************1");
        return positionService.get(departmentId);
    }
}

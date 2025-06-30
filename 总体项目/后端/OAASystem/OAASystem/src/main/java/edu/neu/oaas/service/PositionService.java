package edu.neu.oaas.service;

import edu.neu.oaas.mapper.PositionMapper;
import edu.neu.oaas.pojo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {
    @Autowired
    private PositionMapper positionMapper;

    public List<Position> get(Integer departmentId){
        return positionMapper.getAll(departmentId);
    }

    public void insert(Position position){
        positionMapper.insert(position);
    }

    public void delete(Position position){
        positionMapper.delete(position);
    }
}

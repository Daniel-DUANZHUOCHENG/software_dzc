package edu.neu.oaas.service;


import edu.neu.oaas.mapper.CarouselImageMapper;
import edu.neu.oaas.pojo.CarouselImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class CarouselImageService {
    @Autowired
    private CarouselImageMapper carouselImageMapper;

    private static final String IMAGE_BASE_PATH = "E:/OAASystem/OAASystem/src/main/resources/static/images/";

    public CarouselImage saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = IMAGE_BASE_PATH + fileName;

        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        file.transferTo(dest);

        CarouselImage image = new CarouselImage();
        image.setName(file.getOriginalFilename());
        image.setUrl("/images/" + fileName);
        carouselImageMapper.insertImage(image);
        return image;
    }


    public List<CarouselImage> getAllImages() {
        return carouselImageMapper.getAllImages();
    }

    public CarouselImage getImageById(Long id) {
        return carouselImageMapper.getImageById(id);
    }

    public void deleteImage(Long id) {
        CarouselImage image = carouselImageMapper.getImageById(id);
        if (image != null) {
            File file = new File(IMAGE_BASE_PATH + image.getUrl().split("/images/")[1]);
            if (file.exists()) {
                file.delete();
            }
            carouselImageMapper.deleteImage(id);
        }
    }
}


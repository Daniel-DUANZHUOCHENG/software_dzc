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

    private static final String IMAGE_BASE_PATH = System.getProperty("user.dir") + "/src/main/resources/static/images/";

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
        List<CarouselImage> images = carouselImageMapper.getAllImages();
        
        // 如果数据库中没有轮播图，返回默认图片
        if (images == null || images.isEmpty()) {
            CarouselImage defaultImage = new CarouselImage();
            defaultImage.setId(0L);
            defaultImage.setName("默认轮播图");
            defaultImage.setUrl("/images/banner/banner1.jpg");
            
            images = new java.util.ArrayList<>();
            images.add(defaultImage);
        }
        
        return images;
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


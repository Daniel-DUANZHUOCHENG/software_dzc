package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.CarouselImage;
import edu.neu.oaas.service.CarouselImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/carousel")
public class CarouselImageController {
    @Autowired
    private CarouselImageService carouselImageService;

    @PostMapping("/upload")
    public ResponseEntity<CarouselImage> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            CarouselImage savedImage = carouselImageService.saveImage(file);
            return ResponseEntity.ok(savedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images")
    public ResponseEntity<List<CarouselImage>> getAllImages() {
        List<CarouselImage> images = carouselImageService.getAllImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<CarouselImage> getImageById(@PathVariable Long id) {
        CarouselImage image = carouselImageService.getImageById(id);
        if (image == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(image);
    }

    @DeleteMapping("/image/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        carouselImageService.deleteImage(id);
        return ResponseEntity.ok().build();
    }
}

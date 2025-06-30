package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.CarouselImage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CarouselImageMapper {
    @Select("SELECT * FROM carousel_images")
    List<CarouselImage> getAllImages();

    @Insert("INSERT INTO carousel_images (name, url) VALUES (#{name}, #{url})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertImage(CarouselImage image);

    @Delete("DELETE FROM carousel_images WHERE id = #{id}")
    void deleteImage(Long id);

    @Select("SELECT * FROM carousel_images WHERE id = #{id}")
    CarouselImage getImageById(Long id);
}


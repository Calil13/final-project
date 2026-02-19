package org.example.finalproject.service

import org.example.finalproject.dto.ProductImageDto
import org.example.finalproject.entity.ProductImage
import org.example.finalproject.entity.Products
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.repository.ProductImageRepository
import org.example.finalproject.repository.ProductsRepository
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

class ProductImageServiceTest extends Specification {

    ProductsRepository productsRepository = Mock()
    ProductImageRepository productImageRepository = Mock()

    ProductImageService service

    @TempDir
    Path tempDir

    def setup() {
        service = new ProductImageService(productsRepository, productImageRepository)
        service.@uploadPath = tempDir.toString()
    }

    def "getProductImages should return empty list when no images found"() {
        given:
        productImageRepository.findByProductId(1L) >> []

        when:
        def result = service.getProductImages(1L)

        then:
        result.isEmpty()
    }

    def "getProductImages should map entities to dto"() {
        given:
        ProductImage image = new ProductImage("/uploads/test.jpg", new Products())
        image.setId(10L)

        productImageRepository.findByProductId(1L) >> [image]

        when:
        def result = service.getProductImages(1L)

        then:
        result.size() == 1
        result[0].id == 10L
        result[0].imageUrl == "/uploads/test.jpg"
    }

    def "uploadImage should save file and persist image entity"() {
        given:
        Products product = new Products()
        productsRepository.findById(1L) >> Optional.of(product)

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.jpg",
                        "image/jpeg",
                        "data".bytes
                )

        when:
        def result = service.uploadImage(1L, [file])

        then:
        1 * productImageRepository.save(_ as ProductImage) >> { args ->
            ProductImage img = args[0]
            img.setId(5L)
            return img
        }

        result.size() == 1
        Files.list(tempDir).count() == 1
    }


    def "uploadImage should throw when product not found"() {
        given:
        productsRepository.findById(99L) >> Optional.empty()

        when:
        service.uploadImage(99L, [])

        then:
        thrown(NotFoundException)
    }

    def "deleteImage should delete file and remove entity"() {
        given:
        Products product = new Products()
        ProductImage image = new ProductImage("/uploads/test.jpg", product)
        image.setId(7L)

        productImageRepository.findById(7L) >> Optional.of(image)

        Path filePath = tempDir.resolve("test.jpg")
        Files.write(filePath, "content".bytes)

        when:
        def result = service.deleteImage(7L)

        then:
        !Files.exists(filePath)
        1 * productImageRepository.delete(image)
        result == "Image deleted successfully."
    }

    def "deleteImage should throw when image not found"() {
        given:
        productImageRepository.findById(1L) >> Optional.empty()

        when:
        service.deleteImage(1L)

        then:
        thrown(NotFoundException)
    }
}

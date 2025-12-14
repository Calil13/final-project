package org.example.finalproject.service

import org.example.finalproject.entity.ProductImage
import org.example.finalproject.entity.Products
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.repository.ProductImageRepository
import org.example.finalproject.repository.ProductsRepository
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

class ProductImageServiceTest extends Specification {

    ProductsRepository productRepository = Mock()
    ProductImageRepository productImageRepository = Mock()

    ProductImageService service

    void setup() {
        service = new ProductImageService(productRepository, productImageRepository)
        service.uploadPath = "/test/uploads"
    }

    def "getProductImages should return images when found"() {
        given:
        def productId = 1L
        def images = [new ProductImage("url1", null)]
        productImageRepository.findByProductId(productId) >> images

        when:
        def result = service.getProductImages(productId)

        then:
        result.size() == 1
        result[0].imageUrl == "url1"
    }

    def "getProductImages should throw NotFoundException when no images"() {
        given:
        productImageRepository.findByProductId(1L) >> []

        when:
        service.getProductImages(1L)

        then:
        thrown(NotFoundException)
    }

    def "uploadImage should upload files and save ProductImage"() {
        given:
        def product = new Products()
        productRepository.findById(1L) >> Optional.of(product)

        MultipartFile file = Mock()
        file.isEmpty() >> false
        file.getOriginalFilename() >> "image.png"

        // Mock file.transferTo()
        file.transferTo(_ as File) >> { /* no-op */ }

        ProductImage savedImage = new ProductImage("/uploads/test.png", product)
        productImageRepository.save(_ as ProductImage) >> savedImage

        when:
        def result = service.uploadImage(1L, [file])

        then:
        result.size() == 1
        result[0].imageUrl.contains("/uploads/")
        1 * productImageRepository.save(_ as ProductImage)
    }

    def "uploadImage should throw NotFoundException if product not found"() {
        given:
        productRepository.findById(1L) >> Optional.empty()

        when:
        service.uploadImage(1L, [])

        then:
        thrown(NotFoundException)
    }

    def "deleteImage should delete image file and repository entry"() {
        given:
        def image = new ProductImage("/uploads/img1.png", null)
        productImageRepository.findById(3L) >> Optional.of(image)

        File mockFile = Mock()
        GroovyMock(File, global: true)
        new File("/test/uploads/img1.png") >> mockFile
        mockFile.exists() >> true
        mockFile.delete() >> true

        when:
        def result = service.deleteImage(3L)

        then:
        result == "Image deleted successfully!"
        1 * productImageRepository.delete(image)
    }

    def "deleteImage should throw NotFoundException if image not found"() {
        given:
        productImageRepository.findById(1L) >> Optional.empty()

        when:
        service.deleteImage(1L)

        then:
        thrown(NotFoundException)
    }
}


package org.example.finalproject.service

import org.example.finalproject.dto.VendorChangeStoreNameDto
import org.example.finalproject.entity.Users
import org.example.finalproject.entity.Vendor
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.repository.UsersRepository
import org.example.finalproject.repository.VendorRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class VendorsServiceSpec extends Specification {

    UsersRepository usersRepository = Mock()
    VendorRepository vendorRepository = Mock()

    VendorsService service = new VendorsService(
            null, usersRepository, vendorRepository, null, null
    )

    def setup() {
        Authentication authentication = Mock()
        authentication.getName() >> "test@example.com"

        SecurityContext securityContext = Mock()
        securityContext.getAuthentication() >> authentication

        SecurityContextHolder.setContext(securityContext)
    }

    def "changeStoreName - success"() {
        given:
        def dto = new VendorChangeStoreNameDto("New Store")

        def user = Users.builder()
                .id(1L)
                .email("test@example.com")
                .build()

        def vendor = Vendor.builder()
                .id(5L)
                .storeName("Old Store")
                .user(user)
                .build()

        usersRepository.findByEmail("test@example.com") >> Optional.of(user)
        vendorRepository.findByUser(user) >> Optional.of(vendor)

        when:
        def result = service.changeStoreName(dto)

        then:
        result == "Store name successfully updated!"
        vendor.storeName == "New Store"

        1 * vendorRepository.save(vendor)
    }

    def "changeStoreName - user not found"() {
        given:
        usersRepository.findByEmail("test@example.com") >> Optional.empty()

        when:
        service.changeStoreName(new VendorChangeStoreNameDto("NewName"))

        then:
        thrown(NotFoundException)
    }

    def "changeStoreName - vendor not found"() {
        given:
        def user = Users.builder()
                .id(1L)
                .email("test@example.com")
                .build()

        usersRepository.findByEmail("test@example.com") >> Optional.of(user)
        vendorRepository.findByUser(user) >> Optional.empty()

        when:
        service.changeStoreName(new VendorChangeStoreNameDto("NewName"))

        then:
        thrown(NotFoundException)
    }
}


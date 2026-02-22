package org.example.finalproject.service


import org.example.finalproject.dto.AddressDto
import org.example.finalproject.entity.Address
import org.example.finalproject.entity.Users
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.mapper.AddressMapper
import org.example.finalproject.repository.AddressRepository
import org.example.finalproject.repository.UsersRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class AddressServiceTest extends Specification {
    UsersRepository usersRepository = Mock()
    AddressRepository addressRepository = Mock()
    AddressMapper addressMapper = Mock()

    AddressService addressService = new AddressService(usersRepository, addressRepository, addressMapper)

    def setup() {
        def authentication = Mock(Authentication)
        def securityContext = Mock(SecurityContext)

        SecurityContextHolder.setContext(securityContext)
        securityContext.getAuthentication() >> authentication
        authentication.getName() >> "test@example.com"
    }

    def "should return address successfully"() {
        given: "a user and their address"
        def user = new Users(email: "test@example.com")
        def address = new Address(city: "Baku", street: "Nizami", home: "5")
        def expectedDto = new AddressDto(city: "Baku", street: "Nizami", home: "5")

        when: "getAddresses is called"
        def result = addressService.getAddresses()

        then: "repositories are called and data is returned"
        1 * usersRepository.findByEmailAndDeletedFalse("test@example.com") >> Optional.of(user)
        1 * addressRepository.findByUser(user) >> Optional.of(address)
        1 * addressMapper.toResponseDto(address) >> expectedDto

        and: "result matches expected data"
        result.city == "Baku"
        result.street == "Nizami"
    }

    def "should throw NotFoundException when user does not exist"() {
        given: "no user found in database"
        usersRepository.findByEmailAndDeletedFalse(_ as String) >> Optional.empty()

        when: "getAddresses is called"
        addressService.getAddresses()

        then: "NotFoundException is thrown"
        def e = thrown(NotFoundException)
        e.message == "User not found!"
    }

    def "should update address successfully"() {
        given: "an existing user and address with new data"
        def addressDto = new AddressDto(city: "London", street: "Baker St", home: "221B")
        def user = new Users(id: 1L, email: "test@example.com")
        def existingAddress = new Address(city: "Old City", street: "Old Street", home: "1")

        when: "updateAddress is called"
        def response = addressService.updateAddress(addressDto)

        then: "user is found and address is saved with new values"
        1 * usersRepository.findByEmailAndDeletedFalse(_) >> Optional.of(user)
        1 * addressRepository.findByUser(user) >> Optional.of(existingAddress)
        1 * addressRepository.save({ Address addr ->
            addr.city == "London" && addr.street == "Baker St" && addr.home == "221B"
        }) >> { it[0 as String] }

        and: "success message is returned"
        response == "Address changed successfully."
    }

    def "should throw NotFoundException when address record is missing during update"() {
        given: "a user exists but has no address record"
        def user = new Users(email: "test@example.com")
        usersRepository.findByEmailAndDeletedFalse(_ as String) >> Optional.of(user)
        addressRepository.findByUser(user) >> Optional.empty()

        when: "updateAddress is called"
        addressService.updateAddress(new AddressDto())

        then: "NotFoundException is thrown"
        def e = thrown(NotFoundException)
        e.message == "Address not found!"
    }
}

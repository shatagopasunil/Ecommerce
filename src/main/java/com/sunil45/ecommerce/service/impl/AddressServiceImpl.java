package com.sunil45.ecommerce.service.impl;

import com.sunil45.ecommerce.dto.AddressDTO;
import com.sunil45.ecommerce.exceptions.ResourceNotFoundException;
import com.sunil45.ecommerce.model.Address;
import com.sunil45.ecommerce.model.User;
import com.sunil45.ecommerce.repository.AddressRepository;
import com.sunil45.ecommerce.repository.UserRepository;
import com.sunil45.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO getAddressesById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + addressId + " does not exist"));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        return user.getAddresses().stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address dbAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + addressId + " does not exist"));
        dbAddress.setCity(addressDTO.getCity());
        dbAddress.setPinCode(addressDTO.getPinCode());
        dbAddress.setState(addressDTO.getState());
        dbAddress.setCountry(addressDTO.getCountry());
        dbAddress.setStreet(addressDTO.getStreet());
        dbAddress.setBuildingName(addressDTO.getBuildingName());

        Address updatedAddress = addressRepository.save(dbAddress);
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + addressId + " does not exist"));

        addressRepository.delete(address);
    }
}

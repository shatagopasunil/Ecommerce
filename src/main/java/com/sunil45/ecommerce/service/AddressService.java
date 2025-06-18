package com.sunil45.ecommerce.service;

import com.sunil45.ecommerce.dto.AddressDTO;
import com.sunil45.ecommerce.model.User;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();

    AddressDTO getAddressesById(Long addressId);

    List<AddressDTO> getUserAddresses(User user);

    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    void deleteAddress(Long addressId);
}

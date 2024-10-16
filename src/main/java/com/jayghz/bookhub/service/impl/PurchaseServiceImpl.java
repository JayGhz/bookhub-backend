package com.jayghz.bookhub.service.impl;

import java.util.*;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import com.jayghz.bookhub.dto.PurchaseCreateDTO;
import com.jayghz.bookhub.dto.PurchaseDTO;
import com.jayghz.bookhub.exception.ResourceNotFoundException;
import com.jayghz.bookhub.mapper.PurchaseMapper;
import com.jayghz.bookhub.model.entity.Purchase;
import com.jayghz.bookhub.model.entity.User;
import com.jayghz.bookhub.model.enums.PaymentStatus;
import com.jayghz.bookhub.repository.PurchaseRepository;
import com.jayghz.bookhub.repository.UserRepository;
import com.jayghz.bookhub.service.PurchaseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final PurchaseMapper purchaseMapper;

    @Override
    @Transactional
    public PurchaseDTO createPurchase(PurchaseCreateDTO purchaseCreateDTO) {
        
        // Convertir PurchaseCreateDTO a Purchase
        Purchase purchase = purchaseMapper.toPurchaseCreateDTO(purchaseCreateDTO);

        User customer = userRepository.findById(purchaseCreateDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Establecer la fecha de creación de la compra
        purchase.setCreatedAt(LocalDateTime.now());
        // Establecer el estado de pago de la compra
        purchase.setPaymentStatus(PaymentStatus.PENDING);

        Float total = purchase.getItems().stream()
            .map(item -> item.getQuantity() * item.getBook().getPrice())
            .reduce(0.0f, Float::sum);
        purchase.setTotal(total);

        purchase.setCustomer(customer);
        purchase.getItems().forEach(item -> item.setPurchase(purchase));

        Purchase savePurchase = purchaseRepository.save(purchase);

        return purchaseMapper.toPurchaseDTO(savePurchase);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseDTO> getPurchasesHistoryByUserId(Integer userId) {
        return purchaseRepository.findByCustomerId(userId).stream()
            .map(purchaseMapper::toPurchaseDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Override
    public Purchase confirmPurchase(Integer purchaseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'confirmPurchase'");
    }

    @Override
    public Purchase getPurchaseById(Integer id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPurchaseById'");
    }

}

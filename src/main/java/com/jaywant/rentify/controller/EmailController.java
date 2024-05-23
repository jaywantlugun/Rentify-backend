package com.jaywant.rentify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jaywant.rentify.models.User;
import com.jaywant.rentify.repository.UserRepository;

@RestController
public class EmailController {

    @Autowired
    private JavaMailSender emailSender;
    
    @Autowired
    UserRepository userRepository;

    @PostMapping("/send-email")
    public String sendEmail(
    		@RequestParam("userId") Integer userId,
            @RequestParam("ownerId") Integer ownerId
            ) {
    	
    	User buyer = userRepository.findById(userId).orElse(null);
    	User seller = userRepository.findById(ownerId).orElse(null);
	
    	
        SimpleMailMessage buyerToSellerMessage = new SimpleMailMessage();
        buyerToSellerMessage.setTo(seller.getEmail());
        buyerToSellerMessage.setSubject("Property applied");
        buyerToSellerMessage.setText(buyer.getFirstName()+" has applied for property. His phone number is - "+buyer.getPhoneNumber());
        emailSender.send(buyerToSellerMessage);
        
        SimpleMailMessage sellerToBuyerMessage = new SimpleMailMessage();
        sellerToBuyerMessage.setTo(buyer.getEmail());
        sellerToBuyerMessage.setSubject("Property applied successfully");
        sellerToBuyerMessage.setText("Property seller details. Name = "+seller.getFirstName()+" "+seller.getLastName()+"."+" Phone number = "+seller.getPhoneNumber());
        emailSender.send(sellerToBuyerMessage);
        
        
        return "Email sent successfully!";
    }
}

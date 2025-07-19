package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentMapper {
    private final static BigDecimal amount = new BigDecimal("19.90");

    public static Payment toPayment(String request) {
        var correlationId = JsonParse.extractUUIDFromRequest(request);

        var payment = new Payment(correlationId, amount,
                Instant.now());

        payment.json = JsonParse.buildPaymentDTO(payment);

        return payment;
    }
}

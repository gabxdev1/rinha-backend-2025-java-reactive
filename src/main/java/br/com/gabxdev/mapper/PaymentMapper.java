package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentMapper {
    public static Payment toPayment(String request) {
        var correlationId = JsonParse.extractUUIDFromRequest(request);
        var amount = JsonParse.extractAmountFromRequest(request);

        var payment = new Payment(correlationId, new BigDecimal(amount),
                Instant.now());

        payment.json = JsonParse.buildPaymentDTO(payment);

        return payment;
    }
}

package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;

import static br.com.gabxdev.mapper.JsonTemplate.PAYMENT_POST_REQUEST;

public class PaymentMapper {
    private final static BigDecimal amount = new BigDecimal("19.90");

    public static Payment toPayment(String request) {
        var correlationId = JsonParse.extractUUIDFromRequest(request);

        var paymentPostToProcessorRequest = new Payment(correlationId, amount,
                Instant.now());

        paymentPostToProcessorRequest.json = java.lang.String.format(PAYMENT_POST_REQUEST,
                correlationId, amount, paymentPostToProcessorRequest.getRequestedAt());

        return paymentPostToProcessorRequest;
    }
}

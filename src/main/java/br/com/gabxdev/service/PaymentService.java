package br.com.gabxdev.service;

import br.com.gabxdev.middleware.PaymentMiddleware;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import br.com.gabxdev.response.PaymentSummaryGetResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZoneOffset;

import static br.com.gabxdev.mapper.JsonParse.parseInstant;

@Service
public class PaymentService {

    private final InMemoryPaymentDatabase paymentRepository;

    private final PaymentMiddleware paymentMiddleware;

    public PaymentService(InMemoryPaymentDatabase paymentRepository, PaymentMiddleware paymentMiddleware) {
        this.paymentRepository = paymentRepository;
        this.paymentMiddleware = paymentMiddleware;
    }

    public PaymentSummaryGetResponse paymentSummaryToMerge(String fromS, String toS) {
        var from = parseInstant(fromS);
        var to = parseInstant(toS);

        if (from.atZone(ZoneOffset.UTC).getYear() == 2000) {
            return paymentRepository.getTotalSummary();
        } else {
            return paymentRepository.getSummaryByTimeRange(from, to);
        }
    }

    public Mono<PaymentSummaryGetResponse> getPaymentSummary(String fromS, String toS) {
        var from = parseInstant(fromS);
        var to = parseInstant(toS);

        return paymentMiddleware.syncPaymentSummary(from, to)
                .then(Mono.fromSupplier(() -> {
                    if (from.atZone(ZoneOffset.UTC).getYear() == 2000) {
                        return paymentRepository.getTotalSummary();
                    } else {
                        return paymentRepository.getSummaryByTimeRange(from, to);
                    }
                }))
                .flatMap(paymentMiddleware::takeSummaryMerged);
    }

    public void purgePayments() {
        paymentRepository.deleteAll();

        paymentMiddleware.purgePayments();
    }

    public void purgePaymentsInternal() {
        paymentRepository.deleteAll();
    }
}

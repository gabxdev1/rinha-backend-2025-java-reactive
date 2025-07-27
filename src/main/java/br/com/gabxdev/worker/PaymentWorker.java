package br.com.gabxdev.worker;

import br.com.gabxdev.client.PaymentProcessorClient;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;

@Component
public class PaymentWorker {

    @Value("${rinha.server.worker-pool-size}")
    private Integer workerPoolSize;

    @Value("${rinha.queue-buffer}")
    private Integer queueBuffer;

    private ArrayBlockingQueue<Payment> queue;

    private final InMemoryPaymentDatabase paymentRepository;

    private final PaymentProcessorClient processorClient;

    public PaymentWorker(InMemoryPaymentDatabase paymentRepository, PaymentProcessorClient processorClient) {
        this.paymentRepository = paymentRepository;
        this.processorClient = processorClient;
    }

    @PostConstruct
    public void start() {
        this.queue = new ArrayBlockingQueue<>(queueBuffer);

        for (int i = 0; i < workerPoolSize; i++) {
            Thread.startVirtualThread(this::runWorker);
        }
    }

    private void runWorker() {
        while (true) {
            var request = takePayment();

            processPayment(request);
        }
    }

    public Payment takePayment() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void enqueue(Payment request) {
        queue.offer(request);
    }

    private void processPayment(Payment payment) {
        var isSuccessful = processorClient.sendPayment(payment);

        if (isSuccessful) {
            paymentRepository.save(payment);

            return;
        }

        enqueue(payment);
    }
}

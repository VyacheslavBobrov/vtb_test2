package ru.bobrov.vyacheslav.test2.repository;

import lombok.val;
import org.springframework.stereotype.Repository;
import ru.bobrov.vyacheslav.test2.models.AccountInfo;
import ru.bobrov.vyacheslav.test2.models.Payment;
import ru.bobrov.vyacheslav.test2.models.ProductCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class PaymentRepositoryImpl extends PaymentRepository {
    // Не thread safe, не сохраняется в надежное хранилище, решение исключительно для тестов
    private final List<Payment> payments = new ArrayList<>();

    @Override
    public void clear() {
        payments.clear();
    }

    @Override
    public void save(Collection<Payment> payments) {
        this.payments.addAll(payments);
    }

    @Override
    public Stream<Payment> findAll() {
        return payments.stream();
    }

    @Override
    public List<Payment> findAllInPeriod(LocalDateTime start, LocalDateTime end) {
        return payments.stream()
                .filter(payment -> start.isBefore(payment.getDateTime()) && end.isAfter(payment.getDateTime()))
                // start.isEqual(), end.isEqual() - если границы диапазона требуется включать в выборку
                .toList();
    }

    @Override
    public Double spentOnFood(LocalDateTime start, LocalDateTime end) {
        return findAllInPeriod(start, end)
                .stream()
                .filter(payment -> payment.getProductCategory().equals(ProductCategory.FOOD))
                .map(Payment::getAmount)
                .reduce(Double::sum)
                .orElse(0.0);
    }

    @Override
    public List<ProductCategory> spentMoreThanPlanned(Double planned) {
        return payments.stream()
                .collect(Collectors.groupingBy(Payment::getProductCategory)).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().map(Payment::getAmount).reduce(Double::sum).orElse(0.0)))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > planned)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public Map<ProductCategory, List<Payment>> groupedByCategory(String accountNumber, String bic) {
        return payments.stream()
                .filter(payment -> accountNumber.equals(payment.getFrom().getAccountNumber()) &&
                        bic.equals(payment.getFrom().getBic()))
                .collect(Collectors.groupingBy(Payment::getProductCategory));
    }

    @Override
    public List<Payment> paymentsBetweenDifferentAccounts() {
        return payments.stream()
                .filter(payment -> !payment.getFrom().equals(payment.getTo()))
                .toList();
    }

    @Override
    public Double paymentSumInPeriod(LocalDateTime start, LocalDateTime end) {
        return findAllInPeriod(start, end).stream()
                .map(Payment::getAmount)
                .reduce(Double::sum).orElse(0.0);
    }

    private List<Payment> findBiggestPayments() {
        val maxPayment = payments.stream().map(Payment::getAmount).max(Double::compareTo)
                .orElseThrow(IllegalArgumentException::new);
        return payments.stream()
                .filter(payment -> payment.getAmount() >= maxPayment)
                .toList();
    }

    @Override
    public List<LocalDate> findDaysWithBiggestPayment() {
        return findBiggestPayments().stream()
                .map(Payment::getDateTime)
                .map(LocalDate::from)
                .collect(Collectors.toSet())
                .stream().toList();
    }

    @Override
    public LocalDate findFirstDayWithBiggestPayment() {
        return findBiggestPayments().stream().findFirst().orElseThrow(IllegalArgumentException::new)
                .getDateTime().toLocalDate();
    }

    @Override
    public List<AccountInfo> findPaymentsCountForAccount() {
        val paymentsByAccount = payments.stream().collect(Collectors.groupingBy(Payment::getTo));
        return paymentsByAccount.entrySet().stream()
                .map(entry -> new AccountInfo(entry.getKey(), entry.getValue().size()))
                .toList();
    }

    @Override
    public Integer paymentsCount(String accountNumber, String bic) {
        long count = payments.stream()
                .map(Payment::getTo)
                .filter(account -> accountNumber.equals(account.getAccountNumber()) && bic.equals(account.getBic()))
                .count();

        return (int) count;
    }
}

package ru.bobrov.vyacheslav.test2.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.bobrov.vyacheslav.test2.models.Payment;
import ru.bobrov.vyacheslav.test2.models.ProductCategory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
public class PaymentRepositoryTest {
    private static final String PAYMENTS_JSON = "src/test/resources/payments.json";
    @Autowired
    @Setter
    private ObjectMapper mapper;

    @Autowired
    @Setter
    private PaymentRepository repository;
    private long paymentCount;

    @BeforeEach
    public void initRepo() throws IOException {
        repository.clear();
        File file = new File(PAYMENTS_JSON);
        List<Payment> payments = mapper.readValue(file, new TypeReference<>() {
        });
        paymentCount = payments.size();
        repository.save(payments);
    }

    @Test
    public void paymentsInRepo_findAll_allPayments() {
        long paymentsCount = repository.findAll().count();

        assertEquals("Wrong payments count", this.paymentCount, paymentsCount);
    }

    @Test
    public void period_findAllInPeriod_paymentsFromPeriod() {
        var from = LocalDateTime.of(2023, 4, 14, 16, 57, 6);
        var to = LocalDateTime.of(2023, 4, 14, 18, 57, 6);

        var payments = repository.findAllInPeriod(from, to);

        assertEquals("Wrong payments count", 3, payments.size());

        var wrongTime = payments.stream()
                .map(Payment::getDateTime)
                .anyMatch(time -> from.isAfter(time) || to.isBefore(time));
        assertFalse(wrongTime);
    }

    @Test
    public void foodCategory_spentOnFood_sum() {
        var from = LocalDateTime.of(2023, 4, 14, 16, 57, 6);
        var to = LocalDateTime.of(2023, 4, 14, 18, 57, 6);

        var spent = repository.spentOnFood(from, to);
        assertEquals("Wrong spent", 1.0, spent);
    }

    @Test
    public void payments_spentMoreThanPlanned_categories() {
        var categories = repository.spentMoreThanPlanned(3.0);
        assertEquals("Wrong categories count", 2, categories.size());
        assertTrue("Wrong categories",
                Set.of(ProductCategory.FOOD, ProductCategory.EDUCATION).containsAll(categories));
    }

    @Test
    public void payments_groupedByCategory_allCategory() {
        var paymentsByCategory = repository.groupedByCategory("55555", "11111");

        var expectedCategory = Set.of(ProductCategory.FOOD, ProductCategory.MEDICINE);
        assertEquals("Wrong category count", expectedCategory.size(), paymentsByCategory.keySet().size());
        assertTrue("Wrong category set", expectedCategory.containsAll(paymentsByCategory.keySet()));

        assertEquals("Wrong payments list for FOOD",
                2, paymentsByCategory.get(ProductCategory.FOOD).size());
        assertEquals("Wrong payments list for FOOD",
                1, paymentsByCategory.get(ProductCategory.MEDICINE).size());
    }

    @Test
    public void payments_paymentsBetweenDifferentAccounts_allPayments() {
        var payments = repository.paymentsBetweenDifferentAccounts();
        assertEquals("Wrong payments count", (int) this.paymentCount, payments.size());
    }

    @Test
    public void payments_paymentSumInPeriod_correctSum() {
        var from = LocalDateTime.of(2023, 4, 14, 16, 57, 6);
        var to = LocalDateTime.of(2023, 4, 14, 18, 57, 6);

        var sum = repository.paymentSumInPeriod(from, to);

        assertEquals("Wrong payments sum", 10.0, sum);
    }

    @Test
    public void payments_findDaysWithBiggestPayment_dayList() {
        var days = repository.findDaysWithBiggestPayment();
        var expectedDays = Set.of(14, 18);

        assertEquals("Wrong days count", expectedDays.size(), days.size());
        assertTrue("Wrong day set", expectedDays.containsAll(days.stream()
                .map(LocalDate::getDayOfMonth).toList()));
    }

    @Test
    public void payments_findFirstDayWithBiggestPayment_correctDay() {
        var day = repository.findFirstDayWithBiggestPayment();
        assertEquals("Wrong day num", 14, day.getDayOfMonth());
    }

    @Test
    public void payments_findPaymentsCountForAccount_correctInfo() {
        var accountsInfo = repository.findPaymentsCountForAccount();

        assertEquals("Wrong accounts num", 2, accountsInfo.size());

        var accountInfo1 = accountsInfo.get(0);
        var account1 = accountInfo1.getAccount();
        assertEquals("Wrong account num", "22222", account1.getAccountNumber());
        assertEquals("Wrong bic", "22222", account1.getBic());
        assertEquals("Wrong payments num", 6, accountInfo1.getCount());


        var accountInfo2 = accountsInfo.get(1);
        var account2 = accountInfo2.getAccount();
        assertEquals("Wrong account num", "11111", account2.getAccountNumber());
        assertEquals("Wrong bic", "22222", account2.getBic());
        assertEquals("Wrong payments num", 1, accountInfo2.getCount());
    }

    @Test
    public void payments_paymentsCount_correctCount() {
        var count = repository.paymentsCount("22222", "22222");
        assertEquals("Wrong payments count", 6, count);
    }
}

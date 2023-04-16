package ru.bobrov.vyacheslav.test2.repository;

import ru.bobrov.vyacheslav.test2.models.AccountInfo;
import ru.bobrov.vyacheslav.test2.models.Payment;
import ru.bobrov.vyacheslav.test2.models.ProductCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class PaymentRepository {
    /**
     * Очистить репозиторий
     */
    public abstract void clear();

    /**
     * Сохранить платежи
     *
     * @param payments список платежей
     */
    public abstract void save(Collection<Payment> payments);

    /**
     * Найти все счета
     *
     * @return список всех счетов
     */
    public abstract Stream<Payment> findAll();

    /**
     * Найти все платежи за указанный интервал
     *
     * @param start начало периода
     * @param end   конец периода
     * @return платежи за указанный период
     */
    public abstract List<Payment> findAllInPeriod(LocalDateTime start, LocalDateTime end);

    /**
     * Найти общее количество денег, потраченных на еду за указанный интервал
     *
     * @param start начало периода
     * @param end   конец периода
     * @return сумма трат на еду
     */
    public abstract Double spentOnFood(LocalDateTime start, LocalDateTime end);

    /**
     * Найти категории продуктов, на которые было потрачено больше чем планировалось
     *
     * @param planned планируемая сумма
     * @return список категорий с превышением трат
     */
    public abstract List<ProductCategory> spentMoreThanPlanned(Double planned);

    /**
     * Найти все платежи со счета, сгруппированные по категориям
     *
     * @param accountNumber счет
     * @param bic           БИК
     * @return платежи, сгруппированные по счетам
     */
    public abstract Map<ProductCategory, List<Payment>> groupedByCategory(String accountNumber, String bic);

    /**
     * Найти все платежи между разными счетами
     *
     * @return платежи между разными счетами
     */
    public abstract List<Payment> paymentsBetweenDifferentAccounts();

    /**
     * Найти сумму платежей за период
     *
     * @param start начало периода
     * @param end   конец периода
     * @return сумма платежей
     */
    public abstract Double paymentSumInPeriod(LocalDateTime start, LocalDateTime end);

    /**
     * Получить дни с самым большим платежом
     *
     * @return дни с самым большим платежом
     */
    public abstract List<LocalDate> findDaysWithBiggestPayment();

    /**
     * Получить первый попавшийся день с самым большим платежом
     *
     * @return день с самым большим платежом
     */
    public abstract LocalDate findFirstDayWithBiggestPayment();

    /**
     * Получить информацию по количеству переводов на счета получателя
     *
     * @return количество переводов на счета получателя
     */
    public abstract List<AccountInfo> findPaymentsCountForAccount();

    /**
     * Получить количество переводов на заданный счет
     *
     * @param accountNumber Счет
     * @param bic           БИК
     * @return количество переводов
     */
    public abstract Integer paymentsCount(String accountNumber, String bic);
}

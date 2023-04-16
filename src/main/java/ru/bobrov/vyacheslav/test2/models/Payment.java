package ru.bobrov.vyacheslav.test2.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    Account from;
    Account to;
    Double amount;
    LocalDateTime dateTime;
    ProductCategory productCategory;
}

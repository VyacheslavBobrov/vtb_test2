package ru.bobrov.vyacheslav.test2.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    String accountNumber;
    String bic;
}

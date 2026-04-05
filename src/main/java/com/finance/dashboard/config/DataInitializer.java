package com.finance.dashboard.config;

import com.finance.dashboard.model.AppUser;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.model.Role;
import com.finance.dashboard.model.UserStatus;
import com.finance.dashboard.repository.AppUserRepository;
import com.finance.dashboard.repository.FinancialRecordRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(AppUserRepository userRepository, FinancialRecordRepository recordRepository) {
        return args -> {
            if (userRepository.count() > 0 || recordRepository.count() > 0) {
                return;
            }

            AppUser admin = userRepository.save(new AppUser(
                    "Alice Admin",
                    "alice.admin@finance.local",
                    Role.ADMIN,
                    UserStatus.ACTIVE,
                    Instant.now()
            ));

            userRepository.save(new AppUser(
                    "Brian Analyst",
                    "brian.analyst@finance.local",
                    Role.ANALYST,
                    UserStatus.ACTIVE,
                    Instant.now()
            ));

            userRepository.save(new AppUser(
                    "Vera Viewer",
                    "vera.viewer@finance.local",
                    Role.VIEWER,
                    UserStatus.ACTIVE,
                    Instant.now()
            ));

            recordRepository.save(new FinancialRecord(
                    new BigDecimal("4200.00"),
                    RecordType.INCOME,
                    "Salary",
                    LocalDate.now().minusDays(18),
                    "Monthly salary payment",
                    Instant.now().minusSeconds(86400L * 18),
                    admin.getFullName()
            ));
            recordRepository.save(new FinancialRecord(
                    new BigDecimal("200.00"),
                    RecordType.INCOME,
                    "Freelance",
                    LocalDate.now().minusDays(12),
                    "Design side project",
                    Instant.now().minusSeconds(86400L * 12),
                    admin.getFullName()
            ));
            recordRepository.save(new FinancialRecord(
                    new BigDecimal("1250.00"),
                    RecordType.EXPENSE,
                    "Rent",
                    LocalDate.now().minusDays(16),
                    "Apartment rent",
                    Instant.now().minusSeconds(86400L * 16),
                    admin.getFullName()
            ));
            recordRepository.save(new FinancialRecord(
                    new BigDecimal("320.50"),
                    RecordType.EXPENSE,
                    "Groceries",
                    LocalDate.now().minusDays(10),
                    "Weekly groceries",
                    Instant.now().minusSeconds(86400L * 10),
                    admin.getFullName()
            ));
            recordRepository.save(new FinancialRecord(
                    new BigDecimal("89.99"),
                    RecordType.EXPENSE,
                    "Internet",
                    LocalDate.now().minusDays(8),
                    "Home internet bill",
                    Instant.now().minusSeconds(86400L * 8),
                    admin.getFullName()
            ));
            recordRepository.save(new FinancialRecord(
                    new BigDecimal("560.00"),
                    RecordType.INCOME,
                    "Bonus",
                    LocalDate.now().minusDays(5),
                    "Performance bonus",
                    Instant.now().minusSeconds(86400L * 5),
                    admin.getFullName()
            ));
            recordRepository.save(new FinancialRecord(
                    new BigDecimal("145.25"),
                    RecordType.EXPENSE,
                    "Transport",
                    LocalDate.now().minusDays(3),
                    "Fuel and commute",
                    Instant.now().minusSeconds(86400L * 3),
                    admin.getFullName()
            ));
            recordRepository.save(new FinancialRecord(
                    new BigDecimal("75.00"),
                    RecordType.EXPENSE,
                    "Entertainment",
                    LocalDate.now().minusDays(1),
                    "Movie tickets",
                    Instant.now().minusSeconds(86400L),
                    admin.getFullName()
            ));
        };
    }
}

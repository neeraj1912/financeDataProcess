package com.finance.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=LEGACY",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FinanceDashboardApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void viewerCanAccessDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                        .header("X-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").exists())
                .andExpect(jsonPath("$.monthlyTrends").isArray());
    }

    @Test
    void viewerCannotReadFinancialRecords() throws Exception {
        mockMvc.perform(get("/api/records")
                        .header("X-User-Id", "3"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateFinancialRecord() throws Exception {
        String requestBody = """
                {
                  "amount": 150.75,
                  "type": "EXPENSE",
                  "category": "Utilities",
                  "recordDate": "2026-04-01",
                  "notes": "Electricity bill"
                }
                """;

        mockMvc.perform(post("/api/records")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("Utilities"))
                .andExpect(jsonPath("$.createdByName").value("Alice Admin"));
    }
}

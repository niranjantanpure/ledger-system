package com.niranjan.ledger.transaction;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LedgerTransactionRepository transactionRepository;

	@BeforeEach
	void setUp() {
		transactionRepository.deleteAll();
	}

	@Test
	void createsTransaction() throws Exception {
		mockMvc.perform(post("/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "amount": 125.50,
								  "type": "INCOME",
								  "category": "Salary",
								  "description": "March salary",
								  "occurredAt": "2026-03-24T10:15:30Z"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern("/transactions/\\d+")))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.amount").value(125.50))
				.andExpect(jsonPath("$.type").value("INCOME"))
				.andExpect(jsonPath("$.category").value("Salary"))
				.andExpect(jsonPath("$.description").value("March salary"))
				.andExpect(jsonPath("$.occurredAt").value("2026-03-24T10:15:30Z"));
	}

	@Test
	void listsTransactionsInReverseChronologicalOrder() throws Exception {
		mockMvc.perform(post("/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "amount": 12.00,
								  "type": "EXPENSE",
								  "category": "Food",
								  "description": "Lunch",
								  "occurredAt": "2026-03-24T09:00:00Z"
								}
								"""))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "amount": 250.00,
								  "type": "INCOME",
								  "category": "Freelance",
								  "description": "Project payment",
								  "occurredAt": "2026-03-24T11:00:00Z"
								}
								"""))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/transactions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].description").value("Project payment"))
				.andExpect(jsonPath("$[1].description").value("Lunch"));
	}

	@Test
	void rejectsInvalidTransaction() throws Exception {
		mockMvc.perform(post("/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "amount": 0,
								  "type": "EXPENSE",
								  "category": "",
								  "description": "Bad request",
								  "occurredAt": "2026-03-24T11:00:00Z"
								}
								"""))
				.andExpect(status().isBadRequest());
	}
}

package com.ebroker.trade.integrationTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ebroker.trade.controller.TradeController;
import com.ebroker.trade.dto.EquityDTO;
import com.ebroker.trade.entity.Equity;
import com.ebroker.trade.entity.Fund;
import com.ebroker.trade.repository.EquityRepository;
import com.ebroker.trade.repository.FundsRepository;
import com.ebroker.trade.util.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	FundsRepository fundsRepo;
	
	@MockBean
	EquityRepository equityRepo;

	@Autowired
	TradeController tradeController;

	@Test
	void shouldReturnHomePage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Home Page.")));
	}
	
	@Test
	void shouldReturnFund_availableInDB() throws Exception {
		Fund fund = new Fund(1000);
		fund.setId(1);
		Mockito.when(fundsRepo.findById(1)).thenReturn(Optional.of(fund));
		mockMvc.perform(MockMvcRequestBuilders.get("/fund"))
		  .andExpect(MockMvcResultMatchers.status().isOk())
		  .andExpect(MockMvcResultMatchers.jsonPath("$",
		  Matchers.is("Available fund = 1000.0")));
	}
	
	@Test
	void shouldReturn_zeroFund() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/fund"))
		  .andExpect(MockMvcResultMatchers.status().isOk())
		  .andExpect(MockMvcResultMatchers.jsonPath("$",
		  Matchers.is("Available fund = 0.0")));
	}

	@Test
	void shouldTestAddFund_successCase_fundNotExists() throws Exception {
		double amount = 1000;
		Fund fund = new Fund(amount);
		Mockito.when(fundsRepo.save(Mockito.any(Fund.class))).thenReturn(fund);
		mockMvc.perform(MockMvcRequestBuilders.post("/fund?amount=" + amount))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Funds Added Sucessfully")));
	}
	
	@Test
	void shouldTestAddFund_successCase_fundExists() throws Exception{
		double amount = 1000;
		Fund fund = new Fund(amount);
		Mockito.when(fundsRepo.findById(1)).thenReturn(Optional.of(fund));
		Mockito.when(fundsRepo.save(fund)).thenReturn(fund);
		mockMvc.perform(MockMvcRequestBuilders.post("/fund?amount=" + amount))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Funds Added Sucessfully")));
	}
	
	@Test
	void shouldTestAddFund_failCase() throws Exception{
		double amount = 1000;
		mockMvc.perform(MockMvcRequestBuilders.post("/fund?amount=" + amount))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Something went wrong.")));
	}
	
	@Test
	void shouldReturnEquityList() throws Exception{
		List<Equity> list = new ArrayList<>();
		Equity hdfc = new Equity("hdfc", 4);
		hdfc.setId(1);
		list.add(hdfc);
		Mockito.when(equityRepo.findAll()).thenReturn(list);
		mockMvc.perform(MockMvcRequestBuilders.get("/equity"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
	}
	
	@Test
	void shouldTestTrade_outOfWorkingHour() throws Exception{
		try(MockedStatic<DateUtil> date = Mockito.mockStatic(DateUtil.class)) {
			date.when(DateUtil :: checkForWorkingHours).thenReturn(false);
			ObjectMapper mapper = new ObjectMapper();  
			EquityDTO equityDto = new EquityDTO("hdfc", 2500, 2, "buy");
			mockMvc.perform(MockMvcRequestBuilders.post("/trade").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(equityDto)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Trades can be executed from Monday to Friday and between 9AM to 5PM only.")));
		} 
	}
	
	@Test
	void shouldTestCheckForWorkingHours() throws Exception{
		EquityDTO equityDto = new EquityDTO("hdfc", 2500, 2, "buy");
		ObjectMapper mapper = new ObjectMapper();  
		mockMvc.perform(MockMvcRequestBuilders.post("/trade").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(equityDto)))
		.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	
	@Test
	void shouldTestTrade_invalidOrder() throws Exception{
		try(MockedStatic<DateUtil> date = Mockito.mockStatic(DateUtil.class)) {
			date.when(DateUtil :: checkForWorkingHours).thenReturn(true);
			ObjectMapper mapper = new ObjectMapper();  
			EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "invalid");
			mockMvc.perform(MockMvcRequestBuilders.post("/trade").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(equityDTO)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Please check your request and try again.")));
		} 
	}
	
	@Test
	void shouldTestTrade_buyOrder_insufficientFund() throws Exception{
		try(MockedStatic<DateUtil> date = Mockito.mockStatic(DateUtil.class)) {
			date.when(DateUtil :: checkForWorkingHours).thenReturn(true);
			ObjectMapper mapper = new ObjectMapper();  
			EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "buy");
			mockMvc.perform(MockMvcRequestBuilders.post("/trade").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(equityDTO)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("You don't have sufficient funds to buy equity.")));
		} 
	}
	
	@Test
	void shouldTestTrade_buyOrder_existingEquity() throws Exception {
		try(MockedStatic<DateUtil> date = Mockito.mockStatic(DateUtil.class)) {
			date.when(DateUtil :: checkForWorkingHours).thenReturn(true);
			ObjectMapper mapper = new ObjectMapper();  
			EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "buy");
			Fund fund = new Fund(10000);
			Mockito.when(fundsRepo.findById(1)).thenReturn(Optional.of(fund));
			Mockito.when(equityRepo.findByStockName("hdfc")).thenReturn(new Equity("hdfc", 10));
			mockMvc.perform(MockMvcRequestBuilders.post("/trade").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(equityDTO)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Equity bought successfully.")));
		} 
	}
	
	@Test
	void shouldTestTrade_buyOrder_newEquity() throws Exception {
		try(MockedStatic<DateUtil> date = Mockito.mockStatic(DateUtil.class)) {
			date.when(DateUtil :: checkForWorkingHours).thenReturn(true);
			ObjectMapper mapper = new ObjectMapper();  
			EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "buy");
			Fund fund = new Fund(10000);
			Mockito.when(fundsRepo.findById(1)).thenReturn(Optional.of(fund));
			mockMvc.perform(MockMvcRequestBuilders.post("/trade").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(equityDTO)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Equity bought successfully.")));
		} 
	}
	
	@Test
	void shouldTestTrade_sellOrder_insufficientEquity() throws Exception {
		try(MockedStatic<DateUtil> date = Mockito.mockStatic(DateUtil.class)) {
			date.when(DateUtil :: checkForWorkingHours).thenReturn(true);
			ObjectMapper mapper = new ObjectMapper();  
			EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "sell");
			Mockito.when(equityRepo.findByStockName("hdfc")).thenReturn(new Equity("hdfc", 1));
			mockMvc.perform(MockMvcRequestBuilders.post("/trade").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(equityDTO)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("You don't have sufficient equity to sell")));
		} 
	}
	
	@Test
	void shouldTestTrade_sellOrder_success() throws Exception {
		try(MockedStatic<DateUtil> date = Mockito.mockStatic(DateUtil.class)) {
			date.when(DateUtil :: checkForWorkingHours).thenReturn(true);
			ObjectMapper mapper = new ObjectMapper();  
			EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "sell");
			Mockito.when(equityRepo.findByStockName("hdfc")).thenReturn(new Equity("hdfc", 10));
			mockMvc.perform(MockMvcRequestBuilders.post("/trade").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(equityDTO)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Equity sold successfully.")));
		} 
	}
}

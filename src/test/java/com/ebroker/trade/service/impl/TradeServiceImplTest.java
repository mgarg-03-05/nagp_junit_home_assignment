package com.ebroker.trade.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ebroker.trade.dto.EquityDTO;
import com.ebroker.trade.entity.Equity;
import com.ebroker.trade.entity.Fund;
import com.ebroker.trade.repository.EquityRepository;
import com.ebroker.trade.repository.FundsRepository;
import com.ebroker.trade.util.DateUtil;

public class TradeServiceImplTest {

	@Mock
	FundsRepository fundsRepo;
	
	@Mock
	EquityRepository equityRepo;
	
	@InjectMocks
	TradeServiceImpl tradeService;
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldReturnFund() {
		Fund actual = new Fund(10);
		Mockito.when(fundsRepo.getById(1)).thenReturn(actual);
		final Fund expectedFund = tradeService.getFunds();
		Assertions.assertEquals(expectedFund, actual);
	}
	
	@Test
	public void shouldAddFund() {
		Fund actual = new Fund(10);
		Mockito.when(fundsRepo.getById(1)).thenReturn(actual);
		actual.setFund(10+20);
		Mockito.when(fundsRepo.save(actual)).thenReturn(actual);
		final Fund expected = tradeService.addFunds(20);
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void shouldReturnListOfEquity() {
		Equity hdfc = new Equity("HDFC", 10);
		hdfc.setId(1);
		List<Equity> actual = new ArrayList<>();
		actual.add(hdfc);
		Mockito.when(equityRepo.findAll()).thenReturn(actual);
		final List<Equity> expected = tradeService.getEquity();
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void shouldFailBuyEquity() {
		EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "buy");
		Fund fund = new Fund(10);
		Mockito.when(fundsRepo.getById(1)).thenReturn(fund);
		String actual = "You don't have sufficient funds to buy equity.";
		final String expected = tradeService.executeBuyOrder(equityDTO);
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void shouldBuyEquitySuccessfullyForNewEquity() {
		EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "buy");
		Fund fund = new Fund(5000);
		Mockito.when(fundsRepo.getById(1)).thenReturn(fund);
		String actual = "Equity bought successfully.";
		final String expected = tradeService.executeBuyOrder(equityDTO);
		Mockito.verify(equityRepo).save(Mockito.any(Equity.class));
		Mockito.verify(fundsRepo).save(fund);
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void shouldBuyEquitySuccessfullyForExistingEquity() {
		EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "buy");
		Fund fund = new Fund(5000);
		Mockito.when(fundsRepo.getById(1)).thenReturn(fund);
		Mockito.when(equityRepo.findByStockName("hdfc")).thenReturn(new Equity("hdfc",10));
		String actual = "Equity bought successfully.";
		final String expected = tradeService.executeBuyOrder(equityDTO);
		Mockito.verify(equityRepo).save(Mockito.any(Equity.class));
		Mockito.verify(fundsRepo).save(fund);
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void shouldFailSellOrder(){
		EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "sell");
		String actual = "You don't have sufficient equity to sell";
		final String expected = tradeService.executeSellOrder(equityDTO);
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void shouldSellOrderSuccessfully() {
		EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "sell");
		Equity equity = new Equity("hdfc", 2);
		Fund fund = new Fund(0);
		Mockito.when(equityRepo.findByStockName("hdfc")).thenReturn(equity);
		Mockito.when(fundsRepo.getById(1)).thenReturn(fund);
		String actual = "Equity sold successfully.";
		final String expected = tradeService.executeSellOrder(equityDTO);
		Mockito.verify(equityRepo).save(equity);
		Mockito.verify(fundsRepo).save(fund);
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void shouldReturnOutOfWorkingHour() {
		String actual = "Trades can be executed from Monday to Friday and between 9AM to 5PM only.";
		EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "sell");
		try(MockedStatic<DateUtil> date = Mockito.mockStatic(DateUtil.class)) {
			date.when(DateUtil :: checkForWorkingHours).thenReturn(false);
			final String expected = tradeService.trade(equityDTO);
			Assertions.assertEquals(expected, actual);
		} 
	}
	
	@Test
	public void shouldReturnInvalidOrder() {
		EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "invalid");
		String actual = "Something went wrong. Please check your request and try again.";
		try(MockedStatic<DateUtil> date = Mockito.mockStatic(DateUtil.class)) {
		  date.when(DateUtil :: checkForWorkingHours).thenReturn(true);
		  final String expected = tradeService.trade(equityDTO);
		  Assertions.assertEquals(expected, actual);
		} 
	}
	
	@Test
	public void shouldCallExecuteBuyOrderMethod() {
		EquityDTO equityDTO = new EquityDTO("hdfc", 2500, 2, "buy");
		String actual = "You don't have sufficient funds to buy equity.";
		try(MockedStatic<DateUtil> date = Mockito.mockStatic(DateUtil.class)) {
		  date.when(DateUtil :: checkForWorkingHours).thenReturn(true);
		  Mockito.when(fundsRepo.getById(1)).thenReturn(new Fund(5000));
		  Mockito.when(tradeService.executeBuyOrder(equityDTO)).thenReturn(actual);
		  final String expected = tradeService.trade(equityDTO);
		  Assertions.assertEquals(expected, actual);
		} 
	}
	
	/*
	 * @Test public void shouldCallExecuteSellOrderMethod() { EquityDTO equityDTO =
	 * new EquityDTO("hdfc", 2500, 2, "sell"); String actual =
	 * "You don't have sufficient equity to sell"; try(MockedStatic<DateUtil> date =
	 * Mockito.mockStatic(DateUtil.class)) { date.when(DateUtil ::
	 * checkForWorkingHours).thenReturn(true);
	 * Mockito.when(equityRepo.findByStockName("hdfc")).thenReturn(null);
	 * Mockito.when(tradeService.executeSellOrder(equityDTO)).thenReturn(actual);
	 * final String expected = tradeService.trade(equityDTO);
	 * Assertions.assertEquals(expected, actual); } }
	 */
}

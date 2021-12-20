package com.ebroker.trade.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EquityDTOTest {

	@Test
	void shouldTestSetName() {
		String actualName = "hdfc";
		EquityDTO equityDTO = new EquityDTO();
		equityDTO.setName(actualName);
		Assertions.assertEquals(equityDTO.getName(), actualName);
	}
	
	@Test
	void shouldTestSetPerStockPrice() {
		double actualPrice = 2500;
		EquityDTO equityDTO = new EquityDTO();
		equityDTO.setPerStockPrice(actualPrice);;
		Assertions.assertEquals(equityDTO.getPerStockPrice(), actualPrice);
	}
	
	@Test
	void shouldTestSetOrderType() {
		String actualType= "buy";
		EquityDTO equityDTO = new EquityDTO();
		equityDTO.setOrderType(actualType);
		Assertions.assertEquals(equityDTO.getOrderType(), actualType);
	}
	
	@Test
	void shouldTestSetQuantity() {
		int actualQuantity = 10;
		EquityDTO equityDTO = new EquityDTO();
		equityDTO.setQuantity(actualQuantity);
		Assertions.assertEquals(equityDTO.getQuantity(), actualQuantity);
	}
}

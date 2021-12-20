package com.ebroker.trade;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ebroker.trade.controller.TradeController;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NagpUnitTestingApplicationTest {
	
	@Autowired
	TradeController tradeController;
	
	@Test
	void contextLoads() {
		Assertions.assertThat(tradeController).isNotNull();
	}

}

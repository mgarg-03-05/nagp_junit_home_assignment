package com.ebroker.trade.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ebroker.trade.entity.Equity;
import com.ebroker.trade.entity.Fund;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class RepositoryTest {

	@Autowired
	private FundsRepository fundsRepo;
	
	@Autowired
	EquityRepository equityRepo;
	
	@Test
	public void shouldTestFundRepo() {
		Fund fund = new Fund(1000); 
		fund.setId(2); 
		fund = fundsRepo.save(fund);
		Fund dbFund = fundsRepo.getById(2);
		Assertions.assertThat(dbFund).extracting(f -> f.getFund() == 1000);
		fundsRepo.deleteAll();
		Assertions.assertThat(fundsRepo.findAll().isEmpty());
	}
	
	@Test
	public void shouldTestEquityRepo() {
		Equity equity = new Equity("hdfc", 10);
		equityRepo.save(equity);
		Equity result = equityRepo.findByStockName("hdfc");
		Assertions.assertThat(result).isEqualTo(equity);
		equityRepo.deleteAll();
		Assertions.assertThat(equityRepo.findAll().isEmpty());
	}
	
}

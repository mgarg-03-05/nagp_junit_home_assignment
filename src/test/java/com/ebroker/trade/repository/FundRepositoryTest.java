package com.ebroker.trade.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ebroker.trade.entity.Fund;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class FundRepositoryTest {

	@Autowired
	private FundsRepository fundsRepo;
	
	@Test
	public void shouldTestFundRepo() {
		Fund fund = new Fund(1000); 
		fund.setId(1); 
		fund = fundsRepo.save(fund);
		Fund dbFund = fundsRepo.getById(1);
		Assertions.assertThat(dbFund).extracting(f -> f.getFund() == 1000);
		fundsRepo.deleteAll();
		Assertions.assertThat(fundsRepo.findAll().isEmpty());
	}	
}

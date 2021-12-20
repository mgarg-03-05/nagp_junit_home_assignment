package com.ebroker.trade.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ebroker.trade.entity.Equity;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class EquityRepositoryTest {
	
	@Autowired
	EquityRepository equityRepo;

	@Test
	void shouldTestEquityRepo() {
		Equity equity = new Equity("hdfc", 10);
		equityRepo.save(equity);
		Equity result = equityRepo.findByStockName("hdfc");
		Assertions.assertThat(result).isEqualTo(equity);
		equityRepo.deleteAll();
		Assertions.assertThat(equityRepo.findAll()).isEmpty();
	}
}

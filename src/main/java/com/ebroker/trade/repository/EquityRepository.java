package com.ebroker.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebroker.trade.entity.Equity;

@Repository
public interface EquityRepository extends JpaRepository<Equity, Integer>{

	public Equity findByStockName(String stockName);

}

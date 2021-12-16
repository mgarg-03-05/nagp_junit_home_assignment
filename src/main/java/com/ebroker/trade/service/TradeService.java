package com.ebroker.trade.service;

import java.util.List;

import com.ebroker.trade.dto.EquityDTO;
import com.ebroker.trade.entity.Equity;
import com.ebroker.trade.entity.Fund;

public interface TradeService {

	public Fund addFunds(double fund);
	public String trade(EquityDTO entityDTO);
	public Fund getFunds();
	public List<Equity> getEquity();
}

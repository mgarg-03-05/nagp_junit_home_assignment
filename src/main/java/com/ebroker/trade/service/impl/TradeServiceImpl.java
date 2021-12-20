package com.ebroker.trade.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebroker.trade.dto.EquityDTO;
import com.ebroker.trade.entity.Equity;
import com.ebroker.trade.entity.Fund;
import com.ebroker.trade.repository.EquityRepository;
import com.ebroker.trade.repository.FundsRepository;
import com.ebroker.trade.service.TradeService;
import com.ebroker.trade.util.DateUtil;
import com.ebroker.trade.util.OrderType;

@Service
public class TradeServiceImpl implements TradeService{
	
	
	@Autowired
	FundsRepository fundsRepo;

	@Autowired
	EquityRepository equityRepo;
	
	@Override
	public Fund getFunds() {
		Optional<Fund> fund = fundsRepo.findById(1);
		if(fund.isPresent()) {
			return fund.get();
		}else {
			return new Fund(0);
		}	
	}
	
	@Override
	public Fund addFunds(double fundAmount) {
		Fund existing;
		Optional<Fund> fund = fundsRepo.findById(1);
		if(fund.isPresent()) {
			existing = fund.get();
		}else {
			existing = new Fund(0);
		}	
		existing.setAmount(existing.getAmount() + fundAmount);
		return fundsRepo.save(existing);
	}

	@Override
	public String trade(EquityDTO equityDTO) {
		try {
			boolean isWorkingHour = DateUtil.checkForWorkingHours();
			if(!isWorkingHour) {
				return "Trades can be executed from Monday to Friday and between 9AM to 5PM only.";
			}
			OrderType type = OrderType.valueOf(equityDTO.getOrderType().toUpperCase());
			if(OrderType.SELL.equals(type)){
				return executeSellOrder(equityDTO);
			}else{
				return executeBuyOrder(equityDTO);
			}
		}catch(Exception e) {
			return "Please check your request and try again.";
		}
	}

	
	public String executeBuyOrder(EquityDTO equityDTO) {
		Fund availableFund = getFunds();
		double totalOrderAmount = equityDTO.getQuantity() * equityDTO.getPerStockPrice();
		if(availableFund.getAmount()< totalOrderAmount) {
			return "You don't have sufficient funds to buy equity.";
		}
		Equity equity = equityRepo.findByStockName(equityDTO.getName().toLowerCase());
		if(equity != null) {
			equity.setStockQuantity(equity.getStockQuantity() + equityDTO.getQuantity());
		}else {
			equity = new Equity();
			equity.setStockName(equityDTO.getName().toLowerCase());
			equity.setStockQuantity(equityDTO.getQuantity());
		}
		availableFund.setAmount(availableFund.getAmount() - totalOrderAmount);
		equityRepo.save(equity);
		fundsRepo.save(availableFund);
		return "Equity bought successfully.";	
	}

	public String executeSellOrder(EquityDTO equityDTO) {
		Equity existing = equityRepo.findByStockName(equityDTO.getName().toLowerCase());
		if(existing == null || equityDTO.getQuantity() > existing.getStockQuantity()) {
			return "You don't have sufficient equity to sell";
		}
		Fund availableFund = getFunds();
		double totalOrderAmount = equityDTO.getQuantity() * equityDTO.getPerStockPrice();
		existing.setStockQuantity(existing.getStockQuantity() - equityDTO.getQuantity());
		availableFund.setAmount(availableFund.getAmount() + totalOrderAmount);
		equityRepo.save(existing);
		fundsRepo.save(availableFund);
		return "Equity sold successfully.";
	}

	@Override
	public List<Equity> getEquity() {
		return equityRepo.findAll();
	}
}

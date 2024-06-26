package com.ebroker.trade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ebroker.trade.dto.EquityDTO;
import com.ebroker.trade.entity.Equity;
import com.ebroker.trade.service.TradeService;

@RestController
public class TradeController {
	
	@Autowired
	TradeService tradeService;
	
	@GetMapping("/")
	public ResponseEntity<String> homePage(){
		return new ResponseEntity<>("Home Page.", HttpStatus.OK);
	}
	
	@GetMapping("/fund")
	public ResponseEntity<String> getFunds(){
		return new ResponseEntity<>("Available fund = "+tradeService.getFunds().getAmount(), HttpStatus.OK);
	}
	
	@PostMapping("/fund")
	public ResponseEntity<String> addFunds(@RequestParam double amount ){
		if(tradeService.addFunds(amount) != null) {
			return new ResponseEntity<>("Funds Added Sucessfully", HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Something went wrong.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/equity")
	public ResponseEntity<List<Equity>> equity(){
		return new ResponseEntity<>(tradeService.getEquity(), HttpStatus.OK);
	}
	
	@PostMapping("/trade")
	public ResponseEntity<String> trade(@RequestBody EquityDTO equityDTO){
		return new ResponseEntity<>(tradeService.trade(equityDTO), HttpStatus.OK);
	}
}

package com.ebroker.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebroker.trade.entity.Fund;

@Repository
public interface FundsRepository extends JpaRepository<Fund, Integer>{

}

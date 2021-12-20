package com.ebroker.trade.util;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class DateUtilTest {
	private static LocalDateTime dateTime;
	
	@Test
	@DisplayName("Should check if datetime is weekday")
	void shouldtest_weekDay() {
		String str = "2021-12-13 10:30";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		dateTime = LocalDateTime.parse(str, formatter);
		try(MockedStatic<LocalDateTime> date = Mockito.mockStatic(LocalDateTime.class)) {
			date.when(LocalDateTime :: now).thenReturn(dateTime);
			Assertions.assertTrue(DateUtil.checkForWorkingHours());
		} 
	}
	
	@Test
	@DisplayName("Should check if datetime is out working hour")
	void shouldTest_outOfWorkingHour() {
		String str = "2021-12-13 08:30";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		dateTime = LocalDateTime.parse(str, formatter);
		try(MockedStatic<LocalDateTime> date = Mockito.mockStatic(LocalDateTime.class)) {
			date.when(LocalDateTime :: now).thenReturn(dateTime);
			Assertions.assertFalse(DateUtil.checkForWorkingHours());
		} 
	}
	
	@Test
	@DisplayName("Should check if datetime is weekend or out of working hours")
	void shouldtest_weekend() {
		String str = "2021-12-12 10:30";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		dateTime = LocalDateTime.parse(str, formatter);
		try(MockedStatic<LocalDateTime> date = Mockito.mockStatic(LocalDateTime.class)) {
			date.when(LocalDateTime :: now).thenReturn(dateTime);
			Assertions.assertFalse(DateUtil.checkForWorkingHours());
		} 
	}
}

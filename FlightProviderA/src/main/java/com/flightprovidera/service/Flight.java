package com.flightprovidera.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Flight {

	private String flightNo;
	private String origin;
	private String destination;
	private LocalDateTime departuredatetime;
	private LocalDateTime arrivaldatetime;
	private BigDecimal price;

	public Flight(String flightNo, String origin, String destination, LocalDateTime departuredatetime,
			LocalDateTime arrivaldatetime, BigDecimal price) {
		super();
		this.flightNo = flightNo;
		this.origin = origin;
		this.destination = destination;
		this.departuredatetime = departuredatetime;
		this.arrivaldatetime = arrivaldatetime;
		this.price = price;
	}
	public String getFlightNo() {
		return flightNo;
	}
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public LocalDateTime getDeparturedatetime() {
		return departuredatetime;
	}
	public void setDeparturedatetime(LocalDateTime departuredatetime) {
		this.departuredatetime = departuredatetime;
	}
	public LocalDateTime getArrivaldatetime() {
		return arrivaldatetime;
	}
	public void setArrivaldatetime(LocalDateTime arrivaldatetime) {
		this.arrivaldatetime = arrivaldatetime;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}

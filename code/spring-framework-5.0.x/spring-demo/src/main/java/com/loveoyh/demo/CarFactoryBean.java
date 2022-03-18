package com.loveoyh.demo;

import org.springframework.beans.factory.FactoryBean;

public class CarFactoryBean implements FactoryBean<Car> {

	@Override
	public Car getObject() throws Exception {
		Car car = new Car();
		car.setBrand("Quattro");
		car.setMaxSpeed(300);
		car.setPrice(400000.0);
		return car;
	}

	@Override
	public Class<?> getObjectType() {
		return Car.class;
	}

}

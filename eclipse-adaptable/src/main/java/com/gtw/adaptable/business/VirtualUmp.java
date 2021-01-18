package com.gtw.adaptable.business;

import com.gtw.adaptable.domain.IOrder;

public class VirtualUmp implements Ump {

	@Override
	public void setOrderPromation(IOrder order) {
		order.putAttribute("promation", 0.9);
	}

	@Override
	public float getOrderPromation(IOrder order) {
		return 0.9F;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UmpName umpName() {
		return new UmpName("virtual", "virtualName");
	}
}

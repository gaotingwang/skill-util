package com.gtw.adaptable.business;

import com.gtw.adaptable.common.IAdapterFactory;
import com.gtw.adaptable.domain.IOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class UmpAdapterFactory implements IAdapterFactory {

	private final UmpsService umpsService;

	@Autowired
    public UmpAdapterFactory(UmpsService umpsService) {
		this.umpsService = umpsService;

	}

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == Ump.class) {
			IOrder order = (IOrder) adaptableObject;
			Ump ump = umpsService.ump(order.getType());
			if (ump== null) {
				ump = umpsService.ump("general");
			}
			return ump;
 		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { Ump.class };
	}


}

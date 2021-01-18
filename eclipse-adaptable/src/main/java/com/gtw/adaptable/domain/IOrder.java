package com.gtw.adaptable.domain;

import java.util.List;

import com.gtw.adaptable.common.IAdaptable;

public interface IOrder extends IAdaptable {
    public void addItem(DrinkType drinkType, int shots, boolean iced);
    public List<OrderItem> getItems();
    public void putAttribute(String key, Object value);
	public String getType();
    public Object getAttribute(String key);

}

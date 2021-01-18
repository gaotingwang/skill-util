/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gtw.adaptable.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gtw.adaptable.common.PlatformObject;

public class Order extends PlatformObject implements IOrder{

    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    private int number;
    
    private String type;
    
    private float price;

	private final Map<String, Object> attributes = new HashMap<String, Object>();

    public Order(String type, int number) {
    	this.type= type;
        this.number = number;
    }

	@Override
    public String getType() {
		return type;
	}
    
    @Override
    public void putAttribute(String key, Object value) {
    	this.attributes.put(key, value);
    }
    
    @Override
    public Object getAttribute(String key) {
    	return this.attributes.get(key);
    }
    
    @Override
    public void addItem(DrinkType drinkType, int shots, boolean iced) {
        this.orderItems.add(new OrderItem(this, drinkType, shots, iced));
    }

    public int getNumber() {
        return number;
    }

    @Override
    public List<OrderItem> getItems() {
        return this.orderItems;
    }

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

    @Override
    public String bizType() {
        return getType();
    }
}

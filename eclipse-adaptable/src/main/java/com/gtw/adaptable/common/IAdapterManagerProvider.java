package com.gtw.adaptable.common;

/**
 * The callback interface for the elements desiring to lazily supply
 * information to the adapter manager.
 * 
 * @since org.eclipse.core.runtime 3.2
 */
public interface IAdapterManagerProvider {

	/**
	 * Add factories. The method called before the AdapterManager starts
	 * using factories.
	 *  
	 * @param adapterManager the adapter manager that is about to be used
	 * @return <code>true</code> if factories were added; <code>false</code> 
	 * if no factories were added in this method call.
	 */
	public boolean addFactories(AdapterManager adapterManager);
}

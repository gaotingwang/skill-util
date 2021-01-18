package com.gtw.adaptable.common;

/**
 * An internal interface that exposes portion of AdapterFactoryProxy functionality
 * without the need to import the class itself.
 */
public interface IAdapterFactoryExt {

	/**
	 * Loads the real adapter factory, but only if its associated plug-in is
	 * already loaded. Returns the real factory if it was successfully loaded.
	 * @param force if <code>true</code> the plugin providing the 
	 * factory will be loaded if necessary, otherwise no plugin activations
	 * will occur.
	 */
	public IAdapterFactory loadFactory(boolean force);

	public String[] getAdapterNames();
}

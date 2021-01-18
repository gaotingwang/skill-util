package com.gtw.adaptable.common;

public abstract class PlatformObject implements IAdaptable {
	/**
	 * Constructs a new platform object.
	 */
	public PlatformObject() {
		super();
	}

	public abstract String bizType();

	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> if
	 * no such object can be found.
	 * <p>
	 * This implementation of the method declared by <code>IAdaptable</code>
	 * passes the request along to the platform's adapter manager; roughly
	 * <code>Platform.getAdapterManager().getAdapter(this, adapter)</code>.
	 * Subclasses may override this method (however, if they do so, they
	 * should invoke the method on their superclass to ensure that the
	 * Platform's adapter manager is consulted).
	 * </p>
	 *
	 * @param adapter the class to adapt to
	 * @return the adapted object or <code>null</code>
	 * @see IAdaptable#getAdapter(Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		return AdapterManager.getDefault().getAdapter(this, adapter);
	}
}

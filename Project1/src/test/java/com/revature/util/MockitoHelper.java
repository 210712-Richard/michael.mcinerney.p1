package com.revature.util;

import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

public class MockitoHelper<T> {

	private final Class<T> type;

	public MockitoHelper(Class<T> type) {
		this.type = type;
	}

	// Function to set the UserDAO dao to a Mockito mock to verify the functions are
	// being called.
	public T setPrivateMock(Object set, String field) {
		// Using reflection to get the private dao.
		T mock = Mockito.mock(type);
		try {
			FieldSetter.setField(set, set.getClass().getDeclaredField(field), mock);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return mock;
	}
}

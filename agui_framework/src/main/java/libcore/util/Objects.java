/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package libcore.util;

/**
 * Object utility methods.
 */
public class Objects {

    private Objects() {
		super();
	}
    
	public static boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}

	public static int hashCode(Object o) {
		return (o == null) ? 0 : o.hashCode();
	}

	/**
     * Ensures the given object isn't {@code null}.
     *
     * @return the given object
     * @throws NullPointerException if the object is null
     */
    public static <T> T nonNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    /**
     * Ensures the given object isn't {@code null}.
     *
     * @return the given object
     * @throws NullPointerException if the object is null
     */
    public static <T> T nonNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }
}

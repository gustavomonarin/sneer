package wheel.lang;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Collections {

	private static final Comparator<Object> TO_STRING_COMPARATOR = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			return String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString());
		}
	};	

	public static <T> List<T> sortByToString(Iterable<T> iterable) {
		ArrayList<T> result = new ArrayList<T>();
		for (T element : iterable)
			result.add(element);
		
		java.util.Collections.sort(result, TO_STRING_COMPARATOR);
		
		return result;
	}

	public static <A, B> Iterable<B> map(Iterable<A> source, Functor<A, B> functor) {
		ArrayList<B> result = new ArrayList<B>();
		for (A a : source) {
			result.add(functor.evaluate(a));
		}
		return result;
	}

	public static <E> List<E> toList(Iterable<E> iterable) {
		List<E> lst = new ArrayList<E>();
		Iterator<E> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			E object = iterator.next();
			lst.add(object);
		}
		return lst;
	}

}

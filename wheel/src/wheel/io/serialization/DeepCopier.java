package wheel.io.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DeepCopier {
	
	/**
	 * Same as deepCopy(original, new JavaSerializer()).
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * 
	 */
	public static <T> T deepCopy(T original) {
	    return deepCopy(original, new JavaSerializer());
	}

	/**
	 * Produce a deep copy of the given object. Serializes the entire object to a byte array in memory. Recommended for
	 * relatively small objects, such as individual transactions.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deepCopy(T original, Serializer serializer) {
		try {
		    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            serializer.writeObject(byteOut, original);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            return (T) serializer.readObject(byteIn);
        } catch (Exception shouldNeverHappen) {
			throw new RuntimeException(shouldNeverHappen);
        }
	}

	/**
	 * Produce a deep copy of the given object. Serializes the object through a pipe between two threads. Recommended for
	 * very large objects, such as an entire prevalent system. The current thread is used for serializing the original
	 * object in order to respect any synchronization the caller may have around it, and a new thread is used for
	 * deserializing the copy.
	 * @throws IOException 
	 */
	public static Object pipedDeepCopy(Object original, Serializer serializer) {
		try {
			return naivePipedDeepCopy(original, serializer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Object naivePipedDeepCopy(Object original, Serializer serializer) throws IOException, InterruptedException {
		PipedOutputStream outputStream = new PipedOutputStream();
		PipedInputStream inputStream = new PipedInputStream(outputStream);

		Consumer consumer = new Consumer(inputStream, serializer);
		consumer.start();

		try {
			serializer.writeObject(outputStream, original);
		} finally {
			outputStream.close();
		}

		consumer.join();
		return consumer.getResult();
	}

	private static class Consumer extends Thread {

		private final InputStream _inputStream;
		private final Serializer _serializer;

		private Object _result;
		
		private RuntimeException _unexpectedException;

		
		public Consumer(InputStream inputStream, Serializer serializer) {
			_inputStream = inputStream;
			_serializer = serializer;
		}

		@Override
		public void run() {
			try {
				_result = _serializer.readObject(_inputStream);
			} catch (Throwable t) {
				_unexpectedException = new RuntimeException(t);
			}
				
			readAnyTrailingBytesWrittenBySillySerializers();
		}

		private void readAnyTrailingBytesWrittenBySillySerializers() {
			try {
				while (_inputStream.read() != -1) {}
			} catch (IOException e) {
				// The object has already been successfully deserialized anyway.
			}
		}

		public Object getResult() {
			if (_unexpectedException != null) throw _unexpectedException;
			return _result;
		}

	}

}

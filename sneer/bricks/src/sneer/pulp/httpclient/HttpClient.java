package sneer.pulp.httpclient;

import java.io.IOException;

import sneer.brickness.Brick;
import wheel.lang.Pair;

public interface HttpClient extends Brick {

	String get(String url, Pair<String, String>... headers) throws IOException;

}

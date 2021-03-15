package iskallia.vault.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class IgnoreEmpty {

	public static class IntegerAdapter extends TypeAdapter<Integer> {
		@Override
		public void write(JsonWriter out, Integer value) throws IOException {
			if(value == null || value == 0) {
				out.nullValue();
			} else {
				out.value(value);
			}
		}

		@Override
		public Integer read(JsonReader in) throws IOException {
			if(in.peek() == JsonToken.NULL) {
				in.nextNull();
				return 0;
			}

			return in.nextInt();
		}
	}

	public static class DoubleAdapter extends TypeAdapter<Double> {
		@Override
		public void write(JsonWriter out, Double value) throws IOException {
			if(value == null || value == 0.0D) {
				out.nullValue();
			} else {
				out.value(value);
			}
		}

		@Override
		public Double read(JsonReader in) throws IOException {
			if(in.peek() == JsonToken.NULL) {
				in.nextNull();
				return 0.0D;
			}

			return in.nextDouble();
		}
	}

	public static class StringAdapter extends TypeAdapter<String> {
		@Override
		public void write(JsonWriter out, String value) throws IOException {
			if(value == null || value.isEmpty()) {
				out.nullValue();
			} else {
				out.value(value);
			}
		}

		@Override
		public String read(JsonReader in) throws IOException {
			if(in.peek() == JsonToken.NULL) {
				in.nextNull();
				return "";
			}

			return in.nextString();
		}
	}


}

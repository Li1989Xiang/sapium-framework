package com.sapium.parser.impl;

import java.io.Reader;
import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sapium.pagefactory.ByStrategies;
import com.sapium.resource.IDataResource;
import com.sapium.utils.SapiumLogger;

public class JsonParser extends AbstractParser {
	private static final Logger log = SapiumLogger.get();
	
	public JsonParser() {
		super();
	}
	
	public JsonParser(IDataResource res) {
		super(res);
	}
	
	@Override
	public <T> T parse(Class<T> clazz) {
		log.debug("begin: param - " + clazz);
		
		T ret = null;
		
		try(Reader reader = res.getReader()) {
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(By.class, new BysDeserializer());
			Gson gson = builder.create();
			ret = gson.fromJson(reader, clazz);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		
		log.debug("end: return - " + ret);
		
		return ret;
	}
	
	private static class BysDeserializer implements JsonDeserializer<By> {

		@Override
		public By deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			By ret = null;
			JsonObject jo = json.getAsJsonObject();
			String byStr = jo.get("by").getAsString();
			ByStrategies by = ByStrategies.toByStrategies(byStr);
			String value = jo.get("value").getAsString();
			ret = by.getBy(value);
			
			return ret;
		}
		
	}
}

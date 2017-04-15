// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles.conf;
import goryachev.common.util.KeepAll;
import goryachev.fx.FX;
import java.io.File;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import javafx.collections.ObservableList;


/**
 * Top level configuration object.
 */
@KeepAll
public class Locations
{
	public final ObservableList<Location> locations = FX.observableArrayList();
	
	
	public Locations()
	{
	}


	public static Locations fromJson(String s) throws Exception
	{
		return gson().fromJson(s, Locations.class);
	}
	
	
	public static String toJson(Locations loc)
	{
		return gson().toJson(loc);
	}
	
	
	private static Gson gson()
	{
		return 
			new GsonBuilder().
			setPrettyPrinting().
			registerTypeAdapter(ObservableList.class, new InstanceCreator()
			{
				public Object createInstance(Type type)
				{
					return FX.observableArrayList();
				}
			}).
			registerTypeAdapter(File.class, new JsonDeserializer<File>() // TODO JsonSerDes
			{
				public File deserialize(JsonElement json, Type t, JsonDeserializationContext context) throws JsonParseException
				{
					String path = json.getAsString();
					return new File(path);
				}
			}).
			registerTypeAdapter(File.class, new JsonSerializer<File>()
			{
				public JsonElement serialize(File f, Type t, JsonSerializationContext context)
				{
					return new JsonPrimitive(f.toString());
				}
			}).
			create();
	}
}

public package iskallia.vault.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iskallia.vault.Vault;

import java.io.*;

public abstract class Config {

	private static Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
	protected String root = "config/" + Vault.MOD_ID + "/";
	protected String extension = ".json";

	public void generateConfig() {
		this.reset();

		try {
			this.writeConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File getConfigFile() {
		return new File(this.root + this.getName() + this.extension);
	}

	public abstract String getName();

	public Config readConfig() {
		try {
			return GSON.fromJson(new InputStreamReader(new FileInputStream(getConfigFile()),"utf-8"), this.getClass());
		} catch (FileNotFoundException e) {
			this.generateConfig();
		} catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

		return this;
	}

	protected abstract void reset();

	public void writeConfig() throws IOException {
		File dir = new File(this.root);
		if(!dir.exists() && !dir.mkdirs())return;
		if(!this.getConfigFile().exists() && !this.getConfigFile().createNewFile())return;
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(getConfigFile()),"utf-8");
		GSON.toJson(this, writer);
		writer.flush();
		writer.close();
	}

}

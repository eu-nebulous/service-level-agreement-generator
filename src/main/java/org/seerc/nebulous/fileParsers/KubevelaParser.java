package org.seerc.nebulous.fileParsers;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

public class KubevelaParser {
	private Yaml yaml;

	private Map<String, Object> kubevelaYml;
	private Map<String, Object> spec;
	private List<Object> components;
	
	public KubevelaParser(InputStream inputStream) {
		LoaderOptions options = new LoaderOptions();
		yaml = new Yaml(options);
		kubevelaYml = yaml.load(inputStream);	
		
		spec = getMap(kubevelaYml, "spec");
		components = getList(spec, "components");
	}
	
	public List<Object> getComponents(){
		return components;
	}
	public Map<String, Object> getComponent(int index){
		return (Map<String, Object>) components.get(index);
	}
	
	public String getComponentName(int index) {
		return (String) getComponent(index).get("name");
	}

	public Map<String, Map<String, String>> getResources(int compIndex){
		 Map<String, Object> r;
		 Map<String, Map<String, String>> res;
		 
		 r = getMap(getMap(getComponent(compIndex), "properties"), "resources");
		 res = new HashMap<String, Map<String, String>>();
		 if(r != null)
			 for(Map.Entry<String, Object> entry: r.entrySet()) {
				 
				 res.put(entry.getKey(), (Map<String, String>) entry.getValue());
			 }
			 
		 return res;
	}
	
	public HashMap<String, Object> getMap(Map<String, Object> map, String key){
		return (HashMap<String, Object>) map.get(key);
	}
		
	public HashMap<String, Object> getMap(List<Object> list, String key){
		HashMap<String, Object> map = null;
			
		for(int i = 0; i < list.size(); i++) {
			Object obj = list.get(i);
			if(!(obj instanceof Map))
				continue;
			
			map = (HashMap<String, Object>) obj;
			System.out.println(map);
		}
			
		return map;
	}
	public List<Object> getList(Map<String, Object> map, String key){
		return (List<Object>) map.get(key); 
	}
	public void print(HashMap<String, Object> map) {
		map.forEach((t, u) -> System.out.println(t + "\t" + u + "\n"));
	}
	public void print(List<Object> list) {
		list.forEach((t) -> System.out.println(t + "\n"));
	}
}

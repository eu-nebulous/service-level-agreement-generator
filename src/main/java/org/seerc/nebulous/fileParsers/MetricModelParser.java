package org.seerc.nebulous.fileParsers;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("unchecked")
public class MetricModelParser {
	
		private Yaml yaml;
		
		private Map<String, Object> metricModelYml;
		private Map<String, Object> spec;
		private List<Object> components;

		
	public MetricModelParser(InputStream inputStream) {
		LoaderOptions options = new LoaderOptions();
		yaml = new Yaml(options);
		metricModelYml = yaml.load(inputStream);	
		
		spec = getMap(metricModelYml, "spec");
		components = getList(spec, "components");
	}
	
	public List<Map<String, Object>> getComponentMetrics(int index){
		return (List<Map<String, Object>>) getComponent(index).get("metrics");
	}
	public List<Map<String, Object>>getComponentRequirements(int index){
		return (List<Map<String, Object>>) getComponent(index).get("requirements");
	}
	
	public Map<String, Object> getMetadata(){
		return getMap(metricModelYml, "metadata");
	}
	
	public List<Object> getComponents(){
		return components;
	}
	public Map<String, Object> getComponent(int index){
		return (Map<String, Object>) components.get(index);
	}

	public List<Object> getScopes(){
		return (List<Object>) spec.get("scopes");
	}
//		String apiVersion = (String) metricModelYml.get("apiVersion");
//		
//		
//			
//		for(int i = 0; i < component.size(); i++) {
//			HashMap<String, Object> currComponent = (HashMap<String, Object>) component.get(i);
//			List<Object> metrics = getList(currComponent, "metrics");
//			
//			for(int j = 0; j < metrics.size(); j++) {
//				HashMap<String, Object> currMetric = (HashMap<String, Object>) metrics.get(j);
//				System.out.println(currMetric);
//			}
//			
//		}
//	}

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
//			
		
//			metricModel.getSpec().getComponents().get(0).getMetrics().sort((o1, o2) -> {
//				String f1 = o1.getFormula(), f2 = o2.getFormula();
//				String s1 = o1.getSensor(), s2 = o2.getSensor();
//				
//				if((f1 == null && f2 == null) || (s1 == null && s2 == null))
//					return 0;
//				else if((f1 != null && f2 == null) || (s1 == null && s2 != null))
//						return 1;
//				else
//					return -1;
//			});
//			
//			List<Metric> metrics = metricModel.getSpec().getComponents().get(i).getMetrics();
//			
//			if(metrics != null)
//				metrics.forEach(metric ->{
//					
//					 Window win = null;
//					 Schedule sch = null;
//					 Formula form;
//					 nebulous.semantics.applications.metrics.Sensor sens;
//					 
//					 if(metric.getWindow() != null) {
//						 String[] winComponents = metric.getWindow().split(" ");
//						 win = new Window(null, null, Integer.parseInt( winComponents[1]), winComponents[0].equals(WindowType.SLIDING.toString())? WindowType.SLIDING : WindowType.FIXED , metric.getOutput().split(" ")[0].equals("all")? WindowTypeSize.BOTH_MATCH: null);
//					 }			 
//					 if(metric.getOutput() != null) {
//						 String[] schComponents = metric.getOutput().split(" ");
//						 sch = new Schedule(null, null, null, null, Double.parseDouble(schComponents[1]));
//					 }
//					 if(metric.getFormula() != null) {
//						 String[] formComp = metric.getFormula().split("[()]");
//						 FormulaFunctions func = FormulaFunctions.valueOf(formComp[0].equals("add")? "PLUS": (formComp[0].toUpperCase()));
//						 nebulous.semantics.applications.metrics.Metric m = null;
//						 for(int j = 0; j < app.getMetrics().size(); j++) {
//						 	String name  = app.getMetrics().get(j).getName(null, null);
//						 	if(name.equals(formComp[1])) {
//						 		m = app.getMetrics().get(j);
//						 		break;
//						 	}
//						 }
//						 form = new Formula(func, m);
//						 
//						 app.addMetric(metric.getName(), win, null, sch, form, null);
//					 }
//						 
//					 if(metric.getSensor() != null) {
//						 sens = new nebulous.semantics.applications.metrics.Sensor(null, null);
//						 app.addMetric(metric.getName(), win, null, sch, sens, null);
//					 }				 
//				});
//			
//			List<Requirement> requirements = metricModel.getSpec().getComponents().get(i).getRequirements();
//			if(requirements != null)
//				requirements.forEach(requirement -> {
//					
//					 ServiceLevel normal = app.addServiceLevel("NORMAL");
//					 
//					 String[] reqComp = requirement.getConstraint().split(" ");
//					 
//					 nebulous.semantics.applications.metrics.Metric m = null;
//					 for(int j = 0; j < app.getMetrics().size(); j++) {
//					 	String name  = app.getMetrics().get(j).getName("", null);
//					 	if(name.equals(reqComp[0])) {
//					 		m = app.getMetrics().get(j);
//					 		break;
//					 	}
//					 }
//					 ComparisonOperators operator = null;
//					 
//					 switch (reqComp[1]) {
//					case "<":
//						operator = ComparisonOperators.LESS_THAN;
//						break;
//					case ">":
//						operator = ComparisonOperators.GREATER_THAN;
//						break;
//					case "<=":
//						operator = ComparisonOperators.LESS_EQUAL_THAN;
//						break;
//					case ">=":
//						operator = ComparisonOperators.GREATER_EQUAL_THAN;
//						break;
//					case "=":
//						operator = ComparisonOperators.EQUALS;
//						break;
//					case "<>":
//						operator = ComparisonOperators.NOT_EQUALS;
//						break;
//					}
//					 
//					 app.addServiceLevelObjective(normal, m, operator, Double.parseDouble(reqComp[2]), null, null);
//				});
//		}
//		return app;


}

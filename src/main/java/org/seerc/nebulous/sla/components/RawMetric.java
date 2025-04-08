package org.seerc.nebulous.sla.components;

public class RawMetric extends Metric{
	private Sensor sensor;

	public RawMetric() {
		sensor = new Sensor();
	}
	public RawMetric(Metric m) {
		sensor = new Sensor();
		this.setName(m.getName());
		this.setOutput(m.getOutput());
		this.setWindow(m.getWindow());
	}
	public RawMetric(String name) {
		this.setName(name);
		sensor = null;
		output = null;
		window = null;
	}
	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	@Override
	public String toString() {
		return "RawMetric [sensor = " + sensor + ", name = " + name + ", window = " + window + ", output = " + output
				+ "]";
	}
	
	
}

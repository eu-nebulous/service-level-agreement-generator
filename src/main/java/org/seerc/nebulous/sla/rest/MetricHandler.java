package org.seerc.nebulous.sla.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.qpid.protonj2.client.Message;

import eu.nebulouscloud.exn.core.Context;
import eu.nebulouscloud.exn.core.Handler;

public class MetricHandler extends Handler{
	@Override
	public void onMessage(String key, String address, Map body, Message message, Context context) {
		super.onMessage(key, address, body, message, context);
//		metricOutput = new ArrayList<Object>();
//		metricOutput.addAll((List) body.get("metrics"));
	}
}

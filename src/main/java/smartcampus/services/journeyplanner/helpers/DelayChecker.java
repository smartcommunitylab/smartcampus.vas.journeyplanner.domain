/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package smartcampus.services.journeyplanner.helpers;

import it.sayservice.platform.smartplanner.data.message.TType;
import it.sayservice.platform.smartplanner.data.message.Transport;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertDelay;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertType;
import it.sayservice.platform.smartplanner.data.message.alerts.CreatorType;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import smartcampus.services.trentomale.TrainsAlertsSent;
import smartcampus.services.trentomale.TrentoMaleTrain;

public class DelayChecker {
	
	// TODO: maybe change agencyId, routeId...
	public static AlertDelay checkDelay(TrentoMaleTrain train) {
			AlertDelay delay = new AlertDelay();
			delay.setCreatorId("");
			delay.setCreatorType(CreatorType.SERVICE);
			delay.setDelay(train.getDelay() * (1000 * 60));
			delay.setDescription("");
			delay.setNote("");

			Transport t = new Transport();

			t.setAgencyId("10");
			t.setRouteId("RG");
			t.setTripId("" + train.getNumber());
			t.setType(TType.TRAIN);
			delay.setTransport(t);
			
			delay.setType(AlertType.DELAY);
			
			Calendar cal = new GregorianCalendar();
			
			cal.set(Calendar.MILLISECOND, 0);
			
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			
			cal.set(Calendar.HOUR_OF_DAY, 4);
			long from = cal.getTimeInMillis();
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			long to = cal.getTimeInMillis();
			delay.setFrom(from);
			delay.setTo(to);
			
			if (train.getDelay() > 0) {
				delay.setNote("Train " + train.getNumber() + " has a delay of " + train.getDelay() + " minutes.");
			} else {
				delay.setNote("Train " + train.getNumber() + " is on time.");
			}
			
			delay.setId(delay.getTransport().getTripId() + "_" + delay.getFrom() + "_" + delay.getTo());
			
			
			return delay;

	}
	
	public static TrainsAlertsSent checkNewAlerts(TrainsAlertsSent sent, TrentoMaleTrain train) {
		TrainsAlertsSent newSent = new TrainsAlertsSent();
		newSent.setAlerts(sent.getAlerts());
		
		String delay = buildDate() + "_" + train.getDelay();
		if (!sent.getAlerts().containsKey(train.getNumber())) {
			newSent.getAlerts().put(train.getNumber(), delay);
			return newSent;
		}
		
		if (sent.getAlerts().get(train.getNumber()).equals(delay)) {
			return null;
		}
		
		newSent.getAlerts().put(train.getNumber(), delay);
		return newSent;
	}	
	
	public static TrainsAlertsSent cleanOldAlerts(TrainsAlertsSent sent) {
		TrainsAlertsSent newSent = new TrainsAlertsSent();
		newSent.setAlerts(new TreeMap<Long, String>(sent.getAlerts()));		
		
		String delay = buildDate();
		for (Long key: sent.getAlerts().keySet()) {
			if (!sent.getAlerts().get(key).startsWith(delay)) {
				newSent.getAlerts().remove(key);
			}
		}
		return newSent;
	}
	
	private static String buildDate() {
		Calendar cal = new GregorianCalendar();
		return cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.YEAR);
	}
	
}

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

import it.sayservice.platform.smartplanner.data.message.Itinerary;
import it.sayservice.platform.smartplanner.data.message.Leg;
import it.sayservice.platform.smartplanner.data.message.alerts.Alert;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertDelay;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertParking;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertStrike;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class AlertUpdater {
	
	static Logger log = Logger.getLogger(AlertUpdater.class);

	public static Itinerary updateAlerts(Itinerary itinerary, Alert alert) {
		try {
		for (Leg leg : itinerary.getLeg()) {
			Alert old = null;
			if (alert instanceof AlertDelay) {
				if (!leg.getTransport().equals(((AlertDelay) alert).getTransport())) {
					continue;
				}
				if (leg.getAlertDelayList() == null) {
					leg.setAlertDelayList(new ArrayList<AlertDelay>());
				}
				for (AlertDelay alertDelay : leg.getAlertDelayList()) {
					if (alertDelay.getTransport().equals(((AlertDelay) alert).getTransport())) {
						old = alertDelay;
						break;
					}
				}
				if (old != null) {
					leg.getAlertDelayList().remove(old);
				}
				leg.getAlertDelayList().add((AlertDelay) alert);
			
			} else if (alert instanceof AlertStrike) {
				if (!leg.getTransport().equals(((AlertStrike) alert).getTransport())) {
					continue;
				}
				if (leg.getAlertStrikeList() == null) {
					leg.setAlertStrikeList(new ArrayList<AlertStrike>());
				}
				for (AlertStrike alertStrike : leg.getAlertStrikeList()) {
					if (alertStrike.getTransport().equals(((AlertStrike) alert).getTransport())) {
						old = alertStrike;
						break;
					}
				}
				if (old != null) {
					leg.getAlertStrikeList().remove(old);
				}
				leg.getAlertStrikeList().add((AlertStrike) alert);
			
			} else if (alert instanceof AlertParking) {
				if (!leg.getTo().getStopId().equals(((AlertParking)alert).getPlace())) {
					continue;
				}
				if (leg.getAlertParkingList() == null) {
					leg.setAlertParkingList(new ArrayList<AlertParking>());
				}				
				for (AlertParking alertParking : leg.getAlertParkingList()) {
					if (alertParking.getPlace().equals(((AlertParking) alert).getPlace())) {
						old = alertParking;
						break;
					}
				}
				if (old != null) {
					leg.getAlertParkingList().remove(old);
				}
				leg.getAlertParkingList().add((AlertParking) alert);
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Cannot update itinerary");
		}

		return itinerary;
	}
	
	public static Itinerary removeExpired(Itinerary itinerary) {
		for (Leg leg : itinerary.getLeg()) {
			List<Alert> toRemove = new ArrayList<Alert>();
			for (Alert alert :leg.getAlertDelayList()) {
				if (AlertUpdater.isExpired(alert)) {
					toRemove.add(alert);
				}
				leg.getAlertDelayList().removeAll(toRemove);
			}
			toRemove = new ArrayList<Alert>();
			for (Alert alert :leg.getAlertParkingList()) {
				if (AlertUpdater.isExpired(alert)) {
					toRemove.add(alert);
				}
				leg.getAlertParkingList().removeAll(toRemove);		
			}
			toRemove = new ArrayList<Alert>();
			for (Alert alert :leg.getAlertStrikeList()) {
				if (AlertUpdater.isExpired(alert)) {
					toRemove.add(alert);
				}
				leg.getAlertStrikeList().removeAll(toRemove);		
			}			
		}
		
		return itinerary;
	}
	
	private static boolean isExpired(Alert alert) {
		long now = System.currentTimeMillis();
		return alert.getTo() < now;
	}
	

}

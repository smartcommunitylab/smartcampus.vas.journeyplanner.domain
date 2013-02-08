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
import it.sayservice.platform.smartplanner.data.message.SimpleLeg;
import it.sayservice.platform.smartplanner.data.message.StopId;
import it.sayservice.platform.smartplanner.data.message.Transport;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertDelay;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertParking;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertStrike;
import it.sayservice.platform.smartplanner.data.message.journey.RecurrentJourney;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class AlertFilter {

	public static boolean filterDelay(Itinerary itinerary, AlertDelay alert) {
		return filterDelay(itinerary.getLeg(), alert);
	}
	
	public static boolean filterStrike(Itinerary itinerary, AlertStrike alert) {
		return filterStrike(itinerary.getLeg(), alert);
	}	
	
	public static boolean filterParking(Itinerary itinerary, AlertParking alert) {
		return filterParking(itinerary.getLeg(), alert);
	}		
	
	public static boolean filterDelay(RecurrentJourney journey, AlertDelay alert) {
		return filterRecurrentDelay(journey, alert);
	}
	
	public static boolean filterStrike(RecurrentJourney journey, AlertStrike alert) {
		return filterRecurrentStrike(journey, alert);
	}	
	
	public static boolean filterParking(RecurrentJourney journey, AlertParking alert) {
		return filterRecurrentParking(journey, alert);
	}			
	
	
	private static boolean filterDelay(List<Leg> legs, AlertDelay alert) {
		for (Leg leg: legs) {
			if (areEqual(leg.getTransport(),alert.getTransport(), true, true, true, true)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean filterRecurrentDelay(RecurrentJourney journey, AlertDelay alert) {
		String transportId = alert.getTransport().getAgencyId() + "_" + alert.getTransport().getRouteId();
		if (!journey.getMonitorLegs().containsKey(transportId) || journey.getMonitorLegs().get(transportId) == false) {
			return false;
		}
		
		for (SimpleLeg leg: journey.getLegs()) {
			if (areEqual(leg.getTransport(),alert.getTransport(), true, true, true, true)) {
				Calendar cal = new GregorianCalendar();
				cal.setTimeInMillis(System.currentTimeMillis());
				if (journey.getParameters().getRecurrence().contains(cal.get(Calendar.DAY_OF_WEEK))) {
					return true;
				}
			}
		}
		return false;
	}		
	
	private static boolean filterStrike(List<Leg> legs, AlertStrike alert) {
		for (Leg leg: legs) {
			if (areEqual(leg.getTransport(),alert.getTransport(), true, false, false, true)) {
				return true;
			}
		}
		return false;
	}	
	
	private static boolean filterRecurrentStrike(RecurrentJourney journey, AlertStrike alert) {
		// TBD
		return false;
	}
	
	
	private static boolean filterParking(List<Leg> legs, AlertParking alert) {
		for (Leg leg: legs) {
			StopId stop = leg.getFrom().getStopId();
			if (stop == null) {
				continue;
			}
			if (areEqual(stop, alert.getPlace(), true, true)) {
				return true;
			}
			stop = leg.getTo().getStopId();
			if (stop == null) {
				continue;
			}			
			if (areEqual(stop, alert.getPlace(), true, true)) {
				return true;
			}			
		}
		return false;
	}			
	
	private static boolean filterRecurrentParking(RecurrentJourney journey, AlertParking alert) {
		// TBD
		return false;
	}	
	
	private static boolean areEqual(Transport t1, Transport t2, boolean agency, boolean route, boolean trip, boolean type) {
		boolean result = true;
		if (agency) {
			result &= areEqualOrNull(t1.getAgencyId(),t2.getAgencyId());
		}
		if (result && route) {
			result &= areEqualOrNull(t1.getRouteId(),t2.getRouteId());
		}
		if (result && trip) {
			result &= areEqualOrNull(t1.getTripId(),t2.getTripId());
		}		
		if (result && type) {
			result &= areEqualOrNull(t1.getType(),t2.getType());
		}				
		
		return result;
//		return t1.getAngencyId().equals(t2.getAngencyId()) && t1.getRouteId().equals(t2.getRouteId()) && t1.getTripId().equals(t2.getTripId()) && t1.getType().equals(t2.getType());
	}
	
	private static boolean areEqual(StopId s1, StopId s2, boolean agency, boolean id) {
		boolean result = true;
		if (agency) {
			result &= areEqualOrNull(s1.getAgencyId(),s2.getAgencyId());
		}
		if (result && id) {
			result &= areEqualOrNull(s1.getId(),s2.getId());
		}		
		
		return result;
//		return s1.getAgencyId().equals(s2.getAgencyId()) && s1.getId().equals(s2.getId());
	}
	
	private static boolean areEqualOrNull(Object o1, Object o2) {
		if ((o1 == null) != (o2 == null)) {
			return false;
		}
		if (o1 != null) {
			return o1.equals(o2);
		}
		return false;
	}
		
}

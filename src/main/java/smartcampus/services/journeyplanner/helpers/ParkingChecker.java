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

import it.sayservice.platform.smartplanner.data.message.Position;
import it.sayservice.platform.smartplanner.data.message.StopId;
import it.sayservice.platform.smartplanner.data.message.TType;
import it.sayservice.platform.smartplanner.data.message.Transport;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertDelay;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertParking;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertType;
import it.sayservice.platform.smartplanner.data.message.alerts.CreatorType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import smartcampus.services.oraritreni.delays.GenericTrain;
import smartcampus.services.parkings.Parking;
import smartcampus.services.parkings.ParkingsAlertsSent;
import smartcampus.services.trentomale.TrainsAlertsSent;

public class ParkingChecker {

	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private static Log logger = LogFactory.getLog(ParkingChecker.class);

	public static AlertParking checkParking(Parking parking) {
		AlertParking places = new AlertParking();
		places.setCreatorId("");
		places.setCreatorType(CreatorType.SERVICE);
		places.setNoOfvehicles(parking.getVehicles());
		places.setPlacesAvailable(parking.getFreePlaces());
		places.setDescription("");
		places.setNote("");
		
		StopId t = new StopId();
		
		t.setAgencyId(parking.getAgencyId());
		t.setId(parking.getId());

		places.setPlace(t);
		places.setType(AlertType.DELAY);
		
		places.setFrom(System.currentTimeMillis());
		places.setTo(System.currentTimeMillis() + 1000 * 60 * 5);

		places.setNote(parking.getAddress());

		places.setId(places.getPlace().getId() + "_" + CreatorType.SERVICE + "_" + places.getFrom() + "_" + places.getTo());

		return places;

	}

	public static ParkingsAlertsSent checkNewAlerts(ParkingsAlertsSent sent, Parking parking) {
		ParkingsAlertsSent newSent = new ParkingsAlertsSent();
		newSent.setAlerts(sent.getAlerts());

		String places = buildDate() + "_" + parking.getFreePlaces();
		
		if (!sent.getAlerts().containsKey(parking.getId())) {
			newSent.getAlerts().put(parking.getId(), places);
			return newSent;
		}

		if (sent.getAlerts().get(parking.getId()).equals(places)) {
			return null;
		}

		newSent.getAlerts().put(parking.getId(), places);
		
		
		
		return newSent;
	}

	public static ParkingsAlertsSent cleanOldAlerts(ParkingsAlertsSent sent) {
		ParkingsAlertsSent newSent = new ParkingsAlertsSent();
		newSent.setAlerts(new TreeMap<String, String>(sent.getAlerts()));

		String delay = buildDate();
		for (String key : sent.getAlerts().keySet()) {
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

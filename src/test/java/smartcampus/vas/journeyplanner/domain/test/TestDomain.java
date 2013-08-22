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
package smartcampus.vas.journeyplanner.domain.test;

import it.sayservice.platform.client.ServiceBusClient;
import it.sayservice.platform.client.jms.JMSServiceBusClient;
import it.sayservice.platform.core.domain.DomainObject;
import it.sayservice.platform.core.message.Core.DODataRequest;
import it.sayservice.platform.core.message.Core.DomainEvent;
import it.sayservice.platform.domain.test.DomainListener;
import it.sayservice.platform.domain.test.DomainTestHelper;
import it.sayservice.platform.smartplanner.data.message.Itinerary;
import it.sayservice.platform.smartplanner.data.message.Position;
import it.sayservice.platform.smartplanner.data.message.Transport;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertDelay;
import it.sayservice.platform.smartplanner.data.message.alerts.CreatorType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;

import smartcampus.services.journeyplanner.AlertFactoryDOEngine;
import smartcampus.services.journeyplanner.GetOrdinanzeRoveretoDOEngine;
import smartcampus.services.journeyplanner.GetParkingDOEngine;
import smartcampus.services.journeyplanner.GetTrainsDOEngine;
import smartcampus.services.journeyplanner.ItineraryFactoryDOEngine;
import smartcampus.services.journeyplanner.ItineraryObjectDOEngine;
import smartcampus.services.journeyplanner.ParkingAlertsSenderDOEngine;
import smartcampus.services.journeyplanner.RecurrentJourneyFactoryDOEngine;
import smartcampus.services.journeyplanner.RecurrentJourneyObjectDOEngine;
import smartcampus.services.journeyplanner.RoadAlertSenderDOEngine;
import smartcampus.services.journeyplanner.TrainsAlertsSenderDOEngine;
import smartcampus.services.journeyplanner.UserAlertSenderDOEngine;
import smartcampus.services.journeyplanner.UserAlertSenderInterface;

public class TestDomain {

	public static void main(String[] args) throws Exception {
		HornetQJMSConnectionFactory cf = new HornetQJMSConnectionFactory(
				false,
				new TransportConfiguration(
						"org.hornetq.core.remoting.impl.netty.NettyConnectorFactory"));
		ServiceBusClient client = new JMSServiceBusClient(cf);

		DomainTestHelper helper = new DomainTestHelper(client,
				new DomainListener() {
					public void onDomainEvents(List<DomainEvent> events) {
						for (DomainEvent e : events) {
							if ("alertDelay".equals(e.getEventSubtype()) && e.getAllTypesList().contains("smartcampus.services.journeyplanner.UserAlertSender")) {
								System.err.println(e);  
							}
						}
						// DO someth...
					}

					public void onDataRequest(DODataRequest req) {
						// DO someth...
					}
				});

//		checkAlertsForSingleJourney(helper);
		checkAlertsForRecurrentJourney(helper);
	}

	private static void checkAlertsForSingleJourney(DomainTestHelper helper)
			throws IOException, JsonParseException, JsonMappingException,
			Exception {
		helper.cleanDomainData();
		initDomain(helper);
		// save itinerary
		// signal alert - should be updated
		// signal another alert with small delay - no update
		// signal another alert with large delay - should be updated
		// signal alert after previous expires - should be updated

		String s = "{\"monitor\":true,\"originalTo\":{\"lon\":\"11.11889\",\"stopId\":null,\"name\":null,\"stopCode\":null,\"lat\":\"46.066695\"},\"name\":\"test\",\"data\":{\"to\":{\"lon\":\"11.11889\",\"stopId\":{\"id\":\"\",\"agencyId\":\"\"},\"name\":\"corner of path and sidewalk\",\"stopCode\":\"null\",\"lat\":\"46.066695\"},\"endtime\":1377171804000,\"leg\":[{\"to\":{\"lon\":\"11.150209\",\"stopId\":{\"id\":\"25015x\",\"agencyId\":\"12\"},\"name\":\"POVO  \\\"Fac. Scienze\\\"\",\"stopCode\":\"null\",\"lat\":\"46.063316\"},\"endtime\":1377170639000,\"alertParkingList\":[],\"duration\":557000,\"startime\":1377170082000,\"transport\":{\"tripId\":\"null\",\"routeShortName\":\"null\",\"routeId\":\"null\",\"type\":\"WALK\",\"agencyId\":\"null\"},\"alertDelayList\":[],\"alertStrikeList\":[],\"from\":{\"lon\":\"11.151824063634374\",\"stopId\":{\"id\":\"\",\"agencyId\":\"\"},\"name\":\"service road\",\"stopCode\":\"null\",\"lat\":\"46.06679860551396\"},\"legGeometery\":{\"levels\":\"null\",\"length\":27,\"points\":\"\"},\"legId\":\"null_null\"},{\"to\":{\"lon\":\"11.119949\",\"stopId\":{\"id\":\"20130c\",\"agencyId\":\"12\"},\"name\":\"Piazza Dante  Dogana\",\"stopCode\":\"null\",\"lat\":\"46.072592\"},\"endtime\":1377171300000,\"alertParkingList\":[],\"duration\":660000,\"startime\":1377170640000,\"transport\":{\"tripId\":\"05R-Feriale_032\",\"routeShortName\":\"5\",\"routeId\":\"05R\",\"type\":\"BUS\",\"agencyId\":\"12\"},\"alertDelayList\":[],\"alertStrikeList\":[],\"from\":{\"lon\":\"11.150209\",\"stopId\":{\"id\":\"25015x\",\"agencyId\":\"12\"},\"name\":\"POVO  \\\"Fac. Scienze\\\"\",\"stopCode\":\"null\",\"lat\":\"46.063316\"},\"legGeometery\":{\"levels\":\"null\",\"length\":109,\"points\":\"\"},\"legId\":\"12_05R-Feriale_032\"},{\"to\":{\"lon\":\"11.119568\",\"stopId\":{\"id\":\"20125p\",\"agencyId\":\"12\"},\"name\":\"Piazza Dante  \\\"Stazione FS\\\"\",\"stopCode\":\"null\",\"lat\":\"46.071917\"},\"endtime\":1377171372000,\"alertParkingList\":[],\"duration\":72000,\"startime\":1377171300000,\"transport\":{\"tripId\":\"null\",\"routeShortName\":\"null\",\"routeId\":\"null\",\"type\":\"WALK\",\"agencyId\":\"null\"},\"alertDelayList\":[],\"alertStrikeList\":[],\"from\":{\"lon\":\"11.119949\",\"stopId\":{\"id\":\"20130c\",\"agencyId\":\"12\"},\"name\":\"Piazza Dante  Dogana\",\"stopCode\":\"null\",\"lat\":\"46.072592\"},\"legGeometery\":{\"levels\":\"null\",\"length\":8,\"points\":\"\"},\"legId\":\"null_null\"},{\"to\":{\"lon\":\"11.118642\",\"stopId\":{\"id\":\"21590z\",\"agencyId\":\"12\"},\"name\":\"Rosmini  S.Maria Maggiore\",\"stopCode\":\"null\",\"lat\":\"46.0681\"},\"endtime\":1377171660000,\"alertParkingList\":[],\"duration\":120000,\"startime\":1377171540000,\"transport\":{\"tripId\":\"09A-Feriale_022\",\"routeShortName\":\"9\",\"routeId\":\"09A\",\"type\":\"BUS\",\"agencyId\":\"12\"},\"alertDelayList\":[],\"alertStrikeList\":[],\"from\":{\"lon\":\"11.119568\",\"stopId\":{\"id\":\"20125p\",\"agencyId\":\"12\"},\"name\":\"Piazza Dante  \\\"Stazione FS\\\"\",\"stopCode\":\"null\",\"lat\":\"46.071917\"},\"legGeometery\":{\"levels\":\"null\",\"length\":7,\"points\":\"\"},\"legId\":\"12_09A-Feriale_022\"},{\"to\":{\"lon\":\"11.11889\",\"stopId\":{\"id\":\"\",\"agencyId\":\"\"},\"name\":\"corner of path and sidewalk\",\"stopCode\":\"null\",\"lat\":\"46.066695\"},\"endtime\":1377171803000,\"alertParkingList\":[],\"duration\":143000,\"startime\":1377171660000,\"transport\":{\"tripId\":\"null\",\"routeShortName\":\"null\",\"routeId\":\"null\",\"type\":\"WALK\",\"agencyId\":\"null\"},\"alertDelayList\":[],\"alertStrikeList\":[],\"from\":{\"lon\":\"11.118642\",\"stopId\":{\"id\":\"21590z\",\"agencyId\":\"12\"},\"name\":\"Rosmini  S.Maria Maggiore\",\"stopCode\":\"null\",\"lat\":\"46.0681\"},\"legGeometery\":{\"levels\":\"null\",\"length\":13,\"points\":\"\"},\"legId\":\"null_null\"}],\"duration\":1722000,\"startime\":1377170082000,\"walkingDuration\":772,\"from\":{\"lon\":\"11.151824063634374\",\"stopId\":{\"id\":\"\",\"agencyId\":\"\"},\"name\":\"service road\",\"stopCode\":\"null\",\"lat\":\"46.06679860551396\"}},\"originalFrom\":{\"lon\":\"11.151796\",\"stopId\":null,\"name\":null,\"stopCode\":null,\"lat\":\"46.066799\"}}";
		Map<String,Object> parsed = new ObjectMapper().readValue(s, Map.class);
		Map<String, Object> pars = new HashMap<String, Object>();
		Object data = parsed.get("data");
		pars.put("itinerary", new ObjectMapper().convertValue(data, Itinerary.class));
		String clientId = new ObjectId().toString();
		pars.put("clientId", clientId);
		pars.put("userId", "1");
		pars.put("originalFrom", new ObjectMapper().convertValue(parsed.get("originalFrom"), Position.class));
		pars.put("originalTo", new ObjectMapper().convertValue(parsed.get("originalTo"), Position.class));
		pars.put("name", parsed.get("name"));
		helper.invokeDOOperation("smartcampus.services.journeyplanner.ItineraryFactory", "smartcampus.services.journeyplanner.ItineraryFactory.0", "saveItinerary", pars);

		Map<String, Object> alertPars = createAlert(1000*20,5,"05R-Feriale_032");
		helper.invokeDOOperation("smartcampus.services.journeyplanner.AlertFactory", "smartcampus.services.journeyplanner.AlertFactory.0", "submitAlertDelay", alertPars);

		alertPars = createAlert(1000*20,3,"05R-Feriale_032");
		helper.invokeDOOperation("smartcampus.services.journeyplanner.AlertFactory", "smartcampus.services.journeyplanner.AlertFactory.0", "submitAlertDelay", alertPars);

		alertPars = createAlert(1000*10,15,"05R-Feriale_032");
		helper.invokeDOOperation("smartcampus.services.journeyplanner.AlertFactory", "smartcampus.services.journeyplanner.AlertFactory.0", "submitAlertDelay", alertPars);

		Thread.sleep(11000);
		
		alertPars = createAlert(1000*20,16,"05R-Feriale_032");
		helper.invokeDOOperation("smartcampus.services.journeyplanner.AlertFactory", "smartcampus.services.journeyplanner.AlertFactory.0", "submitAlertDelay", alertPars);
		
		System.err.println();
	}

	private static void checkAlertsForRecurrentJourney(DomainTestHelper helper)
			throws IOException, JsonParseException, JsonMappingException,
			Exception {
		helper.cleanDomainData();
		initDomain(helper);
		// save itinerary
		// signal alert - should be updated
		// signal another alert with small delay - no update
		// signal another alert with large delay - should be updated
		// signal alert after previous expires - should be updated

		String s = "{\"monitor\":true,\"name\":\"test\",\"data\":{\"monitorLegs\":{\"12_05R\":true},\"legs\":[{\"to\":\"Piazza Dante  Dogana\",\"transport\":{\"tripId\":\"05R-Feriale_036\",\"routeShortName\":\"5\",\"routeId\":\"05R\",\"type\":\"BUS\",\"agencyId\":\"12\"},\"from\":\"POVO  \\\"Fac. Scienze\\\"\"}],\"parameters\":{\"to\":{\"lon\":\"11.11889\",\"stopId\":null,\"name\":\"null\",\"stopCode\":\"null\",\"lat\":\"46.066695\"},\"time\":\"02:13PM\",\"routeType\":\"fastest\",\"recurrence\":[1,2,3,4,5,6,7],\"interval\":1200000,\"fromDate\":1377173627699,\"resultsNumber\":1,\"from\":{\"lon\":\"11.151796\",\"stopId\":null,\"name\":\"null\",\"stopCode\":\"null\",\"lat\":\"46.066799\"},\"toDate\":1377778427705,\"transportTypes\":[\"TRANSIT\"]}}}";
		Map<String,Object> parsed = new ObjectMapper().readValue(s, Map.class);
		Map<String, Object> pars = new HashMap<String, Object>();
		pars.put("recurrentJourney", parsed.get("data"));
		pars.put("name", "name");
		String clientId = new ObjectId().toString();
		pars.put("clientId", clientId);
		pars.put("userId", "1");
		pars.put("monitor", true);
		helper.invokeDOOperation("smartcampus.services.journeyplanner.RecurrentJourneyFactory", "smartcampus.services.journeyplanner.RecurrentJourneyFactory.0", "saveRecurrentJourney", pars);
		
		Map<String, Object> alertPars = createAlert(1000*20,5,"05R-Feriale_036");
		helper.invokeDOOperation("smartcampus.services.journeyplanner.AlertFactory", "smartcampus.services.journeyplanner.AlertFactory.0", "submitAlertDelay", alertPars);

		alertPars = createAlert(1000*20,3,"05R-Feriale_036");
		helper.invokeDOOperation("smartcampus.services.journeyplanner.AlertFactory", "smartcampus.services.journeyplanner.AlertFactory.0", "submitAlertDelay", alertPars);

		alertPars = createAlert(1000*10,15,"05R-Feriale_036");
		helper.invokeDOOperation("smartcampus.services.journeyplanner.AlertFactory", "smartcampus.services.journeyplanner.AlertFactory.0", "submitAlertDelay", alertPars);

		Thread.sleep(11000);
		
		alertPars = createAlert(1000*20,16,"05R-Feriale_036");
		helper.invokeDOOperation("smartcampus.services.journeyplanner.AlertFactory", "smartcampus.services.journeyplanner.AlertFactory.0", "submitAlertDelay", alertPars);
		
		System.err.println();
	}

	private static Map<String, Object> createAlert(long dur, int mins, String tripId) throws IOException,
			JsonParseException, JsonMappingException {
		AlertDelay a = new AlertDelay();
		a.setCreatorId("1");
		a.setCreatorType(CreatorType.USER);
		a.setDescription("description");
		a.setEffect("effect");
		a.setEntity(null);
		a.setFrom(System.currentTimeMillis());
		a.setTo(System.currentTimeMillis()+dur);
		a.setId(new ObjectId().toString());
		a.setNote("note");
		a.setPosition(new ObjectMapper().readValue("{\"lon\":\"11.150209\",\"stopId\":{\"id\":\"25015x\",\"agencyId\":\"12\"},\"name\":\"POVO  \\\"Fac. Scienze\\\"\",\"stopCode\":\"null\",\"lat\":\"46.063316\"}", Position.class));
		a.setDelay(60*1000*mins);
		a.setTransport(new ObjectMapper().readValue("{\"tripId\":\""+tripId+"\",\"routeShortName\":\"5\",\"routeId\":\"05R\",\"type\":\"BUS\",\"agencyId\":\"12\"}", Transport.class));
		Map<String, Object> alertPars = new HashMap<String, Object>();
		alertPars.put("newAlert", a);
		return alertPars;
	}

//	protected static void createUserObjects(DomainTestHelper helper)
//			throws Exception {
//		helper.cleanDomainData();
//		initDomain(helper);
//
//		DomainObject userPOIFactory = helper
//				.getDOById(
//						"eu.trentorise.smartcampus.domain.discovertrento.UserPOIFactory",
//						"eu.trentorise.smartcampus.domain.discovertrento.UserPOIFactory.0");
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("creator", "testuser");
//		params.put("data", createGenericPOI());
//		params.put("communityData", createCommunityDataRaw());
//		helper.invokeDOOperation(userPOIFactory.getType(),
//				userPOIFactory.getId(), "createPOI", params);
//
//		DomainObject userEventFactory = helper
//				.getDOById(
//						"eu.trentorise.smartcampus.domain.discovertrento.UserEventFactory",
//						"eu.trentorise.smartcampus.domain.discovertrento.UserEventFactory.0");
//		params.clear();
//		params.put("creator", "testuser");
//		params.put("data", createGenericEvent());
//		params.put("communityData", createCommunityDataRaw());
//		helper.invokeDOOperation(userEventFactory.getType(),
//				userEventFactory.getId(), "createEvent", params);
//
//		DomainObject userStoryFactory = helper
//				.getDOById(
//						"eu.trentorise.smartcampus.domain.discovertrento.StoryFactory",
//						"eu.trentorise.smartcampus.domain.discovertrento.StoryFactory.0");
//		params.clear();
//		params.put("creator", "testuser");
//		params.put("data", createGenericStory());
//		params.put("communityData", createCommunityDataRaw());
//		helper.invokeDOOperation(userStoryFactory.getType(),
//				userStoryFactory.getId(), "createStory", params);
//
//		List<DomainObject> list = helper
//				.getDOByType("eu.trentorise.smartcampus.domain.discovertrento.UserPOIObject");
//		for (DomainObject o : list) {
//			helper.invokeDOOperation(o.getType(), o.getId(), "deletePOI",
//					new HashMap<String, Object>());
//		}
//		list = helper
//				.getDOByType("eu.trentorise.smartcampus.domain.discovertrento.UserEventObject");
//		for (DomainObject o : list) {
//			helper.invokeDOOperation(o.getType(), o.getId(), "deleteEvent",
//					new HashMap<String, Object>());
//		}
//		list = helper
//				.getDOByType("eu.trentorise.smartcampus.domain.discovertrento.StoryObject");
//		for (DomainObject o : list) {
//			helper.invokeDOOperation(o.getType(), o.getId(), "deleteStory",
//					new HashMap<String, Object>());
//		}
//	}

	private static void initDomain(DomainTestHelper helper) {
		helper.start(
				  new AlertFactoryDOEngine(),
				  new GetOrdinanzeRoveretoDOEngine(),
				  new GetParkingDOEngine(),
				  new ItineraryFactoryDOEngine(),
				  new ItineraryObjectDOEngine(),
				  new GetTrainsDOEngine(),
				  new ParkingAlertsSenderDOEngine(),

				  new RecurrentJourneyFactoryDOEngine(),
				  new RecurrentJourneyObjectDOEngine(),
				  new RoadAlertSenderDOEngine(),
				  new TrainsAlertsSenderDOEngine(),
				  new UserAlertSenderDOEngine()
				  );
	}
}

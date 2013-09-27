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
package smartcampus.services.ordinanze;

import it.sayservice.platform.core.domain.actions.DataConverter;
import it.sayservice.platform.core.domain.ext.Tuple;
import it.sayservice.platform.smartplanner.data.message.RoadElement;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertRoad;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertRoadType;
import it.sayservice.platform.smartplanner.data.message.alerts.CreatorType;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.protobuf.ByteString;

import eu.trentorise.smartcampus.services.ordinanzerovereto.data.message.Ordinanzerovereto.Ordinanza;
import eu.trentorise.smartcampus.services.ordinanzerovereto.data.message.Ordinanzerovereto.Via;

public class OrdinanzeRoveretoConverter implements DataConverter {

	private static final String DIVIETO_DI_TRANSITO_E_DI_SOSTA = "divieto di transito e di sosta";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	private static final String DIVIETO_DI_TRANSITO = "divieto di transito";
	private static final String DIVIETO_DI_SOSTA = "divieto di sosta";
	private static final String DIVIETO_DI_SOSTA_CON = "divieto di sosta con rimozione coatta";
	private static final long PERMANENT_TIME_FRAME = 1000*60*60*24*60; // two months
	
	@Override
	public Serializable toMessage(Map<String, Object> parameters) {
		if (parameters != null) {
			return new TreeMap<String, Object>(parameters);
		} else {
			return new TreeMap<String, Object>();
		}
	}

	@Override
	public Object fromMessage(Serializable object) {
		List<ByteString> data = (List<ByteString>) object;
		Tuple res = new Tuple();
		List<AlertRoad> list = new ArrayList<AlertRoad>();
		for (ByteString bs : data) {
			try {
				Ordinanza t = Ordinanza.parseFrom(bs);
				if (t.getVieCount() == 0) continue;
				for (int i = 0; i < t.getVieCount(); i++) {
					Via via = t.getVie(i);
					AlertRoad ar = new AlertRoad();
					ar.setAgencyId("COMUNE_DI_ROVERETO");
					ar.setCreatorType(CreatorType.SERVICE);
					ar.setDescription(t.getOgetto());
					ar.setEffect(via.hasTipologia() && !via.getTipologia().isEmpty() ? via.getTipologia() : t.getTipologia());
					ar.setFrom(sdf.parse(t.getDal()).getTime());
					ar.setTo(sdf.parse(t.getAl()).getTime());
					ar.setId(t.getId()+"_"+via.getCodiceVia());
					ar.setRoad(toRoadElement(via,t));
					ar.setChangeTypes(getTypes(via,t));
					if (!t.getTipologia().equals("Permanente") ||
					    ar.getFrom() > System.currentTimeMillis()-PERMANENT_TIME_FRAME) 
					{
						list.add(ar);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		res.put("data", list.toArray(new AlertRoad[list.size()]));
		return res;
	}

	private AlertRoadType[] getTypes(Via via, Ordinanza t) {
		String type = via.hasTipologia() && ! via.getTipologia().isEmpty() ? via.getTipologia() : t.getTipologia();
		if (DIVIETO_DI_TRANSITO_E_DI_SOSTA.equals(type) || t.getOgetto().toLowerCase().contains(DIVIETO_DI_TRANSITO_E_DI_SOSTA)) {
			return new AlertRoadType[]{AlertRoadType.PARKING_BLOCK, AlertRoadType.ROAD_BLOCK};
		}
		if (DIVIETO_DI_TRANSITO.equals(type) || t.getOgetto().toLowerCase().contains(DIVIETO_DI_TRANSITO)) {
			return new AlertRoadType[]{AlertRoadType.ROAD_BLOCK};
		}
		if (DIVIETO_DI_SOSTA.equals(type) || DIVIETO_DI_SOSTA_CON.equals(type) || t.getOgetto().toLowerCase().contains(DIVIETO_DI_SOSTA)) {
			return new AlertRoadType[]{AlertRoadType.PARKING_BLOCK};
		}
		if ("senso unico alternato".equals(type) || t.getOgetto().toLowerCase().contains("senso unico alternato")) {
			return new AlertRoadType[]{AlertRoadType.DRIVE_CHANGE};
		}
		if ("doppio senso di marcia".equals(type) || t.getOgetto().toLowerCase().contains("doppio senso di marcia")) {
			return new AlertRoadType[]{AlertRoadType.DRIVE_CHANGE};
		}
		if (type.contains("limitazione della velocit")) {
			return new AlertRoadType[]{AlertRoadType.DRIVE_CHANGE};
		}
		return new AlertRoadType[]{AlertRoadType.OTHER};
	}

	private RoadElement toRoadElement(Via via, Ordinanza t) {
		RoadElement re = new RoadElement();
		re.setLat(via.getLat()+"");
		re.setLon(via.getLng()+"");
		if (via.hasAlCivico()) re.setToNumber(via.getAlCivico());
		if (via.hasAlIntersezione()) re.setToIntersection(via.getAlIntersezione());
		if (via.hasCodiceVia()) re.setStreetCode(via.getCodiceVia());
		if (via.hasDalCivico()) re.setFromNumber(via.getDalCivico());
		if (via.hasDalIntersezione()) re.setFromIntersection(via.getDalIntersezione());
		if (via.hasDescrizioneVia()) re.setStreet(via.getDescrizioneVia());
		if (via.hasNote()) re.setNote(via.getNote());
		return re;
	}

}

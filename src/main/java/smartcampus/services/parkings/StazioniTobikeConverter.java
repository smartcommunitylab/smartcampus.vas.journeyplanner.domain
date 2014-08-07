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
package smartcampus.services.parkings;

import it.sayservice.platform.core.domain.actions.DataConverter;
import it.sayservice.platform.core.domain.ext.Tuple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.protobuf.ByteString;

import eu.trentorise.smartcampus.service.parcheggi.data.message.Parcheggi.Parcheggio;
import eu.trentorise.smartcampus.service.tobike.data.message.Tobike.Stazione;

public class StazioniTobikeConverter implements DataConverter {

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
		List<Parking> list = new ArrayList<Parking>();
		for (ByteString bs : data) {
			try {
				Stazione s = Stazione.parseFrom(bs);
				Parking p = new Parking();
				p.setAgencyId("BIKE_SHARING_TOBIKE_ROVERETO");
				p.setId(s.getNome() + " - Rovereto");
				p.setAddress(s.getIndirizzo());
				p.setFreePlaces(s.getPosti());
				p.setVehicles(s.getBiciclette());
				list.add(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		res.put("data", list.toArray(new Parking[list.size()]));
		return res;
	}

}

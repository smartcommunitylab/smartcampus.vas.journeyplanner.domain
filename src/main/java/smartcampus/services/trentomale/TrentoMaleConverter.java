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
package smartcampus.services.trentomale;

import it.sayservice.platform.core.domain.actions.DataConverter;
import it.sayservice.platform.core.domain.ext.Tuple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import smartcampus.service.trentomale.data.message.Trentomale.Train;
import smartcampus.services.oraritreni.delays.GenericTrain;

import com.google.protobuf.ByteString;

public class TrentoMaleConverter implements DataConverter{

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
		List<ByteString> data = (List<ByteString>)object;
		Tuple res = new Tuple();
		List<GenericTrain> list = new ArrayList<GenericTrain>();
		for (ByteString bs : data) {
			try {
				Train t = Train.parseFrom(bs);
				GenericTrain tmt = new GenericTrain();
					tmt.setDelay(t.getDelay());
					tmt.setId("" + t.getId());
					tmt.setTripId("" + t.getNumber());
					tmt.setDirection(t.getDirection());
					tmt.setTime(t.getTime());
					tmt.setStation(t.getStation());
					tmt.setAgencyId("10");
					if ("Trento".equalsIgnoreCase(t.getDirection())) {
						tmt.setRouteId("556");
					} else {
						tmt.setRouteId("555");
					}
					list.add(tmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		res.put("data", list.toArray(new GenericTrain[list.size()]));
		return res;
	}

}

package br.com.erudio.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.mapper.Mapper;

import com.github.dozermapper.core.DozerBeanMapperBuilder;

public class DozerMapper {
	
	private static com.github.dozermapper.core.Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	public static <O, D> D parseObject(O origin, Class<D> destination) {
		return mapper.map(origin, destination);
	}
	
	public static <O, D> List<D> parseListObjects(List<O> origin, Class<D> destination) {
		List<D> destinationObjects = new ArrayList<D>();
		for (O o : origin) {
			destinationObjects.add(mapper.map(o, destination));
		}
		return destinationObjects;
	}

}

package br.com.erudio;

//import java.util.Map;
//
//import org.apache.commons.collections.map.HashedMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
//import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;

@SpringBootApplication
public class Startup {

	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);  
		
//		Map<String, PasswordEncoder> encoders = new HashedMap();
//		Pbkdf2PasswordEncoder pbkdf2encoder = new Pbkdf2PasswordEncoder("",8,185000,SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
//		encoders.put("pbkdf2", pbkdf2encoder);
//		DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
//		passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2encoder);
//		
//		String result1 = passwordEncoder.encode("admin1234");
//		String result2 = passwordEncoder.encode("admin1234");
//		System.out.println("My hash result1 "+ result1);
//		System.out.println("My hash result2 "+ result2);
	}

}

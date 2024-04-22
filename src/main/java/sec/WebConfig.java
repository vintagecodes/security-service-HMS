//package sec;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@EnableWebMvc
//public class WebConfig {
//	
//	@Bean
//	public WebMvcConfigurer configurer()
//	{
//		return new WebMvcConfigurer() {
//			
//			public void addCorsMapping(CorsRegistry corsRegistry)
//			{
//				corsRegistry.addMapping("/api/user-auth/**")
//				.allowedOrigins("https://thehealthconsultor.netlify.app","http://localhost:4200")
//				.allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.OPTIONS.name(), HttpMethod.DELETE.name())
//				.allowedHeaders("*")
//				.allowCredentials(true);
//			}
//		};
//	}
//
//}

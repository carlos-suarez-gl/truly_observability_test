package io.sentry.samples.example;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.JavaVersion;
import org.springframework.boot.system.SystemProperties;
import org.springframework.core.SpringVersion;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import io.sentry.Sentry;

@SpringBootApplication
@RestController
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	private static Map<String, Integer> inventory = new HashMap<>();
	private RestTemplate restTemplate;
	
    private String applicationName = "Sentry+Jaeger";

	private void checkout(List<Item> cart) {

		Map<String, Integer> tempInventory = inventory;
		for(Item item : cart) {
			int currentInventory = tempInventory.get(item.getId());
			if(currentInventory <= 0) {
				String message = "No inventory for " + item.getId();
				System.out.println(message);
				throw new RuntimeException(message);
			}
			currentInventory = currentInventory - 1;
			tempInventory.put(item.getId(), currentInventory);
		}
		inventory = tempInventory;
	}

	public void Application(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

	@CrossOrigin(origins = "http://localhost:5000")
	@PostMapping("/checkout")
	public void CheckoutCart(@RequestBody Order order) {
		Sentry.configureScope(scope -> {
			scope.setExtra("cart", order.toJson());
		});

		MDC.put("springVersion", SpringVersion.getVersion());
		MDC.put("jdkVersion", SystemProperties.get("java.version"));

		try {
			checkout(order.getCart());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("/message")
	public String CaptureMessage() {
		Sentry.configureScope(scope -> {
			scope.setExtra("springVersion", SpringVersion.getVersion());
			scope.setExtra("jdkVersion", SystemProperties.get("java.version"));
		});
		MDC.put("springVersion", SpringVersion.getVersion());
		MDC.put("jdkVersion", SystemProperties.get("java.version"));
		logger.info("Called capture-message");
		return "Success";
	}

	@GetMapping("/handled")
	public String HandledError() {
		String someLocalVariable = "stack locals";

		try {
			int example = 1/0;
		} catch (Exception e) {
			logger.error("caught exception", e);
			return "Fail";
		}
		return "Success";
	}

	// This Sentry event produced by this is marked as handled:true
	@GetMapping("/unhandled")
	public String UnhandledError() {
		throw new RuntimeException("Unhandled Exception!");
	} 

	public static void main(String[] args) {
		inventory.put("wrench", 1);
		inventory.put("nails", 1);
		inventory.put("hammer", 1);

		SpringApplication.run(Application.class, args);
	}

	@GetMapping("/hello")
	public String Hello(@RequestParam(value="name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/path1")
    public ResponseEntity path1() {

        logger.info("Incoming request at {} for request /path1 ", applicationName);
        String response = restTemplate.getForObject("http://localhost:8090/path2", String.class);
        return ResponseEntity.ok("response from /path1 + " + response);
    }


    @GetMapping("/path2")
    public ResponseEntity path2() {
        logger.info("Incoming request at {} at /path2", applicationName);
        return ResponseEntity.ok("response from /path2 ");
    }

	@GetMapping("/")
	public String rootCall() {
		return "This is the root url";
	}
}

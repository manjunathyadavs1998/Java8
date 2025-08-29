
what is the difference b/w Error and Exception? How to handle them in java

| Feature            | `Exception`                                                                       | `Error`                                                            |
| ------------------ | --------------------------------------------------------------------------------- | ------------------------------------------------------------------ |
| **Definition**     | Conditions that a program **should catch and handle**                             | Conditions that are **serious and usually not meant to be caught** |
| **Hierarchy**      | Subclass of `Throwable`                                                           | Subclass of `Throwable`                                            |
| **Examples**       | `NullPointerException`, `IOException`, `SQLException`, `IllegalArgumentException` | `OutOfMemoryError`, `StackOverflowError`, `LinkageError`           |
| **Can be caught?** | ✅ Yes (try-catch)                                                                 | ⚠️ Technically yes, but **not recommended**                        |
| **Recovery**       | Often possible                                                                    | Rarely possible                                                    |
| **Typical Source** | Application logic, external systems                                               | JVM, system-level failures                                         |


java.lang.Object
 └── java.lang.Throwable
      ├── java.lang.Error        ← Serious problems
      └── java.lang.Exception    ← Recoverable problems
           └── java.lang.RuntimeException ← Unchecked
✅ Types of Exceptions
1. Checked Exceptions

	Must be declared or handled (throws or try-catch)

	E.g., IOException, SQLException
	
	public void readFile() throws IOException {
    Files.readAllLines(Paths.get("data.txt"));
	}

2. Unchecked Exceptions (aka RuntimeExceptions)

	Don’t require explicit handling

	E.g., NullPointerException, IllegalArgumentException

	public void divide(int a, int b) {
		int result = a / b; // Can throw ArithmeticException
	}
	
❌ Common Errors
	⚠️ Don't usually try to handle these:

	OutOfMemoryError: JVM can't allocate memory

	StackOverflowError: Infinite recursion

	InternalError: JVM internal bug

	NoClassDefFoundError: Class not found at runtime

	Even though you can catch them, it’s generally bad practice, except for:

	Logging or failing gracefully in critical systems.
	
		try {
		// risky operation
	} catch (OutOfMemoryError e) {
		// log and fail gracefully (not recover)
	}



What happens internally with @SpringBootApplication?

	@SpringBootApplication is equivalent to combining these three annotations:
	@SpringBootConfiguration
	@EnableAutoConfiguration
	@ComponentScan
	
	
What happens internally with @SpringBootApplication?
@SpringBootApplication is equivalent to combining these three annotations:
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan

Let me break down each part:
1️⃣ @SpringBootConfiguration

	It’s a specialized form of @Configuration (from Spring Framework).

	Marks the class as a source of bean definitions for the application context.

	Essentially tells Spring: “This class provides configuration”.

2️⃣ @EnableAutoConfiguration

	This is the magic behind Spring Boot’s auto-configuration feature.

	It tells Spring Boot to automatically configure your application based on the dependencies present on the classpath.

	For example:

	If spring-web is on the classpath, it sets up a default embedded Tomcat server.

	If spring-data-jpa is present, it configures an EntityManagerFactory.

	Uses Spring Factories and conditional annotations (@ConditionalOnClass, @ConditionalOnMissingBean) internally to decide what to configure.

3️⃣ @ComponentScan

	Enables component scanning on the package where the class with @SpringBootApplication resides.

	It automatically detects Spring components like @Component, @Service, @Repository, @Controller, and registers them as beans.

	This means you don’t need to manually define beans in XML or Java config for those components.

Summary: What happens when Spring Boot starts?

	It scans the package and sub-packages of the main class for components (@ComponentScan).

	Loads beans defined in the class annotated with @SpringBootConfiguration.

	Triggers auto-configuration (@EnableAutoConfiguration) which:

	Checks dependencies on the classpath.

	Applies default configurations if no user-defined beans are found.

	Creates an application context and starts the embedded server (if applicable).

	Runs your application!

Bonus: You can also see this from source:
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Inherited
	@SpringBootConfiguration
	@EnableAutoConfiguration
	@ComponentScan(excludeFilters = {
		@ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class)
	})
	public @interface SpringBootApplication {
	}
	


| Aspect           | `@Configuration`                                                 | `@EnableAutoConfiguration`                                                                                                             |
| ---------------- | ---------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| **Purpose**      | Marks a class as a source of explicit bean definitions in Spring | Enables Spring Boot’s auto-configuration mechanism that configures beans automatically based on classpath, properties, and environment |
| **Use case**     | You define beans manually using `@Bean` methods                  | Spring Boot automatically configures beans behind the scenes for you, based on dependencies                                            |
| **Scope**        | Typically for your own application-specific configuration        | Loads many pre-written configurations shipped by Spring Boot and third-party libraries                                                 |
| **How it works** | Processes the annotated class to instantiate and register beans  | Scans `META-INF/spring.factories` for auto-configuration classes and applies them if conditions are met                                |
| **Control**      | You explicitly define what beans to create                       | You can enable/disable or override auto-configurations with properties or conditional annotations                                      |
| **Example**      |                                                                  |                                                                                                                                        |


| Annotation      | Purpose                                             | Typical Layer                     | Special Features                                                                                                             |
| --------------- | --------------------------------------------------- | --------------------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| **@Component**  | Generic stereotype for any Spring-managed component | Any layer                         | Basic component registration                                                                                                 |
| **@Repository** | Indicates **persistence layer** (DAO) component     | Data access / Repository layer    | Adds **exception translation** for persistence exceptions (e.g., converts JDBC exceptions to Spring's `DataAccessException`) |
| **@Service**    | Indicates **service layer** business logic          | Service / business logic layer    | Semantic clarity — no additional behavior by default                                                                         |
| **@Controller** | Indicates **presentation layer** (MVC controller)   | Web layer (handles HTTP requests) | Supports Spring MVC, request mapping, and view resolution                                                                    |

Detailed Explanation
1. @Component

	The most generic stereotype.

	Can be used anywhere.

	Useful when no specific role applies.

	All other annotations (@Repository, @Service, @Controller) are meta-annotated with @Component — so they get picked up by component scanning.

2. @Repository

	Marks a DAO or repository class that accesses the database.

	Key feature: Spring provides automatic exception translation for classes annotated with @Repository.

	Translates native persistence exceptions (e.g., SQL exceptions) into Spring’s consistent DataAccessException hierarchy.

	This makes your DAO layer more portable and easier to handle exceptions.

3. @Service

	Marks service classes that contain business logic.

	No special behavior by default (just semantic clarity).

	Helps developers and tools (like IDEs) understand that this class contains service logic.

4. @Controller

	Used in Spring MVC to mark web controllers.

	Processes HTTP requests and returns views or responses.

	Supports annotations like @RequestMapping, @GetMapping, etc.

	For REST APIs, you often use @RestController (which combines @Controller + @ResponseBody).

Is it okay to annotate a repository as @Service? Will it cause problems?

	Technically:

	Yes, Spring will detect it as a component and instantiate it.

	Your repository will function if annotated with @Service.

	But it’s not recommended because:

	You lose the automatic exception translation that @Repository provides.

	It breaks semantic clarity, making your code harder to read and maintain.

	Future developers may misunderstand the role of the class.

	So:

	Always use @Repository for data access components.

	Use @Service for business logic.

	Use @Controller for web layer.
	
	
==============================================================================================

✅ What is Dependency Injection (DI)?

	Dependency Injection is a design pattern that allows you to pass ("inject") dependencies (objects your class needs to work) from the outside, rather than having the class create them itself.
	Without DI:
		public class Car {
    private Engine engine = new Engine(); // hardcoded dependency
	}
	With DI:
		public class Car {
		private Engine engine;

		// constructor injection
		public Car(Engine engine) {
			this.engine = engine;
		}
	}



⚙️ How Spring implements it internally?

Spring provides Dependency Injection via its IoC Container (ApplicationContext).

Here’s what happens internally:

1️⃣ Spring Scans Classes

	During application startup, Spring scans the classpath for annotations like:

	@Component, @Service, @Repository, @Controller

	Or beans defined via @Bean methods in @Configuration classes

2️⃣ Spring Creates a Bean Definition for each managed class

A bean definition contains metadata:

	Class name

	Scope (singleton by default)

	Dependencies

	Constructor or method to use for instantiation

3️⃣ Spring Builds the Dependency Graph

	It analyzes which beans depend on which others:

	Constructor parameters

	@Autowired fields/methods

	@Value injected configs

4️⃣ Spring Resolves Dependencies and Instantiates Beans

	In the right order, Spring:

	Instantiates beans using reflection

	Injects dependencies (constructor, field, or setter)

	Applies AOP proxies if needed

	Adds the fully initialized bean to the ApplicationContext

🧠 What happens inside the JVM at runtime.

1. Reflection Usage

	Spring uses reflection (via Constructor.newInstance() and Field.set()) to create and inject objects.

	No bytecode changes to your classes occur unless proxies or CGLIB are used.

2. ApplicationContext Holds All Beans

	Spring stores all created beans in a centralized registry.

	Beans are looked up and injected from this singleton container.

3. No New JVM Features Are Introduced

	Everything happens within standard Java (reflection, class loading).

	Your app classes are loaded normally by the JVM.

	Spring creates the object graph for you, rather than you doing new manually.
	
	
| Injection Type        | Test Setup Complexity              | Dependency Visibility | Immutability | Test Isolation     | Recommended?                      |
| --------------------- | ---------------------------------- | --------------------- | ------------ | ------------------ | --------------------------------- |
| Constructor Injection | Simple (pass mocks in ctor)        | High                  | Yes          | High               | ✔️ Best practice                  |
| Setter Injection      | Moderate (call setters manually)   | Moderate              | No           | Moderate           | ✔️ Good for optional dependencies |
| Field Injection       | Simple with Mockito `@InjectMocks` | Low                   | No           | Depends on Mockito | ⚠️ Avoid in production            |

When field must be final?

	When you want immutable objects.

	When you want to design thread-safe classes without synchronization.

	When dependencies are mandatory and must not change.

	When you want to take advantage of constructor injection.


🧬 What changes when using DI vs regular object creation.

| Aspect                                 | Regular Object Creation (`new`)                           | Dependency Injection (DI) (e.g., Spring)                             |
| -------------------------------------- | --------------------------------------------------------- | -------------------------------------------------------------------- |
| **Who creates the object?**            | The class or the caller explicitly calls `new`            | The **framework (Spring IoC container)** creates and manages objects |
| **Object lifecycle management**        | Manual—you create, configure, and manage lifecycle        | Framework manages lifecycle (creation, initialization, destruction)  |
| **Coupling**                           | Tight coupling — class controls its dependencies directly | Loose coupling — dependencies are provided externally                |
| **Code readability & maintainability** | Explicit `new` calls scattered around code                | Clean, declarative wiring via annotations/configuration              |
| **Flexibility**                        | Less flexible; changing dependencies means editing code   | More flexible; swap implementations/config externally                |
| **Testability**                        | Harder to test; dependencies are embedded, hard to mock   | Easier to test; dependencies injected, easy to replace with mocks    |
| **Use of JVM resources**               | Objects created on-demand manually                        | Objects often created once (singleton beans), cached, reused         |
| **Use of Reflection**                  | Typically none; direct instantiation                      | Uses reflection to instantiate and inject dependencies               |
| **Dependency Graph Management**        | Developer manually manages order and dependencies         | Container automatically manages dependencies and instantiation order |
| **AOP and Proxies**                    | Not possible natively                                     | Framework can wrap objects with proxies for cross-cutting concerns   |


========================================================

🔹 What is a Circular Dependency?

	A circular dependency happens when two (or more) beans (classes) in Spring depend on each other directly or indirectly.

	Direct circular dependency:
	Class A needs Class B, and Class B needs Class A.

	Indirect circular dependency:
	Class A → needs Class B → needs Class C → needs Class A.

	When Spring tries to create these beans, it can’t resolve who should be created first, leading to a circular reference error (unless Spring proxies it).
	
🔹 Root Cause

	The root cause is usually bad design / tightly coupled components.

	Each bean is trying to directly control or depend on another.

	Instead of clearly separating responsibilities, logic is tangled.

In simple terms:
	❌ Too much “give me what I need” → beans tightly hold references to each other.
	✅ Better design → beans collaborate via interfaces, events, or service layers.
	
	@Component
	class OrderService {
		private final PaymentService paymentService;

		@Autowired
		OrderService(PaymentService paymentService) {
			this.paymentService = paymentService;
		}

		public void placeOrder() {
			System.out.println("Placing order...");
			paymentService.processPayment();
		}
	}

	@Component
	class PaymentService {
		private final OrderService orderService;

		@Autowired
		PaymentService(OrderService orderService) {
			this.orderService = orderService;
		}

		public void processPayment() {
			System.out.println("Processing payment...");
			orderService.placeOrder(); // 🔴 Circular call
		}
	}


🔹 Real-World Analogy

	Imagine two government offices:

	Office A won’t approve a document until it’s stamped by Office B.

	Office B won’t stamp it until it’s signed by Office A.
	Result: The document never gets approved → deadlock.
	
🔹 How to Fix It

	Refactor design – usually the best solution.

	Extract common functionality into a new service.

	Use events or callbacks instead of direct method calls.

Example (better design):
	@Component
	class OrderService {
		private final PaymentGateway paymentGateway;

		@Autowired
		OrderService(PaymentGateway paymentGateway) {
			this.paymentGateway = paymentGateway;
		}
	}

	@Component
	class PaymentGateway {
		public void processPayment() {
			System.out.println("Payment processed!");
		}
	}
	
	Use @Lazy injection (not recommended unless quick fix).
	@Autowired
	@Lazy
	private OrderService orderService;

🔹 How Spring Resolves Circular Dependencies Internally
	Spring has a partial solution for circular dependencies — but only for singleton beans (not for prototypes).

	It uses a clever trick called early bean references.
		
		1.Spring’s 3-Level Cache for Bean Creation

			When Spring creates beans, it maintains three caches inside the DefaultSingletonBeanRegistry:

			singletonObjects → Fully created singletons (ready-to-use beans).

			earlySingletonObjects → Half-created beans (exposed early so others can use them).

			singletonFactories → Object factories (proxies or references for beans not yet fully initialized).
			
		2️⃣ Circular Dependency Resolution Flow

			Let’s take our earlier OrderService ↔ PaymentService example (but without infinite recursion in methods).

			Step 1: Spring starts creating OrderService.

			Constructor injection → needs PaymentService.

			Step 2: Spring starts creating PaymentService.

			Constructor injection → needs OrderService.

			At this point, Spring sees:
			👉 “Wait, I’m already creating OrderService… but PaymentService needs it now!”

			Instead of failing immediately, Spring does this:

			Places an early reference of OrderService in earlySingletonObjects.

			Provides that half-built object to PaymentService.

			So now:

			PaymentService gets a reference to OrderService (though not fully initialized yet).

			Once both beans finish initialization, Spring replaces early references with fully built beans.

			✅ This resolves many circular dependencies automatically.


===================================================================================================

🔹 What is application.yml?

	In Spring Boot, the application.yml (or application.properties) file is the central configuration file.

	It defines application-wide settings (like server port, DB configs, logging, profiles, etc.).

	Uses YAML format → more structured & readable than .properties.

    You can define multiple profiles (dev, qa, prod) inside it, so the app can load different configs depending on the environment.
	
		spring:
		 profiles:
		   active: prod   # 👈 This makes sure "dev" section gets activated

		---

		spring:
		  config:
			activate:
			  on-profile: prod

		  application:
			name: Shopfiy

		  datasource:
			url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:dummy}
			driver-class-name: com.mysql.cj.jdbc.Driver
			username: ${DB_USERNAME:root}
			password: ${DB_PASSWORD:root}

		  jpa:
			hibernate:
			  ddl-auto: update
			show-sql: true


		server:
		  port: 8083

		spring:
		  config:
			activate:
			  on-profile: dev
			  
			  


		server:
		  port: 8086

		spring:
		  config:
			activate:
				on-profile: prod
				
		server:
		  port: 8082

		spring:
		  config:
			activate:
			  on-profile: qa
			  
			  
===================================================================================================

IOC VS DI

| Feature         | IoC                                                  | DI                                                   |
| --------------- | ---------------------------------------------------- | ---------------------------------------------------- |
| Meaning         | Principle that inverts object control to a container | Technique to implement IoC by supplying dependencies |
| Level           | High-level concept                                   | Concrete mechanism                                   |
| Who handles it? | Container/framework                                  | Container via injection                              |
| Example         | Spring IoC container manages beans                   | `@Autowired` injecting a bean                        |



=====================================================================================================

🌐 HTTP (HyperText Transfer Protocol)
	Definition: HTTP is a protocol that defines how clients (like browsers, Postman, or your React app) communicate with servers (like Spring Boot, Django, Node.js).

	Nature:

		Stateless → Each request is independent; server doesn’t remember past requests unless explicitly managed (using session/cookies/JWT).

		Client-Server → Client sends a request, server responds.

		Request/Response model → Always one request → one response.

Structure of an HTTP Request

	Request Line: Method + URL + HTTP version (e.g., GET /users HTTP/1.1)

	Headers: Metadata (e.g., Content-Type: application/json)

	Body (optional): Data sent with request (e.g., JSON in POST).

🛠 Structure of an HTTP Response

	Status Line: Status code + HTTP version (e.g., 200 OK)

	Headers: Metadata (e.g., Content-Length: 123)

	Body (optional): Data returned (e.g., JSON object).
	
| Method      | Purpose                                            | Example                              | Idempotent?    | Body Allowed? |
| ----------- | -------------------------------------------------- | ------------------------------------ | -------------- | ------------- |
| **GET**     | Retrieve data                                      | `GET /users` → fetch users           | ✅ Yes          | ❌ No          |
| **POST**    | Create new resource                                | `POST /users` with JSON body         | ❌ No           | ✅ Yes         |
| **PUT**     | Update/Replace a resource                          | `PUT /users/1`                       | ✅ Yes          | ✅ Yes         |
| **PATCH**   | Partial update                                     | `PATCH /users/1` (update only email) | ❌ No (usually) | ✅ Yes         |
| **DELETE**  | Remove a resource                                  | `DELETE /users/1`                    | ✅ Yes          | (Optional)    |
| **HEAD**    | Same as GET but returns only headers               | `HEAD /users`                        | ✅ Yes          | ❌ No          |
| **OPTIONS** | Tells allowed methods for a resource (CORS checks) | `OPTIONS /users`                     | ✅ Yes          | ❌ No          |


📊 HTTP Status Codes
	HTTP status codes are grouped into 5 categories:
	
	✅ 1xx – Informational

		Rarely used by us directly, more internal.

		100 Continue → Client should continue request.

		101 Switching Protocols → Used in WebSockets, protocol upgrade.
	
	✅ 2xx – Success
	
		| Code               | Meaning                                | Example                                                |
		| ------------------ | -------------------------------------- | ------------------------------------------------------ |
		| **200 OK**         | Request succeeded.                     | `GET /users` returns a JSON list.                      |
		| **201 Created**    | New resource created.                  | `POST /users` with user details → returns new user ID. |
		| **202 Accepted**   | Request accepted but still processing. | Async operations, like file uploads.                   |
		| **204 No Content** | Success but no response body.          | `DELETE /users/1` → nothing to return.                 |

	⚠️ 3xx – Redirection
	
		| Code                      | Meaning                        | Example                       |
		| ------------------------- | ------------------------------ | ----------------------------- |
		| **301 Moved Permanently** | URL permanently moved.         | Old API endpoint → new one.   |
		| **302 Found**             | Temporary redirection.         | Used in login redirects.      |
		| **304 Not Modified**      | Cached version is still valid. | Browser caching optimization. |

	❌ 4xx – Client Errors
	
		| Code                       | Meaning                               | Example                                               |
		| -------------------------- | ------------------------------------- | ----------------------------------------------------- |
		| **400 Bad Request**        | Invalid input.                        | Sending malformed JSON to API.                        |
		| **401 Unauthorized**       | Not logged in / No valid credentials. | Accessing `/profile` without token.                   |
		| **403 Forbidden**          | Logged in but not allowed.            | User trying to access admin-only API.                 |
		| **404 Not Found**          | Resource doesn’t exist.               | `GET /users/99` but user doesn’t exist.               |
		| **405 Method Not Allowed** | Wrong HTTP method.                    | Using `POST` on an endpoint that only supports `GET`. |
		| **409 Conflict**           | Request conflicts with server state.  | Registering with existing email.                      |
		| **429 Too Many Requests**  | Rate limiting.                        | Sending too many API calls in short time.             |

	💀 5xx – Server Errors
	
		| Code                          | Meaning                                          | Example                               |
		| ----------------------------- | ------------------------------------------------ | ------------------------------------- |
		| **500 Internal Server Error** | Generic error.                                   | NullPointerException in Java backend. |
		| **502 Bad Gateway**           | Server got invalid response from another server. | Reverse proxy issues.                 |
		| **503 Service Unavailable**   | Server is down/overloaded.                       | Server maintenance.                   |
		| **504 Gateway Timeout**       | Upstream server not responding in time.          | Slow DB query causing timeout.        |
		
=============================================================================================================================================
		
1️⃣ HTTP (HyperText Transfer Protocol)

	What it is: A protocol used by web browsers and servers to communicate.

	Default port: 80

	Format: Text-based (human-readable), uses request/response model.

	Serialization: JSON, XML, plain text, etc.

	Use case: Traditional web applications, REST APIs.
	
	Request:
		GET /products/123 HTTP/1.1
		Host: example.com

	Response:
		HTTP/1.1 200 OK
		Content-Type: application/json

	{
	  "id": 123,
	  "name": "Laptop"
	}



2️⃣ HTTPS (HyperText Transfer Protocol Secure)

	What it is: HTTP + encryption using SSL/TLS.

	Default port: 443

	Format: Same as HTTP (still text-based, request/response).

	Difference: Data is encrypted, so attackers can’t see or modify traffic.

	Use case: Banking, e-commerce, login pages, secure REST APIs.

	Example:
		If you send username=manju&password=12345,

		On HTTP → attackers can read this directly.

		On HTTPS → attackers see encrypted gibberish like fj3#@!dkf93kf....

		👉 HTTPS is just HTTP + security layer.
		
3️⃣ gRPC (Google Remote Procedure Call)

	What it is: A high-performance communication protocol built on HTTP/2.

	Format: Binary (uses Protocol Buffers = Protobuf, not JSON).

	Communication: Supports synchronous and asynchronous calls.

	Special feature: Supports bi-directional streaming (both client and server can send data continuously).

	Use case: Microservices communication, real-time apps (e.g., chat, video streaming), inter-service backend calls.
	
	Protobuf definition:
		syntax = "proto3";

		service ProductService {
		  rpc GetProduct (ProductRequest) returns (ProductResponse);
		}

		message ProductRequest {
		  int32 id = 1;
		}

		message ProductResponse {
		  int32 id = 1;
		  string name = 2;
		}

		ProductResponse res = productService.getProduct(ProductRequest.newBuilder().setId(123).build());
		System.out.println(res.getName());  // "Laptop"
	
| Feature        | HTTP                  | HTTPS                         | gRPC                                   |
| -------------- | --------------------- | ----------------------------- | -------------------------------------- |
| Protocol Layer | HTTP/1.1              | HTTP/1.1 + TLS                | HTTP/2                                 |
| Data Format    | Text (JSON, XML)      | Text (encrypted)              | Binary (Protobuf)                      |
| Security       | None                  | Encrypted                     | Encrypted (TLS by default)             |
| Performance    | Slower (verbose JSON) | Slower (due to TLS handshake) | Faster (binary, multiplexing)          |
| Streaming      | ❌ No                  | ❌ No                       | ✅ Yes (bi-directional)               |
| Best For       | Simple APIs, websites | Secure websites/APIs          | Microservices, real-time communication |
	
	
	
==========================================================================================

1. @Controller

	Used to define a controller in Spring MVC.
	Typically returns views (HTML, JSP, Thymeleaf, etc.) instead of JSON or raw data.
	Requires @ResponseBody on methods if you want to return raw data (JSON, XML, String, etc.).
	
		@Controller
		public class HomeController {

			@GetMapping("/home")
			public String homePage(Model model) {
				model.addAttribute("message", "Welcome to Home Page!");
				return "home";  // maps to home.html (Thymeleaf or JSP)
			}

			@GetMapping("/greet")
			@ResponseBody
			public String greet() {
				return "Hello User!"; // Returns raw data (because of @ResponseBody)
			}
		}

2. @RestController

	Introduced in Spring 4.

	Combination of @Controller + @ResponseBody.

	Every method returns raw data (JSON, XML, String) by default.

	Commonly used for REST APIs.
	
		@RestController
		public class UserController {

			@GetMapping("/user")
			public User getUser() {
				return new User(1, "Alice", "alice@email.com");
				// Automatically converted to JSON by Jackson
			}

			@GetMapping("/hello")
			public String sayHello() {
				return "Hello from REST Controller";  // Returns String as response body
			}
		}
		
| Feature                | `@Controller`                    | `@RestController`            |
| ---------------------- | -------------------------------- | ---------------------------- |
| Primary Use            | Web pages (MVC)                  | REST APIs (JSON/XML)         |
| Return Type (default)  | View (HTML/JSP)                  | Raw data (JSON, XML, String) |
| Needs `@ResponseBody`? | ✅ Yes (for JSON/data)            | ❌ No (implicit)              |
| Example Use Case       | Rendering a Thymeleaf login page | Returning user data as JSON  |



========================================================================================================================

📌 The Contract between equals() and hashCode()

	Java defines a strict contract that any class must follow if it overrides equals() and hashCode():

		If two objects are equal (equals() returns true), they must have the same hash code.

			Example: a.equals(b) → true ⇒ a.hashCode() == b.hashCode()

			Otherwise, hash-based collections (HashMap, HashSet) will break.

		If two objects are not equal (equals() returns false), their hash codes may still be the same.

			This is called a hash collision. Collections handle this internally using buckets + chaining/trees.

			If you override equals(), you must also override hashCode().

			Failing to do so can cause inconsistent behavior in collections.
	Example:-
			
		import java.util.Objects;

		class Person {
			String name;
			int age;

			Person(String name, int age) {
				this.name = name;
				this.age = age;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (!(o instanceof Person)) return false;
				Person person = (Person) o;
				return age == person.age && name.equals(person.name);
			}

			@Override
			public int hashCode() {
				return Objects.hash(name, age);
			}
		}

		Person p1 = new Person("Alice", 25);
		Person p2 = new Person("Alice", 25);

		System.out.println(p1.equals(p2)); // true
		System.out.println(p1.hashCode() == p2.hashCode()); // true ✅

		HashSet<Person> set = new HashSet<>();
		set.add(p1);
		set.add(p2);

		System.out.println(set.size()); // ✅ 1


🔑 Summary

	Equal objects must have equal hash codes.

	Unequal objects can share hash codes (collisions allowed).

	Always override both equals() and hashCode() together.

	Collections like HashMap, HashSet, and Hashtable rely on this contract.		
	
	
==========================================================================

	
HashMap Design:-

	HashMap<String, Integer> map = new HashMap<>();
		--creates new ObjectArray of size 16 and default load factor will be 0.75
					class Node<K,V> {
						final K key;
						V value;
						final int hash;
						Node<K,V> next;  // linked list chaining
					}
		PUT(K, V):-
			-compute hash of key(int hash = hash(key))
			-find index/bucket(int index = (n - 1) & hash)
			-Traverse bucket:
				If empty:- Insert Entry, increment size;
				If not empty:-
					-compare the hash
						If hash matches:-
							check for equals methods, if matches, just update the value else create new entry and link with existing node and increment the size
						else:
							append new entry and increment the size
			If loadfactor>thershold:-
				resize the object array to double, copy the contents from original to new and change the reference
				
			T.C:-O(logn)
				
		GET(K):-
			find hash of the key{hash=hash(key)}
			find index{int index = (n - 1) & hash}
			check for equals, if matches return associated value else recursive BST iteration
			if index doesnt have any data return null
			T.C:-O(logn)
			
		
		REMOVE(KEY):-
			Internally calls GET API for checking and removes associated key-value(entry)
			If not availble return null or false according to use case
			T.C:-O(2logn)
			update size:- size--
			If collison(Multiple entrys, remove the entry and balancing of binary tree)
			If thershold got reduced restructure the object array
			
		SIZE():-
			Maintain the global variable and return the size;
			
			
			
	Example:- 		
	HashMap<String, String> map = new HashMap<>();
	map.put("A", "Apple");
	map.put("B", "Banana");
	map.get("A");
	map.remove("B");
	map.size();
	
	Step 1: put("A", "Apple")

		hash("A") → say 1001 → index = 1

		table[1] = Entry("A","Apple")

	Step 2: put("B", "Banana")

		hash("B") → say 1100 → index = 4

		table[4] = Entry("B","Banana")

	Step 3: get("A")

		hash("A") → index 1

		table[1] has "A" → return "Apple"

	Step 4: remove("B")

		hash("B") → index 4

		found entry, unlink it → return "Banana"

	Step 5: size()

		Only "A" remains → return 1

			

| Feature                   | HashMap     | LinkedHashMap           | TreeMap        |
| ------------------------- | ----------- | ----------------------- | -------------- |
| **Order**                 | None        | Insertion/Access        | Sorted         |
| **Data Structure**        | Hash Table  | Hash Table + LinkedList | Red-Black Tree |
| **Time Complexity (avg)** | O(1)        | O(1)                    | O(log n)       |
| **Null Keys**             | 1 allowed   | 1 allowed               | ❌ Not allowed  |
| **Use Case**              | Fast lookup | Maintain order          | Sorted map     |

		


==========================================================================================================

ArrayList vs Linked LinkedList

| Feature / Aspect                 | **ArrayList**                                                                        | **LinkedList**                                               |
| -------------------------------- | ------------------------------------------------------------------------------------ | ------------------------------------------------------------ |
| **Underlying Data Structure**    | Dynamic array                                                                        | Doubly Linked List                                           |
| **Memory Usage**                 | Less memory (just stores elements)                                                   | More memory (each node stores data + prev + next references) |
| **Access (get by index)**        | **O(1)** (direct index access)                                                       | **O(n)** (traverses from head/tail)                          |
| **Insertion at End**             | **O(1)** (amortized, but resizing may occur)                                         | **O(1)**                                                     |
| **Insertion at Beginning**       | **O(n)** (shift all elements)                                                        | **O(1)**                                                     |
| **Insertion/Deletion in Middle** | **O(n)** (shift elements)                                                            | **O(n)** (find node, then link/unlink in O(1))               |
| **Iteration**                    | Faster (cache-friendly, contiguous memory)                                           | Slower (pointer chasing in memory)                           |
| **Resizing**                     | Needs resizing (grows by 50% when full)                                              | No resizing (nodes added dynamically)                        |
| **Cache Locality**               | Good (stored contiguously)                                                           | Poor (scattered in memory)                                   |
| **Null Elements**                | Allows multiple nulls                                                                | Allows multiple nulls                                        |
| **Fail-Fast Behavior**           | Yes (throws `ConcurrentModificationException` if modified while iterating)           | Yes (same fail-fast iterator)                                |
| **Thread-Safety**                | Not synchronized (must use `Collections.synchronizedList` or `CopyOnWriteArrayList`) | Not synchronized                                             |
| **Use Cases**                    | Best for random access & read-heavy operations                                       | Best for frequent insertions/deletions, queues, and deques   |
| **Traversal Direction**          | Forward only (via index/iterator)                                                    | Bidirectional (via ListIterator → prev/next links)           |
| **Overhead**                     | Low (only backing array)                                                             | High (extra 2 references per node)                           |




3. Algorithmically – ConcurrentHashMap Operations:-
✅ PUT in ConcurrentHashMap (JDK 8+)

	Compute hash of key → find bucket.

	If bucket is empty, CAS insert new node (lock-free).

	If bucket has nodes:

	Acquire lock on that bucket.

	Traverse linked list (or tree) to insert/update.

	Release lock.

✅ GET in ConcurrentHashMap

	Compute hash → find bucket.

	Traverse list/tree without locking (fields are volatile, so reads are safe).

	Return value (may be slightly stale but safe).

✅ REMOVE in ConcurrentHashMap

	Find bucket by hash.

	Lock that bucket.

	Remove node (unlink from list / tree).

	Release lock.

✅ SIZE in ConcurrentHashMap

	No global lock (unlike old versions).

	Uses counter cells and sum of counts.

	May be approximate in concurrent operations.
	
HashMap vs ConcurrentHashMap
	
| Feature                            | **HashMap**                                                                       | **ConcurrentHashMap**                                                         |
| ---------------------------------- | --------------------------------------------------------------------------------- | ----------------------------------------------------------------------------- |
| **Thread-Safety**                  | ❌ Not thread-safe (must be synchronized externally)                               | ✅ Thread-safe for concurrent access                                           |
| **Null Keys/Values**               | 1 null key, multiple null values allowed                                          | ❌ No null key, ❌ No null values                                               |
| **Performance in Multi-threading** | Poor (needs `Collections.synchronizedMap()` which locks the entire map)           | Much better (fine-grained locking / CAS)                                      |
| **Iteration**                      | Fail-fast (throws `ConcurrentModificationException` if modified during iteration) | Weakly consistent (doesn’t throw exception, reflects modifications partially) |
| **Use Case**                       | Single-threaded applications                                                      | Multi-threaded / concurrent applications                                      |


| Feature              | **ArrayList**                                           | **Vector**                                              | **CopyOnWriteArrayList**                                                      |
| -------------------- | ------------------------------------------------------- | ------------------------------------------------------- | ----------------------------------------------------------------------------- |
| **Thread-Safety**    | ❌ Not synchronized                                      | ✅ Synchronized (legacy)                                 | ✅ Thread-safe using copy-on-write mechanism                                   |
| **Performance**      | ✅ Fast (no sync overhead)                               | ❌ Slower (method-level synchronization)                 | ❌ Slower for write-heavy operations (copying array on every write)            |
| **Default Growth**   | Increases by **50%**                                    | Increases by **100%** (doubles)                         | Increases by **50%** (same as ArrayList internally)                           |
| **Fail-Fast**        | ✅ Iterator is fail-fast                                 | ✅ Iterator is fail-fast (but Enumeration is not)        | ❌ Iterator is **fail-safe** (doesn’t throw `ConcurrentModificationException`) |
| **Read Operations**  | ✅ Fast                                                  | ❌ Slower (sync overhead)                                | ✅ Very fast (reads happen on an immutable snapshot, no locking)               |
| **Write Operations** | ✅ Fast                                                  | ❌ Slower                                                | ❌ Very slow (array copy for every write)                                      |
| **Use Case**         | Best for **non-threaded apps** where performance is key | Rarely used, only when legacy synchronization is needed | Best for **multi-threaded, read-heavy apps** with few writes                  |



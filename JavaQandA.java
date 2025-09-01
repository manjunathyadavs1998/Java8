
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

================================================================================================================

What is a Functional Interface?
	-exactly one abstract method
	-Write clean, concise code using lambdas.
	-To Acheive functional programming
	-Treat functions as first-class citizens (pass them around just like data).
	-Example:-
	Runnable r = () -> System.out.println("Hello from thread");
	new Thread(r).start();
	
	
Built-in Functional Interfaces

	Java provides many ready-to-use functional interfaces in java.util.function package, such as:

		Predicate<T> → returns boolean (used for conditions).
		Function<T, R> → takes input T and returns output R.
		Consumer<T> → takes input T, returns nothing (used for actions).
		Supplier<T> → takes no input, returns T
		
🚀 Java 8 New Features

	1. Lambda Expressions
	2. Functional Interfaces
	3. Streams
	4. Default and static methods in Interface
	5. Method References
	6. Optional Class
	7. New Date & Time API
	8. Collectors API
	9. Nashorn JavaScript Engine
	10. Completable future and Async Programming
===============================================================================================
	
Java 8 introduced CompletableFuture in java.util.concurrent to solve these issues:
✅ Non-blocking async programming
✅ Chaining tasks (thenApply, thenAccept, thenRun)
✅ Combining multiple futures (thenCombine, allOf, anyOf)
✅ Exception handling (exceptionally, handle)

import java.util.concurrent.*;
import java.util.*;

class Order {
    String orderId;
    String items;
    String paymentStatus;
    String shippingStatus;

    public Order(String orderId, String items, String paymentStatus, String shippingStatus) {
        this.orderId = orderId;
        this.items = items;
        this.paymentStatus = paymentStatus;
        this.shippingStatus = shippingStatus;
    }

    @Override
    public String toString() {
        return "OrderId: " + orderId +
               ", Items: " + items +
               ", Payment: " + paymentStatus +
               ", Shipping: " + shippingStatus;
    }
}

public class OrderServiceDemo {

    // Simulating REST calls (with delays)
    static String fetchOrderDetails(String orderId) {
        sleep(500); // Simulate network delay
        return "Book, Laptop";
    }

    static String fetchPaymentStatus(String orderId) {
        sleep(700);
        return "PAID";
    }

    static String fetchShippingStatus(String orderId) {
        sleep(1000);
        return "SHIPPED (ETA 3 days)";
    }

    static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }

    public static void main(String[] args) {
        String orderId = "123";

        // Use CompletableFuture to call services in parallel
        CompletableFuture<String> orderFuture =
                CompletableFuture.supplyAsync(() -> fetchOrderDetails(orderId));

        CompletableFuture<String> paymentFuture =
                CompletableFuture.supplyAsync(() -> fetchPaymentStatus(orderId));

        CompletableFuture<String> shippingFuture =
                CompletableFuture.supplyAsync(() -> fetchShippingStatus(orderId));

        // Combine results when all are done
        CompletableFuture<Order> finalOrder = orderFuture
                .thenCombine(paymentFuture, (items, payment) -> new Order(orderId, items, payment, null))
                .thenCombine(shippingFuture, (partialOrder, shipping) -> {
                    partialOrder.shippingStatus = shipping;
                    return partialOrder;
                });

        // Get result
        Order order = finalOrder.join();
        System.out.println(order);
    }
}

==============================================================

Java Streams:-
	-To acheive functional Programming
	-It was pipeline that flows data through operations.
		A Stream pipeline has 3 stages:
			Source → Collection, Array, I/O, Generator
			Intermediate Operations (transformations) → map(), filter(), sorted(), etc.
			Lazy: They don’t run until a terminal operation is called.
			Terminal Operation (produces result) → collect(), forEach(), reduce(), count().
			
==================================================================================

🔹 1. What is a Sealed Class?
	-A sealed class is a class or interface that restricts which other classes or interfaces can extend or implement it.
	Example:-
	public sealed class Shape permits Circle, Rectangle, Square {
    // common properties
	}
	
	// Only these 3 are allowed
	public sealed abstract class Payment permits CreditCardPayment, UpiPayment, NetBankingPayment {
		public abstract void processPayment(double amount);
	}
	
	public final class CreditCardPayment extends Payment {
		@Override
		public void processPayment(double amount) {
			System.out.println("Processing credit card payment of $" + amount);
		}
	}

	public final class UpiPayment extends Payment {
		@Override
		public void processPayment(double amount) {
			System.out.println("Processing UPI payment of $" + amount);
		}
	}

	public non-sealed class NetBankingPayment extends Payment {
		@Override
		public void processPayment(double amount) {
			System.out.println("Processing NetBanking payment of $" + amount);
		}
	}
	
	public class PaymentProcessor {
		public static void process(Payment payment, double amount) {
			switch (payment) {
				case CreditCardPayment c -> c.processPayment(amount);
				case UpiPayment u -> u.processPayment(amount);
				case NetBankingPayment n -> n.processPayment(amount);
			}
		}

		public static void main(String[] args) {
			Payment p1 = new CreditCardPayment();
			Payment p2 = new UpiPayment();
			Payment p3 = new NetBankingPayment();

			process(p1, 1000);
			process(p2, 500);
			process(p3, 2000);
		}
	}
	
	Sealed Class+Record+Pattern Matching
	
	public sealed interface Payment permits CreditCardPayment, UpiPayment, NetBankingPayment {
		double amount();
	}

	// Each payment type is a record (immutable + compact)
	public record CreditCardPayment(double amount, String cardNumber) implements Payment {}
	public record UpiPayment(double amount, String upiId) implements Payment {}
	public record NetBankingPayment(double amount, String bankName) implements Payment {}
	
	public class PaymentProcessor {

		static String process(Payment payment) {
			return switch (payment) {
				case CreditCardPayment(double amount, String cardNumber) ->
					"CreditCard payment of $" + amount + " using card " + cardNumber;

				case UpiPayment(double amount, String upiId) ->
					"UPI payment of $" + amount + " via " + upiId;

				case NetBankingPayment(double amount, String bankName) ->
					"NetBanking payment of $" + amount + " through " + bankName;
			};
		}

		public static void main(String[] args) {
			Payment p1 = new CreditCardPayment(1500, "1234-5678-9876-5432");
			Payment p2 = new UpiPayment(750, "alice@upi");
			Payment p3 = new NetBankingPayment(5000, "HDFC Bank");

			System.out.println(process(p1));
			System.out.println(process(p2));
			System.out.println(process(p3));
		}
	}
	
==========================================================================================

Fail-Fast Vs Fail Safe:-
	-Structural modification on Collections leads to Concurrent modification Exception
	-Maintains mod count of the Collections, any structure change or increase in the count cause the issues
	Example:-
	public class FailFastSafe {

    public static void main(String[] args) {
        List<Integer> input= new ArrayList<>();
        input.add(10);
        input.add(20);
        input.add(30);
        Iterator<Integer>iterator=input.iterator();
        while (iterator.hasNext()){
            input.add(70);
            Integer next=iterator.next();

        }
    }
}
	
	
Fail Safe DS:- CopyOnWriteArrayList, CopyOnWriteArraySet, ConcurrentHashMap

	Fail-safe iterators avoid CME entirely by iterating over a snapshot (copy) of the collection.
	This means:

	The iterator does not see concurrent modifications in the original collection.

	Instead, it works on a separate snapshot.

	Structural modifications to the original collection don’t affect iteration.
	
	
	import java.util.*;
	import java.util.concurrent.CopyOnWriteArrayList;

	public class FailFastSafe {
		public static void main(String[] args) {
			List<Integer> input = new CopyOnWriteArrayList<>();
			input.add(10);
			input.add(20);
			input.add(30);

			Iterator<Integer> iterator = input.iterator();

			while (iterator.hasNext()) {
				// ✅ Allowed in fail-safe iterators
				input.add(70);
				Integer next = iterator.next();
				System.out.println(next);
			}

			System.out.println("Final List: " + input);
		}
	}
	
	
======================================================================================

Cloneable Interface
	It’s a marker interface (java.lang.Cloneable).
	Has no methods.
	Tells JVM that the class is allowed to be cloned using Object.clone().
	If a class does not implement Cloneable and you call clone(), it throws CloneNotSupportedException
	
	-Shallow Clone:-
	Default behavior of Object.clone().
	Creates a new object but does not recursively clone referenced objects (copies references instead).
	Both objects share the same references inside.
	Example:-
		class Address {
			String city;
			Address(String city) { this.city = city; }
		}

		class Employee implements Cloneable {
			String name;
			Address address;

			Employee(String name, Address address) {
				this.name = name;
				this.address = address;
			}

			@Override
			protected Object clone() throws CloneNotSupportedException {
				return super.clone(); // shallow copy
			}
		}

		public class ShallowCloneExample {
			public static void main(String[] args) throws Exception {
				Address addr = new Address("New York");
				Employee e1 = new Employee("Bob", addr);
				Employee e2 = (Employee) e1.clone();

				e2.address.city = "Chicago";

				System.out.println(e1.address.city); // Chicago ❌ changed in original too
			}
		}
		
	Deep Clone:-
		Not provided by default.
		You must manually override clone() to also clone referenced objects.
		Each object has its own copy of nested objects.
		Example:-

			class  Address implements Cloneable{
				String area;
				String state;
				public Address(String area, String state){
					this.area=area;
					this.state=state;
				}

				@Override
				public String toString() {
					return "Address{" +
							"area='" + area + '\'' +
							", state='" + state + '\'' +
							'}';
				}

				@Override
				public Address clone() {
					try {
						Address clone = (Address) super.clone();
						// TODO: copy mutable state here, so the clone can't change the internals of the original
						return clone;
					} catch (CloneNotSupportedException e) {
						throw new AssertionError();
					}
				}
			}

			class Employee implements Cloneable{

				public Integer id;

				@Override
				public String toString() {
					return "Employee{" +
							"id=" + id +
							", name='" + name + '\'' +
							", address=" + address +
							'}';
				}

				public String name;
				public Address address;

				public Employee(Integer id, String name, Address address) {
					this.id = id;
					this.name = name;
					this.address = address;
				}
				public Employee(){

				}

				@Override
				public Employee clone() {
					try {
						Employee employee=(Employee) super.clone();
						employee.address=(Address) address.clone();
						return  employee;
					} catch (CloneNotSupportedException e) {
						throw new AssertionError();
					}
				}
			}
			public class CloneDemo {

				public static void main(String[] args) {
				Address a1=new Address("abc","New York");
				Employee e1=new Employee(1,"msa63",a1);
				Employee e2=e1.clone();
				e1.id=2;
				a1.area="Los Angels";

					System.out.println(e1);
					System.out.println(e2);
				}
			}

================================================================================================

Threading:-

	-Volatile
		volatile is a keyword used with variables in Java to tell the JVM that:
			The variable is stored in main memory (RAM), not just CPU cache.
			Every read of a volatile variable reads directly from main memory.
			Every write updates main memory immediately.
			So, multiple threads always see the latest updated value.
		Example (Without volatile):-Problem: Thread t1 may never stop, because it might be reading running from CPU cache (stale value), not updated by Thread t2.
			class SharedData {
				boolean running = true;  // not volatile
			}

			public class VolatileDemo {
				public static void main(String[] args) throws InterruptedException {
					SharedData data = new SharedData();

					// Thread 1: keeps running
					Thread t1 = new Thread(() -> {
						while (data.running) {
							// Looping
						}
						System.out.println("Stopped!");
					});
					t1.start();

					Thread.sleep(1000);

					// Thread 2: sets running = false
					data.running = false;
					System.out.println("Updated running to false");
				}
			}
		Example(With Volatile):-Fix with volatile
			class SharedData {
			volatile boolean running = true;  // ensures visibility
			}
		
	-Transient
		transient is used to mark variables that should NOT be serialized.
		When an object is serialized, transient fields are skipped (set to default values like null, 0, or false when deserialized).
		Example:-
			import java.io.*;

			class User implements Serializable {
				String username;
				transient String password;  // will not be saved in serialization

				User(String username, String password) {
					this.username = username;
					this.password = password;
				}
			}

			public class TransientDemo {
				public static void main(String[] args) throws Exception {
					User user = new User("manju", "secret123");

					// Serialize object
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.ser"));
					oos.writeObject(user);
					oos.close();

					// Deserialize object
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.ser"));
					User deserializedUser = (User) ois.readObject();
					ois.close();

					System.out.println("Username: " + deserializedUser.username); // manju
					System.out.println("Password: " + deserializedUser.password); // null (not saved)
				}
			}

| Feature     | `volatile` 🔄                                   | `transient` 🚫                                |
| ----------- | ----------------------------------------------- | --------------------------------------------- |
| Purpose     | Ensure **thread visibility** (multi-threading). | Exclude fields from **serialization**.        |
| Scope       | Multithreading (JVM memory).                    | Serialization (I/O).                          |
| Example Use | Flags, stop signals, config switches.           | Passwords, temporary cache, sensitive data.   |
| Effect      | Forces read/write from main memory.             | Variable not saved when object is serialized. |


Atomic Operations VS Non Atomic Operations:-
	-volatile only ensures visibility, but it does not guarantee atomicity. 
	-Data Corruption due to Race Condition
	Example:-

		class CounterVolatile {
			private volatile int count = 0;

			public void increment() {
				count++;   // NOT atomic
			}

			public void decrement() {
				count--;   // NOT atomic
			}

			public int getCount() {
				return count;
			}
		}

		public class TestVolatile {
			public static void main(String[] args) throws InterruptedException {
				CounterVolatile counter = new CounterVolatile();

				Runnable task1 = () -> {
					for (int i = 0; i < 100000; i++) {
						counter.increment();
					}
				};

				Runnable task2 = () -> {
					for (int i = 0; i < 100000; i++) {
						counter.decrement();
					}
				};

				Thread t1 = new Thread(task1);
				Thread t2 = new Thread(task2);

				t1.start(); t2.start();
				t1.join();  t2.join();

				System.out.println("Final count = " + counter.getCount());
			}
		}
		
	Solution:- AtomicInteger uses low-level atomic operations provided by the CPU via the Compare-And-Swap (CAS) mechanism.

			Key points:

			AtomicInteger stores the value in volatile int → ensures visibility.
			Updates are done using CAS loops:
			Read the current value.
			Compute the new value.
			Attempt to write new value only if old value hasn’t changed.
			If it has changed (another thread updated it), retry.
			This ensures no locks are used, but operations are still atomic.
	
	import java.util.concurrent.atomic.AtomicInteger;

	class CounterAtomic {
		private AtomicInteger count = new AtomicInteger(0);

		public void increment() {
			count.incrementAndGet();   // Atomic
		}

		public int getCount() {
			return count.get();
		}
	}

	public class TestAtomic {
		public static void main(String[] args) throws InterruptedException {
			CounterAtomic counter = new CounterAtomic();

			Runnable task = () -> {
				for (int i = 0; i < 1000; i++) {
					counter.increment();
				}
			};

			Thread t1 = new Thread(task);
			Thread t2 = new Thread(task);

			t1.start(); t2.start();
			t1.join();  t2.join();

			System.out.println("Final count = " + counter.getCount());
		}
	}
	
	| Feature                     | `volatile int`                                          | `AtomicInteger`                        |
| --------------------------- | ------------------------------------------------------- | -------------------------------------- |
| Visibility                  | ✅ Yes (threads see latest value)                        | ✅ Yes                                  |
| Atomicity (safe increments) | ❌ No                                                    | ✅ Yes                                  |
| Performance                 | Faster (simple read/write)                              | Slightly slower (CAS operations)       |
| Use Case                    | Flags, state changes (e.g., `volatile boolean running`) | Counters, accumulators, atomic updates |


🔹 1. Thread Lifecycle States

	A thread in Java can be in one of six states as defined in the Thread.State enum:

		NEW:-Thread object is created but not yet started.
		RUNNABLE:-Thread is eligible to run but not necessarily running immediately.
		RUNNING (Thread is actually executing on CPU,not explicitly in Thread.State, JVM treats RUNNABLE as eligible to run)
		BLOCKED:-Thread is blocked waiting for a monitor lock (synchronized block/method).
		WAITING:-Thread is waiting indefinitely for another thread to notify it.
		TIMED_WAITING:-Thread is waiting for a limited time.
		TERMINATED:-Thread has completed execution.
		
Race Conditions:- Multiple Threads trying to access shared resource and concurrent modification leads to data loss
	Example:-
		class Bank{
			private int bal;
			Bank(int bal){
				this.bal=bal;
			}
			public  void increment(){
				bal++;
			}
			public void decrement(){
				bal--;
			}

			public int getBal(){
				return bal;
			}
		}
		public class RaceConditionDemo {
			public static void main(String[] args) throws InterruptedException {
				Bank b=new Bank(100);
				Runnable r1=()->{
					for(int i=0; i<100000; i++) {
						b.increment();
					}
				};
				Runnable r2=()->{
					for(int i=0; i<100000; i++) b.decrement();
				};
				Thread t1=new Thread(r1);
				Thread t2=new Thread(r2);
				t1.start();
				t2.start();
				t1.join();
				t2.join();
				System.out.println(b.getBal());
			}
		}
	Solution:- Use Syncronized method/block



			class Bank{
				private int bal;
				Bank(int bal){
					this.bal=bal;
				}
				public  synchronized void increment(){
					bal++;
				}
				public synchronized void  decrement(){
					bal--;
				}

				public int getBal(){
					return bal;
				}
			}
			public class RaceConditionDemo {
				public static void main(String[] args) throws InterruptedException {
					Bank b=new Bank(100);
					Runnable r1=()->{
						for(int i=0; i<100000; i++) {
							b.increment();
						}
					};
					Runnable r2=()->{
						for(int i=0; i<100000; i++) b.decrement();
					};
					Thread t1=new Thread(r1);
					Thread t2=new Thread(r2);
					t1.start();
					t2.start();
					t1.join();
					t2.join();
					System.out.println(b.getBal());
				}
			}
			
			
			class Counter {
				private int count = 0;
				private final Object lock = new Object();

				public void increment() {
					synchronized (lock) {  // critical section
						count++;
					}
				}
			}
			
🔹 1. What is ReentrantLock?

	ReentrantLock is a lock implementation in the java.util.concurrent.locks package.

	Key Features:

		Reentrant: The same thread can acquire the lock multiple times without getting blocked.
		Flexible: Provides advanced features like tryLock(), lockInterruptibly(), fairness policy, etc.
		Explicit Locking: Unlike synchronized, you manually acquire and release the lock.
		Always release the lock in finally block to avoid deadlocks.
		Threads trying to acquire a locked ReentrantLock will wait until it’s released.
		
		
| API                                    | Description                                                                     |
| -------------------------------------- | ------------------------------------------------------------------------------- |
| `lock()`                               | Acquires the lock, waits indefinitely if necessary                              |
| `unlock()`                             | Releases the lock                                                               |
| `tryLock()`                            | Attempts to acquire the lock **without blocking**, returns `true` if successful |
| `tryLock(long timeout, TimeUnit unit)` | Attempts to acquire the lock within a **time limit**                            |
| `lockInterruptibly()`                  | Acquires lock but allows **thread interruption**                                |
| `isLocked()`                           | Returns `true` if the lock is held by any thread                                |
| `isHeldByCurrentThread()`              | Returns `true` if the current thread holds the lock                             |
| `getHoldCount()`                       | Number of times the current thread has acquired the lock (reentrant count)      |


Example:-


	import java.util.concurrent.locks.Condition;
	import java.util.concurrent.locks.Lock;
	import java.util.concurrent.locks.ReentrantLock;

	class BankAccount {
		private int balance = 1000;
		Lock lock = new ReentrantLock(true);  // fair lock
		private Condition sufficientFunds = lock.newCondition();

		// Deposit money
		public void deposit(int amount) {
			lock.lock();
			try {
				balance += amount;
				System.out.println(Thread.currentThread().getName() + " deposited: " + amount + " | Balance: " + balance);
				sufficientFunds.signalAll(); // notify waiting threads
			} finally {
				lock.unlock();
			}
		}

		// Withdraw money (wait if insufficient)
		public void withdraw(int amount) throws InterruptedException {
			lock.lock();
			try {
				while (balance < amount) {
					System.out.println(Thread.currentThread().getName() + " waiting to withdraw: " + amount);
					sufficientFunds.await();  // wait for deposit
				}
				balance -= amount;
				System.out.println(Thread.currentThread().getName() + " withdrew: " + amount + " | Balance: " + balance);
			} finally {
				lock.unlock();
			}
		}

		public int getBalance() {
			return balance;
		}
	}

	public class ReentrantLockDemoBank {
		public static void main(String[] args) {
			BankAccount account = new BankAccount();

			Runnable depositor = () -> {
				for (int i = 0; i < 5; i++) {
					account.deposit(500);
					try { Thread.sleep(200); } catch (InterruptedException e) {}
				}
			};

			Runnable withdrawer = () -> {
				for (int i = 0; i < 5; i++) {
					try {
						if (account.lock.tryLock()) {  // try to acquire lock without waiting
							try {
								account.withdraw(700);
							} finally {
								account.lock.unlock();
							}
						} else {
							System.out.println(Thread.currentThread().getName() + " could not acquire lock, skipping withdraw");
						}
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};

			Thread t1 = new Thread(depositor, "Depositor-1");
			Thread t2 = new Thread(withdrawer, "Withdrawer-1");
			Thread t3 = new Thread(withdrawer, "Withdrawer-2");

			t1.start(); t2.start(); t3.start();
		}
	}
	
Deadlock:-
	-two or more threads are blocked forever, each waiting for a resource held by another thread.

	Key condition for deadlock (Coffman conditions):

		Mutual Exclusion: A resource can be held by only one thread at a time.
		Hold and Wait: A thread holding a resource is waiting for another resource.
		No Preemption: A resource cannot be forcibly taken from a thread.
		Circular Wait: Two or more threads form a circular chain of waiting.

		Example:-
			package com.dsa.top.dsa_problems.java.threads;

			class Resource{

				String resource;
				Resource(String s){
					this.resource=s;
				}
			}

			public class DeadLockDemo {
				public static void main(String[] args) {
					Resource r1=new Resource("R1");
					Resource r2=new Resource("R2");

					Runnable run1=()->{
						synchronized (r1){
							System.out.println("Thread 1 calling");
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
							synchronized (r2){
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									throw new RuntimeException(e);
								}
							}
						}
					};


					Runnable run2=()->{
						System.out.println("Thread 2 calling");
						synchronized (r2){
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
							synchronized (r1){
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									throw new RuntimeException(e);
								}
							}
						}
					};

					Thread t1=new Thread(run1);
					Thread t2=new Thread(run2);
					t1.start();
					t2.start();
				}

			}
3. How to Detect Deadlock

	Thread Dumps: Use jstack <pid> or IDE tools to see threads and locks.
	VisualVM / JConsole: Shows blocked threads and resources.
	Code Logging: Log every lock acquisition to detect circular waits.
	
4. How to Solve or Prevent Deadlock

A. Lock Ordering

Always acquire locks in same order in all threads.

	synchronized (r1) {
		synchronized (r2) {
			// safe
		}
	}
B. TryLock with Timeout (ReentrantLock)

	Instead of blocking forever, use tryLock(timeout):

		Lock lock1 = new ReentrantLock();
		Lock lock2 = new ReentrantLock();

		if (lock1.tryLock(1, TimeUnit.SECONDS)) {
			try {
				if (lock2.tryLock(1, TimeUnit.SECONDS)) {
					try { /* critical section */ } 
					finally { lock2.unlock(); }
				}
			} finally {
				lock1.unlock();
			}
		}
C. Deadlock Detection

	Periodically check for threads waiting on each other using ThreadMXBean

D. Use Thread Safe Collections/DataStructures

Callable Interface:-

		import java.util.concurrent.*;
		import java.util.*;

		public class CallableMultiDemo {
			public static void main(String[] args) throws InterruptedException, ExecutionException {
				// Thread pool of size 2
				ExecutorService executor = Executors.newFixedThreadPool(2);

				// List to hold Future objects
				List<Future<Integer>> futures = new ArrayList<>();

				// Submit 10 tasks
				for (int i = 1; i <= 10; i++) {
					int taskId = i;
					Callable<Integer> task = () -> {
						System.out.println("Task " + taskId + " running on: " + Thread.currentThread().getName());
						int sum = 0;
						for (int j = 1; j <= 5; j++) {
							sum += j;
							Thread.sleep(100); // simulate work
						}
						return sum;
					};
					futures.add(executor.submit(task));
				}

				// Get results
				for (int i = 0; i < futures.size(); i++) {
					System.out.println("Result of Task " + (i+1) + " = " + futures.get(i).get());
				}

				executor.shutdown();
			}
		}
		
=======================================================================================================================================

What is Auto-Configuration?

	Auto-Configuration is a Spring Boot feature that automatically configures Spring beans based on:
	Classpath dependencies (e.g., if H2 database jar is on classpath, it configures an in-memory database).
	Beans defined by the developer (custom beans override auto-configured ones).
	Properties in application.properties or application.yml.

Execution Flow Internally
	@SpringBootApplication
	Meta-annotation that combines:
		@SpringBootConfiguration (marks configuration class)
		@EnableAutoConfiguration (triggers auto-configuration)

@ComponentScan (scans beans)
	Spring Boot starts and scans for @SpringBootApplication.
	@EnableAutoConfiguration triggers AutoConfigurationImportSelector.
	AutoConfigurationImportSelector reads all spring.factories entries.
	For each auto-config class:
	Checks conditional annotations (@ConditionalOnClass, etc.).
	Loads the configuration if conditions are satisfied.
	User-defined beans override auto-configured beans if @ConditionalOnMissingBean is used.
	
@Configuration VS @Bean

| Feature                  | `@Configuration`                                           | `@Bean`                                                          |
| ------------------------ | ---------------------------------------------------------- | ---------------------------------------------------------------- |
| **Level**                | Class-level                                                | Method-level                                                     |
| **Purpose**              | Marks a class as **source of bean definitions**            | Declares a **single Spring bean**                                |
| **Scope of use**         | Entire class                                               | Single method / bean                                             |
| **Singleton behavior**   | Ensures **CGLIB proxy** wraps methods to enforce singleton | Singleton enforced **only if called via `@Configuration` class** |
| **Dependency Injection** | Can contain multiple `@Bean` methods with dependencies     | Can inject other beans via method parameters                     |
| **Replaces**             | XML `<beans>` container                                    | XML `<bean>` definition                                          |
| **Multiple beans**       | Can define **many beans**                                  | Defines **one bean per method**                                  |

SpringBootProfiles:-
	A Profile in Spring Boot is a logical grouping of configuration settings.
	It allows you to switch between different configurations for different environments (like dev, test, prod) without changing code.
	Helps in environment-specific beans, properties, and behavior
	
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.context.annotation.Profile;

	@Configuration
	public class AppConfig {

		@Bean
		@Profile("dev")
		public DataSource devDataSource() {
			System.out.println("Dev DataSource created");
			return new HikariDataSource();
		}

		@Bean
		@Profile("prod")
		public DataSource prodDataSource() {
			System.out.println("Prod DataSource created");
			return new HikariDataSource();
		}
	}
	
	
Global Exceptions at Spring Boot:-
	Create Custom Exception Class based on checked/unchecked Exception and define Controller advice class for global exception
	@ControllerAdvice @ExceptionHandler(ResourceNotFoundException.class)
	@ControllerAdvice → Marks class as global exception handler.
	@ExceptionHandler(Exception.class) → Defines which exception this method handles.
	ResponseEntity → Lets you return status + body.
	
		package com.example.demo.exception;

		import org.springframework.http.HttpStatus;
		import org.springframework.http.ResponseEntity;
		import org.springframework.web.bind.annotation.ControllerAdvice;
		import org.springframework.web.bind.annotation.ExceptionHandler;
		import org.springframework.web.context.request.WebRequest;

		// This makes it global
		@ControllerAdvice
		public class GlobalExceptionHandler {

			// Handle a specific exception
			@ExceptionHandler(ResourceNotFoundException.class)
			public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
				return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
			}

		@ExceptionHandler(ResourceNotFoundException.class)
		public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
			ErrorResponse error = new ErrorResponse(
					LocalDateTime.now(),
					ex.getMessage(),
					request.getDescription(false));
			return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
		}

		@ExceptionHandler(Exception.class)
		public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
			ErrorResponse error = new ErrorResponse(
					LocalDateTime.now(),
					"Internal Server Error",
					request.getDescription(false));
			return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		}
		package com.example.demo.exception;

		public class ResourceNotFoundException extends RuntimeException {
			public ResourceNotFoundException(String message) {
				super(message);
			}
		

		}
		
		package com.example.demo.exception;

		import java.time.LocalDateTime;

		public class ErrorResponse {
			private LocalDateTime timestamp;
			private String message;
			private String details;

			public ErrorResponse(LocalDateTime timestamp, String message, String details) {
				this.timestamp = timestamp;
				this.message = message;
				this.details = details;
			}

			// getters & setters
		}


===================================================================================================================

Transaction:-

	-It is an annotation used in Spring to manage transactions declaratively (without manually writing transaction code).
	-A transaction is a unit of work that should be executed completely or not at all (ACID principles).
	-Example: In a banking app, transferring money → debit from A, credit to B. If credit fails, debit must roll back.
	
Here’s the internal flow:

	Proxy Creation

		When Spring sees @Transactional, it creates a proxy for the bean (using AOP).

		Calls to that method go through the proxy, not directly.

		Example:

		BankService proxy = (BankService) ctx.getBean("bankService");
		proxy.transfer(1, 2, 100);  // proxy intercepts this call


	Transaction Interceptor

		The proxy invokes TransactionInterceptor, a Spring class that wraps your method call.

	PlatformTransactionManager

		TransactionInterceptor delegates to a PlatformTransactionManager implementation (like DataSourceTransactionManager, JpaTransactionManager, HibernateTransactionManager).
		This manager starts/commits/rolls back the transaction with the underlying DB (via JDBC or JPA).

	Method Execution

		Spring opens a DB connection, sets autoCommit = false.
		Executes your business logic.
		Commit / Rollback
		If method finishes without exception → commit.
		If exception occurs (default: unchecked RuntimeException or Error) → rollback.

	Client → Proxy → TransactionInterceptor
		   → PlatformTransactionManager
				→ Begin Transaction
				→ Call Target Method
				→ Commit / Rollback
				
				
=============================================================================================================

Ways to Override Default Configurations in Spring Boot:-
	-Using Application.yml or properties
		Example:-
		spring.datasource.url=jdbc:mysql://localhost:3306/testdb
		spring.datasource.username=root
		spring.datasource.password=pass

		# override Hikari default
		spring.datasource.hikari.maximum-pool-size=20
	-Defining Your Own @Bean (Bean Override)
		Example: Override the default ObjectMapper used by Jackson:
		@Configuration
		public class CustomJacksonConfig {

			@Bean
			public ObjectMapper objectMapper() {
				ObjectMapper mapper = new ObjectMapper();
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				return mapper;  // replaces default bean
			}
		}
		
	-Exclude the dependicies of the class:-
		@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
		public class MyApplication { }
	-Conditional Beans
	-Custom Beans and Configurations
	
| Aspect                         | Constructor Injection  | Field Injection      | Setter Injection          |
| ------------------------------ | ---------------------- | -------------------- | ------------------------- |
| **Visibility of dependencies** | Clear (in constructor) | Hidden (not obvious) | Visible (through setters) |
| **Immutability**               | ✅ Yes (with `final`)   | ❌ No                 | ❌ No                      |
| **Optional dependencies**      | ❌ No                   | ❌ No                 | ✅ Yes                     |
| **Testing**                    | ✅ Easy                 | ❌ Harder             | ✅ Easy                    |
| **Spring Recommendation**      | ✅ Preferred            | ❌ Not recommended    | ⚠️ Use when optional      |


============================================================================================================================

Primary VS Qaulifier
	-When you have multiple beans of the same type, Spring doesn’t know which one to inject → it throws NoUniqueBeanDefinitionException
	
		@Component
		class PayPalPaymentService implements PaymentService {}

		@Component
		class StripePaymentService implements PaymentService {}

		@Component
		class OrderService {
			@Autowired
			private PaymentService paymentService; // ❌ Ambiguous – which one?
		}
	1. @Primary

		-Marks a bean as the default choice when multiple candidates are found.
		-Use when one bean should be injected most of the time.
		✔️ Pros: Simple, global default.
		❌ Cons: If you need multiple different injections, you still need @Qualifier.

			@Component
			@Primary
			class PayPalPaymentService implements PaymentService {}

			@Component
			class StripePaymentService implements PaymentService {}

			@Component
			class OrderService {
				@Autowired
				private PaymentService paymentService;  // ✅ Injects PayPalPaymentService
			}
	
	2. @Qualifier

		-Lets you choose exactly which bean should be injected.
		-Use when you have multiple beans of the same type and need to specify explicitly.
		✔️ Pros: Precise control.
		❌ Cons: Slightly more verbose than @Primary.

			@Component("paypalService")
			class PayPalPaymentService implements PaymentService {}

			@Component("stripeService")
			class StripePaymentService implements PaymentService {}

			@Component
			class OrderService {
				private final PaymentService paymentService;

				@Autowired
				public OrderService(@Qualifier("stripeService") PaymentService paymentService) {
					this.paymentService = paymentService; // ✅ Injects StripePaymentService
				}
			}
	
	3. Using Both Together

		-You can mix them:
		-Mark one as default with @Primary.
		-Override it selectively with @Qualifier
		
			@Component
			@Primary
			class PayPalPaymentService implements PaymentService {}

			@Component
			class StripePaymentService implements PaymentService {}

			@Component
			class OrderService {
				@Autowired
				@Qualifier("stripePaymentService")
				private PaymentService paymentService;  // ✅ Stripe overrides PayPal
			}

| Annotation        | HTTP Method                         | Example Usage                                               | Notes                         |
| ----------------- | ----------------------------------- | ----------------------------------------------------------- | ----------------------------- |
| `@RequestMapping` | ALL (GET, POST, PUT, DELETE, PATCH) | `@RequestMapping(value="/users", method=RequestMethod.GET)` | Generic, older, less readable |
| `@GetMapping`     | GET                                 | `@GetMapping("/users")`                                     | Fetch/read data               |
| `@PostMapping`    | POST                                | `@PostMapping("/users")`                                    | Create new resource           |
| `@PutMapping`     | PUT                                 | `@PutMapping("/users/{id}")`                                | Full update (idempotent)      |
| `@DeleteMapping`  | DELETE                              | `@DeleteMapping("/users/{id}")`                             | Delete resource               |


| Feature               | `@PutMapping` (PUT)        | `@PatchMapping` (PATCH)                |
| --------------------- | -------------------------- | -------------------------------------- |
| Purpose               | Replace entire resource    | Update part of a resource              |
| Idempotent?           | ✅ Yes                      | ⚠️ Not guaranteed                      |
| Requires full object? | ✅ Yes                      | ❌ No                                   |
| Common use            | Updating full user profile | Updating only user’s email/phone       |
| Example               | Replace whole `User` JSON  | Update just `"email": "new@email.com"` |


| Feature       | `@PathVariable`                 | `@RequestParam`                          |
| ------------- | ------------------------------- | ---------------------------------------- |
| Location      | URI path segment                | Query string / form data                 |
| Purpose       | Identify a specific resource    | Filter, search, sort, optional params    |
| Required?     | Always (unless marked optional) | Can set `required=false` or defaultValue |
| Example URL   | `/users/5`                      | `/users?page=2&size=20`                  |
| Example Usage | `@PathVariable("id") Long id`   | `@RequestParam("page") int page`         |


1. @PathVariable

		Extracts values from the URI path.
		Used when the value is part of the resource identifier.
		Example (RESTful style):

				@GetMapping("/users/{id}")
				public String getUser(@PathVariable("id") Long userId) {
					return "User ID: " + userId;
				}
				

2. @RequestParam

		Extracts values from the query string or form data.
		Used for filtering, searching, optional params.
		Example:
				@GetMapping("/users")
				public String getUsers(@RequestParam(name = "page", defaultValue = "1") int page,
									   @RequestParam(name = "size", defaultValue = "10") int size) {
					return "Page: " + page + ", Size: " + size;
				}
				
===============================================================================================================

| Feature     | `@ExceptionHandler`                 | `@ControllerAdvice`                               |
| ----------- | ----------------------------------- | ------------------------------------------------- |
| Scope       | Specific to one controller          | Global (all controllers)                          |
| Placement   | Inside a controller class           | Separate class (usually with `@ControllerAdvice`) |
| Reusability | Not reusable                        | Centralized & reusable                            |
| Best for    | Handling exceptions in one endpoint | Handling exceptions app-wide                      |


@Async

	Used to run methods asynchronously in a separate thread.
	The caller does not block/wait for the method to finish.
	Requires @EnableAsync in the config class.
	Runs using a ThreadPoolTaskExecutor (can be customized).
	👉 The request returns immediately ✅, while email sending happens in background.
	
			@Configuration
			@EnableAsync
			public class AsyncConfig {
			}

			@Service
			public class NotificationService {

				@Async
				public void sendEmail(String user) {
					System.out.println("Sending email to " + user + " - " + Thread.currentThread().getName());
					try {
						Thread.sleep(3000); // simulate delay
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("Email sent to " + user);
				}
			}
			
			@RestController
			@RequestMapping("/users")
			public class UserController {
				private final NotificationService notificationService;

				public UserController(NotificationService notificationService) {
					this.notificationService = notificationService;
				}

				@PostMapping("/{user}/notify")
				public String notifyUser(@PathVariable String user) {
					notificationService.sendEmail(user); // Runs async
					return "Notification triggered for " + user;
				}
			}
			
🔹 @Scheduled

	-Used to schedule tasks at fixed intervals, delays, or cron expressions.
	-Requires @EnableScheduling in the config class.
	-Useful for cron jobs, cleanup tasks, periodic monitoring, etc.
	-👉 Spring will automatically trigger these jobs without manual calls.
	

		@Configuration
		@EnableScheduling
		public class SchedulerConfig {
		}

		@Component
		public class ReportScheduler {

			// Run every 5 seconds
			@Scheduled(fixedRate = 5000)
			public void generateReport() {
				System.out.println("Report generated at " + new Date());
			}

			// Run 10 seconds after the last execution finishes
			@Scheduled(fixedDelay = 10000)
			public void cleanup() {
				System.out.println("Cleanup job finished at " + new Date());
			}

			// Run using cron (every day at midnight)
			@Scheduled(cron = "0 0 0 * * ?")
			public void dailyTask() {
				System.out.println("Daily job executed at " + new Date());
			}
		}
		
| Feature          | `@Async`                                                          | `@Scheduled`                                      |
| ---------------- | ----------------------------------------------------------------- | ------------------------------------------------- |
| Purpose          | Run methods asynchronously in background                          | Run methods periodically on schedule              |
| Trigger          | Called by your code (non-blocking)                                | Automatically triggered by Spring                 |
| Requires         | `@EnableAsync`                                                    | `@EnableScheduling`                               |
| Threading        | Uses async executor thread pool                                   | Runs on Spring’s scheduled task executor          |
| Example Use Case | Sending emails, processing payments, calling APIs without waiting | Batch jobs, cleanup tasks, periodic health checks |

=============================================================================

@Cacheable

	-Used to cache the result of a method.
	-Next time the method is called with the same parameters, Spring will return the cached value instead of running the method again.
	-Great for performance optimization (e.g., DB queries, API calls).

		@Service
		public class ProductService {

			// Caches result of method with key = productId
			@Cacheable(value = "products", key = "#productId")
			public Product getProductById(String productId) {
				System.out.println("Fetching product from DB: " + productId);
				return new Product(productId, "Laptop", 75000);
			}
		}

	Execution Flow:

		-First call → method executes, result cached.
		-Second call with same ID → method skipped, cached result returned.
		
@CacheEvict

	Used to remove entries from cache (to keep it fresh).
	Commonly used after update/delete operations, so old cached data doesn’t stay around.

			@Service
			public class ProductService {

				@CacheEvict(value = "products", key = "#productId")
				public void deleteProduct(String productId) {
					System.out.println("Deleting product from DB: " + productId);
					// delete from DB logic
				}

				@CacheEvict(value = "products", allEntries = true)
				public void clearCache() {
					System.out.println("Clearing all product cache");
				}
			}
| Feature    | `@Cacheable`                        | `@CacheEvict`                    |
| ---------- | ----------------------------------- | -------------------------------- |
| Purpose    | Store method result in cache        | Remove entries from cache        |
| Trigger    | When method is called               | After method execution           |
| Common Use | Expensive queries, repeated lookups | Updates, deletes, cache refresh  |
| Key        | Needs `key` expression              | Needs `key` or `allEntries=true` |


====================================================================================================================


| Feature            | JPA                                                        | Hibernate                                                                                                  |
| ------------------ | ---------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| **Type**           | Specification / API                                        | Framework / Implementation                                                                                 |
| **Provides**       | Interfaces, annotations (`@Entity`, `@Id`, `@OneToMany`)   | Actual implementation of JPA + extra features                                                              |
| **SQL Generation** | Defines that ORM should generate SQL                       | Actually generates SQL (e.g., `INSERT`, `SELECT`)                                                          |
| **Vendor**         | Part of Java EE / Jakarta EE                               | Open-source project (by Red Hat)                                                                           |
| **Support**        | Can use any JPA provider (Hibernate, EclipseLink, OpenJPA) | Hibernate can work with JPA or its own proprietary API                                                     |
| **Extra Features** | None (only defines standard)                               | Yes: caching (`@Cache`), batch fetching, criteria API, lazy loading tuning, HQL (Hibernate Query Language) |


PERSIST VS MERGE

| Feature                   | `persist()`                           | `merge()`                                                   |
| ------------------------- | ------------------------------------- | ----------------------------------------------------------- |
| **Entity State**          | Works on **new (transient)** entities | Works on **detached** (and transient too)                   |
| **Return Value**          | `void`                                | Returns a **managed copy**                                  |
| **Managed Object**        | The passed entity becomes managed     | A **new instance** is managed; passed entity stays detached |
| **SQL Triggered**         | Always `INSERT`                       | `INSERT` if new, `UPDATE` if existing                       |
| **Duplicate ID Handling** | Throws `EntityExistsException`        | Updates existing record instead of exception                |
| **Use Case**              | When creating **new records**         | When updating or re-attaching **detached objects**          |


============================================================================================================================================

@OneToOne (One-to-One)

	👉 One row in Table A corresponds to exactly one row in Table B.
		Example: User ↔ Profile

			@Entity
			class User {
				@Id @GeneratedValue
				private Long id;
				private String name;

				@OneToOne(cascade = CascadeType.ALL)
				@JoinColumn(name = "profile_id")  // foreign key in User table
				private Profile profile;
			}

			@Entity
			class Profile {
				@Id @GeneratedValue
				private Long id;
				private String bio;
			}


			| id | name  | profile\_id (FK) |
			| -- | ----- | ---------------- |
			| 1  | Alice | 101              |
			

			| id  | bio            |
			| --- | -------------- |
			| 101 | "Software Dev" |
			
2. @OneToMany and @ManyToOne

	👉 Classic Parent ↔ Children relationship.
	@OneToMany = parent holds a list of children.
	@ManyToOne = child points to parent.

	Example: Department ↔ Employee
		@Entity
		class Department {
			@Id @GeneratedValue
			private Long id;
			private String name;

			@OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
			private List<Employee> employees = new ArrayList<>();
		}

		@Entity
		class Employee {
			@Id @GeneratedValue
			private Long id;
			private String name;

			@ManyToOne
			@JoinColumn(name = "department_id")  // FK in Employee table
			private Department department;
		}

		| id | name |
		| -- | ---- |
		| 1  | IT   |
		
		| id  | name | department\_id (FK) |
		| --- | ---- | ------------------- |
		| 101 | John | 1                   |
		| 102 | Mary | 1                   |
		
@ManyToMany

	👉 Many records in A relate to many records in B.
	Hibernate creates a join table.
	
		@Entity
		class Student {
			@Id @GeneratedValue
			private Long id;
			private String name;

			@ManyToMany
			@JoinTable(
				name = "student_course",
				joinColumns = @JoinColumn(name = "student_id"),
				inverseJoinColumns = @JoinColumn(name = "course_id")
			)
			private List<Course> courses = new ArrayList<>();
		}

		@Entity
		class Course {
			@Id @GeneratedValue
			private Long id;
			private String title;

			@ManyToMany(mappedBy = "courses")
			private List<Student> students = new ArrayList<>();
		}
		
		| id | name |
		| -- | ---- |
		| 1  | Alex |
		| 2  | Bob  |


		| id  | title   |
		| --- | ------- |
		| 101 | Math    |
		| 102 | Science |


		| student\_id | course\_id |
		| ----------- | ---------- |
		| 1           | 101        |
		| 1           | 102        |
		| 2           | 101        |


		| Relationship                | Example                | DB Mapping               |
		| --------------------------- | ---------------------- | ------------------------ |
		| `@OneToOne`                 | User ↔ Profile         | Foreign Key in one table |
		| `@OneToMany` + `@ManyToOne` | Department ↔ Employees | FK in child table        |
		| `@ManyToMany`               | Student ↔ Courses      | Separate Join Table      |
		
=============================================================================================================
EAGER VS LAZYLOADING

| Feature              | Lazy Loading                              | Eager Loading                                |
| -------------------- | ----------------------------------------- | -------------------------------------------- |
| **When data loads?** | Only when accessed                        | Immediately with parent                      |
| **Default for**      | Collections (`@OneToMany`, `@ManyToMany`) | Single relations (`@ManyToOne`, `@OneToOne`) |
| **Performance**      | Efficient if association not always used  | Might fetch unnecessary data                 |
| **Queries executed** | Multiple (N+1 risk if not handled)        | Usually one (JOIN), but heavier              |
| **Exception Risk**   | LazyInitializationException               | None                                         |

@ManyToOne(fetch = FetchType.LAZY)  // override EAGER to LAZY
@OneToMany(fetch = FetchType.EAGER) // override LAZY to EAGER

| Association Type | Default Fetching |
| ---------------- | ---------------- |
| `@ManyToOne`     | EAGER            |
| `@OneToOne`      | EAGER            |
| `@OneToMany`     | LAZY             |
| `@ManyToMany`    | LAZY             |


Dyanmic Query for lazy/eager fetching based on client request

==================================================================================================================================

JVM Internals:-

	JVM is an abstract machine that provides a runtime environment for executing Java bytecode.
	When you run a .java file → compiled into .class (bytecode) → JVM executes it.

		It mainly consists of:
		Class Loader Subsystem
		Runtime Data Areas (Memory model)
		Execution Engine
		Native Interface (JNI)
		Native Method Libraries

		Class Loader Subsystem

			Responsible for loading .class files into JVM memory when first referenced.
			Steps:

				Loading → .class bytecode is loaded.
				Linking
				Verification (bytecode validity & security checks)
				Preparation (allocate memory for class variables, default values)
				Resolution (replace symbolic references with actual references)
				Initialization → static blocks and static variables are initialized.
				Types of Class Loaders (follow Parent Delegation Model):
					Bootstrap ClassLoader → loads core Java (rt.jar, java.base).
					Extension ClassLoader → loads jre/lib/ext or java.ext.dirs.
					Application ClassLoader → loads from classpath.
					Custom ClassLoaders → created by developers.
					
	
	Runtime Data Areas (Memory Model)

		This is JVM Memory Layout.
		When JVM starts, it divides memory into different areas:
		Method Area (a.k.a Metaspace in Java 8+)

				Stores class structure (metadata), static variables, constants, method bytecode.
				One per JVM.
				Before Java 8 → part of PermGen. After Java 8 → Metaspace (uses native memory).
				Heap Area
				Stores objects and instance variables.
				Shared across threads.
				Managed by Garbage Collector (GC).
				Divided into:
					Young Generation (Eden + Survivor spaces)
					Old Generation (Tenured)

		Stack Area

			Stores stack frames for each thread.
			Each frame has:
			Local variables
			Operand stack

		Frame data (method return, exception handling)

			Each thread has its own stack.

		PC Register (Program Counter)
			Each thread has one.
			Stores address of current executing JVM instruction.
			Native Method Stack
			Stores native (C/C++) method calls.


| Reference Type | Accessible (`get()`)     | GC Behavior                              | Use Case                   |
| -------------- | ------------------------ | ---------------------------------------- | -------------------------- |
| **Strong**     | ✅ Yes                    | Not eligible until reference is gone     | Normal object usage        |
| **Soft**       | ✅ Yes (until low memory) | Collected only when JVM **needs memory** | Memory-sensitive caching   |
| **Weak**       | ✅ Yes (until GC runs)    | Collected eagerly once no strong refs    | WeakHashMap, caches        |
| **Phantom**    | ❌ Always null            | Collected but tracked via ReferenceQueue | Cleanup after finalization |


| Property                  | Volatile Variable | Non-Volatile Variable |
| ------------------------- | ----------------- | --------------------- |
| Can be read/written       | ✅ Yes             | ✅ Yes                 |
| Visibility across threads | ✅ Guaranteed      | ❌ Not guaranteed      |
| Atomicity of operations   | ❌ Not guaranteed  | ❌ Not guaranteed      |


| Aspect     | Atomicity                          | Thread Safety                                         |
| ---------- | ---------------------------------- | ----------------------------------------------------- |
| Definition | Operation is indivisible           | Class or method works correctly with multiple threads |
| Scope      | Single operation                   | Can include multiple operations or methods            |
| Guarantees | No interference for that operation | Correct results even with concurrency                 |
| Example    | `AtomicInteger.incrementAndGet()`  | `Collections.synchronizedList(list)`                  |


| Aspect     | Busy-Waiting                | Non-Blocking                                | Blocking                     |
| ---------- | --------------------------- | ------------------------------------------- | ---------------------------- |
| CPU Usage  | High (spins)                | Moderate (retries, spin lightly)            | Low (thread sleeps/waits)    |
| Latency    | Low (immediate detection)   | Low-medium                                  | Medium-high                  |
| Complexity | Simple                      | Medium (atomic ops, CAS)                    | Simple with standard APIs    |
| Use Case   | Very short waits, spinlocks | Lock-free data structures, high concurrency | Producer-consumer, I/O waits |


| Feature                  | **JDK Proxy**             | **CGLIB Proxy**                    |
| ------------------------ | ------------------------- | ---------------------------------- |
| Works with               | Interfaces only           | Concrete classes (via subclassing) |
| Library dependency       | Built-in (JDK)            | External (CGLIB, ASM)              |
| Performance              | Slightly faster (lighter) | Slightly slower (bytecode gen)     |
| Can proxy final classes? | ❌ No                      | ❌ No                               |
| Can proxy final methods? | ✅ Yes (if in interface)   | ❌ No (subclass can’t override)     |


| Feature     | Optimistic Locking | Pessimistic Locking         |
| ----------- | ------------------ | --------------------------- |
| Approach    | Assume no conflict | Assume conflict will happen |
| Mechanism   | Version check      | Database locks (row/table)  |
| Concurrency | High               | Low (more blocking)         |
| Risk        | Retry failures     | Deadlocks, waits            |
| Best for    | Rare conflicts     | Frequent conflicts          |


| Aspect          | Integer PK     | String PK                             |
| --------------- | -------------- | ------------------------------------- |
| Storage per key | 4–8 bytes      | Variable (10–100+ bytes)              |
| Comparisons     | O(1) (CPU op)  | O(n) (char by char)                   |
| Node capacity   | High (compact) | Lower (fewer per node)                |
| Index depth     | Shallow (fast) | Deeper (slower)                       |
| Best for        | Surrogate keys | Natural keys (like email, UUID, etc.) |













	



		






	
	
	
	






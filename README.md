# @BodyParam
Adds support for @BodyParam annotation within Spring Boot web controllers to allow controller methods to use request body values directly as method parameters.

```json
{
    "exampleField":"value"
}
```

```java
@PostMapping("/create")
void testMethod(@BodyParam(path = "exampleField") String value) {
	System.out.println("value = " + value);
}
```

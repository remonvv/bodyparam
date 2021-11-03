# @BodyParam
Adds support for @BodyParam annotation within Spring Boot web controllers to allow controller methods to use request body values directly as method parameters.  

Currently the request body parser supports the following HTTP media types :  
* application\json
* application\xml

## Examples
*JSON*
```json
{
    "exampleString":"value"
    "exampleNumber":1
}
```


*Java Code*
```java
@PostMapping("/create")
void testMethod(
	@BodyParam(path = "exampleString") String stringValue,
	@BodyParam(path = "exampleNumber") int integerValue
	) {
	System.out.println("string = " + stringValue);
	System.out.println("number = " + integerValue);
}
```

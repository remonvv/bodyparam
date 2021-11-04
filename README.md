![Build](https://github.com/remonvv/bodyparam/actions/workflows/maven.yml/badge.svg)
![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.remonvv/bodyparam/badge.svg)

# @BodyParam
Adds support for `@BodyParam` annotation within Spring Boot web controllers to allow controller methods to use request body values directly as method parameter values without having to define a request body DTO class.  

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
*XML*
```xml
<root><!--Arbitrary name for root node-->
    <exampleString>value</exampleString>
    <exampleNumber>1</exampleNumber>
</root>
```

*Java Code*
```java
@PostMapping
void testMethod(
	@BodyParam(path = "exampleString") String stringValue,
	@BodyParam(path = "exampleNumber") int integerValue) {
	System.out.println("string = " + stringValue);
	System.out.println("number = " + integerValue);
}
```
*JSON*
```json
{
    "parent": {
    	"child":"value"
    }
}
```


*Java Code*
```java
@PostMapping
void testMethod(@BodyParam(path = "parent.child") String value) {
	System.out.println("string = " + value);
}
```
*JSON*
```json
{
    "requiredValue": "required"
}
```


*Java Code*
```java
@PostMapping
void testMethod(
	@BodyParam(path = "requiredValue") String requiredValue,
	@BodyParam(path = "optionalValue", required = false) String optionalValue) {
	System.out.println("value = " + value);
	System.out.println("optional value provided? = " + (optionalValue != null));
}
```
*JSON*
```json
{
    "optionalValue": "required"
}
```


*Java Code*
```java
@PostMapping
void testMethod(@BodyParam(path = "optionalValue", defaultValue = "default") String optionalValue) {
	System.out.println("optional value = " + optionalValue);
}
```

If the code is compiled with the javac parameter `-parameters` the compiler will add method parameter names to the class files. In that case `@BodyParam` is able to use that parameter name as the default path for `@BodyParam` annotation method parameters. This allows for the following simplification :  
*JSON*
```json
{
    "field":"value"
}
```

Note below that no explicit path is defined. The parameter name resolver will attempt to get the parameter name (`field` in this example) directly from the compiled class file and use that name to search for a value in the request body.
*Java Code*
```java
@PostMapping
void testMethod(
	@BodyParam String field) {
	System.out.println("field value = " + field);
}
```
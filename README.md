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
@PostMapping("/create")
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
@PostMapping("/create")
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
@PostMapping("/create")
void testMethod(@BodyParam(path = "optionalValue", defaultValue = "default") String optionalValue) {
	System.out.println("optional value = " + optionalValue);
}
```

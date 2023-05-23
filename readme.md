# What is this project
This is a project written in Java for a Senior Software Engineer position coding assessment. It is responsible for taking XML, applying transformations and outputting data in JSON. This project was written in around 3-4 hours.

#### Key concepts
- Recursion
- Tree traversal
- Configuration based coding
- Unit testing
- Documentation and code clarity

## Instructions given for the task:
Please write a Java class to convert the XML input file to the JSON output file (below).  You are welcome to use any existing libraries. However, you should write generalized code supported by a mapping file (think JSON or XML, but you’re not constrained to those file types). This means that there should be no hardcoded references to either field names or field values in your Java code. The end goal would be a class that can convert any structure of XML to JSON given the proper mapping file. This will include processes to change field names and field values.
Please email us your Java project with sample files in one zip file. You do not have to include the external JAR files.

### XML input file:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<patients>
  <patient>
       <id>1234</id>
       <gender>m</gender>
       <name>John Smith</name>
       <state>Michigan</state>
       <dateOfBirth>03/04/1962</dateOfBirth >
  </patient>
  <patient>
       <id>5678</id>
       <gender>f</gender>
       <name>Jane Smith</name>
       <state>Ohio</state>
       <dateOfBirth>08/24/1971</dateOfBirth>
  </patient>
</patients>
```

JSON output file:
```json
[
       {
              "patientid": 1234,
              "sex": "male",
              "state": "MI",
              "name": "John Smith",
              "age": 55
       },
       {
              "patientid": 5678,
              "sex": "female",
              "state": "OH",
              "name": "Jane Smith",
              "age": 45
       }
]
 ```


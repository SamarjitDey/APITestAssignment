# APITestAssignment
API Test Assignment using Groovy

This is an assignment from Assurity Consulting to create an automated test with the listed acceptance criteria:
API = https://api.tmsandbox.co.nz/v1/Categories/6327/Details.json?catalogue=false

Acceptance Criteria:

	* Name = "Carbon credits"
	* CanRelist = true
	* The Promotions element with Name = "Gallery" has a Description that contains the text "2x larger image" 

## Getting Started

These instructions will get you a copy of the project on the sample API automation framework up and running on your local machine (windows 7 or 10) for development and testing purposes. 


### Prerequisites

* Local machine (32 bit) running in Windows 7 or 10
* run "java -version" from command line and check java version. it should be - jdk 1.8. 
* If jdk 1.8 is not installed, download it from https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html and run it to installed
* Select Start -> Computer -> System Properties -> Advanced system settings -> Environment Variables -> System variables -> Click on New -> Enter JAVA_HOME in Variable_name -> Enter path (C:\Program Files (x86)\Java\jdk1.8.0_201) for jdk1.8 in Variable_value -> Ok
* download groovy-2.5.6 (zip file) from https://dl.bintray.com/groovy/maven/apache-groovy-sdk-2.5.6.zip and place it in some location
* copy the path for groovy-2.5.6 from local machine and navigate to Start -> Computer -> System Properties -> Advanced system settings -> Environment Variables -> System variables -> Click on New -> Enter GROOVY_HOME in Variable_name -> Enter copied path for groovy-2.5.6 in Variable_value -> Ok
* In system environment variables, prepend %GROOVY_HOME%\bin;%JAVA_HOME%\bin in the PATH variable
* run "groovy -v" from command line and check if "Groovy Version: 2.5.6 JVM: 1.8.0_201 Vendor: Oracle Corporation OS: Windows 7 or 10" is reported 
* Create profile in github repository using the URL: https://github.com/join
* Open the link - and download a copy of the project in your local machine
* Any browser (preferrably IE or Chrome) should be installed and default browser should be set

## Running the tests

* Navigate to the path where APITest has been downloaded.
* Go to address bar and type 'cmd' and command prompt should launch
* Type "groovy APITest.groovy" in the command line and hit enter
* Test should run and report should automatically generate and launch

### Framework folder structure


* Input data is maintained in TestData folder. The values have been taken from assignment for creating automation test with the given acceptance criteria data. 
```
API = https://api.tmsandbox.co.nz/v1/Categories/6327/Details.json?catalogue=false
Name = "Carbon credits"
CanRelist = true
Promotions.Name = "Gallery" and Promotions.Description contains "2x larger image"
```
* Please note that the data combination below (except the API at the top) doesn't need to follow the sequence and can be altered to add or delete more data. The program can handle any number of inputs that can be validated with respect to the API, except for the input which has two combinations with parent.child node relationship. For such inputs, one has to follow the pattern as shown below - [parent.child equals "some value" and parent.child conteins "some value" or vice-versa]
```
A sample scenario could be:
API = https://api.tmsandbox.co.nz/v1/Categories/6327/Details.json?catalogue=false
DefaultDuration = "Carbon credits"
CanListClassifieds = true
Promotions.Description contains "Lowest position" and Promotions.Name = "Basic"
```

### And coding style tests

Coding has been done using groovy language and most use of dynamic typing, try-catch blocks, maps, lists, conditional statements using ternary operators. Thus making maximum use of groovy libraries.

```
sourceValA = sourceValA.matches("\".*\"") ? sourceValA.replaceAll("\"", "") : sourceValA
```

## Built With

* [Groovy Console](https://dl.bintray.com/groovy/maven/apache-groovy-sdk-2.5.6.zip)

## Versioning

For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

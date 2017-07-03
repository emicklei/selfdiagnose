### 2.11.0
- OGNL and Javassist are now shaded into selfdiagnose. This solves dependency conflict issues.
### 2.10.1
- Read version from pom.properties
- fix problem with override task config
### 2.10.0
- Prevent `java.lang.OutOfMemoryError: unable to create new native thread` that might happen if diagnostic tasks do not complete within a timeout period
### 2.9.0
- adds "checks" and "failures" attributes to JSON and XML reporter, just like HTML
### 2.8.8
- replaced usage of reflection method with a cast to the correct type in ReportMavenPOMProperties
### 2.8.7
- changed msg of environment property checker
### 2.8.6
- fixed since
### 2.8.5
- add remoteConfigurationAllowed = false 
- no duplicate content-type header fix (by soudmaijer)
### 2.8.2
- fixed a security issue related to OGNL expression usage (https://www.intelligentexploit.com/view-details.html?id=17155)
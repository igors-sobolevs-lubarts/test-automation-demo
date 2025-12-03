# Test Automation demo (TestNG + Allure + Docker)
This demo is a proof-of-concept for a test automation framework that uses Maven, TestNG, Allure, and Docker.
## Requirements
- Docker (for running tests inside containers)
- Java 17 + Maven (for running the test suite locally)
- Allure CLI (optional - only needed if you want to generate reports locally)
- Google Chrome (for running tests inside containers - latest or compatible version)
- ChromeDriver (for running tests inside containers - matching your installed Chrome version)


## Environment variables (required)
- `API_BASE_URL` - e.g. https://restful-booker.herokuapp.com
- `WEB_BASE_URL` - e.g. https://www.google.com
- `BROWSER` - e.g. chrome

## Local (Maven) run
```bash
export API_BASE_URL="https://restful-booker.herokuapp.com"
export WEB_BASE_URL="https://www.google.com"
export BROWSER="chrome"

# run tests:
mvn test -Dsurefire.suiteXmlFiles=testng.xml
# generate report:
allure generate /allure-results --clean -o /allure-report
```
## Run using Docker (without installing maven, etc. locally) - takes longer but makes the demo portable
Build Docker
```bash
docker build -t test-automation-demo -f docker/Dockerfile .
```
Execute tests and build the report
```bash
docker run --rm -v ${PWD}/allure-results:/app/allure-results -v ${PWD}/allure-report:/app/allure-report test-automation-demo
```
Build a separate docker container to view the report
```bash
docker build -t allure-report-server -f docker/Dockerfile_report .
```
Run the report server and make it available on http://localhost:8080
```bash
docker run --rm -p 8080:8080 -v ${PWD}/allure-report:/report allure-report-server
```
to stop the server, press `Ctrl+C` in the same terminal where you ran the docker container.
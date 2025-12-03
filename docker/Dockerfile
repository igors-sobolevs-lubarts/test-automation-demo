# ==========================
# 1. Build Stage
# ==========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:resolve dependency:resolve-plugins
COPY src ./src
RUN mvn -B clean package -DskipTests=true

# ==========================
# 2. Runtime Stage
# ==========================
FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# Copy full project so Maven can run tests
COPY pom.xml .
COPY src ./src
COPY testng.xml .

# Install tools + Chrome + ChromeDriver
RUN apt-get update && apt-get install -y wget unzip gnupg apt-transport-https \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list' \
    && apt-get update && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

RUN CHROME_MAJOR=$(google-chrome --version | grep -oP '\d+' | head -1) && \
    DRIVER_VERSION=$(wget -qO- "https://googlechromelabs.github.io/chrome-for-testing/LATEST_RELEASE_${CHROME_MAJOR}") && \
    wget -q "https://storage.googleapis.com/chrome-for-testing-public/${DRIVER_VERSION}/linux64/chromedriver-linux64.zip" && \
    unzip chromedriver-linux64.zip && \
    mv chromedriver-linux64/chromedriver /usr/local/bin/chromedriver && \
    chmod +x /usr/local/bin/chromedriver && \
    rm -rf chromedriver-linux64*

# Install Allure CLI
ENV ALLURE_VERSION="2.29.0"
RUN wget -q "https://github.com/allure-framework/allure2/releases/download/${ALLURE_VERSION}/allure-${ALLURE_VERSION}.zip" && \
    unzip allure-${ALLURE_VERSION}.zip -d /opt/allure && \
    ln -s /opt/allure/allure-${ALLURE_VERSION}/bin/allure /usr/bin/allure && \
    rm allure-${ALLURE_VERSION}.zip

# Env variables
ENV WEB_BASE_URL="https://www.google.com"
ENV API_BASE_URL="https://restful-booker.herokuapp.com"
ENV BROWSER="chrome"

# Volumes for Allure
VOLUME ["/app/allure-results"]
VOLUME ["/app/allure-report"]

# Run TestNG via Maven and generate Allure report
ENTRYPOINT ["/bin/bash", "-c", "\
  mvn test -Dsurefire.suiteXmlFiles=testng.xml ; \
  echo 'Generating Allure report...' && \
  allure generate /app/allure-results --clean -o /app/allure-report && \
  echo 'Allure report available in /app/allure-report' && \
  echo 'Cleaning allure-results...' && \
  rm -rf /app/allure-results/* && \
  echo 'allure-results cleared' \
"]


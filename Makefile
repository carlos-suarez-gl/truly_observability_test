# Must have `sentry-cli` installed globally
# Following variable must be passed in
#  SENTRY_AUTH_TOKEN
SENTRY_ORG=will-captel
SENTRY_PROJECT=java-1
SENTRY_RELEASE=`sentry-cli releases propose-version`
ENVIRONMENT=test

deploy: setup_release run_jar

setup_release: create_release # associate_commits

create_release:
	sentry-cli releases -o $(SENTRY_ORG) new -p $(SENTRY_PROJECT) $(SENTRY_RELEASE)

associate_commits:
	sentry-cli releases -o $(SENTRY_ORG) -p $(SENTRY_PROJECT) set-commits --auto $(SENTRY_RELEASE)

run_jar:
	mvn clean package && \
	 java -Dsentry.release=$(SENTRY_RELEASE) -Dsentry.environment=$(ENVIRONMENT) -jar target/example-0.0.1-SNAPSHOT.jar

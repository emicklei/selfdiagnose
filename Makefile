dist:
	mvn source:jar javadoc:jar repository:bundle-create

build:
	mvn clean install

eclipse:
	mvn clean eclipse:eclipse
JAVAC = javac
JAVA = java

SOURCES = $(wildcard *.java)
CLASSES = $(SOURCES:.java=.class)

.PHONY: all run clean store jar latex

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $< 

run: all
	$(JAVA) Main

clean:
	rm -f *.class *.zip *.jar

store: 
	$(JAVA) Main > ExperimentStorage.txt

jar: all
	jar cfe Prac2.jar Main *.class

rj: Prac2.jar	
	java -jar Prac2.jar

pdf: report.tex

sources:
	zip sourceFiles.zip *.java *.tex *.aux *.log

list:
	unzip -l sourceFiles.zip

unzip:
	unzip sourceFiles.zip

submission: report.pdf sourceFiles.zip Prac2.jar
	zip u21516261_u24594522_u23588579.zip sourceFiles.zip Prac2.jar report.pdf



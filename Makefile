JAVAC = javac
JAVA = java

SOURCES = Map.java Experiment.java algorithmn.java
CLASSES = $(SOURCES:.java=.class)

.PHONY: all run clean store jar latex

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $<

run: all
	$(JAVA) Experiment

clean:
	rm -f *.class *.zip

store: 
	$(JAVA) Experiment > ExperimentStorage.txt

jar: all
	jar cfe Experiment.jar Experiment *.class

rj: Experiment.jar	
	java -jar Experiment.jar

pdf: report.tex
	pdflatex report.tex

sources:
	zip sourceFiles.zip *.java *.tex *.aux *.log

list:
	unzip -l sourceFiles.zip

unzip:
	unzip sourceFiles.zip

submission: report.pdf sourceFiles.zip Experiment.jar
	zip u21516261_u24594522_u23588579.zip sourceFiles.zip Experiment.jar report.pdf



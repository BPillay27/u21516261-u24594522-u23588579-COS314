JAVAC=javac
JAVA=java
SRCDIR=.
BINDIR=bin
SOURCES=$(wildcard $(SRCDIR)/*.java)

.PHONY: all compile run clean

all: compile

compile: $(BINDIR)
	$(JAVAC) -d $(BINDIR) $(SOURCES)

$(BINDIR):
	mkdir -p $(BINDIR)

run: compile
	$(JAVA) -cp $(BINDIR) OurTest

clean:
	rm -rf $(BINDIR) results_*.txt

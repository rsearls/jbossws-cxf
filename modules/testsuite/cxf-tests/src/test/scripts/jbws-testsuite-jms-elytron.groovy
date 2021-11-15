def root = new XmlParser().parse(inputFile)
/**
 * This is a tmp dummy script to satisfy the testsuite's requirement
 * for finding a script by this name.  No alterations are make to
 * the standalone.xml as they are not needed.
 */
def writer = new StringWriter()
writer.println('<?xml version="1.0" encoding="UTF-8"?>')
new XmlNodePrinter(new PrintWriter(writer)).print(root)
def f = new File(outputFile)
f.write(writer.toString())
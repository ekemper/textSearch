javac -cp ./lib/junit-4.6.jar:. ./search/TextSearcher.java ./search/TextTokenizer.java ./search/TextSearcherTest.java &&
echo "compilation success!" &&
java -cp ./lib/junit-4.6.jar:. org.junit.runner.JUnitCore search.TextSearcherTest


#java -cp junit-4.12.jar;hamcrest-core-1.3.jar;. org.junit.runner.JUnitCore UserDAOTest ProductDAOTest

#/Users/ek/dev/TextSearchProblem/lib/hamcrest-core-2.2.jar

#:./lib/hamcrest-core-2.2.jar
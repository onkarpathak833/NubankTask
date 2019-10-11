# NubankTask

This application runs transaction/operations against business rules and gives appropritae business specific response.

#REQUIREMENTS

jdk 1.8 and sbt version 1.3.2 for building a project

#HOW TO BUILD?

From project root run command :
sbt clean compile
sbt assembly (for fat/uber jar)
sbt package (for provided/thin jar)

You should get jar file named "Nubank-assembly-0.1.jar". you can also rename this jar file from build.sbt
# HOW TO RUN

From executable jar run jar by passing "operations.txt" file as a command line parameter containing all the input operations to program :

e.g. java -jar Nubank-assembly-0.1.jar "/Users/Documents/Nubank/Data/operations.txt"

This will give you output on stdout/console as :

{"account":{"active-card":true,"available-limit":100},"violations":[]}
{"account":{"active-card":true,"available-limit":150},"violations":["account-already-initialized"]}
{"account":{"active-card":true,"available-limit":80},"violations":[]}
{"account":{"active-card":true,"available-limit":70},"violations":[]}
{"account":{"active-card":true,"available-limit":60},"violations":[]}
{"account":{"active-card":true,"available-limit":35},"violations":[]}
{"account":{"active-card":true,"available-limit":35},"violations":["high-frequency-small-interval"]}

#TEST

Unit and integration test are part of scala/test folder.

#INPUT FILE

sample input and output file are provided in test/resources folder.




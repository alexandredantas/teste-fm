from openjdk:11.0.2

ADD ./bin/teste-gympass-assembly-0.1.jar /teste-gympass.jar

VOLUME /var/teste

CMD java -jar /teste-gympass.jar /var/teste/input
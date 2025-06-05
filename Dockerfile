FROM gradle:latest

COPY / .

RUN gradle installDist

CMD ./build/install/app/bin/app
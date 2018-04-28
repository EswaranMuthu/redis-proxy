FROM gradle:4.3-jdk-alpine
ADD --chown=gradle . /code
WORKDIR /code
CMD ["gradle", "--stacktrace", "bootRun"]

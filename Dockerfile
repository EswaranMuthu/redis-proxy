FROM gradle:4.3-jdk9
# Set the working directory to /app
WORKDIR /code
# Copy the current directory contents into the container at /app
ADD --chown=gradle . /code
# Make port 80 available to the world outside this container
EXPOSE 8080
CMD ["gradle", "--stacktrace", "bootRun"]

  # To start Application using Gradle
        ➜ ./gradlew bootRun
  # To start Application using Docker
          1) Execute from docker hub 
          ➜ docker run -p 8080:8080 --rm eswaran/redis-proxy:1.0.0 
      Docker cloud repo : https://cloud.docker.com/swarm/eswaran/repository/docker/eswaran/redis-proxy/general
     ********************************************************************
        2) Execute from local
          ➜ docker build -t redis-proxy .
          ➜ docker run --rm -p 8080:8080 redis-proxy

# High level design 
  ![alt text](https://github.com/EswaranMuthu/redis-proxy/blob/master/redis-Proxy-Architecture.png)
  1) Request for redis value from client.
  2) Request intercepted (using Spring AOP) by CacheService.
  3) Api to Redis Cache proxy - CacheImpl 
  4) Check if value present in proxy cache memory - CacheMemoryManager
      - If present extend TTL (update LRU)
  5) & 6) retrun value if present in memory 
  7) If value present return to browser / Client
  8) & 9) Else connect to backup instance of Redis using RESP 
      - Also udpate redis Cache proxy 
      - Return value back to Client / browser  
      
  10) Spring Batch impl to remove record after expiry - IdleObjectCollector    

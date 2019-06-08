Learning [spring-cloud-circuitbreaker](https://github.com/spring-cloud-incubator/spring-cloud-circuitbreaker) 

1. Start Eureka

```bash
$ spring cloud eureka
```

2. Start all 3 services. The `slow-service` is running on port 8090

3. Try the `slow-service-client`

```bash
⇒  http :8081
HTTP/1.1 200
Content-Length: 23
Content-Type: text/plain;charset=UTF-8
Date: Fri, 07 Jun 2019 15:10:30 GMT

Hello from slow-service
```

4. Try the `slow-service-reactive-client`

```bash
⇒  http :8081
HTTP/1.1 200
Content-Length: 23
Content-Type: text/plain;charset=UTF-8
Date: Fri, 07 Jun 2019 15:12:04 GMT

Hello from slow-service
```

5. Now, shut down the `slow-service` and try both clients again.

6. Try the `slow-service-client`

```bash
⇒  http :8080
HTTP/1.1 200
Content-Length: 8
Content-Type: text/plain;charset=UTF-8
Date: Fri, 07 Jun 2019 15:13:14 GMT

fallback
```

7. Try the `slow-service-reactive-client`

```bash
⇒  http :8081
HTTP/1.1 500
Connection: close
Content-Type: application/json;charset=UTF-8
Date: Fri, 07 Jun 2019 15:13:26 GMT
Transfer-Encoding: chunked

fallback
```




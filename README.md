# Phát triển và bảo mật ứng dụng Micro Services Movie Web

## 1. Tổng quan
### Architecture 
![](https://i.imgur.com/pzmRvN5.png)


#### **Frontend**: ReactJS
#### **Backend**: Java Spring Boot
#### **Database**: Mysql, MongoDb, Elastic Search, Neo4j

### **DevSecOps Pipeline**:
- **Frontend**: Snyk, Trivy-fs, Trivy Scan
- **Backend**: Sonarqube, Trivy-fs, Trivy Scan
- **Pipeline**: Gitlab
- **DAST**: Arachni
- **Performence Testing** : K6
- **Registry**: Dockerhub

### **Monitoring**:
- **Agents**: Fluentd, Node Exporter
- **Prometheus**
- **Grafana**

#### Message Queue: Kafka
#### IDP: KeyCloak
#### Metadata:  Firebase Storage
#### Web Server: Nginx
#### Backup: AWS S3
#### Cloud:  AWS

## 2. Chi tiết dự án
### Mô hình triển khai :
- Mô hình Micro Services với Docker Conatiner

[![](https://i.ibb.co/10VTMBk/Screenshot-2024-12-03-162954.png)](https://ibb.co/kGnXJzt)

### Chi tiết các Service:
- Triển khai mô hình MVC cho từng service theo nguyên tắc Separation of Concerns
- Mọi traffic từ Application tới ApiGateway đều phải được Authentication với Identity Service Còn việc authorization phụ thuộc vào service đích
- Api Gateway : Sử dụng Spring Cloud Gateway để chịu tải và chuyển tiếp tới các services khác
- Identity Service: Authentication mọi request tới ApiGateway với KeyCloak
- Profile Service: Quản lí Profile User với Graph Database là Neo4J
- Notification Service: Manage và gửi các thông báo của hệ thống với message queue là Kafka và Mail Services là Brevo
- Movie Service: Manage Movie, List Movie, Comment
- Search Service: Filter Search cho ứng dụng với Elastic Search , Movie Data được đồng bộ hóa từ MongoDB sang với LogStash


### KeyCloak:
- Sử dụng như phương thức OpenID
- Manage và Onboard toàn bộ user của hệ thống với những thông tin quan trọng mà chỉ mỗi KeyCloak được giữ (Password, CertBot)
#### Hệ thống phân quyền gồm 2 phần: 
- RBAC: Custom để quản lí Role Permissions của user với Application
- PBAC: Mặc định của KeyCloak nhằm kiểm quyền truy cập dựa trên chinh sách của nó 

### Các tính năng triển khai:
- Login Register Basic (Username, Password)
- Oauth2 với Google
- Gửi nhận thông báo với Kafka
- Lựa chọn các list phim (Most Rating Most Movie)
- Xem phim theo thể loại (Movie , TV Show)
- Comment cho mỗi phim
- Đánh Rating cho mỗi phim
- Filter Search Movie để tìm phim thuận lợi
- Chỉnh sửa profile cá nhân như (City, BirthDay, Image)
- Tìm phim theo gợi ý


### DevSecOps Pipeline: 
Mô hình : 
![](https://i.imgur.com/7RyTwx0.png)



- Ứng dụng trước khi được triển khai thực tế sẽ phải qua quy trình kiểm thử như sau
- SAST(Sonarqube): Là công cụ Static Application Security Testing dùng test source code của Spring
- SAST(Snyk): Là công cụ Static Application Security Testing dùng test source code ReactJS
- SCA(Trivy fs): Là công cụ Software Composition Analysis dùng kiểm thử
các library và dependencies
- DAST(Arachni): Là công cụ Dynamic Application Security Testing dùng test
các malware của application sau khi đã deploy
- Performance Testing(K6): Công cụ kiểm thử Performance của application

### Monitoring:
![](https://elroydevops.tech/wp-content/uploads/2023/01/workflow.png)

- Node Exporter Agent: Agent ở server triển khai application để kiểm soát
các metrics như (CPU, Memory) tới Prometheus
- Fluentd: Logging Agent ở server triển khai application để handle logs như
logs của web server để nếu có DDos thì có thể config ACL của VPC AWS
- Prometheus : Nhận dữ liệu từ các agent đã triển khai và gửi nó cho
grafana
- Grafana: Trực quan hóa dữ liệu nhận được từ Prometheus
-> Khi triển khai sẽ tiến hành xài tools để DDOS vào website để test khả năng monitoring

### Backup:
- Firebase Storage: Backup những metadata của application như video , ảnh movie , ảnh user
- AWS S3: Backup những database nào triển khai cho ứng dụng dưới dạng docker container(elastic search , mysql , neo4j). Rieeng MongoDB được backup bởi MongoCloud

### Link:
- Project Github: https://github.com/nhatanh2709/movie-web-spring-boot
- Pipeline DevSecOps Gitlab: https://gitlab.com/spring-boot-movie-web
- Movie Web: https://nhatanhmovie.website
- Prometheus : http://18.143.118.15:9090
- Grafana Dashboard : http://18.143.118.15:3000
- Keycloak Seft-Host: https://keycloak.website

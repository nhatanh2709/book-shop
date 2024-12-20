# Phát triển và bảo mật ứng dụng Micro Services Movie Web

## 1. Tổng quan
### Architecture 
![](https://imgur.com/PJmtUjI.png)


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
#### Cloud Security Application: 
- **Provider**: AWS
- **Firewall**: AWS WAFs
- **CDN**: Cloudfront
- **Monitoring** : CloudWatch
- **Routing**: Route53
- **Certificate** : AWS Certificate Management

## 2. Chi tiết dự án
### Mô hình triển khai :
- Mô hình Micro Services với Docker Conatiner

![Description of the image](https://imgur.com/xWfyK8q.png)

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
- Sử dụng như 1 Idp(Identity Provider)
- Manage và Onboard toàn bộ user của hệ thống với những thông tin quan trọng mà chỉ mỗi KeyCloak được giữ (Password, Certificate)
#### Hệ thống phân quyền gồm 2 phần: 
- RBAC: Custom để quản lí Role Permissions của user với Application
- PBAC: Mặc định của KeyCloak nhằm kiểm quyền truy cập dựa trên chinh sách của nó 
#### Giao thức triển khai:
- OpenID Connect: Triển khai theo mô hình Oauth2(Google) kết hợp Authentication 

### Các tính năng triển khai:
- Login Register Basic (Username, Password)
- Login với dịch vụ bên thứ 3 như Google
- Xác thực Captcha với Google Recaptcha
- Thay đổi mật khẩu 
- Gửi nhận thông báo với Kafka và Mail Service là Brevo 
- Lựa chọn các list phim (Most Rating Most Movie)
- Xem phim theo thể loại (Movie , TV Show)
- Comment cho mỗi phim
- Đánh Rating cho mỗi phim
- Filter Search Movie với tiêu đề
- Quản lí chỉnh sửa profile cá nhân
- Tìm phim theo Recommend Movie System
- Thực hiện mua gói phim để xem được nhiều dịch vụ hơn với Momo Sandbox
- Upload phim và hình ảnh với Storage Provider là Firebase


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
- AWS S3: Backup những database nào triển khai cho ứng dụng dưới dạng docker container(Elastic Search , Mysql , Neo4j). Rieeng MongoDB được backup bởi MongoCloud

### Cloud Security Application:
![](https://imgur.com/zYk6EEK.png)
- AWS WAFs: Là dịch vụ Web Application Firewall được enable để ngăn chặn:
  - Anti DDos Layer 7
  - Top 10 OWSAP**
  - Traffic from Back-list , VPN
  - Request from USA
- Cloudfront : Là dịch vụ CDN làm giảm latency của traffic ở khắp mọi nơi và được kết hợp với AWS Shield là giải pháp Anti DDos layer 3, 4
- CloudWatch : Là giải pháp Monitoring của Cloud giúp giám sát metrics tới WAF và CloudFront với các tùy chọn như : 
  - Block Requests and Access Request
  - IP of request
  - Country of request
  - Browser of request
- Route53: Là dịch vụ routing nhằm handle traffic tới CloudFront và kết hợp với AWS Certificate Management làm certificate cho request 


### Link:
- Project Github: https://github.com/nhatanh2709/movie-web-spring-boot
- Pipeline DevSecOps Gitlab: https://gitlab.com/spring-boot-movie-web
- Movie Web: https://nhatanhmovie.website
- Prometheus : http://18.143.118.15:9090
- Grafana Dashboard : http://18.143.118.15:3000
- Keycloak Seft-Host: https://keycloak.website

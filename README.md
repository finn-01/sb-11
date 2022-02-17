


> Written with [StackEdit](https://stackedit.io/).
# Spring Boot JPA + MySQL
### **Spring Boot JPA**

**Spring Boot JPA** là một phần trong hệ sinh thái Spring Data, nó tạo ra một layer ở giữa tầng service và database, giúp chúng ta thao tác với database một cách dễ dàng hơn, tự động config và giảm thiểu code thừa thãi.

**Spring Boot JPA** đã wrapper Hibernate và tạo ra một interface mạnh mẽ. Nếu như bạn gặp khó khăn khi làm việc với Hibernate thì đừng lo, bạn hãy để **Spring JPA** làm hộ.

### **Cài đặt**

Để thêm Spring JPA vào project, bạn cần thêm dependency `spring-boot-starter-data-jpa`.

Ngoài ra, để connect tới MySql, chúng ta cần driver tương ứng, vì vậy phải bổ sung thêm cả dependency `mysql-connector-java` vào _pom.xml_.

### **Tạo Table và dữ liệu**

Trước khi bắt đầu, chúng ta cần tạo ra dữ liệu trong Database. Ở đây tôi chọn `MySQL`.

Dưới đây là SQL Script để tạo DATABASE `micro_db`. Chứa một TABLE duy nhất là `User`.

Khi chạy script này, nó sẽ tự động insert vào db 100 `User`.

```
CREATE DATABASE micro_db;
use micro_db;
CREATE TABLE `user`
(
  `id`         bigint(20) NOT NULL      AUTO_INCREMENT,
  `hp`   		int  NULL          DEFAULT NULL,
  `stamina`    int                  DEFAULT NULL,
  `atk`      int                    DEFAULT NULL,
  `def`      int                    DEFAULT NULL,
  `agi`      int                    DEFAULT NULL,
  PRIMARY KEY (`id`)
);


DELIMITER $$
CREATE PROCEDURE generate_data()
BEGIN
  DECLARE i INT DEFAULT 0;
  WHILE i < 100 DO
    INSERT INTO `user` (`hp`,`stamina`,`atk`,`def`,`agi`) VALUES (i,i,i,i,i);
    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL generate_data();
```

### **Tạo Model User**

Khi đã có dữ liệu trong Database. Chúng ta sẽ tạo một Class trong Java để mapping thông tin.

_User.java_

```java
@Entity
@Table(name = "user")
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -297553281792804396L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mapping thông tin biến với tên cột trong Database
    @Column(name = "hp")
    private int hp;
    @Column(name = "stamina")
    private int stamina;

    // Nếu không đánh dấu @Column thì sẽ mapping tự động theo tên biến
    private int atk;
    private int def;
    private int agi;
}
```
### **JpaRepository**

Để sử dụng **Spring JPA**, bạn cần sử dụng interface `JpaRepository`.

Yêu cầu của interface này đó là bạn phải cung cấp 2 thông tin:

1.  Entity (Đối tượng tương ứng với Table trong DB)
2.  Kiểu dữ liệu của khóa chính (primary key)

Ví dụ: Tôi muốn lấy thông tin của bảng `User` thì làm như sau:

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

Vậy thôi, `@Repository` đánh dấu `UserRepository` là một Bean và chịu trách nhiệm giao tiếp với DB.

**Spring Boot** sẽ tự tìm thấy và khởi tạo ra đối tượng `UserRepository` trong Context. Việc tạo ra `UserRepository` hoàn toàn tự động và tự config, vì chúng ta đã kế thừa `JpaRepository`.

Bây giờ, việc lấy ra toàn bộ `User` sẽ như sau:

```java
@Autowired
UserRepository userRepository;

userRepository.findAll()
                .forEach(System.out::println);
```

Đơn giản và ngắn gọn hơn rất nhiều.

Nếu bạn tìm kiếm thì sẽ thấy `UserRepository` có hàng chục method mà chúng ta không cần viết lại nữa. Vì nó kế thừa `JpaRepository` rồi.

### **Demo**

Bây giờ chúng ta sẽ làm ứng dụng Demo các tính năng cơ bản với `JpaRepository`

Bước đầu tiên là config thông tin về MySQL trong `application.properties`

_application.properties_

```makefile
spring.datasource.url=jdbc:mysql://localhost:3306/micro_db?useSSL=false
spring.datasource.username=root
spring.datasource.password=root


## Hibernate Properties
# The SQL dialect makes Hibernate generate better SQL for the chosen database
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto = update

logging.level.org.hibernate = ERROR
```

**Spring JPA** sẽ tự kết nối cho chúng ta, mà không cần thêm một đoạn code nào cả.

_User.java_

```java
@Entity
@Table(name = "user")
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -297553281792804396L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mapping thông tin biến với tên cột trong Database
    @Column(name = "hp")
    private int hp;
    @Column(name = "stamina")
    private int stamina;

    // Nếu không đánh dấu @Column thì sẽ mapping tự động theo tên biến
    private int atk;
    private int def;
    private int agi;
}
```

_App.java_

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class App {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class, args);
        UserRepository userRepository = context.getBean(UserRepository.class);

        // Lấy ra toàn bộ user trong db
        userRepository.findAll()
                      .forEach(System.out::println);

        // Lưu user xuống database
        User user = userRepository.save(new User());
        // Khi lưu xong, nó trả về User đã lưu kèm theo Id.
        System.out.println("User vừa lưu có ID: " + user.getId());
        Long userId = user.getId();
        // Cập nhật user.
        user.setAgi(100);
        // Update user
        // Lưu ý, lúc này đối tượng user đã có Id.
        // Nên nó sẽ update vào đối tượng có Id này
        // chứ không insert một bản ghi mới
        userRepository.save(user);

        // Query lấy ra user vừa xong để kiểm tra xem.
        User user2 = userRepository.findById(userId).get();
        System.out.println("User: " + user);
        System.out.println("User2: " + user2);

        // Xóa User khỏi DB
        userRepository.delete(user);

        // In ra kiểm tra xem userId còn tồn tại trong DB không
        User user3 = userRepository.findById(userId).orElse(null);
        System.out.println("User3: " + user2);

    }

}
```

OUTPUT chương trình:

```makefile
User vừa lưu có ID: 104
// sau khi update, cả 2 đối tượng user đều có giá trị agi mới
User vừa lưu có ID: 101
User: User(id=101, hp=0, stamina=0, atk=0, def=0, agi=100)
User2: User(id=101, hp=0, stamina=0, atk=0, def=0, agi=100)
User3: User(id=101, hp=0, stamina=0, atk=0, def=0, agi=100)
```

https://loda.me/articles/sb11-huong-dan-spring-boot-jpa-mysql

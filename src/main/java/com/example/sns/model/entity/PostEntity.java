package com.example.sns.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"post\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"post\" SET deleted_at = NOW() where id =?")  // delete 쿼리가 날라왔을 때 delete된 시간을 처리해줌
@Where(clause = "deleted_at is NULL") // deleted_At이 NULL 인것만 가져온다.
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "body", columnDefinition = "TEXT") // Column 타입을 text로 하겠다.
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")// 외래키가 있는 주인쪽에서 상대방한테 JoinColumn을 건다.
    private UserEntity role;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    // 매번 엔티티를 만들어 줄때마다 업데이트하고 귀찮으니까 어노테이션으로 자동으로 넣을 수 있도록한다.
    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updateAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

}

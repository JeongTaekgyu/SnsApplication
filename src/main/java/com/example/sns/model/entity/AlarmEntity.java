package com.example.sns.model.entity;

import com.example.sns.model.AlarmArgs;
import com.example.sns.model.AlarmType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"alarm\"", indexes = {
        @Index(name = "user_id_idx", columnList = "user_id")
        // name : 인덱스 이름,  columnList : 인덱스를 어떤 컬럼에 걸건지
})
@Getter
@Setter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) // jsonb타입을 정의한다.
@SQLDelete(sql = "UPDATE \"alarm\" SET deleted_at = NOW() where id =?")  // delete 쿼리가 날라왔을 때 delete된 시간을 처리해줌
@Where(clause = "deleted_at is NULL") // deleted_At이 NULL 인것만 가져온다.
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 알람을 받는 사람
    @ManyToOne
    @JoinColumn(name = "user_id")// 외래키가 있는 주인쪽에서 상대방한테 JoinColumn을 건다.
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    // json 형태만 맞으면 어떤 데이터가 와도 문제가 없다.
    @Type(type = "jsonb")   // jsonb는 압축해서 저장이 되며 또한 인덱스를 걸 수 있다. json은 그렇지 않다. (jsonb는 Postgresql 에서 지원된다.)
    @Column(columnDefinition = "json")  // Postgresql 은 json 타입을 지원한다.
    private AlarmArgs args;   // 알람이 발생된 주체

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

    public static AlarmEntity of(UserEntity userEntity, AlarmType alarmType, AlarmArgs args) {
        AlarmEntity entity = new AlarmEntity();
        entity.setUser(userEntity);
        entity.setAlarmType(alarmType);
        entity.setArgs(args);
        return entity;
    }
}

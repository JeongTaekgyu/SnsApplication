package com.example.sns.repository;

import com.example.sns.model.entity.CommentEntity;
import com.example.sns.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentEntityRepository extends JpaRepository<CommentEntity, Integer> {

    Page<CommentEntity> findAllByPost(PostEntity post, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE CommentEntity entity SET deleted_at = NOW() where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity postEntity);
    // 영속성을 관리하기 위해서는 db에서 데이터를 불러오는데
    // 삭제를 하기 위해서 db에서 데이터를 불러오게 하지 않고 삭제 쿼리만 날리게 한다.
    // 즉, 삭제하기 위해 데이터를 가져오지 않고 삭제 쿼리만 날린다.
}

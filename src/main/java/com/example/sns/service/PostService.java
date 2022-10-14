package com.example.sns.service;

import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.model.AlarmArgs;
import com.example.sns.model.AlarmType;
import com.example.sns.model.Comment;
import com.example.sns.model.Post;
import com.example.sns.model.entity.*;
import com.example.sns.model.event.AlarmEvent;
import com.example.sns.producer.AlarmProducer;
import com.example.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final AlarmService alarmService;
    private final AlarmProducer alarmProducer;

    @Transactional
    public void create(String title, String body, String userName){
        // user find
        UserEntity userEntity = getUserEntityOrException(userName);
        // post save
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId){
        UserEntity userEntity = getUserEntityOrException(userName);
        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

//        return Post.fromEntity(postEntityRepository.save(postEntity));
        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));  // saveAndFlush
        // save() 메소드는 바로 DB 에 저장되지 않고 영속성 컨텍스트에 저장되었다가 flush() 또는 commit() 수행 시 DB에 저장됨
        // 근데 save 하면 db에 updatedAt이 저장은 되는데 반환할때 updatedAt 이 null로 들어감 (saveAndFlush하면 updatedAt이 정상적으로 반환됨)
        // saveAndFlush() 메소드는 실행중(트랜잭션)에 즉시 data를 flush 한다.
        // saveAndFlush() 메소드는 Spring Data JPA 에서 정의한 JpaRepository 인터페이스의 메소드이다.
    }

    public void delete(String userName, Integer postId){
        UserEntity userEntity = getUserEntityOrException(userName);

        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable){
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable){
        UserEntity userEntity = getUserEntityOrException(userName);

        // 내가 작성한 포스트중에서 findAll 해야한다.
        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);
        UserEntity userEntity = getUserEntityOrException(userName);

        // check liked -> throw
        // 이미 해당 게시물에 like 눌렀으면 ALREADY_LIKED 에러 발생시킨다.
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
        });

        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));

        alarmProducer.send(new AlarmEvent(postEntity.getUser().getId(),
                AlarmType.NEW_LIKE_ON_POST,
                new AlarmArgs(userEntity.getId(), postEntity.getId()) ));
    }

    @Transactional
    public long likeCount(Integer postId) {
        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

        // count liked -> throw
        // 이렇게 가져오면 Post의 list를 전부 가져와야해서 효율적이지 못함
//        List<LikeEntity> likeEntities = likeEntityRepository.findAllByPost(postEntity);
//        return likeEntities.size();

        // 해당 포스트의 like count만 가져온다.
        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, String userName, String comment){
        PostEntity postEntity = getPostEntityOrException(postId);
        UserEntity userEntity = getUserEntityOrException(userName);

        // comment save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));
        // 알람은 post를 작성한 사람에게 보낸다.
        alarmProducer.send(new AlarmEvent(postEntity.getUser().getId(),
                AlarmType.NEW_COMMENT_ON_POST,
                new AlarmArgs(userEntity.getId(), postEntity.getId()) ));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable){
        PostEntity postEntity = getPostEntityOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
                                                                            // a -> Comment.fromEntity(a)
    }

    // post exist
    private PostEntity getPostEntityOrException(Integer postId){
        return postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }

    // user exist
    private UserEntity getUserEntityOrException(String userName){
        return userEntityRepository.findByUserName(userName).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }
}

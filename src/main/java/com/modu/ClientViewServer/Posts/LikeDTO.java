package com.modu.ClientViewServer.Posts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Date;

@NoArgsConstructor
@Builder
@Setter
@AllArgsConstructor
@Component("likedto")
public class LikeDTO {
    private long likeId;
    private long likeMemberId;
    private Date createAt;
    private long postId;

    @Override
    public String toString() {
        return "LikeDTO{" +
                "likeId=" + likeId +
                ", likeMemberId=" + likeMemberId +
                ", createAt=" + createAt +
                ", postId=" + postId +
                '}';
    }

}




package com.example.lms_api.dto.response.communit_response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommunityStatsResponse {
    private long totalMembers;
    private long totalTopics;
    private long totalReplies;
    private int liveOnlineCount;
}

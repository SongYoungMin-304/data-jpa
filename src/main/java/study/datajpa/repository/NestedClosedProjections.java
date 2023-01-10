package study.datajpa.repository;

import study.datajpa.entity.Team;

public interface NestedClosedProjections {

    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo{
        String getName();
    }
}

package com.d2vfactory.throwmoney.domain.user.repository;

import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import com.d2vfactory.throwmoney.domain.user.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {

    List<UserRoom> findAllByUser(User user);

    //List<UserRoom> findAllByRoom(Room room);

    @Query("select distinct r from UserRoom r  left join fetch r.user  left join fetch r.room where r.room = ?1 order by r.id")
    List<UserRoom> fetchAllByRoom(Room room);
}

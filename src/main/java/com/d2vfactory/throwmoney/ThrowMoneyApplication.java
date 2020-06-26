package com.d2vfactory.throwmoney;

import com.d2vfactory.throwmoney.domain.token.Token;
import com.d2vfactory.throwmoney.domain.token.repository.TokenRepository;
import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import com.d2vfactory.throwmoney.domain.user.UserRoom;
import com.d2vfactory.throwmoney.domain.user.repository.RoomRepository;
import com.d2vfactory.throwmoney.domain.user.repository.UserRepository;
import com.d2vfactory.throwmoney.domain.user.repository.UserRoomRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@EnableJpaAuditing
@SpringBootApplication
public class ThrowMoneyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThrowMoneyApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository,
                                      RoomRepository roomRepository,
                                      UserRoomRepository userRoomRepository,
                                      TokenRepository tokenRepository) {
        return args -> {

            // 기본 사용자/대화방 정보 테스트 셋

            User user1 = new User("송길주");
            User user2 = new User("라이언");
            User user3 = new User("어피치");
            User user4 = new User("무지");
            User user5 = new User("콘");
            User user6 = new User("프로도");
            User user7 = new User("네오");
            User user8 = new User("튜브");
            User user9 = new User("제이지");


            List.of(user1, user2, user3, user4, user5, user6, user7, user8, user9)
                    .stream()
                    .forEach(x -> userRepository.save(x));


            Room room1 = new Room("대화방1");
            Room room2 = new Room("대화방2");
            Room room3 = new Room("대화방3");


            List.of(room1, room2, room3)
                    .stream()
                    .forEach(x -> roomRepository.save(x));


            UserRoom userRoom11 = new UserRoom(user1, room1);
            UserRoom userRoom21 = new UserRoom(user2, room1);
            UserRoom userRoom31 = new UserRoom(user3, room1);
            UserRoom userRoom41 = new UserRoom(user4, room1);
            UserRoom userRoom51 = new UserRoom(user5, room1);
            UserRoom userRoom61 = new UserRoom(user6, room1);
            UserRoom userRoom71 = new UserRoom(user7, room1);

            UserRoom userRoom12 = new UserRoom(user1, room2);
            UserRoom userRoom72 = new UserRoom(user7, room2);
            UserRoom userRoom82 = new UserRoom(user8, room2);
            UserRoom userRoom92 = new UserRoom(user9, room2);

            UserRoom userRoom23 = new UserRoom(user2, room3);
            UserRoom userRoom53 = new UserRoom(user5, room3);


            List.of(userRoom11, userRoom21, userRoom31, userRoom41, userRoom51, userRoom61, userRoom71,
                    userRoom12, userRoom72, userRoom82, userRoom92, userRoom23, userRoom53)
                    .stream()
                    .forEach(x -> userRoomRepository.save(x));


            // 토큰
            // alphanemeric : 62 chars
            // token : 3 length
            // => 62^3 = 238,328
            int maxRows = 20;
            Set<String> tokenSet = new HashSet<>();
            while (tokenSet.size() < maxRows) {
                tokenSet.add(RandomStringUtils.randomAlphanumeric(3));
            }

            Iterator<String> iterator = tokenSet.iterator();
            while (iterator.hasNext()) {
                tokenRepository.save(new Token(iterator.next()));
            }

        };

    }
}

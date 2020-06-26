package com.d2vfactory.throwmoney.domain.user;

import com.d2vfactory.throwmoney.config.StringPrefixedSequenceIdGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "ROOM")
@ToString
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_seq")
    @GenericGenerator(
            name = "room_seq",
            strategy = "com.d2vfactory.throwmoney.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "1"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "room-"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
    @Column(name = "ROOM_ID")
    private String id;

    private String name;

    public Room(String name) {
        this.name = name;

    }

}

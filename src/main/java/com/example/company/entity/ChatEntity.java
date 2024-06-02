package com.example.company.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chats")
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "main_user_id", nullable = false)
    private UserEntity mainUser;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private UserEntity participant;
}

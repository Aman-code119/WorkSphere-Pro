package com.example.CHAT.ANNOUNCEMENT.SERVICE.Repository;

import com.example.CHAT.ANNOUNCEMENT.SERVICE.Model.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Chat_Repository extends JpaRepository<ChatType,Long> {


    List<ChatType> findByChatType(String chatType);
}

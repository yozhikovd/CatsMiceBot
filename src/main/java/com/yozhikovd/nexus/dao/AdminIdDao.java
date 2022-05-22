package com.yozhikovd.nexus.dao;

import com.yozhikovd.nexus.models.AdminId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminIdDao extends CrudRepository<AdminId,Long> {

    boolean existsAdminChatIdByChatId(String chatId);

}

package com.yozhikovd.nexus.telegram;

import com.yozhikovd.nexus.dao.AdminIdDao;
import com.yozhikovd.nexus.dao.OrderDao;
import com.yozhikovd.nexus.models.AdminId;
import com.yozhikovd.nexus.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Iterator;
import java.util.List;

@Component
public class CatsMiceAdminBot extends TelegramLongPollingBot {

    @Lazy
    @Autowired
    CatsMiceUserBot catsMiceUserBot;

    @Autowired
    OrderDao orderDao;

    @Autowired
    AdminIdDao adminIdDao;

    SendMessage sm = new SendMessage();

    @Value("${telegram.admin.bot.username}")
    private String username;

    @Value("${telegram.admin.bot.token}")
    private String token;

    @Override
    public void onUpdateReceived(Update update) {

        String chatId = update.getMessage().getChatId().toString();
        boolean isAdminAuth = adminIdDao.existsAdminChatIdByChatId(chatId);

        if ("/help".equals(update.getMessage().getText().trim())) {
            sendMessage("/start_? - авторизовать админа, где ? пароль", chatId);
            sendMessage("/allOrders - посмотреть все заказы", chatId);
            sendMessage("/changeStatus_*_? - изменить статус заказа где * - номер заказа, ? " +
                    "- статус заказа (INWORK - в работе, DONE- готов). Например : /changeStatus_120_INWORK", chatId);
            sendMessage("/deleteAdmin - удалить авторизацию админа", chatId);
        }

        if ((update.getMessage().getText().trim()).contains("/start")) {
            var isAdminIdExists = adminIdDao.existsAdminChatIdByChatId(chatId);

            if (!isAdminIdExists) {

                List<String> attrs = List.of((update.getMessage().getText().trim()).split("_"));

                if (attrs.size() > 1 && "admin".equals(attrs.get(1))) {
                    var adminChatId = new AdminId(chatId);
                    adminIdDao.save(adminChatId);
                    sendMessage("Админ авторизован", chatId);
                    isAdminAuth = true;
                } else {
                    sendMessage("Неверный пароль для авторизации", chatId);
                    return;
                }
            } else {
                sendMessage("Админ уже авторизован", chatId);
                return;
            }
        }

        if ("/allOrders".equals(update.getMessage().getText().trim()) && isAdminAuth) {

            Iterator<Order> iterator = orderDao.findAll().iterator();

            while (iterator.hasNext()) {
                Order order = iterator.next();
                sendMessage(order.toString(), chatId);
            }
        }

        if ("/deleteAdmin".equals(update.getMessage().getText().trim()) && isAdminAuth) {
            adminIdDao.deleteAll();
        }

        if ((update.getMessage().getText().trim()).contains("/changeStatus") && isAdminAuth) {

            List<String> attrs = List.of((update.getMessage().getText().trim()).split("_"));

            String statusToChange = attrs.get(2);

            switch (statusToChange) {
                case "INWORK":
                    var order = orderDao.findById(Long.parseLong(attrs.get(1))).get();
                    order.setOrderStatus(statusToChange);
                    orderDao.save(order);
                    catsMiceUserBot.sendMessage("Ваш заказ - ", order.getCustomerTelegramId());
                    catsMiceUserBot.sendMessage(order.getProduct() + " - взят в работу", order.getCustomerTelegramId());
                    break;
                case "DONE": {
                    var order2 = orderDao.findById(Long.parseLong(attrs.get(1))).get();
                    order2.setOrderStatus(statusToChange);
                    orderDao.save(order2);
                    catsMiceUserBot.sendMessage("Ваш заказ - ", order2.getCustomerTelegramId());
                    catsMiceUserBot.sendMessage(order2.getProduct() + " - готов, курьер Влад уже мчится на скейтборде", order2.getCustomerTelegramId());
                    break;
                }
            }
        }
    }

    public void sendMessage(String message, String chatId) {
        sm.setChatId(chatId);
        sm.setText(message);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}

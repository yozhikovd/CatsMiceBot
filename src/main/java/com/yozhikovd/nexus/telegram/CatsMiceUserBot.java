package com.yozhikovd.nexus.telegram;

import com.yozhikovd.nexus.dao.AdminIdDao;
import com.yozhikovd.nexus.dao.OrderDao;
import com.yozhikovd.nexus.dao.ProductDao;
import com.yozhikovd.nexus.models.Order;
import com.yozhikovd.nexus.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Iterator;
import java.util.Objects;

@Component
public class CatsMiceUserBot extends TelegramLongPollingBot {

    @Autowired
    CatsMiceAdminBot catsMiceAdminBot;


    @Autowired
    ProductDao productDao;
    @Autowired
    OrderDao orderDao;

    @Autowired
    AdminIdDao adminIdDao;

    @Value("${telegram.user.bot.username}")
    private String username;

    @Value("${telegram.user.bot.token}")
    private String token;

    SendMessage sm = new SendMessage();


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String adminId = adminIdDao.findAll().iterator().next().getChatId();

            if ("/start".equals(update.getMessage().getText().trim())) {
                String message = "Добрый день! Выберите позицию для заказа : ";
                sendMessage(message, chatId);

                Iterator<Product> iterator = productDao.findAll().iterator();

                while (iterator.hasNext()) {
                    Product product = iterator.next();
                    sendMessage(product.toString(), chatId);
                }
                sendMessage("Для заказа пришлите номер товара", chatId);
                return;
            }

            Long idProduct = null;
            String temp = update.getMessage().getText().trim();
            try {
                idProduct = Long.parseLong(temp);
            } catch (Exception ignored) {
                sendMessage("Хуйню прекращай писать", chatId);
            }

            if (Objects.nonNull(idProduct)) {
                sendMessage("Вы выбрали :", chatId);
                var item = productDao.findById(idProduct);
                if (item.isPresent()) {
                    sendMessage(item.get().toString(), chatId);

                    var order = new Order(item.get().getProductName(), update.getMessage().getFrom().getUserName(), "NEW", chatId);
                    orderDao.save(order);

                    catsMiceAdminBot.sendMessage("Хуйлан ебашь на кухню тебе поступил заказ", adminId);
                    catsMiceAdminBot.sendMessage(order.toString(), adminId);

                } else {
                    sendMessage("Такой позиции не найдено", chatId);
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

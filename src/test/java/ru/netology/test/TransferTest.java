package ru.netology.test;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import ru.netology.data.Data;
import ru.netology.data.DbInteraction;

import static ru.netology.data.Data.getCardTransferInfo;
import static org.hamcrest.Matchers.*;

public class TransferTest {

    public String shouldLoginAndGetToken() {
        Data.login();
        return Data.verify(DbInteraction.getVerificationCode(Data.getAuthInfo()));
    }

    @Test
    void shouldTransferToOwnCardAndRevers() {
        String token = shouldLoginAndGetToken();
        Data.CardTransferInfo cardTransferInfo = getCardTransferInfo("5559 0000 0000 0001", "5559 0000 0000 0002", "5000");
        String jsonCardData = new Gson().toJson(cardTransferInfo);
        Data.transfer(token, jsonCardData);
        Data.showCards(token)
                .body("[0].balance", equalTo(15000))
                .body("[1].balance", equalTo(5000));
        Data.CardTransferInfo cardTransferInfoForRevers = getCardTransferInfo("5559 0000 0000 0002", "5559 0000 0000 0001", "5000");
        String jsonCardDataForRevers = new Gson().toJson(cardTransferInfoForRevers);
        Data.transfer(token, jsonCardDataForRevers);
        Data.showCards(token)
                .body("[0].balance", equalTo(10000))
                .body("[1].balance", equalTo(10000));
    }

    @Test
    void shouldTransferTOthersCardAndRevers() {
        String token = shouldLoginAndGetToken();
        Data.CardTransferInfo cardTransferInfo = getCardTransferInfo("5559 0000 0000 0001", "5559 0000 0000 7777", "5000");
        String jsonCardData = new Gson().toJson(cardTransferInfo);
        Data.transfer(token, jsonCardData);
        Data.showCards(token)
                .body("[1].balance", equalTo(5000));
        Data.CardTransferInfo cardTransferInfoForRevers = getCardTransferInfo("5559 0000 0000 7777", "5559 0000 0000 0001", "5000");
        String jsonCardDataForRevers = new Gson().toJson(cardTransferInfoForRevers);
        Data.transfer(token, jsonCardDataForRevers);
        Data.showCards(token)
                .body("[1].balance", equalTo(10000));
    }

    @Test
    void shouldNotTransferIfAmountExceeded() {
        String token = shouldLoginAndGetToken();
        Data.CardTransferInfo cardTransferInfo = getCardTransferInfo("5559 0000 0000 0001", "5559 0000 0000 7777", "50000");
        String jsonCardData = new Gson().toJson(cardTransferInfo);
        Data.transfer(token, jsonCardData);
        Data.showCards(token)
                .body("[1].balance", equalTo(10000));
    }
}